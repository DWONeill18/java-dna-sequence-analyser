
import argparse
import re
import subprocess
import tempfile
import xml.etree.ElementTree as ET

from collections import defaultdict
from pathlib import Path


METRICS = ["INSTRUCTION", "LINE", "BRANCH"]


def run_command(command, cwd=None):
    result = subprocess.run(
        command,
        cwd=cwd,
        text=True
    )

    return result.returncode == 0


def read_coverage(xml_path):
    root = ET.parse(xml_path).getroot()

    counters = {}

    for metric in METRICS:
        counter = root.find(f"counter[@type='{metric}']")

        if counter is None:
            counters[metric] = (0, 0, 0)
            continue

        covered = int(counter.attrib["covered"])
        missed = int(counter.attrib["missed"])
        total = covered + missed

        counters[metric] = (covered, missed, total)

    return counters


def read_lines(xml_path):
    root = ET.parse(xml_path).getroot()

    results = {}

    for package in root.findall("package"):
        package_name = package.attrib["name"]

        for source in package.findall("sourcefile"):
            source_name = source.attrib["name"]

            for line in source.findall("line"):
                key = (
                    package_name,
                    source_name,
                    int(line.attrib["nr"])
                )

                results[key] = {
                    "covered": int(line.attrib["ci"]),
                    "missed": int(line.attrib["mi"]),
                    "branches_covered": int(line.attrib["cb"]),
                    "branches_missed": int(line.attrib["mb"])
                }

    return results


def discover_tests(project):
    test_root = project / "src/test/java"
    tests = {}

    for file in sorted(test_root.rglob("*Test.java")):
        source = file.read_text(encoding="utf-8")

        match = re.search(
            r"^\s*package\s+([\w.]+)\s*;",
            source,
            re.MULTILINE
        )

        package = match.group(1) if match else ""
        class_name = file.stem

        qualified_name = (
            f"{package}.{class_name}"
            if package else class_name
        )

        tests[qualified_name] = file

    return tests


def collect_individual_coverage(
    project,
    label,
    cli_jar,
    temporary_directory
):
    tests = discover_tests(project)
    reports = {}
    failures = []

    output_directory = temporary_directory / label
    output_directory.mkdir(parents=True, exist_ok=True)

    for test_class, test_file in tests.items():

        safe_name = (
            test_file.relative_to(
                project / "src/test/java"
            ).with_suffix("").as_posix().replace("/", "_")
        )

        execution_file = output_directory / f"{safe_name}.exec"
        xml_file = output_directory / f"{safe_name}.xml"

        print(f"Running {label}: {test_class}")

        success = run_command([
            "mvn",
            "-B",
            "-f", str(project / "pom.xml"),
            f"-Dtest={test_class}",
            f"-Djacoco.destFile={execution_file}",
            "test"
        ])

        if not success:
            failures.append(test_class)
            print(f"WARNING: Isolated test failed: {test_class}")
            continue

        if not execution_file.exists():
            failures.append(test_class)
            print(f"WARNING: No JaCoCo data for {test_class}")
            continue

        success = run_command([
            "java",
            "-jar", str(cli_jar),
            "report", str(execution_file),
            "--classfiles", str(project / "target/classes"),
            "--sourcefiles", str(project / "src/main/java"),
            "--xml", str(xml_file)
        ])

        if success:
            reports[test_class] = read_lines(xml_file)

    return reports, failures


def percentage(covered, missed):
    total = covered + missed

    if total == 0:
        return None

    return covered / total * 100


def format_percentage(value):
    return "N/A" if value is None else f"{value:.2f}%"


