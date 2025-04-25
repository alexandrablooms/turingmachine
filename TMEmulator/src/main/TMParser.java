package main;

import java.util.*;

/**
 * TMParser class provides functionality to parse a binary-encoded Turing Machine.
 */
public class TMParser {
    private static final String TRANSITION_SEPARATOR = "11";
    private static final char BLANK_SYMBOL = '_';

    /**
     * Parse a binary encoded TM and return a TuringMachine object
     *
     * @param binaryEncoding The binary encoding of the Turing Machine
     * @return A TuringMachine object
     * @throws IllegalArgumentException If the binary encoding is invalid
     */
    public static TuringMachine parseTuringMachine(String binaryEncoding) {
        if (binaryEncoding == null || binaryEncoding.isEmpty()) {
            throw new IllegalArgumentException("Binary encoding cannot be null or empty");
        }

        if (!binaryEncoding.matches("[01]+")) {
            throw new IllegalArgumentException("Binary encoding must contain only 0s and 1s");
        }

        // Split the binary encoding into transitions
        String[] transitions = binaryEncoding.split(TRANSITION_SEPARATOR);

        // Sets and maps to store components of the Turing Machine
        Set<String> states = new HashSet<>();
        Set<Character> inputAlphabet = new HashSet<>();
        Set<Character> tapeAlphabet = new HashSet<>();
        Map<String, Map<Character, Object[]>> transitionFunctions = new HashMap<>();
        Set<String> acceptStates = new HashSet<>();

        // Add start and accept state names
        String startState = getStateName(1); // q1 = start state
        String acceptState = getStateName(2); // q2 = accept state
        states.add(startState);
        states.add(acceptState);
        acceptStates.add(acceptState);

        // Add default tape symbols
        char symbol0 = getSymbol(1); // X1 = 0
        char symbol1 = getSymbol(2); // X2 = 1
        char blankSymbol = getSymbol(3); // X3 = blank

        inputAlphabet.add(symbol0);
        inputAlphabet.add(symbol1);

        tapeAlphabet.add(symbol0);
        tapeAlphabet.add(symbol1);
        tapeAlphabet.add(blankSymbol);

        // Parse each transition
        for (String transition : transitions) {
            if (transition.isEmpty()) {
                continue; // Skip empty transitions
            }

            try {
                Object[] parsedTransition = parseTransition(transition);
                int currentState = (int) parsedTransition[0];
                int currentSymbol = (int) parsedTransition[1];
                int newState = (int) parsedTransition[2];
                int newSymbol = (int) parsedTransition[3];
                int direction = (int) parsedTransition[4];

                // Add states to the set
                String currentStateName = getStateName(currentState);
                String newStateName = getStateName(newState);
                states.add(currentStateName);
                states.add(newStateName);

                // Add symbols to the input and tape alphabets
                char symbolChar = getSymbol(currentSymbol);
                char newSymbolChar = getSymbol(newSymbol);

                if (currentSymbol <= 2) { // 0 or 1 are input symbols
                    inputAlphabet.add(symbolChar);
                }

                tapeAlphabet.add(symbolChar);
                tapeAlphabet.add(newSymbolChar);

                // Create transition function
                Map<Character, Object[]> stateTransitions =
                        transitionFunctions.computeIfAbsent(currentStateName, k -> new HashMap<>());

                TuringMachine.Direction dir = getDirection(direction);
                stateTransitions.put(symbolChar, new Object[]{newStateName, newSymbolChar, dir});

            } catch (Exception e) {
                throw new IllegalArgumentException("Error parsing transition: " + transition, e);
            }
        }

        // Check if we have at least one transition
        if (transitionFunctions.isEmpty()) {
            throw new IllegalArgumentException("No valid transitions found in the binary encoding");
        }

        return new TuringMachine(states, inputAlphabet, tapeAlphabet,
                transitionFunctions, startState, blankSymbol, acceptStates);
    }

    /**
     * Parse a single transition in binary format
     * Format: 0ⁱ10ʲ10ᵏ10ˡ10ᵐ represents δ(qᵢ, Xⱼ) = (qₖ, Xₗ, Dₘ)
     *
     * @param binaryTransition The binary transition to parse
     * @return An array containing the parsed components: [currentState, currentSymbol, newState, newSymbol, direction]
     * @throws IllegalArgumentException If the transition format is invalid
     */
    private static Object[] parseTransition(String binaryTransition) {
        String[] parts = binaryTransition.split("1");

        // Filter out empty strings which can occur if there are consecutive 1s
        // or if the string starts/ends with 1
        List<String> nonEmptyParts = new ArrayList<>();
        for (String part : parts) {
            if (!part.isEmpty()) {
                nonEmptyParts.add(part);
            }
        }

        // Check if there are exactly 5 parts
        if (nonEmptyParts.size() != 5) {
            throw new IllegalArgumentException("Invalid transition format: Expected 5 components but found "
                    + nonEmptyParts.size() + " in " + binaryTransition);
        }

        // Parse the components from the counts of zeros
        int currentState = countZeros(nonEmptyParts.get(0));
        int currentSymbol = countZeros(nonEmptyParts.get(1));
        int newState = countZeros(nonEmptyParts.get(2));
        int newSymbol = countZeros(nonEmptyParts.get(3));
        int direction = countZeros(nonEmptyParts.get(4));

        return new Object[]{currentState, currentSymbol, newState, newSymbol, direction};
    }

    /**
     * Helper method to get state name from state number
     * q₁ is start state, q₂ is accept state, etc.
     *
     * @param stateNumber The state number
     * @return The state name
     */
    private static String getStateName(int stateNumber) {
        return "q" + stateNumber;
    }

    /**
     * Helper method to get symbol from symbol number
     * X₁ is 0, X₂ is 1, X₃ is blank, etc.
     *
     * @param symbolNumber The symbol number
     * @return The symbol character
     */
    private static char getSymbol(int symbolNumber) {
        return switch (symbolNumber) {
            case 1 -> '0';
            case 2 -> '1';
            case 3 -> BLANK_SYMBOL;
            default -> (char) ('A' + (symbolNumber - 4));  // Additional symbols
        };
    }

    /**
     * Helper method to get direction from direction number
     * D₁ is LEFT, D₂ is RIGHT
     *
     * @param directionNumber The direction number
     * @return The direction enum value
     */
    private static TuringMachine.Direction getDirection(int directionNumber) {
        return (directionNumber == 1) ? TuringMachine.Direction.LEFT : TuringMachine.Direction.RIGHT;
    }

    /**
     * Count the number of consecutive '0's in a string
     *
     * @param zeros The string containing zeros
     * @return The count of zeros
     */
    private static int countZeros(String zeros) {
        return zeros.length();
    }
}