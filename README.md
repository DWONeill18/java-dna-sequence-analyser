# Java DNA Analyzer

Java DNA Analyzer is a Maven-based console application for running basic DNA analysis workflows. It reads DNA sequences from files, validates and splits them into codons, performs operations such as codon counting, DNA replication, DNA-to-mRNA transcription, random DNA generation, and random DNA mutation, then writes results to output files.

The project is written in Java and is structured around separate classes for the command-line menu, file input/output, validation helpers, and individual DNA analysis operations. It is supported by automated tests for unit-level logic, menu flows, file handling, validation, and error scenarios.

## QA Engineering Focus

This project demonstrates practical QA engineering skills as well as Java development fundamentals. The test suite covers expected workflows and failure paths, including invalid menu input, empty files, malformed DNA sequences, invalid mutation sizes, and file read/write behavior.

From a QA perspective, the project shows:

- JUnit 5 unit tests for DNA helper and analysis logic
- DNA helper tests for codon validation, including null input and reprompt behavior
- Integration-style tests for the interactive console menu
- File input/output testing with sample resources
- Validation and negative-path test coverage
- Maven-based test execution
- GitHub Actions workflow configuration
- JaCoCo code coverage reporting and pull request coverage comparison

## Features

- Interactive command-line menu
- DNA sequence reading from text files
- DNA codon splitting and validation
- Codon occurrence counting
- DNA replication into complementary bases
- DNA transcription into mRNA complements
- Random DNA sequence generation using A, C, G, and T
- Random DNA mutation by inserting generated bases into the codon list
- File output for generated results
- Information menu describing available workflows

Menu options 4 (DNA translation) and 7 (reverse transcription) are placeholders and do not perform those operations yet.

## Tech Stack

- Java 21
- Maven
- JUnit 5
- JaCoCo
- GitHub Actions

## Project Structure

```text
Java-DNA-Analyzer
|-- pom.xml
|-- scripts
|   `-- compare_jacoco.py
|-- src
|   |-- main
|   |   `-- java
|   |       |-- Main.java
|   |       |-- Menu.java
|   |       |-- DNAHelpers.java
|   |       |-- DNAMatch.java
|   |       |-- DNAMutation.java
|   |       |-- ReadDNA.java
|   |       |-- WriteDNA.java
|   |       `-- DNAAnalysis
|   |           |-- DNAReplication.java
|   |           `-- DNATranscription.java
|   `-- test
|       |-- java
|       |   |-- DNAHelpersTest.java
|       |   |-- DNAMutationTest.java
|       |   |-- DNAReplicationTest.java
|       |   |-- DNATranscriptionTest.java
|       |   |-- MainIntegrationTest.java
|       |   |-- MenuIntegrationTest.java
|       |   |-- ReadDNATest.java
|       |   `-- WriteDNATest.java
|       `-- resources
|           |-- empty-dna.txt
|           `-- sample-dna.txt
`-- .github
    `-- workflows
        `-- maven.yml
