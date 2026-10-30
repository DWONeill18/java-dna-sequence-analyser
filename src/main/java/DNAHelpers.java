import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Utility helpers for validating and manipulating DNA strings and codon lists.
 */
public class DNAHelpers {
    private static final Pattern DNA_PATTERN = Pattern.compile("[ACGT]+");

    /**
     * Splits a DNA string into codons (3-base chunks) after validation.
     *
     * @param dna the raw DNA string to split; may be {@code null} or blank
     * @return an immutable list of codons, or an empty list when input is {@code null} or blank
     * @throws IllegalArgumentException when the DNA length is not divisible by 3
     */
    public List<String> dnaToCodons(String dna) {
        if (dna == null || dna.isBlank()) {
            return List.of();
        }
        String normalized = dna.trim().toUpperCase();
        if (normalized.length() % 3 != 0) {
            throw new IllegalArgumentException("DNA length must be divisible by 3");
        }

        List<String> codons = new ArrayList<>();
        for (int i = 0; i < normalized.length(); i += 3) {
            codons.add(normalized.substring(i, i + 3));
        }
        return List.copyOf(codons);
    }

    /**
     * Prompts the user for a codon and prints how many times it appears in the list.
     *
     * @param codons  list of codons to search; may be {@code null} or empty
     * @param scanner input source for user prompts
     * @param out     output stream for prompts and messages
     */
    public void countCodonOccurrences(List<String> codons, Scanner scanner, PrintStream out) {
        countCodonOccurrences(codons, scanner::nextLine, out);
    }

    void countCodonOccurrences(List<String> codons, Supplier<String> readLine, PrintStream out) {
        if (codons == null || codons.isEmpty()) {
            out.println("No codons available.");
            return;
        }

        String normalizedCodon;
        while (true) {
            out.print("Enter codon: ");
            String codon = readLine.get();
            if (codon == null || codon.isBlank()) {
                out.println("Invalid codon. Please enter a 3-letter codon using A, C, G, T.");
                continue;
            }
            normalizedCodon = codon.trim().toUpperCase();
            if (normalizedCodon.length() != 3 || !DNA_PATTERN.matcher(normalizedCodon).matches()) {
                out.println("Invalid codon. Please enter a 3-letter codon using A, C, G, T.");
                continue;
            }
            break;
        }

        int count = 0;
        for (String item : codons) {
            if (normalizedCodon.equals(item)) {
                count++;
            }
        }

        out.println("Codon " + normalizedCodon + " appears " + count + " times.");
    }
}
