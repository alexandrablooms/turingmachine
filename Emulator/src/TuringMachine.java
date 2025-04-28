import java.util.*;

/**
 * Represents a Turing Machine with its states, tape, and transitions.
 * <p>
 * A Turing Machine is a theoretical computational model consisting of:
 * - A tape divided into cells, each holding a symbol
 * - A head that can read and write symbols on the tape and move left or right
 * - A finite set of states with transitions between them
 * - A set of rules (transitions) that determine the machine's behavior
 * </p>
 */
public class TuringMachine {
    /**
     * Symbol encoding for '0'.
     */
    public static final int SYMBOL_ZERO = 1;

    /**
     * Symbol encoding for '1'.
     */
    public static final int SYMBOL_ONE = 2;

    /**
     * Symbol encoding for blank character.
     */
    public static final int SYMBOL_BLANK = 3;

    public static final int SYMBOL_X = 4;
    public static final int SYMBOL_Y = 5;
    public static final int SYMBOL_Z = 6;
    public static final int SYMBOL_HASH = 7;

    /**
     * Default start state (q₁).
     */
    public static final int DEFAULT_START_STATE = 1;

    /**
     * Default accept state (q₂).
     */
    public static final int DEFAULT_ACCEPT_STATE = 2;

    /**
     * Number of cells to display on each side of the head when printing the tape.
     */
    private static final int DISPLAY_CELLS = 15;

    private int currentState;
    private List<Integer> tape;
    private int headPosition;
    private final Map<String, Transition> transitions;
    private final int startState;
    private final int acceptState;
    private int steps;

    // Symbol mapping for flexible input/output
    private final Map<Character, Integer> charToSymbol;
    private final Map<Integer, Character> symbolToChar;

    /**
     * Creates a new Turing Machine with the specified transitions.
     *
     * @param transitionList The list of transitions that define the behavior of this machine.
     */
    public TuringMachine(List<Transition> transitionList) {
        this.currentState = DEFAULT_START_STATE;
        this.tape = new ArrayList<>();
        this.headPosition = 0;
        this.transitions = new HashMap<>();
        this.startState = DEFAULT_START_STATE;
        this.acceptState = DEFAULT_ACCEPT_STATE;
        this.steps = 0;

        // Initialize the symbol mappings
        this.charToSymbol = new HashMap<>();
        this.symbolToChar = new HashMap<>();

        // Setup standard symbol mappings
        initializeSymbolMappings();

        // Initialize the transitions map
        for (Transition t : transitionList) {
            String key = t.getFromState() + "," + t.getReadSymbol();
            transitions.put(key, t);
        }
    }

    /**
     * Initializes the default symbol mappings between characters and their integer representations.
     */
    private void initializeSymbolMappings() {
        // Standard symbols required by the TM encoding
        addSymbolMapping('0', SYMBOL_ZERO);  // X₁ for symbol 0
        addSymbolMapping('1', SYMBOL_ONE);   // X₂ for symbol 1
        addSymbolMapping('␣', SYMBOL_BLANK); // X₃ for blank

        // Common extended symbols
        addSymbolMapping('X', SYMBOL_X);
        addSymbolMapping('Y', SYMBOL_Y);
        addSymbolMapping('Z', SYMBOL_Z);
        addSymbolMapping('#', SYMBOL_HASH);

        // Add lowercase alphabet (map to symbols 8-33)
        for (char c = 'a'; c <= 'z'; c++) {
            if (c != 'x' && c != 'y' && c != 'z') { // Skip x,y,z as they map to X,Y,Z
                addSymbolMapping(c, 8 + (c - 'a'));
            }
        }

        // Alias lowercase x,y,z to uppercase X,Y,Z
        charToSymbol.put('x', SYMBOL_X);
        charToSymbol.put('y', SYMBOL_Y);
        charToSymbol.put('z', SYMBOL_Z);
    }

    /**
     * Adds a bidirectional mapping between a character and its integer symbol representation.
     *
     * @param c           The character.
     * @param symbolValue The integer value representing the symbol.
     */
    private void addSymbolMapping(char c, int symbolValue) {
        charToSymbol.put(c, symbolValue);
        symbolToChar.put(symbolValue, c);
    }