```

## Object-Oriented Design

The application uses Java classes to separate responsibilities. `Menu` controls the user workflow, `ReadDNA` and `WriteDNA` handle file operations, `DNAHelpers` handles shared validation and codon conversion, and classes such as `DNAReplication`, `DNATranscription`, and `DNAMutation` contain focused analysis logic.

The current design is mainly service-based: classes perform operations on DNA strings and codon lists rather than modelling richer domain objects such as `DNASequence`, `Codon`, or `Mutation`. This keeps the project straightforward and testable while leaving room for a fuller domain model.

## Running the Application

Requirements: Java 21 and Maven. From the `Java-DNA-Analyzer` project directory, build the project with Maven:

```bash
mvn clean package
```

Run the application from the project directory using the packaged JAR:

```bash
java -cp target/DNA-Lab-1.0-SNAPSHOT.jar Main
```

The application opens an interactive menu and waits for user input. File paths are interpreted relative to the working directory from which the command is run. Output files replace existing file contents.

## Running Tests

Run the automated test suite:

```bash
mvn test
```

Generate a test coverage report:

```bash
mvn verify
```

After running `mvn verify`, JaCoCo coverage output is generated under:

```text
target/site/jacoco
```

The Maven GitHub Actions workflow runs `mvn clean verify` with Java 21. For pull requests, it also compares JaCoCo coverage against the base branch and uploads the report as a workflow artifact.

## Menu Options

### 1. DNA Match

Reads a DNA file, splits the sequence into codons, prompts for a codon, and reports how many times it appears. It then asks for an output path and writes the original DNA content to that file.

```text
Enter option (1-9): 1
DNA Match
Enter file path: src/test/resources/sample-dna.txt
Enter codon: ACG
Codon ACG appears 1 times.
Enter file path to output file: out/dna-match.txt
```

### 2. DNA Replication

Reads a DNA file and produces its complementary DNA bases (A↔T and C↔G).

```text
Enter option (1-9): 2
DNA Replication
Enter file path: src/test/resources/sample-dna.txt
Enter file path to output file: out/dna-replication.txt
```

### 3. DNA Transcription

Reads a DNA file and transcribes it into its mRNA complement (A→U, T→A, C→G, G→C).

```text
Enter option (1-9): 3
DNA Transcription
Enter file path: src/test/resources/sample-dna.txt
Enter file path to output file: out/rna-transcription.txt
```

### 4. DNA Translation

Placeholder option for future DNA translation functionality; selecting it currently prints the option name only.

### 5. Random DNA Generator

Generates a random DNA sequence and writes it to an output file. Enter a positive number of bases divisible by three; generated sequences use A, C, G, and T.

```text
Enter option (1-9): 5
Random DNA Generator
Enter number of random bases: 9
Enter file path to output file: out/random-dna.txt
```

### 6. Random DNA Mutation

Reads a DNA file, inserts a randomly generated sequence into the codon list, and writes the resulting codons to a file. The number of inserted bases must be greater than zero and divisible by three.

```text
Enter option (1-9): 6
Mutation
Enter file path: src/test/resources/sample-dna.txt
Enter number of bases you want to randomly insert: 6
Enter file path to output file: out/dna-mutation.txt
```

### 7. Reverse Transcription

Placeholder option for future RNA-to-DNA reverse transcription functionality; selecting it currently prints the option name only.

### 8. Information

Prints a short description of each menu option.

### 9. Exit

Closes the application.

```text
Enter option (1-9): 9
Closing down the lab
```

DNA input is trimmed and converted to uppercase before codon splitting. The sequence length must be divisible by three. Analysis operations require the bases A, C, G, and T. Empty or missing input files are reported and the selected workflow is skipped.


## GitHub Actions and JaCoCo Comparison

The `Java CI with Maven` workflow in `.github/workflows/maven.yml` runs on pushes and pull requests targeting `main`. It uses Ubuntu and Java 21 (Temurin), with Maven dependency caching enabled.

For each run, the workflow executes `mvn clean verify`, which runs the tests and creates the JaCoCo report. It uploads `target/site/jacoco/` as the `jacoco-report` artifact. On pushes to `main`, it also submits the Maven dependency graph to GitHub.

For pull requests, the workflow checks out the target branch revision into `baseline`, builds and verifies that revision, and compares its coverage with the pull request. The script `scripts/compare_jacoco.py`:

- Runs each `*Test.java` test class separately against the baseline and pull request code, collecting per-test JaCoCo execution data.
- Compares overall instruction, line, and branch coverage percentages from the JaCoCo XML reports.
- Identifies production source lines that were covered on the baseline but are uncovered in the pull request.
- Lists baseline test classes that covered affected lines and indicates whether those classes still cover them in the pull request.
- Writes a Markdown report to `coverage-comment.md`, which the workflow posts as an updating pull request comment.

The test associations are evidence about which isolated test classes exercised affected lines; they do not identify which test caused a regression. Isolated test failures or missing coverage data are reported as warnings in the comparison. The script reports coverage changes but does not enforce a minimum coverage threshold.
## Future Improvements

- Add full DNA translation functionality
- Add reverse transcription
- Introduce domain classes such as `DNASequence` and `Codon`
- Improve output formatting for mutation results
- Expand CI checks to include coverage thresholds and static analysis

