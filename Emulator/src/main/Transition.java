package main;

/**
 * Represents a transition in a Turing Machine.
 * <p>
 * A transition defines how a Turing Machine changes its state, writes a symbol,
 * and moves the head given a current state and read symbol. Each transition is
 * represented as a 5-tuple: (fromState, readSymbol, toState, writeSymbol, direction).
 * </p>
 */
public class Transition {
    private final int fromState;
    private final int readSymbol;
    private final int toState;
    private final int writeSymbol;
    private final char direction;

    /**
     * Creates a new transition with the specified parameters.
     *
     * @param fromState   The current state from which this transition applies.
     * @param readSymbol  The symbol that must be read for this transition to be valid.
     * @param toState     The state to transition to.
     * @param writeSymbol The symbol to write on the tape.
     * @param direction   The direction to move the head ('L' for left, 'R' for right).
     */
    public Transition(int fromState, int readSymbol, int toState, int writeSymbol, char direction) {
        this.fromState = fromState;
        this.readSymbol = readSymbol;
        this.toState = toState;
        this.writeSymbol = writeSymbol;
        this.direction = direction;
    }

    /**
     * @return The state from which this transition applies.
     */
    public int getFromState() {
        return fromState;
    }

    /**
     * @return The symbol that must be read for this transition to be valid.
     */
    public int getReadSymbol() {
        return readSymbol;
    }

    /**
     * @return The state to transition to.
     */
    public int getToState() {
        return toState;
    }

    /**
     * @return The symbol to write on the tape.
     */
    public int getWriteSymbol() {
        return writeSymbol;
    }

    /**
     * @return The direction to move the head ('L' for left, 'R' for right).
     */
    public char getDirection() {
        return direction;
    }

    /**
     * Returns a string representation of this transition in standard notation.
     *
     * @return A string in the format δ(q_i, X_j) = (q_k, X_l, D) where:
     *         q_i is the from state, X_j is the read symbol,
     *         q_k is the to state, X_l is the write symbol,
     *         and D is the direction (L or R).
     */
    @Override
    public String toString() {
        return "δ(q" + fromState + ", X" + readSymbol + ") = (q" + toState + ", X" + writeSymbol + ", " + direction + ")";
    }
}