    /**
     * Initializes the tape with the specified input string.
     *
     * @param input The input string to initialize the tape with.
     */
    public void initialize(String input) {
        tape.clear();
        headPosition = 0;
        steps = 0;
        currentState = startState;

        // Convert the input to the tape representation
        for (char c : input.toCharArray()) {
            Integer symbolValue = charToSymbol.get(c);
            if (symbolValue != null) {
                tape.add(symbolValue);
            } else {
                // For any unknown character, assign a new symbol value
                int newSymbolValue = charToSymbol.size() + 1;
                addSymbolMapping(c, newSymbolValue);
                tape.add(newSymbolValue);
                System.out.println("Note: Added new symbol mapping for '" + c + "' → X" + newSymbolValue);
            }
        }
    }

    /**
     * Converts a tape symbol (integer) to its character representation.
     *
     * @param symbol The integer symbol.
     * @return The character representation of the symbol, or '?' if unknown.
     */
    private char getCharForSymbol(int symbol) {
        Character c = symbolToChar.get(symbol);
        if (c != null) {
            return c;
        }
        return '?'; // Unknown symbol
    }

    /**
     * Returns the display character for a position on the tape, handling positions outside the tape.
     *
     * @param position The position on the tape.
     * @return The character at that position, or blank if the position is outside the tape.
     */
    private char getDisplaySymbol(int position) {
        if (position < 0 || position >= tape.size()) {
            return '␣'; // Show blanks for positions outside tape
        }
        return getCharForSymbol(tape.get(position));
    }

    /**
     * Gets the symbol at the specified position on the tape.
     *
     * @param position The position on the tape.
     * @return The symbol at that position, or blank if the position is outside the tape.
     */
    private int getSymbolAt(int position) {
        if (position >= 0 && position < tape.size()) {
            return tape.get(position);
        }
        return SYMBOL_BLANK; // Blank
    }

    /**
     * Returns a formatted representation of the tape for display, showing a fixed number
     * of cells on either side of the head position.
     *
     * @return A string representing the tape with the head position marked.
     */
    public String getTapeDisplay() {
        StringBuilder sb = new StringBuilder();

        int startPos = headPosition - DISPLAY_CELLS;
        int endPos = headPosition + DISPLAY_CELLS;

        for (int i = startPos; i <= endPos; i++) {
            if (i == headPosition) {
                sb.append("[").append(getDisplaySymbol(i)).append("]");
            } else {
                sb.append(getDisplaySymbol(i));
            }
        }

        return sb.toString();
    }

    /**
     * Executes one step of the Turing Machine.
     *
     * @return true if a transition was executed, false if the machine has halted.
     */
    public boolean step() {
        // Get the current symbol under the head
        int currentSymbol = getSymbolAt(headPosition);

        // Look up the transition
        String key = currentState + "," + currentSymbol;
        Transition t = transitions.get(key);

        // If no transition, the machine halts
        if (t == null) {
            return false;
        }

        // Update the state
        currentState = t.getToState();

        // Write the new symbol
        // Ensure the tape is big enough
        while (headPosition >= tape.size()) {
            tape.add(SYMBOL_BLANK); // Add blanks
        }

        if (headPosition < 0) {
            // Extend the tape to the left if needed
            List<Integer> newTape = new ArrayList<>();
            int offset = -headPosition;
            for (int i = 0; i < offset; i++) {
                newTape.add(SYMBOL_BLANK); // Add blanks
            }
            newTape.addAll(tape);
            tape = newTape;
            headPosition = 0;
        }

        tape.set(headPosition, t.getWriteSymbol());

        // Move the head
        if (t.getDirection() == 'L') {
            headPosition--;
        } else if (t.getDirection() == 'R') {
            headPosition++;
        }

        // Increment step counter
        steps++;

        return true;
    }

    /**
     * Runs the Turing Machine until it halts (no more applicable transitions).
     */
    public void run() {
        while (step()) {
            // Just keep stepping
        }
    }

    /**
     * Prints the current configuration of the Turing Machine to the console.
     */
    public void printConfiguration() {
        System.out.println("┌─────────────────────────────────────────────────────────────────┐");
        System.out.println("│ Computation State                                               │");
        System.out.println("├─────────────────────────────────────────────────────────────────┤");
        System.out.println("│ Current State: q" + currentState +
                (currentState == acceptState ? " (Accepting)" : ""));
        System.out.println("│ Step Count: " + steps);
        System.out.println("│ Head Position: " + headPosition);
        System.out.println("├─────────────────────────────────────────────────────────────────┤");

        // Create a visual representation of the tape
        System.out.println("│ Tape:                                                           │");
        System.out.println("│ " + getTapeDisplay());

        // Create a pointer to show the head position
        StringBuilder pointerLine = new StringBuilder("│ ");
        for (int i = 0; i < DISPLAY_CELLS + 1; i++) {
            pointerLine.append(" ");
        }
        pointerLine.append("^");

        System.out.println(pointerLine.toString());
        System.out.println("└─────────────────────────────────────────────────────────────────┘");
    }

