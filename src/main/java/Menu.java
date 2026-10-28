import DNAAnalysis.DNAReplication;
import DNAAnalysis.DNATranscription;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Presents the DNA Lab console menu and runs the workflow selected by the user.
 * <p>
 * The menu receives its input and output streams through the constructor, which
 * allows the workflows to be used with the console or alternate streams. Options
 * include DNA matching, replication, transcription, random sequence generation,
 * and random mutation; selecting option 9 ends the menu loop.
 */
public class Menu {

    //Scanner scanner = new Scanner(System.in);  // Create a Scanner object
    private final Scanner scanner;
    private final PrintStream out;

    /**
     * Creates a menu that reads commands from the supplied scanner and writes
     * prompts and results to the supplied stream.
     *
     * @param scanner scanner used for user input
     * @param out     output stream for menu prompts and messages
     */
    public Menu(Scanner scanner, PrintStream out) {
        this.scanner = scanner;
        this.out = out;
    }


    /**
     * Prints the welcome banner.
     */
    public void welcomeMenu() {
        out.println("---------------------------------");
        out.println("---- Welcome to the DNA Lab! ----");
        out.println("---------------------------------");
    }

    /**
     * Prints the numbered menu options.
     */
    private void printMenu() {
        out.println("\n1. DNA Match");
        out.println("2. DNA Replication");
        out.println("3. DNA Transcription");
        out.println("4. DNA Translation");
        out.println("5. Random DNA Generator");
        out.println("6. Random DNA Mutation");
        out.println("7. Reverse Transcription");
        out.println("8. Information");
        out.println("9. Exit\n");
    }

    /**
     * Prompts until the user enters a valid option between 1 and 9.
     *
     * @return the validated menu option
     */
    private int readOption() {
        while (true) {

            printMenu();
            out.print("Enter option (1-9): ");

            if (!scanner.hasNextInt()) {
                out.println("Invalid input, please enter a number from 1-9");
                scanner.nextLine(); // clear bad input
                continue;
            }

            int option = scanner.nextInt();

            if (option < 1 || option > 9) {
                out.println("Please choose a valid option (1-9)");
                continue;
            }

            return option;  // valid input exits loop
        }
    }

