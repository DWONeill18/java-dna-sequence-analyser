# Java DNA Analyzer

Java DNA Analyzer is a Maven-based console application for running basic DNA analysis workflows. It reads DNA sequences from files, validates and splits sequences into codons, performs operations such as codon counting, DNA replication, DNA-to-mRNA transcription, and random DNA mutation, then writes results back to output files.

The project is written in Java and is structured around separate classes for the command-line menu, file input/output, validation helpers, and individual DNA analysis operations. It is also supported by automated tests that cover unit-level logic, integration-style menu flows, file handling, validation, and error scenarios.

## QA Engineering Focus

This project demonstrates practical QA engineering skills as well as Java development fundamentals. It includes automated test coverage for expected workflows and failure paths, including invalid menu input, empty files, malformed DNA sequences, invalid mutation sizes, and file read/write behavior.

From a QA perspective, the project shows:

- JUnit 5 unit tests for DNA helper and analysis logic
- Integration-style tests for the interactive console menu
- File input/output testing with sample resources
- Validation and negative-path test coverage
- Maven-based test execution
- GitHub Actions workflow configuration
- JaCoCo code coverage reporting

## Features

- Interactive command-line menu
- DNA sequence reading from text files
- DNA codon splitting and validation
- Codon occurrence counting
- DNA replication into complementary bases
- DNA transcription into mRNA complements
- Random DNA mutation by inserting generated bases
- File output for generated results
- Information menu describing available workflows

Some menu options are currently placeholders for future development, including DNA translation, random DNA generation, and reverse transcription.

## Tech Stack

- Java 21
- Maven
- JUnit 5
- JaCoCo
- GitHub Actions

## Project Structure

```text
src
|-- main
|   `-- java
|       |-- Main.java
|       |-- Menu.java
|       |-- DNAHelpers.java
|       |-- DNAMutation.java
|       |-- ReadDNA.java
|       |-- WriteDNA.java
|       `-- DNAAnalysis
|           |-- DNAReplication.java
|           `-- DNATranscription.java
`-- test
    |-- java
    |   |-- DNAHelpersTest.java
    |   |-- DNAMutationTest.java
    |   |-- DNAReplicationTest.java
    |   |-- DNATranscriptionTest.java
    |   |-- MainIntegrationTest.java
    |   |-- MenuIntegrationTest.java
    |   |-- ReadDNATest.java
    |   `-- WriteDNATest.java
    `-- resources
        |-- empty-dna.txt
        `-- sample-dna.txt
```

## Object-Oriented Design

The application uses object-oriented Java classes to separate responsibilities. For example, `Menu` controls the user workflow, `ReadDNA` and `WriteDNA` handle file operations, `DNAHelpers` handles shared validation and codon conversion, and classes such as `DNAReplication`, `DNATranscription`, and `DNAMutation` contain focused analysis logic.

The current design is mainly service-based: classes perform operations on DNA strings and codon lists rather than modelling richer domain objects such as `DNASequence`, `Codon`, or `Mutation`. This makes the project straightforward and testable, while leaving room for future improvement toward a fuller object-oriented domain model.

## Running the Application

Build the project with Maven:

```bash
mvn clean package
```

Run the application from the `Main` class in an IDE, or run the packaged JAR:

```bash
java -cp target/DNA-Lab-1.0-SNAPSHOT.jar Main
```

The application opens an interactive menu and waits for user input.

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

## Menu Options

### 1. DNA Match

Reads a DNA file, splits the sequence into codons, prompts for a codon, and reports how many times that codon appears.

```text
Enter option (1-9): 1
DNA Match
Enter file path: src/test/resources/sample-dna.txt
Enter codon: ACG
Codon ACG appears 1 times.
Enter file path to output file: out/dna-match.txt
```

### 2. DNA Replication

Reads a DNA file and produces the complementary DNA sequence.

```text
Enter option (1-9): 2
DNA Replication
Enter file path: src/test/resources/sample-dna.txt
Enter file path to output file: out/dna-replication.txt
```

### 3. DNA Transcription

Reads a DNA file and transcribes the DNA sequence into its mRNA complement.

```text
Enter option (1-9): 3
DNA Transcription
Enter file path: src/test/resources/sample-dna.txt
Enter file path to output file: out/rna-transcription.txt
```

### 4. DNA Translation

Placeholder option for future DNA translation functionality.

### 5. Random DNA Generator

Placeholder option for future random DNA sequence generation.

### 6. Random DNA Mutation

Reads a DNA file, inserts a randomly generated base sequence into the codon list, and writes the mutated result to a file. The number of inserted bases must be greater than zero and divisible by three.

```text
Enter option (1-9): 6
Mutation
Enter file path: src/test/resources/sample-dna.txt
Enter number of bases you want to randomly insert: 6
Enter file path to output file: out/dna-mutation.txt
```

### 7. Reverse Transcription

Placeholder option for future RNA-to-DNA reverse transcription functionality.

### 8. Information

Prints a short description of each menu option.

### 9. Exit

Closes the application.

```text
Enter option (1-9): 9
Closing down the lab
```

## Future Improvements

- Add full DNA translation functionality
- Add random DNA sequence generation
- Add reverse transcription
- Introduce domain classes such as `DNASequence` and `Codon`
- Improve output formatting for mutation results
- Expand CI checks to include coverage thresholds and static analysis