    /**
     * Gets the content of the tape as a readable string, trimming leading and trailing blanks.
     *
     * @return A string representing the non-blank content of the tape.
     */
    public String getTapeContent() {
        StringBuilder sb = new StringBuilder();
        boolean foundNonBlank = false;
        int firstNonBlankIndex = -1;
        int lastNonBlankIndex = -1;

        // Find first and last non-blank symbols
        for (int i = 0; i < tape.size(); i++) {
            if (tape.get(i) != SYMBOL_BLANK) { // Not blank
                if (!foundNonBlank) {
                    foundNonBlank = true;
                    firstNonBlankIndex = i;
                }
                lastNonBlankIndex = i;
            }
        }

        if (!foundNonBlank) {
            return ""; // Empty tape
        }

        // Create the content string
        for (int i = firstNonBlankIndex; i <= lastNonBlankIndex; i++) {
            sb.append(getCharForSymbol(tape.get(i)));
        }

        return sb.toString();
    }

    /**
     * Calculates the decimal value represented by consecutive zeros in unary representation.
     *
     * @return The count of consecutive zeros from the start of the tape.
     */
    public int getUnaryZerosValue() {
        int count = 0;
        for (Integer integer : tape) {
            if (integer == SYMBOL_ZERO) {
                count++;
            } else if (integer != SYMBOL_BLANK) {
                break; // Stop counting on non-zero, non-blank
            }
        }
        return count;
    }

    /**
     * Calculates the decimal value represented by consecutive ones in unary representation.
     *
     * @return The count of consecutive ones from the start of the tape (after any blanks).
     */
    public int getUnaryOnesValue() {
        int count = 0;
        // Skip leading blanks
        int startIndex = 0;
        while (startIndex < tape.size() && tape.get(startIndex) == SYMBOL_BLANK) {
            startIndex++;
        }

        for (int i = startIndex; i < tape.size(); i++) {
            if (tape.get(i) == SYMBOL_ONE) {
                count++;
            } else if (tape.get(i) != SYMBOL_BLANK) {
                break; // Stop counting on non-one, non-blank
            }
        }
        return count;
    }

    /**
     * Checks if the current tape content contains only binary digits (0s and 1s).
     *
     * @return true if the tape contains only 0s and 1s, false otherwise.
     */
    public boolean isBinaryContent() {
        String content = getTapeContent();
        return content.matches("[01]+");
    }

    /**
     * Converts the binary content of the tape to its decimal value.
     *
     * @return The decimal value of the binary number on the tape, or -1 if not a valid binary number.
     */
    public int binaryToDecimal() {
        String content = getTapeContent();

        // Skip any non-binary characters
        if (!isBinaryContent()) {
            return -1; // Not a binary number
        }

        try {
            return Integer.parseInt(content, 2);
        } catch (NumberFormatException e) {
            return -1; // Too large or invalid
        }
    }

    /**
     * Checks if the Turing Machine is in an accepting state.
     *
     * @return true if the current state is the accepting state, false otherwise.
     */
    public boolean isAccepted() {
        return currentState == acceptState;
    }

    /**
     * Gets the current state of the Turing Machine.
     *
     * @return The current state.
     */
    public int getCurrentState() {
        return currentState;
    }

    /**
     * Gets the accepting state of the Turing Machine.
     *
     * @return The accepting state.
     */
    public int getAcceptState() {
        return acceptState;
    }

    /**
     * Gets the current head position on the tape.
     *
     * @return The head position.
     */
    public int getHeadPosition() {
        return headPosition;
    }

    /**
     * Gets the current step count of the Turing Machine.
     *
     * @return The number of steps executed.
     */
    public int getSteps() {
        return steps;
    }

    /**
     * Sets the tape to a new list of symbols.
     *
     * @param newTape The new tape content
     */
    public void setTape(List<Integer> newTape) {
        this.tape = newTape;
    }
}