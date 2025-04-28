import java.util.ArrayList;
import java.util.List;

/**
 * Parser for converting binary string encodings to Turing Machine transitions.
 * <p>
 * This class handles the parsing of binary encoded Turing Machine descriptions
 * according to the encoding scheme defined in the lecture materials. The encoding
 * represents transitions as sequences of 0s and 1s, where:
 * - States are encoded as sequences of 0s (q_i is encoded as i consecutive 0s)
 * - Symbols are encoded as sequences of 0s (X_j is encoded as j consecutive 0s)
 * - Directions are encoded as sequences of 0s (L is 1 consecutive 0, R is 2 consecutive 0s)
 * - The '1' character is used as a separator between components
 * - The "11" sequence is used as a separator between transitions
 * </p>
 */
public class TMParser {
    private static final String TRANSITION_SEPARATOR = "11";
    private static final char COMPONENT_SEPARATOR = '1';
    private static final int LEFT_DIRECTION_CODE = 1;
    private static final char LEFT_DIRECTION = 'L';
    private static final char RIGHT_DIRECTION = 'R';

    /**
     * Counts the number of consecutive zeros starting at a given index in the encoding.
     *
     * @param encoding   The binary encoding string.
     * @param startIndex The index to start counting from.
     * @return The number of consecutive zeros.
     */
    private static int parseZeros(String encoding, int startIndex) {
        int count = 0;
        int i = startIndex;
        while (i < encoding.length() && encoding.charAt(i) == '0') {
            count++;
            i++;
        }
        return count;
    }

    /**
     * Parses a binary string encoding of a Turing machine and converts it to a list of transitions.
     *
     * @param encoding The binary encoding of the Turing machine.
     * @return A list of Transition objects representing the encoded Turing machine.
     */
    public static List<Transition> parseTuringMachine(String encoding) {
        List<Transition> transitions = new ArrayList<>();

        int i = 0;
        while (i < encoding.length()) {
            // Skip the separator "11" if present
            if (i + 1 < encoding.length() &&
                    encoding.charAt(i) == COMPONENT_SEPARATOR &&
                    encoding.charAt(i + 1) == COMPONENT_SEPARATOR) {
                i += 2;
                continue;
            }

            // Parse a transition
            if (i < encoding.length() && encoding.charAt(i) == '0') {
                int fromState = parseZeros(encoding, i);
                i += fromState; // Skip the zeros

                if (i < encoding.length() && encoding.charAt(i) == COMPONENT_SEPARATOR) {
                    i++; // Skip the separator '1'

                    int readSymbol = parseZeros(encoding, i);
                    i += readSymbol; // Skip the zeros

                    if (i < encoding.length() && encoding.charAt(i) == COMPONENT_SEPARATOR) {
                        i++; // Skip the separator '1'

                        int toState = parseZeros(encoding, i);
                        i += toState; // Skip the zeros

                        if (i < encoding.length() && encoding.charAt(i) == COMPONENT_SEPARATOR) {
                            i++; // Skip the separator '1'

                            int writeSymbol = parseZeros(encoding, i);
                            i += writeSymbol; // Skip the zeros

                            if (i < encoding.length() && encoding.charAt(i) == COMPONENT_SEPARATOR) {
                                i++; // Skip the separator '1'

                                int directionCode = parseZeros(encoding, i);
                                i += directionCode; // Skip the zeros

                                // Direction: 1 for L, 2 for R
                                char direction = directionCode == LEFT_DIRECTION_CODE ? LEFT_DIRECTION : RIGHT_DIRECTION;

                                // Add the transition to the list
                                transitions.add(new Transition(fromState, readSymbol, toState, writeSymbol, direction));
                            }
                        }
                    }
                }
            } else {
                // Skip any unexpected characters
                i++;
            }
        }

        return transitions;
    }
}