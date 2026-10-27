import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link DNAMutation}.
 */
@Tag("unit")
class DNAMutationTest {

    /**
     * Verifies random DNA generation returns the requested number of valid bases.
     */
    @Test
    void createRandomDNA_returnsRequestedLengthUsingDnaBases() {
        DNAMutation mutation = new DNAMutation();

        String result = mutation.createRandomDNA(6);

        assertEquals(6, result.length());
        assertTrue(result.matches("[ACGT]+"));
    }

    /**
     * Verifies zero and negative generated lengths are rejected.
     */
    @Test
    void createRandomDNA_nonPositiveLength_throwsException() {
        DNAMutation mutation = new DNAMutation();

        IllegalArgumentException zeroException = assertThrows(
                IllegalArgumentException.class,
                () -> mutation.createRandomDNA(0)
        );
        IllegalArgumentException negativeException = assertThrows(
                IllegalArgumentException.class,
                () -> mutation.createRandomDNA(-3)
        );

        assertEquals("Number of bases generated must be greater than 0", zeroException.getMessage());
        assertEquals("Number of bases generated must be greater than 0", negativeException.getMessage());
    }

    /**
     * Verifies generated lengths that are not divisible by three are rejected.
     */
    @Test
    void createRandomDNA_lengthNotDivisibleByThree_throwsException() {
        DNAMutation mutation = new DNAMutation();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> mutation.createRandomDNA(4)
        );

        assertEquals("Number of bases must be divisible by 3", exception.getMessage());
    }

    /**
     * Verifies mutation inserts a randomly generated base string into the codon list.
     */
    @Test
    void mutate_insertsRandomBasesIntoCodonList() {
        List<String> codons = List.of("ACG", "TGA", "CCT");
        int baseCount = 6;

        DNAMutation mutation = new DNAMutation();
        List<String> result = mutation.mutate(codons, baseCount);

        assertEquals(codons.size() + 1, result.size());
        assertTrue(containsElementOfLengthWithBases(result, baseCount));
        assertTrue(isSubsequence(codons, result));
    }

    /**
     * Verifies invalid mutation sizes (not multiples of 3) throw a helpful exception.
     */
    @Test
    void mutate_invalidBaseCount_throwsExceptionWithMessage() {
        List<String> codons = List.of("ACG", "TGA", "CCT");
        int baseCount = 4;

        DNAMutation mutation = new DNAMutation();
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> mutation.mutate(codons, baseCount)
        );

        assertEquals("Mutation size must be divisible by 3", ex.getMessage());
    }

    /**
     * Verifies zero mutation sizes are rejected.
     */
    @Test
    void mutate_zeroBaseCount_throwsExceptionWithMessage() {
        List<String> codons = List.of("ACG", "TGA", "CCT");

        DNAMutation mutation = new DNAMutation();
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> mutation.mutate(codons, 0)
        );

        assertEquals("Mutation size must be greater than 0", ex.getMessage());
    }

    /**
     * Verifies negative mutation sizes are rejected.
     */
    @Test
    void mutate_negativeBaseCount_throwsExceptionWithMessage() {
        List<String> codons = List.of("ACG", "TGA", "CCT");

        DNAMutation mutation = new DNAMutation();
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> mutation.mutate(codons, -3)
        );

        assertEquals("Mutation size must be greater than 0", ex.getMessage());
    }

    /**
     * Verifies null input produces a list containing only the mutation.
     */
    @Test
    void mutate_nullCodons_returnsSingleMutation() {
        DNAMutation mutation = new DNAMutation();
        List<String> result = mutation.mutate(null, 3);

        assertEquals(1, result.size());
        assertTrue(containsElementOfLengthWithBases(result, 3));
    }

    /**
     * Checks whether a mutation string of the requested length exists in the list.
     *
     * @param result mutated codon list
     * @param length expected mutation length
     * @return {@code true} when a matching base-only element is found
     */
    private boolean containsElementOfLengthWithBases(List<String> result, int length) {
        // Detects whether the mutation string exists without relying on its random value.
        for (String item : result) {
            if (item.length() == length && item.matches("[ACGT]+")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifies the original codons appear in the mutated list in order.
     *
     * @param original original codon list
     * @param mutated mutated codon list
     * @return {@code true} when the original order is preserved
     */
    private boolean isSubsequence(List<String> original, List<String> mutated) {
        // Ensures the original codons remain in order after insertion.
        int i = 0;
        for (String item : mutated) {
            if (i < original.size() && original.get(i).equals(item)) {
                i++;
            }
        }
        return i == original.size();
    }
}