def compare_coverage(
    baseline_xml,
    pr_xml,
    baseline_tests,
    pr_tests,
    project
):
    baseline = read_coverage(baseline_xml)
    current = read_coverage(pr_xml)

    baseline_lines = read_lines(baseline_xml)
    current_lines = read_lines(pr_xml)

    comment = [
        "## 📊 JaCoCo Coverage Comparison",
        "",
        "| Metric | Main | PR | Change |",
        "|---|---:|---:|---:|"
    ]

    for metric in METRICS:
        old_covered, old_missed, _ = baseline[metric]
        new_covered, new_missed, _ = current[metric]

        old_percentage = percentage(old_covered, old_missed)
        new_percentage = percentage(new_covered, new_missed)

        if old_percentage is None or new_percentage is None:
            change = "N/A"
        else:
            delta = new_percentage - old_percentage

            if delta > 0.005:
                change = f"🟢 +{delta:.2f} pp"
            elif delta < -0.005:
                change = f"🔴 {delta:.2f} pp"
            else:
                change = "⚪ 0.00 pp"

        comment.append(
            f"| {metric.title()} | "
            f"{format_percentage(old_percentage)} | "
            f"{format_percentage(new_percentage)} | "
            f"{change} |"
        )

    # Find source lines that were covered on main
    # but are no longer covered by the PR.
    regressions = defaultdict(list)

    for key, old in baseline_lines.items():
        package, source, line_number = key
        new = current_lines.get(key)

        if new is None:
            continue

        source_path = (
            project / "src/main/java" /
            Path(package) / source
        )

        if not source_path.exists():
            continue

        if old["covered"] > 0 and new["covered"] == 0:
            regressions[(package, source)].append(line_number)

    comment.extend([
        "",
        "### 🔎 Production coverage regressions",
        ""
    ])

    if not regressions:
        comment.append("No previously covered source lines have become uncovered.")
    else:
        for (package, source), lines in sorted(regressions.items()):

            comment.append(
                f"**{package}.{Path(source).stem}**"
            )

            comment.append(
                f"- Newly uncovered lines: "
                f"`{', '.join(map(str, sorted(lines)))}`"
            )

            associated_tests = []

            for test_class, test_report in baseline_tests.items():

                covered_lines = [
                    line for line in lines
                    if test_report.get(
                        (package, source, line),
                        {}
                    ).get("covered", 0) > 0
                ]

                if not covered_lines:
                    continue

                pr_report = pr_tests.get(test_class)

                if pr_report is None:
                    status = "not present in PR"
                elif any(
                    pr_report.get(
                        (package, source, line),
                        {}
                    ).get("covered", 0) > 0
                    for line in covered_lines
                ):
                    status = "still covers some affected lines"
                else:
                    status = "no longer covers affected lines"

                associated_tests.append(
                    f"`{test_class}` ({status}; "
                    f"baseline lines: "
                    f"{', '.join(map(str, covered_lines))})"
                )

            if associated_tests:
                comment.append("- Previously associated test classes:")

                for test in associated_tests:
                    comment.append(f"  - {test}")
            else:
                comment.append(
                    "- No individual baseline test class could "
                    "be associated with these lines."
                )

            comment.append("")

    comment.extend([
        "### Test execution",
        "",
        f"- Baseline test classes analysed: {len(baseline_tests)}",
        f"- PR test classes analysed: {len(pr_tests)}",
        "",
        "_Test associations identify classes that exercised "
        "the affected lines in isolation. They do not prove "
        "which test caused a regression._"
    ])

    return "\n".join(comment)


def main():
    parser = argparse.ArgumentParser()

    parser.add_argument("--baseline", required=True)
    parser.add_argument("--pr", required=True)
    parser.add_argument("--jacoco-cli", required=True)
    parser.add_argument("--output", default="coverage-comment.md")

    args = parser.parse_args()

    baseline = Path(args.baseline).resolve()
    pr = Path(args.pr).resolve()
    cli_jar = Path(args.jacoco_cli).resolve()

    baseline_xml = (
        baseline / "target/site/jacoco/jacoco.xml"
    )

    pr_xml = (
        pr / "target/site/jacoco/jacoco.xml"
    )

    for report in [baseline_xml, pr_xml]:
        if not report.exists():
            raise FileNotFoundError(
                f"Missing JaCoCo report: {report}"
            )

    with tempfile.TemporaryDirectory() as temp:
        temp = Path(temp)

        baseline_tests, baseline_failures = (
            collect_individual_coverage(
                baseline, "baseline", cli_jar, temp
            )
        )

        pr_tests, pr_failures = (
            collect_individual_coverage(
                pr, "pr", cli_jar, temp
            )
        )

        comment = compare_coverage(
            baseline_xml,
            pr_xml,
            baseline_tests,
            pr_tests,
            pr
        )

        if baseline_failures or pr_failures:
            comment += "\n\n### ⚠️ Isolated test execution warnings\n"

            for test in baseline_failures:
                comment += f"\n- Baseline: `{test}`"

            for test in pr_failures:
                comment += f"\n- PR: `{test}`"

        Path(args.output).write_text(
            comment,
            encoding="utf-8"
        )

        print("Coverage comparison generated.")


if __name__ == "__main__":
    main()