    /**
     * Displays menu options and runs the selected workflow repeatedly until the
     * user chooses option 9.
     * <p>
     * Invalid menu selections are rejected and the menu is shown again. Workflow
     * input and file I/O errors are reported to the configured output stream.
     */
    public void analysisMenu() {
        boolean running = true;
        while (running) {

            int option = readOption();

            switch (option) {
                case 1 -> {
                    out.println("DNA Match");
                    out.print("Enter file path: ");
                    scanner.nextLine();
                    String fileName = scanner.nextLine();

                    ReadDNA readDNA = new ReadDNA(out);
                    Optional<String> content = readDNA.readFile(fileName);
                    if (content.isEmpty()) {
                        out.println("No data to write; aborting.");
                        break;
                    }

                    DNAHelpers helpers = new DNAHelpers();
                    List<String> codons;
                    try {
                        codons = helpers.dnaToCodons(content.get());
                    } catch (IllegalArgumentException ex) {
                        out.println(ex.getMessage());
                        break;
                    }

                    helpers.countCodonOccurrences(codons, scanner, out);

                    out.print("Enter file path to output file: ");
                    String outputFileName = scanner.nextLine();

                    WriteDNA writeDNA = new WriteDNA();
                    try {
                        writeDNA.writeFile(outputFileName, content.get());
                    } catch (IOException e) {
                        out.println("Error while writing file");
                    }
                }

                case 2 -> {
                    out.println("DNA Replication");

                    out.print("Enter file path: ");
                    scanner.nextLine();
                    String fileName = scanner.nextLine();

                    ReadDNA readDNA = new ReadDNA(out);
                    Optional<String> content = readDNA.readFile(fileName);
                    if (content.isEmpty()) {
                        out.println("No data to write; aborting.");
                        break;
                    }

                    DNAHelpers helpers = new DNAHelpers();
                    List<String> codons;
                    try {
                        codons = helpers.dnaToCodons(content.get());
                    } catch (IllegalArgumentException ex) {
                        out.println(ex.getMessage());
                        break;
                    }
                    if (codons.isEmpty()) {
                        out.println("No codons available.");
                        break;
                    }

                    DNAReplication dnaReplication = new DNAReplication();
                    String result;
                    try {
                        result = dnaReplication.replication(codons);
                    } catch (IllegalArgumentException ex) {
                        out.println(ex.getMessage());
                        break;
                    }

                    out.print("Enter file path to output file: ");
                    String outputFileName = scanner.nextLine();

                    WriteDNA writeDNA = new WriteDNA();
                    try {
                        writeDNA.writeFile(outputFileName, result);
                    } catch (IOException e) {
                        out.println("Error while writing file");
                    }

                }
                case 3 -> {

                    out.println("DNA Transcription");

                    out.print("Enter file path: ");
                    scanner.nextLine();
                    String fileName = scanner.nextLine();

                    ReadDNA readDNA = new ReadDNA(out);
                    Optional<String> content = readDNA.readFile(fileName);
                    if (content.isEmpty()) {
                        out.println("No data to write; aborting.");
                        break;
                    }

                    DNAHelpers helpers = new DNAHelpers();
                    List<String> codons;
                    try {
                        codons = helpers.dnaToCodons(content.get());
                    } catch (IllegalArgumentException ex) {
                        out.println(ex.getMessage());
                        break;
                    }
                    if (codons.isEmpty()) {
                        out.println("No codons available.");
                        break;
                    }

                    DNATranscription dnaTranscription = new DNATranscription();
                    String result;
                    try {
                        result = dnaTranscription.transcription(codons);
                    } catch (IllegalArgumentException ex) {
                        out.println(ex.getMessage());
                        break;
                    }

                    out.print("Enter file path to output file: ");
                    String outputFileName = scanner.nextLine();

                    WriteDNA writeDNA = new WriteDNA();
                    try {
                        writeDNA.writeFile(outputFileName, result);
                    } catch (IOException e) {
                        out.println("Error while writing file");
                    }

                }
                case 4 -> out.println("DNA Translation");
                case 5 -> {

                    out.println("Random DNA Generator");

                    out.println("Enter number of random bases: ");
                    scanner.nextLine();

                    int baseCount;
                    try {
                        baseCount = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException ex) {
                        out.println("Invalid input, please enter a number.");
                        break;
                    }

                    DNAMutation dnaMutation = new DNAMutation();
                    String result;
                    try {
                        result = String.valueOf(dnaMutation.createRandomDNA(baseCount));
                    } catch (IllegalArgumentException ex) {
                        out.println(ex.getMessage());
                        break;
                    }

                    out.print("Enter file path to output file: ");
                    String outputFileName = scanner.nextLine();

                    WriteDNA writeDNA = new WriteDNA();
                    try {
                        writeDNA.writeFile(outputFileName, result);
                    } catch (IOException e) {
                        out.println("Error while writing file");
                    }

                }
                case 6 -> {

                    out.println("Mutation");

                    out.print("Enter file path: ");
                    scanner.nextLine();
                    String fileName = scanner.nextLine();

                    ReadDNA readDNA = new ReadDNA(out);
                    Optional<String> content = readDNA.readFile(fileName);
                    if (content.isEmpty()) {
                        out.println("No data to write; aborting.");
                        break;
                    }

                    DNAHelpers helpers = new DNAHelpers();
                    List<String> codons;
                    try {
                        codons = helpers.dnaToCodons(content.get());
                    } catch (IllegalArgumentException ex) {
                        out.println(ex.getMessage());
                        break;
                    }
                    if (codons.isEmpty()) {
                        out.println("No codons available.");
                        break;
                    }

                    out.print("Enter number of bases you want to randomly insert: ");
                    int baseCount;
                    try {
                        baseCount = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException ex) {
                        out.println("Invalid input, please enter a number.");
                        break;
                    }

                    DNAMutation dnaMutation = new DNAMutation();
                    String result;
                    try {
                        result = String.valueOf(dnaMutation.mutate(codons, baseCount));
                    } catch (IllegalArgumentException ex) {
                        out.println(ex.getMessage());
                        break;
                    }

                    out.print("Enter file path to output file: ");
                    String outputFileName = scanner.nextLine();

                    WriteDNA writeDNA = new WriteDNA();
                    try {
                        writeDNA.writeFile(outputFileName, result);
                    } catch (IOException e) {
                        out.println("Error while writing file");
                    }


                }
                case 7 -> out.println("Reverse Transcription");
                case 8 -> out.println(
                        "Option Information" + System.lineSeparator() +
                                "1. DNA Match - Check whether suspect matches sample DNA" + System.lineSeparator() +
                                "2. DNA Replication - Replicate a DNA strand to give both it's original and complemetary strands" + System.lineSeparator() +
                                "3. DNA Transcription - Transcribe a DNA strand to its mRNA counterpart" + System.lineSeparator() +
                                "4. mRNA Translation - Translate an mRNA strand to amino acids in a polypeptide chain" + System.lineSeparator() +
                                "5. Random DNA Generator - Create a random DNA strand given a number of DNA bases" + System.lineSeparator() +
                                "6. Random DNA Mutation - Insert a random DNA base into a DNA strand to cause a mutation" + System.lineSeparator() +
                                "7. Reverse Transcription - Transcribe an RNA strand back to its DNA counterpart" + System.lineSeparator());
                case 9 -> {
                    out.println("Closing down the lab" + System.lineSeparator());
                    running = false;
                }
            }
        }
    }
}
