package structlab.core.hash;

/**
 * Provides multiple hash-function strategies for hash-table implementations.
 *
 * <h3>Academic references</h3>
 * <ul>
 *   <li><b>DIVISION</b> &mdash; {@code h(k) = k mod m}
 *       (Kretinsky, <i>Fundamental Algorithms</i> Ch.&nbsp;5;
 *        Sedgewick &amp; Wayne, <i>Algorithms</i> &sect;3.4)</li>
 *   <li><b>MULTIPLICATION</b> &mdash; {@code h(k) = floor(m * (gamma*k mod 1))}
 *       with {@code gamma = (sqrt(5)-1)/2}
 *       (Kretinsky Ch.&nbsp;5; Cormen <i>CLRS</i> &sect;11.3.2)</li>
 *   <li><b>JCF7</b> &mdash; supplementary bit-mixing from Java&nbsp;7 HashMap
 *       (defends against poor {@code hashCode()} distributions;
 *        see Sedgewick &sect;3.4 &ldquo;war story&rdquo;)</li>
 *   <li><b>JCF</b> &mdash; simplified bit-mixing from Java&nbsp;8+ HashMap</li>
 * </ul>
 *
 * <p><b>Integer-overflow safety:</b> all branches mask via
 * {@code hashCode & 0x7fffffff} to guarantee a non-negative result,
 * following the Sedgewick convention and avoiding the
 * {@code Math.abs(Integer.MIN_VALUE)} pitfall (which returns a
 * negative number).</p>
 */
public class HashManager {

    /** (sqrt(5) - 1) / 2 &asymp; 0.6180339887 &mdash; the golden-ratio fractional part. */
    private static final double GOLDEN_RATIO_FRAC = (Math.sqrt(5) - 1) / 2;

    public enum HashType {
        /** {@code h(k) = |k| mod m} &mdash; simplest choice; best when m is prime. */
        DIVISION,
        /** {@code h(k) = floor(m * (gamma*k mod 1))} &mdash; multiplication method. */
        MULTIPLICATION,
        /** Java 7 HashMap supplementary hash (XOR right-shifts 20, 12, 7, 4). */
        JCF7,
        /** Java 8+ HashMap supplementary hash (XOR right-shift 16). */
        JCF
    }

    /**
     * Computes a bucket index in {@code [0, tableLength)} for the given hash code.
     *
     * @param hashCode    raw hash code from {@code key.hashCode()}
     * @param tableLength number of buckets (must be &gt; 0)
     * @param hashType    the hashing strategy to apply
     * @return non-negative index in {@code [0, tableLength)}
     */
    public static int hash(int hashCode, int tableLength, HashType hashType) {
        switch (hashType) {
            case DIVISION:
                // Sedgewick §3.4: mask sign bit instead of Math.abs (MIN_VALUE pitfall)
                return (hashCode & 0x7fffffff) % tableLength;
            case MULTIPLICATION:
                // Kretinsky Ch.5: h(k) = floor(m * (gamma*k mod 1))
                return (int) (((GOLDEN_RATIO_FRAC * (hashCode & 0x7fffffff)) % 1.0) * tableLength);
            case JCF7:
                // Java 7 supplementary hash — bit-mixing then modular reduction
                hashCode ^= (hashCode >>> 20) ^ (hashCode >>> 12);
                hashCode = hashCode ^ (hashCode >>> 7) ^ (hashCode >>> 4);
                return (hashCode & 0x7fffffff) % tableLength;
            case JCF:
                // Java 8+ supplementary hash — simplified bit-mixing
                hashCode = hashCode ^ (hashCode >>> 16);
                return (hashCode & 0x7fffffff) % tableLength;
            default:
                throw new IllegalArgumentException("Unknown HashType: " + hashType);
        }
    }
}
