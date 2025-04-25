package main;

import java.util.Map;
import java.util.Set;

/**
 * TuringMachine class represents a standard Turing machine with all its components.
 */
public class TuringMachine {
    private Set<String> states; // Set of states (q1, q2, q3, etc.)
    private Set<Character> inputAlphabet; // Set of allowed input symbols
    private Set<Character> tapeAlphabet; // Set of tape symbols that can appear on the tape
    // First Map: state -> second Map
    // Second Map: symbol -> transition result
    private Map<String, Map<Character, Object[]>> transitionFunctions; // Transition function
    private String startState; // Initial state
    private char blankSymbol; // Symbol for blank spaces (usually '_')
    private Set<String> acceptStates; // Set of accept states

    /**
     * Directions for the Turing machine head movement
     */
    public enum Direction {
        LEFT,  // Move left
        RIGHT  // Move right
    }

    /**
     * Constructor to create a new Turing machine.
     *
     * @param states Set of states
     * @param inputAlphabet Set of input symbols
     * @param tapeAlphabet Set of tape symbols
     * @param transitionFunctions Transition functions
     * @param startState Initial state
     * @param blankSymbol Blank symbol
     * @param acceptStates Set of accept states
     */
    public TuringMachine(Set<String> states,
                         Set<Character> inputAlphabet,
                         Set<Character> tapeAlphabet,
                         Map<String, Map<Character, Object[]>> transitionFunctions,
                         String startState, char blankSymbol,
                         Set<String> acceptStates) {
        this.states = states;
        this.inputAlphabet = inputAlphabet;
        this.tapeAlphabet = tapeAlphabet;
        this.transitionFunctions = transitionFunctions;
        this.startState = startState;
        this.blankSymbol = blankSymbol;
        this.acceptStates = acceptStates;
    }

    /**
     * Create an initial configuration with the given input.
     *
     * @param input The input string for the Turing machine
     * @return The initial configuration
     */
    public TMConfiguration createInitialConfiguration(String input) {
        Tape tape = new Tape(input, blankSymbol);
        return new TMConfiguration(startState, tape);
    }

    /**
     * Performs one step of computation.
     *
     * @param currentConfig The current configuration
     * @return The next configuration, or null if no valid transition exists
     */
    public TMConfiguration step(TMConfiguration currentConfig) {
        String currentState = currentConfig.getCurrentState();
        char currentSymbol = currentConfig.getTape().readSymbol();

        // Check if transition function is defined for current state and symbol
        if (transitionFunctions.containsKey(currentState) &&
                transitionFunctions.get(currentState).containsKey(currentSymbol)) {

            // Get transition result
            Object[] transitionResult = transitionFunctions.get(currentState).get(currentSymbol);

            // Extract new state, symbol to write, and direction
            String newState = (String) transitionResult[0];
            char newSymbol = (char) transitionResult[1];
            Direction direction = (Direction) transitionResult[2];

            // Create a copy of the current config
            TMConfiguration newConfig = currentConfig.copyTMConfig();

            // Update tape (write new symbol)
            newConfig.getTape().writeSymbol(newSymbol);

            // Move head in the specified direction
            if (direction == Direction.LEFT) {
                newConfig.getTape().moveLeft();
            } else {
                newConfig.getTape().moveRight();
            }

            // Update state and step count
            newConfig.setCurrentState(newState);
            newConfig.incrementStepCount();

            return newConfig;
        } else {
            // No valid transition, machine halts
            return null;
        }
    }

    /**
     * Check if the machine is in an accept state.
     *
     * @param config The current configuration
     * @return true if the current state is an accept state, false otherwise
     */
    public boolean isAccepting(TMConfiguration config) {
        return acceptStates.contains(config.getCurrentState());
    }

    /**
     * Check if the machine halts.
     * A machine halts if there is no valid transition for the current state and symbol.
     *
     * @param config The current configuration
     * @return true if the machine halts, false otherwise
     */
    public boolean isHalted(TMConfiguration config) {
        if (config == null) {
            return true;
        }

        String state = config.getCurrentState();
        char symbol = config.getTape().readSymbol();

        return !transitionFunctions.containsKey(state) ||
                !transitionFunctions.get(state).containsKey(symbol);
    }

    /**
     * Get the states of the Turing machine
     *
     * @return The set of states
     */
    public Set<String> getStates() {
        return states;
    }

    /**
     * Get the input alphabet of the Turing machine
     *
     * @return The set of input symbols
     */
    public Set<Character> getInputAlphabet() {
        return inputAlphabet;
    }

    /**
     * Get the tape alphabet of the Turing machine
     *
     * @return The set of tape symbols
     */
    public Set<Character> getTapeAlphabet() {
        return tapeAlphabet;
    }

    /**
     * Get the transition functions of the Turing machine
     *
     * @return The transition functions
     */
    public Map<String, Map<Character, Object[]>> getTransitionFunctions() {
        return transitionFunctions;
    }

    /**
     * Get the start state of the Turing machine
     *
     * @return The start state
     */
    public String getStartState() {
        return startState;
    }

    /**
     * Get the blank symbol of the Turing machine
     *
     * @return The blank symbol
     */
    public char getBlankSymbol() {
        return blankSymbol;
    }

    /**
     * Get the accept states of the Turing machine
     *
     * @return The set of accept states
     */
    public Set<String> getAcceptStates() {
        return acceptStates;
    }

    /**
     * Get a string representation of the Turing machine
     *
     * @return A string representation of the Turing machine
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Turing Machine:\n");
        sb.append("States: ").append(states).append("\n");
        sb.append("Input alphabet: ").append(inputAlphabet).append("\n");
        sb.append("Tape alphabet: ").append(tapeAlphabet).append("\n");
        sb.append("Start state: ").append(startState).append("\n");
        sb.append("Accept states: ").append(acceptStates).append("\n");
        sb.append("Blank symbol: '").append(blankSymbol).append("'\n");
        sb.append("Transition functions:\n");

        for (Map.Entry<String, Map<Character, Object[]>> stateEntry : transitionFunctions.entrySet()) {
            String state = stateEntry.getKey();
            Map<Character, Object[]> symbolTransitions = stateEntry.getValue();

            for (Map.Entry<Character, Object[]> symbolEntry : symbolTransitions.entrySet()) {
                char symbol = symbolEntry.getKey();
                Object[] transition = symbolEntry.getValue();

                String newState = (String) transition[0];
                char newSymbol = (char) transition[1];
                Direction direction = (Direction) transition[2];

                sb.append("  δ(").append(state).append(", '").append(symbol)
                        .append("') = (").append(newState).append(", '").append(newSymbol)
                        .append("', ").append(direction).append(")\n");
            }
        }

        return sb.toString();
    }
}