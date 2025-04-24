package main;

import java.util.*;

public class TMParser {
    private static final String TRANSITION_SEPARATOR = "11";
    private static final char BLANK_SYMBOL = '_';

    /**
     * Parse a binary encoded TM and return a TuringMachine object
     */
    public static TuringMachine parseTuringMachine(String binaryEncoding) {
        String[] transitions = binaryEncoding.split(TRANSITION_SEPARATOR);

        Set<String> states = new HashSet<>();
        Set<Character> inputAlphabet = new HashSet<>();
        Set<Character> tapeAlphabet = new HashSet<>();
        Map<String, Map<Character, Object[]>> transitionFunctions = new HashMap<>();
        Set<String> acceptStates = new HashSet<>();

        // add start and state name
        String startState = getStateName(1); // q1 = start state
        String acceptState = getStateName(2); // q2 = accept state
        states.add(startState);
        states.add(acceptState);
        acceptStates.add(acceptState);

        // add default tape symbols
        char symbol0 = getSymbol(1); // X1 = 0
        char symbol1 = getSymbol(2); // X2 = 1
        char blankSymbol = getSymbol(3); // X3 = blank

        inputAlphabet.add(symbol0);
        inputAlphabet.add(symbol1);

        tapeAlphabet.add(symbol0);
        tapeAlphabet.add(symbol1);
        tapeAlphabet.add(blankSymbol);

        // parse each transition
        for (String transition : transitions) {
            Object[] parsedTransition = parseTransition(transition);
            int currentState = (int) parsedTransition[0];
            int currentSymbol = (int) parsedTransition[1];
            int newState = (int) parsedTransition[2];
            int newSymbol = (int) parsedTransition[3];
            int direction = (int) parsedTransition[4];

            // add states to the set
            states.add(getStateName(currentState));
            states.add(getStateName(newState));

            // add symbols to the input and tape alphabets
            char symbol = getSymbol(currentSymbol);
            inputAlphabet.add(symbol);
            tapeAlphabet.add(symbol);

            char newTapeSymbol = getSymbol(newSymbol);
            tapeAlphabet.add(newTapeSymbol);

            // create transition function
            Map<Character, Object[]> stateTransitions =
                    transitionFunctions.computeIfAbsent(getStateName(currentState), k -> new HashMap<>());
            stateTransitions.put(symbol, new Object[]{getStateName(newState), newTapeSymbol, getDirection(direction)});
        }

        return new TuringMachine(states, inputAlphabet, tapeAlphabet,
                transitionFunctions, startState, blankSymbol, acceptStates);
    }

    /**
     * Parse a single transition in binary format
     * Format: 0ⁱ10ʲ10ᵏ10ˡ10ᵐ represents δ(qᵢ, Xⱼ) = (qₖ, Xₗ, Dₘ)
     */
    private static Object[] parseTransition(String binaryTransition) {
        String[] parts = binaryTransition.split("1");

        // filter out empty strings which can occur if there are consecutive 1s
        // or if the string starts/ends with 1
        List<String> nonEmptyParts = new ArrayList<>();
        for (String part : parts) {
            if (!part.isEmpty()) {
                nonEmptyParts.add(part);
            }
        }

        // check if there are exactly 5 parts
        if (nonEmptyParts.size() != 5) {
            throw new IllegalArgumentException("Invalid transition format: Expected 5 components but found "
                    + nonEmptyParts.size() + " in " + binaryTransition);
        }

        // parse the components from the counts of zeros
        int currentState = countZeros(parts[0]);
        int currentSymbol = countZeros(parts[1]);
        int newState= countZeros(parts[2]);
        int newSymbol = countZeros(parts[3]);
        int direction = countZeros(parts[4]);

        return new Object[]{currentState, currentSymbol, newState, newSymbol, direction};
    }

    /**
     * Helper method to get state name from state number
     * q₁ is start state, q₂ is accept state, etc.
     */
    private static String getStateName(int stateNumber) {
        return "q" + stateNumber;
    }

    /**
     * Helper method to get symbol from symbol number
     * X₁ is 0, X₂ is 1, X₃ is blank, etc.
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
     */
    private static TuringMachine.Direction getDirection(int directionNumber) {
        return (directionNumber == 1) ? TuringMachine.Direction.LEFT : TuringMachine.Direction.RIGHT;
    }

    /**
     * Count the number of consecutive '0's in a string
     */
    private static int countZeros(String zeros) {
        return zeros.length();
    }
}
