package main;

/**
 * TMConfiguration class represents a configuration of a Turing machine.
 * It includes the current state, tape content, and step count.
 */
public class TMConfiguration {
    private String currentState; // current state of the Turing machine (q1, q2, q3, etc.)
    private Tape tape; // tape with its current content and head position
    private int stepCount; // number of steps executed to reach this configuration

    /**
     * Constructor for TMConfiguration to create a new initial configuration
     * @param initialState The initial state of the Turing machine
     * @param tape The initial tape
     */
    public TMConfiguration(String initialState, Tape tape) {
        this.currentState = initialState;
        this.tape = tape;
        this.stepCount = 0;
    }

    /**
     * Constructor for TMConfiguration to create a configuration during computation
     * @param currentState The current state of the Turing machine
     * @param tape The current tape
     * @param stepCount The number of steps executed
     */
    public TMConfiguration(String currentState, Tape tape, int stepCount) {
        this.currentState = currentState;
        this.tape = tape;
        this.stepCount = stepCount;
    }

    /**
     * Get the current state of the Turing machine
     * @return The current state
     */
    public String getCurrentState() {
        return this.currentState;
    }

    /**
     * Get the tape of the Turing machine
     * @return The tape
     */
    public Tape getTape() {
        return this.tape;
    }

    /**
     * Get the number of steps executed to reach this configuration
     * @return The step count
     */
    public int getStepCount() {
        return this.stepCount;
    }

    /**
     * Create a string representation of the current configuration.
     * Format: X1X2...X{i-1}qiX{i}X{i+1}...Xn
     * where q - current state, placed before the symbol it's reading
     * @return A string representation of the configuration
     */
    @Override
    public String toString() {
        // Get tape content before the head
        String before = tape.getTapeContentBefore(15);

        // Get the current symbol
        char currentSymbol = tape.readSymbol();

        // Get tape content after the head
        String after = tape.getTapeContentAfter(15);

        // Build the configuration string: [before][state][currentSymbol][after]
        return before + currentState + currentSymbol + after;
    }

    /**
     * Set the current state of the Turing machine
     * @param currentState The new state
     */
    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    /**
     * Increment the step count
     */
    public void incrementStepCount() {
        this.stepCount++;
    }

    /**
     * Set the step count
     * @param stepCount The new step count
     */
    public void setStepCount(int stepCount) {
        this.stepCount++;
    }

    /**
     * Create a copy of the current configuration
     * @return A new TMConfiguration object with the same state, tape, and step count
     */
    public TMConfiguration copyTMConfig() {
        return new TMConfiguration(this.currentState, this.tape.copyTape(), this.stepCount);
    }
}



