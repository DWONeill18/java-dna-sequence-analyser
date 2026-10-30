import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link DNAHelpers}.
 */
@Tag("unit")
class DNAHelpersTest {

    /**
     * Verifies DNA strings are split into 3-base codons in order.
     */
    @Test
    void dnaToCodons_splitsIntoGroupsOfThree() {
        DNAHelpers helpers = new DNAHelpers();

        List<String> codons = helpers.dnaToCodons("ACGTGACCT");

        assertEquals(List.of("ACG", "TGA", "CCT"), codons);
    }

    /**
     * Ensures non-divisible-by-three DNA lengths throw with the expected message.
     */
    @Test
    void dnaToCodons_nonDivisibleByThree_throwsErrorMessage() {
        DNAHelpers helpers = new DNAHelpers();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> helpers.dnaToCodons("ACGTG")
        );

        assertEquals("DNA length must be divisible by 3", ex.getMessage());
    }

    /**
     * Ensures null input yields an empty codon list.
     */
    @Test
    void dnaToCodons_nullInput_returnsEmptyList() {
        DNAHelpers helpers = new DNAHelpers();

        List<String> codons = helpers.dnaToCodons(null);

        assertEquals(List.of(), codons);
    }

    /**
     * Ensures blank input yields an empty codon list.
     */
    @Test
    void dnaToCodons_blankInput_returnsEmptyList() {
        DNAHelpers helpers = new DNAHelpers();

        List<String> codons = helpers.dnaToCodons("   ");

        assertEquals(List.of(), codons);
    }

    /**
     * Ensures invalid characters are preserved when splitting into codons.
     */
    @Test
    void dnaToCodons_invalidCharacters_returnsCodons() {
        DNAHelpers helpers = new DNAHelpers();

        List<String> codons = helpers.dnaToCodons("ACGTXG");

        assertEquals(List.of("ACG", "TXG"), codons);
    }

    /**
     * Verifies the minimal valid DNA length returns a single codon.
     */
    @Test
    void dnaToCodons_minimalLength_returnsSingleCodon() {
        DNAHelpers helpers = new DNAHelpers();

        List<String> codons = helpers.dnaToCodons("ACG");

        assertEquals(List.of("ACG"), codons);
    }

    /**
     * Confirms large inputs are split into the expected count of codons.
     */
    @Test
    void dnaToCodons_largeInput_returnsExpectedCount() {
        DNAHelpers helpers = new DNAHelpers();
        String dna = "ACG".repeat(1000);

        List<String> codons = helpers.dnaToCodons(dna);

        assertEquals(1000, codons.size());
        assertTrue(codons.stream().allMatch("ACG"::equals));
    }

    /**
     * Ensures lowercase DNA is normalized to uppercase codons.
     */
    @Test
    void dnaToCodons_lowercaseInput_isNormalizedToUppercase() {
        DNAHelpers helpers = new DNAHelpers();

        List<String> codons = helpers.dnaToCodons("acgtga");

        assertEquals(List.of("ACG", "TGA"), codons);
    }

    /**
     * Ensures leading and trailing whitespace is trimmed before validation.
     */
    @Test
    void dnaToCodons_whitespaceIsTrimmed() {
        DNAHelpers helpers = new DNAHelpers();

        List<String> codons = helpers.dnaToCodons("  ACGTGA  ");

        assertEquals(List.of("ACG", "TGA"), codons);
    }

    /**
     * Ensures digits are preserved when splitting into codons.
     */
    @Test
    void dnaToCodons_invalidDigit_returnsCodons() {
        DNAHelpers helpers = new DNAHelpers();

        List<String> codons = helpers.dnaToCodons("ACG1TG");

        assertEquals(List.of("ACG", "1TG"), codons);
    }

    /**
     * Verifies that exact codon matches are counted correctly.
     */
    @Test
    void countCodonOccurrences_countsExactMatches() {
        DNAHelpers helpers = new DNAHelpers();

        List<String> codons = List.of("ACG", "TGA", "CCT", "ACG");
        ByteArrayInputStream in = new ByteArrayInputStream("ACG\n".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        helpers.countCodonOccurrences(codons, new Scanner(in), new PrintStream(out));
        String output = out.toString();

        assertTrue(output.contains("Codon ACG appears 2 times."));
    }

    /**
     * Verifies that zero occurrences are reported when the codon is absent.
     */
    @Test
    void countCodonOccurrences_returnsZeroWhenNotFound() {
        DNAHelpers helpers = new DNAHelpers();

        List<String> codons = List.of("ACG", "TGA", "CCT");
        ByteArrayInputStream in = new ByteArrayInputStream("AAA\n".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        helpers.countCodonOccurrences(codons, new Scanner(in), new PrintStream(out));
        String output = out.toString();

        assertTrue(output.contains("Codon AAA appears 0 times."));
    }

    /**
     * Ensures a helpful message is printed when no codons are provided.
     */
    @Test
    void countCodonOccurrences_noCodonsAvailable_printsMessage() {
        DNAHelpers helpers = new DNAHelpers();

        List<String> codons = List.of();
        ByteArrayInputStream in = new ByteArrayInputStream("ACG\n".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        helpers.countCodonOccurrences(codons, new Scanner(in), new PrintStream(out));
        String output = out.toString();

        assertTrue(output.contains("No codons available."));
    }

    /**
     * Ensures a helpful message is printed when the codon list is null.
     */
    @Test
    void countCodonOccurrences_nullCodons_printsMessage() {
        DNAHelpers helpers = new DNAHelpers();

        ByteArrayInputStream in = new ByteArrayInputStream("ACG\n".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        helpers.countCodonOccurrences(null, new Scanner(in), new PrintStream(out));
        String output = out.toString();

        assertTrue(output.contains("No codons available."));
    }

    /**
     * Ensures invalid codon input triggers a reprompt before counting.
     */
    @Test
    void countCodonOccurrences_repromptsOnInvalidInput() {
        DNAHelpers helpers = new DNAHelpers();

        List<String> codons = List.of("ACG", "TGA", "CCT");
        ByteArrayInputStream in = new ByteArrayInputStream("AX1\nACG\n".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        helpers.countCodonOccurrences(codons, new Scanner(in), new PrintStream(out));
        String output = out.toString();

        assertTrue(output.contains("Invalid codon. Please enter a 3-letter codon using A, C, G, T."));
        assertTrue(output.contains("Codon ACG appears 1 times."));
    }

    /**
     * Ensures incorrect codon length triggers a reprompt before counting.
     */
    @Test
    void countCodonOccurrences_repromptsOnWrongLength() {
        DNAHelpers helpers = new DNAHelpers();

        List<String> codons = List.of("ACG", "TGA", "CCT");
        ByteArrayInputStream in = new ByteArrayInputStream("AA\nACGT\nACG\n".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        helpers.countCodonOccurrences(codons, new Scanner(in), new PrintStream(out));
        String output = out.toString();

        assertTrue(output.contains("Invalid codon. Please enter a 3-letter codon using A, C, G, T."));
        assertTrue(output.contains("Codon ACG appears 1 times."));
    }

    /**
     * Ensures blank codon input triggers a reprompt before counting.
     */
    @Test
    void countCodonOccurrences_repromptsOnBlankInput() {
        DNAHelpers helpers = new DNAHelpers();

        List<String> codons = List.of("ACG", "TGA", "CCT");
        ByteArrayInputStream in = new ByteArrayInputStream("\nACG\n".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        helpers.countCodonOccurrences(codons, new Scanner(in), new PrintStream(out));
        String output = out.toString();

        assertTrue(output.contains("Invalid codon. Please enter a 3-letter codon using A, C, G, T."));
        assertTrue(output.contains("Codon ACG appears 1 times."));
    }

    /**
     * Ensures a null line from the input supplier triggers a reprompt.
     * The supplier makes the defensive null-input branch directly testable;
     * a standard Scanner throws at end of input instead of returning null.
     */
    @Test
    void countCodonOccurrences_repromptsOnNullInput() {
        DNAHelpers helpers = new DNAHelpers();
        int[] calls = {0};
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        helpers.countCodonOccurrences(
                List.of("ACG", "TGA"),
                () -> calls[0]++ == 0 ? null : "ACG",
                new PrintStream(out)
        );

        String output = out.toString();
        assertTrue(output.contains("Invalid codon. Please enter a 3-letter codon using A, C, G, T."));
        assertTrue(output.contains("Codon ACG appears 1 times."));
    }

    /**
     * Ensures internal whitespace still fails length validation.
     */
    @Test
    void dnaToCodons_internalWhitespace_throwsLengthErrorMessage() {
        DNAHelpers helpers = new DNAHelpers();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> helpers.dnaToCodons("ACG TGA")
        );

        assertEquals("DNA length must be divisible by 3", ex.getMessage());
    }

    /**
     * Ensures newline characters still fail length validation.
     */
    @Test
    void dnaToCodons_newline_throwsLengthErrorMessage() {
        DNAHelpers helpers = new DNAHelpers();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> helpers.dnaToCodons("ACG\nTGA")
        );

        assertEquals("DNA length must be divisible by 3", ex.getMessage());
    }
}
