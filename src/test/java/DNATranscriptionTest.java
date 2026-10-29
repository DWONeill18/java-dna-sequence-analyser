import DNAAnalysis.DNATranscription;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link DNAAnalysis.DNATranscription}.
 */
public class DNATranscriptionTest {

    /**
     * Verifies multiple codons are transcribed into a single RNA sequence.
     */
    @Test
    void transcription_multipleCodons_returnsTranscribedSequence() {

        DNATranscription dnaTranscription = new DNATranscription();

        String result = dnaTranscription.transcription(List.of("ACG", "TGA", "CCT"));

        assertEquals("UGCACUGGA", result);
    }

    /**
     * Ensures empty codon lists throw an exception.
     */
    @Test
    void transcription_emptyList_throwsException() {
        DNATranscription dnaTranscription = new DNATranscription();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> dnaTranscription.transcription(List.of())
        );

        assertEquals("Codon list must not be null or empty", exception.getMessage());
    }

    /**
     * Ensures null codon lists throw an exception.
     */
    @Test
    void transcription_nullList_throwsException() {
        DNATranscription dnaTranscription = new DNATranscription();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> dnaTranscription.transcription(null)
        );

        assertEquals("Codon list must not be null or empty", exception.getMessage());
    }

    /**
     * Verifies null codon entries are skipped while usable codons are transcribed.
     */
    @Test
    void transcription_nullCodonEntry_isSkipped() {
        DNATranscription dnaTranscription = new DNATranscription();

        String result = dnaTranscription.transcription(Arrays.asList(null, "ACG"));

        assertEquals("UGC", result);
    }

    /**
     * Ensures blank codons are rejected when no usable codons remain.
     */
    @Test
    void transcription_allBlankCodons_throwsException() {
        DNATranscription dnaTranscription = new DNATranscription();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> dnaTranscription.transcription(List.of("   ", "", "\t"))
        );

        assertEquals("No usable codons after filtering", exception.getMessage());
    }

    /**
     * Ensures invalid DNA bases are rejected with a helpful message.
     */
    @Test
    void transcription_invalidBase_throwsException() {
        DNATranscription dnaTranscription = new DNATranscription();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> dnaTranscription.transcription(List.of("ACX"))
        );

        assertEquals("DNA contains invalid characters: X", exception.getMessage());
    }

    /**
     * Verifies lowercase codons are normalized before transcription.
     */
    @Test
    void transcription_lowercaseCodon_transcribesCorrectly() {
        DNATranscription dnaTranscription = new DNATranscription();

        String result = dnaTranscription.transcription(List.of("acg"));

        assertEquals("UGC", result);
    }
}
