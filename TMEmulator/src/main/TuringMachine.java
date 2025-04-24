package main;

import java.util.Map;
import java.util.Set;

public class TuringMachine {
    private Set<String> states; // set of states (q1, q2, q3, etc.)
    private Set<Character> inputAlphabet; // set of allowed input symbols
    private Set<Character> tapeAlphabet; // set of tape symbols that can appear on the tape
    // First Map: state -> second Map
    // Second Map: symbol -> transition result
    private Map<String, Map<Character, Object[]>> transitionFunctions; // transition function
    private String startState;
    private char blankSymbol; // symbol for blank spaces ('_')
    private Set<String> acceptStates; // set of accept states

    public enum Direction {
        LEFT, RIGHT
    }

    /**
     * Constructor to create a new Turing machine.
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
     */
    public TMConfiguration createInitialConfiguration(String input) {
        Tape tape = new Tape(input, blankSymbol);

        // create and return config with start state
        return new TMConfiguration(startState, tape);
    }

    /**
     * Performs one step of computation.
     * Returns null if machine halts
     */
    public TMConfiguration step(TMConfiguration currentConfig) {
        String currentState = currentConfig.getCurrentState();
        char currentSymbol = currentConfig.getTape().readSymbol();

        // check if transition function is defined for current state and symbol
        if (transitionFunctions.containsKey(currentState) &&
            transitionFunctions.get(currentState).containsKey(currentSymbol)) {
            // get transition result
            Object[] transitionResult = transitionFunctions.get(currentState).get(currentSymbol);

            // extract new state, symbol to write, and direction
            String newState = (String) transitionResult[0];
            char newSymbol = (char) transitionResult[1];
            Direction direction = (Direction) transitionResult[2];

            // create a copy of the current config
            TMConfiguration newConfig = currentConfig.copyTMConfig();

            // update tape (write new symbol)
            newConfig.getTape().writeSymbol(newSymbol);

            // move head in the specified direction
            if (direction == Direction.LEFT) {
                newConfig.getTape().moveLeft();
            } else {
                newConfig.getTape().moveRight();
            }

            // update state and step count
            newConfig.setCurrentState(newState);
            newConfig.setStepCount(newConfig.getStepCount() + 1);

            return newConfig;
        } else {
            // no valid transition, machine halts
            return null;
        }
    }

    /**
     * Check if the machine is in an accept state.
     */
    public boolean isAccepting(TMConfiguration config) {
        return acceptStates.contains(config.getCurrentState());
    }

    /**
     * Check if the machine halts.
     */
    public boolean isHalted(TMConfiguration config) {
        String state = config.getCurrentState();
        char symbol = config.getTape().readSymbol();

        return !transitionFunctions.containsKey(state) ||
                !transitionFunctions.get(state).containsKey(symbol);
    }
}
