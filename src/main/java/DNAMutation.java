import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates random DNA sequences and inserts random sequences into codon lists.
 * <p>
 * Generated sequences use the DNA bases {@code A}, {@code C}, {@code G}, and
 * {@code T}. Mutation insertions are required to contain a positive number of
 * bases divisible by three so they contain complete codons.
 */
public class DNAMutation {

    private static final char[] BASES = {'A', 'C', 'G', 'T'};
    private final SecureRandom random = new SecureRandom();

    /**
     * Generates a random base sequence of the requested length and inserts it into the codon list.
     *
     * @param codons    list of codons to mutate
     * @param baseCount number of random bases to insert; must be greater than 0 and a multiple of 3
     * @return a new list containing the mutation
     * @throws IllegalArgumentException when {@code baseCount} is not greater than 0 or not a multiple of 3
     */
    public List<String> mutate(List<String> codons, int baseCount) {
        if (baseCount <= 0) {
            throw new IllegalArgumentException("Mutation size must be greater than 0");
        }
        if (baseCount % 3 != 0) {
            throw new IllegalArgumentException("Mutation size must be divisible by 3");
        }

        List<String> result = codons == null ? new ArrayList<>() : new ArrayList<>(codons);
        String mutation = randomBases(baseCount);
        int insertIndex = result.isEmpty() ? 0 : random.nextInt(result.size() + 1);
        result.add(insertIndex, mutation);
        return List.copyOf(result);
    }

    private String randomBases(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(BASES[random.nextInt(BASES.length)]);
        }
        return builder.toString();
    }

    /**
     * Creates a random DNA sequence of the requested length.
     *
     * @param length number of bases to generate; must be positive and divisible by three
     * @return a DNA sequence containing exactly {@code length} randomly selected bases
     * @throws IllegalArgumentException if {@code length} is not positive or is not divisible by three
     */
    public String createRandomDNA(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Number of bases generated must be greater than 0");
        }
        if (length % 3 != 0) {
            throw new IllegalArgumentException("Number of bases must be divisible by 3");
        } else {
            StringBuilder builder = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                builder.append(BASES[random.nextInt(BASES.length)]);
            }
            return builder.toString();
        }
    }
}
