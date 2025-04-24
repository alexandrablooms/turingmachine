package main;

public class TMConfiguration {
    private String currentState; // current state of the Turing machine (q1, q2, q3, etc.)
    private Tape tape; // tape with its current content and head position
    private int stepCount; // number of steps executed to reach this configuration

    /**
     * Constructor for TMConfiguration to create a new configuration.
     */
    public TMConfiguration(String initialState, Tape tape) {
        this.currentState = initialState;
        this.tape = tape;
        this.stepCount = 0;
    }

    /**
     * Constructor for TMConfiguration to create a configuration during computation.
     */
    public TMConfiguration(String currentState, Tape tape, int stepCount) {
        this.currentState = currentState;
        this.tape = tape;
        this.stepCount = stepCount;
    }

    /**
     * Get the current state of the Turing machine.
     */
    public String getCurrentState() {
        return this.currentState;
    }

    /**
     * Get the tape of the Turing machine.
     */
    public Tape getTape() {
        return this.tape;
    }

    /**
     * Get the number of steps executed to reach this configuration.
     */
    public int getStepCount() {
        return this.stepCount;
    }

    /**
     * Create a string representation of the current configuration.
     * Format: X1X2...X{i-1}qiX{i}X{i+1}...Xn
     * where q - current state, placed before the symbol it's reading
     */
    @Override
    public String toString() {
        String tapeContent = tape.getTapeContent(15, 15);
        int headPosition = 15;
        StringBuilder sb = new StringBuilder();

        // add symbols before the head
        sb.append(tapeContent.substring(0, headPosition));

        // add state at the head position
        sb.append(currentState);

        // add symbols at and after the head
        sb.append(tapeContent.substring(headPosition));

        return sb.toString();
    }

    /**
     * Set the current state of the Turing machine.
     */
    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    /**
     * Set step count.
     */
    public void setStepCount(int stepCount) {
        this.stepCount++;
    }

    /**
     * Copy of the current configuration.
     */
    public TMConfiguration copyTMConfig() {
        TMConfiguration newTMConfig = new TMConfiguration(this.currentState, this.tape.copyTape(), this.stepCount);
        newTMConfig.currentState = this.currentState;
        newTMConfig.tape = this.tape.copyTape();
        newTMConfig.stepCount = this.stepCount;
        return newTMConfig;
    }
}



