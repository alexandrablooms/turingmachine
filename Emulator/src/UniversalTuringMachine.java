import java.util.*;
import java.io.*;

public class UniversalTuringMachine {
    // Class to represent a transition
    private static class Transition {
        int fromState;
        int readSymbol;
        int toState;
        int writeSymbol;
        char direction;

        public Transition(int fromState, int readSymbol, int toState, int writeSymbol, char direction) {
            this.fromState = fromState;
            this.readSymbol = readSymbol;
            this.toState = toState;
            this.writeSymbol = writeSymbol;
            this.direction = direction;
        }

        @Override
        public String toString() {
            return "δ(q" + fromState + ", X" + readSymbol + ") = (q" + toState + ", X" + writeSymbol + ", " + direction + ")";
        }
    }

    // Class to represent the Turing Machine
    private static class TuringMachine {
        int currentState;
        List<Integer> tape;
        int headPosition;
        Map<String, Transition> transitions;
        int startState;
        int acceptState;
        int steps;

        public TuringMachine(List<Transition> transitionList) {
            this.currentState = 1; // q₁ is the start state
            this.tape = new ArrayList<>();
            this.headPosition = 0;
            this.transitions = new HashMap<>();
            this.startState = 1;
            this.acceptState = 2; // q₂ is the accepting state
            this.steps = 0;

            // Initialize the transitions map
            for (Transition t : transitionList) {
                String key = t.fromState + "," + t.readSymbol;
                transitions.put(key, t);
            }
        }

        // Initialize the tape with input
        public void initialize(String input) {
            tape.clear();
            headPosition = 0;
            steps = 0;
            currentState = startState;

            // Convert the input to the tape representation
            for (char c : input.toCharArray()) {
                if (c == '0') {
                    tape.add(1); // X₁ for symbol 0
                } else if (c == '1') {
                    tape.add(2); // X₂ for symbol 1
                }
            }
        }

        // Helper function to convert a tape symbol to a character
        private char symbolToChar(int symbol) {
            switch (symbol) {
                case 1: return '0';
                case 2: return '1';
                default: return '␣';
            }
        }

        // Get the symbol at a given position on the tape
        private int getSymbolAt(int position) {
            if (position >= 0 && position < tape.size()) {
                return tape.get(position);
            }
            return 3; // Blank
        }

        // Get a formatted representation of the tape for display
        public String getTapeDisplay() {
            StringBuilder sb = new StringBuilder();

            // Calculate display range (at least 15 cells before and after head)
            int startPos = Math.max(0, headPosition - 15);
            int endPos = headPosition + 15;

            // Ensure we have enough cells in our display
            while (endPos >= tape.size()) {
                tape.add(3); // Add blanks as needed
            }

            // Create the display string
            for (int i = startPos; i <= endPos; i++) {
                if (i == headPosition) {
                    sb.append("[").append(symbolToChar(getSymbolAt(i))).append("]");
                } else {
                    sb.append(symbolToChar(getSymbolAt(i)));
                }
            }

            return sb.toString();
        }

        // Execute one step of the Turing Machine
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
            currentState = t.toState;

            // Write the new symbol
            // Ensure the tape is big enough
            while (headPosition >= tape.size()) {
                tape.add(3); // Add blanks
            }

            if (headPosition < 0) {
                // Extend the tape to the left if needed
                List<Integer> newTape = new ArrayList<>();
                int offset = -headPosition;
                for (int i = 0; i < offset; i++) {
                    newTape.add(3); // Add blanks
                }
                newTape.addAll(tape);
                tape = newTape;
                headPosition = 0;
            }

            // Now we can safely write to the tape
            tape.set(headPosition, t.writeSymbol);

            // Move the head
            if (t.direction == 'L') {
                headPosition--;
            } else if (t.direction == 'R') {
                headPosition++;
            }

            // Increment step counter
            steps++;

            return true;
        }

        // Run until halting
        public void run() {
            while (step()) {
                // Just keep stepping
            }
        }

        // Print the current configuration
        public void printConfiguration() {
            System.out.println("┌─────────────────────────────────────────────────────────────────┐");
            System.out.println("│ Computation State                                               │");
            System.out.println("├─────────────────────────────────────────────────────────────────┤");
            System.out.println("│ Current State: q" + currentState +
                    (currentState == acceptState ? " (Accepting)" : ""));
            System.out.println("│ Step Count: " + steps);
            System.out.println("│ Head Position: " + headPosition);
            System.out.println("├─────────────────────────────────────────────────────────────────┤");

            // Create a visual representation of the tape with at least 15 elements before and after
            System.out.println("│ Tape:                                                           │");
            System.out.println("│ " + getTapeDisplay() + "│");

            // Create a pointer to show the head position
            StringBuilder pointerLine = new StringBuilder("│ ");
            int displayStartPos = Math.max(0, headPosition - 15);
            int pointerPosition = headPosition - displayStartPos;

            for (int i = 0; i < pointerPosition * 1 + 1; i++) {
                pointerLine.append(" ");
            }
            pointerLine.append("^");

            // Pad the rest of the line
            while (pointerLine.length() < 67) {
                pointerLine.append(" ");
            }
            pointerLine.append("│");

            System.out.println(pointerLine.toString());
            System.out.println("└─────────────────────────────────────────────────────────────────┘");
        }

        // Get the tape content as a readable string
        public String getTapeContent() {
            StringBuilder sb = new StringBuilder();
            boolean foundNonBlank = false;
            int lastNonBlankIndex = -1;

            // Find the last non-blank symbol
            for (int i = 0; i < tape.size(); i++) {
                if (tape.get(i) != 3) { // Not blank
                    foundNonBlank = true;
                    lastNonBlankIndex = i;
                }
            }

            if (!foundNonBlank) {
                return ""; // Empty tape
            }

            // Create the content string
            for (int i = 0; i <= lastNonBlankIndex; i++) {
                sb.append(symbolToChar(tape.get(i)));
            }

            return sb.toString();
        }

        // Check if in accepting state
        public boolean isAccepted() {
            return currentState == acceptState;
        }
    }

    // Helper function to parse a sequence of zeros
    private static int parseZeros(String encoding, int startIndex) {
        int count = 0;
        int i = startIndex;
        while (i < encoding.length() && encoding.charAt(i) == '0') {
            count++;
            i++;
        }
        return count;
    }

    // Parse a binary string encoding of a Turing machine
    private static List<Transition> parseTuringMachine(String encoding) {
        List<Transition> transitions = new ArrayList<>();

        int i = 0;
        while (i < encoding.length()) {
            // Skip the separator "11" if present
            if (i + 1 < encoding.length() && encoding.charAt(i) == '1' && encoding.charAt(i + 1) == '1') {
                i += 2;
                continue;
            }

            // Parse a transition
            if (i < encoding.length() && encoding.charAt(i) == '0') {
                int fromState = parseZeros(encoding, i);
                i += fromState; // Skip the zeros

                if (i < encoding.length() && encoding.charAt(i) == '1') {
                    i++; // Skip the separator '1'

                    int readSymbol = parseZeros(encoding, i);
                    i += readSymbol; // Skip the zeros

                    if (i < encoding.length() && encoding.charAt(i) == '1') {
                        i++; // Skip the separator '1'

                        int toState = parseZeros(encoding, i);
                        i += toState; // Skip the zeros

                        if (i < encoding.length() && encoding.charAt(i) == '1') {
                            i++; // Skip the separator '1'

                            int writeSymbol = parseZeros(encoding, i);
                            i += writeSymbol; // Skip the zeros

                            if (i < encoding.length() && encoding.charAt(i) == '1') {
                                i++; // Skip the separator '1'

                                int directionCode = parseZeros(encoding, i);
                                i += directionCode; // Skip the zeros

                                // Direction: 1 for L, 2 for R
                                char direction = directionCode == 1 ? 'L' : 'R';

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

    // Convert decimal to unary (sequence of 0s)
    private static String decimalToUnary(int num) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num; i++) {
            sb.append('0');
        }
        return sb.toString();
    }

    // Convert unary input to binary encoding
    private static String unaryToBinary(String unaryInput) {
        StringBuilder binary = new StringBuilder();
        for (char c : unaryInput.toCharArray()) {
            if (c == '0') {
                binary.append("1"); // Symbol for 0
            } else if (c == '1') {
                binary.append("01"); // Symbol for 1
            }
        }
        return binary.toString();
    }

    /**
     * Main method to run the Universal Turing Machine emulator.
     * Supports two modes:
     * 1. Step mode: executes one step at a time with user confirmation
     * 2. Run mode: executes all steps automatically and shows the result
     *
     * Also supports loading from file or using predefined examples:
     * - example1: T₁ = 010010001010011000101010010110001001001010011000100010001010
     * - example2: T₂ = 1010010100100110101000101001100010010100100110001010010100
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Universal Turing Machine Emulator");
        System.out.println("=================================");

        // Get Turing machine encoding
        System.out.println("\nSelect Turing Machine input method:");
        System.out.println("1. Enter binary encoding directly");
        System.out.println("2. Load from file");
        System.out.println("3. Use example T₁ (010010001010011000101010010110001001001010011000100010001010)");
        System.out.println("4. Use example T₂ (1010010100100110101000101001100010010100100110001010010100)");

        System.out.print("\nEnter your choice (1-4): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String tmEncoding = "";

        switch (choice) {
            case 1:
                System.out.println("\nEnter the binary encoding of the Turing machine:");
                tmEncoding = scanner.nextLine().trim();
                break;
            case 2:
                System.out.println("\nEnter the file path:");
                String filePath = scanner.nextLine().trim();
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(filePath));
                    tmEncoding = reader.readLine().trim();
                    reader.close();
                    System.out.println("Successfully loaded encoding from file.");
                } catch (IOException e) {
                    System.out.println("Error reading file: " + e.getMessage());
                    System.out.println("Using example T₁ instead.");
                    tmEncoding = "010010001010011000101010010110001001001010011000100010001010";
                }
                break;
            case 3:
                tmEncoding = "010010001010011000101010010110001001001010011000100010001010";
                System.out.println("Using example T₁.");
                break;
            case 4:
                tmEncoding = "1010010100100110101000101001100010010100100110001010010100";
                System.out.println("Using example T₂.");
                break;
            default:
                System.out.println("Invalid choice. Using example T₁.");
                tmEncoding = "010010001010011000101010010110001001001010011000100010001010";
        }

        // Parse the Turing machine
        List<Transition> transitions = parseTuringMachine(tmEncoding);

        // Print the parsed transitions
        System.out.println("\nParsed Transitions:");
        for (Transition t : transitions) {
            System.out.println(t);
        }

        // Create the Turing machine
        TuringMachine tm = new TuringMachine(transitions);

        // Get input format
        System.out.println("\nSelect input format:");
        System.out.println("1. Binary string (e.g., '01101')");
        System.out.println("2. Unary number (e.g., '000' for 3)");
        System.out.println("3. Decimal number (will be converted to unary)");

        System.out.print("\nEnter your choice (1-3): ");
        int inputFormat = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.println("\nEnter the input for the Turing machine:");
        String input = scanner.nextLine().trim();

        // Process input based on selected format
        switch (inputFormat) {
            case 2: // Unary format - keep as is
                break;
            case 3: // Decimal format - convert to unary
                try {
                    int decimal = Integer.parseInt(input);
                    input = "";
                    for (int i = 0; i < decimal; i++) {
                        input += "0";
                    }
                    System.out.println("Converted to unary: " + input);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid decimal number. Using '0' instead.");
                    input = "0";
                }
                break;
            default: // Binary format (default) - keep as is
                break;
        }

        // Initialize the TM with the input
        tm.initialize(input);

        // Choose execution mode
        System.out.println("\nSelect execution mode:");
        System.out.println("1. Step mode (press Enter to proceed with each step)");
        System.out.println("2. Run mode (execute all steps and show final result)");

        System.out.print("\nEnter your choice (1-2): ");
        int mode = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Show initial configuration
        System.out.println("\nInitial configuration:");
        tm.printConfiguration();

        // Set a reasonable step limit to prevent infinite loops
        final int STEP_LIMIT = 10000;

        if (mode == 1) {
            // Step mode
            System.out.println("\nSTEP MODE: Press Enter to proceed with each step (or 'q' to quit)...");

            boolean running = true;
            while (running && tm.steps < STEP_LIMIT) {
                String command = scanner.nextLine();
                if (command.equals("q")) {
                    System.out.println("Execution cancelled by user.");
                    break;
                }

                boolean stepped = tm.step();
                System.out.println("\nAfter step " + tm.steps + ":");
                tm.printConfiguration();

                if (!stepped) {
                    System.out.println("Machine halted!");
                    running = false;
                }
            }

            if (tm.steps >= STEP_LIMIT) {
                System.out.println("Execution terminated - step limit reached!");
            }
        } else {
            // Run mode
            System.out.println("\nRUN MODE: Executing all steps automatically...");

            long startTime = System.currentTimeMillis();

            // Execute with a step limit to prevent infinite loops
            while (tm.step() && tm.steps < STEP_LIMIT) {
                // Just keep stepping
                if (tm.steps % 1000 == 0) {
                    System.out.println("Processed " + tm.steps + " steps...");
                }
            }

            long endTime = System.currentTimeMillis();

            if (tm.steps >= STEP_LIMIT) {
                System.out.println("Execution terminated - step limit reached!");
            } else {
                System.out.println("Execution completed in " + tm.steps + " steps (" +
                        (endTime - startTime) + "ms)!");
            }

            tm.printConfiguration();
        }

        // Print final result
        System.out.println("\nFINAL RESULT:");
        System.out.println("─────────────────────────────────────────────────────────────────");

        String resultContent = tm.getTapeContent();
        System.out.println("Tape content: " + (resultContent.isEmpty() ? "(empty)" : resultContent));

        if (tm.isAccepted()) {
            System.out.println("Status: ACCEPTED (ended in accepting state q" + tm.acceptState + ")");
        } else {
            System.out.println("Status: NOT ACCEPTED (ended in non-accepting state q" + tm.currentState + ")");
        }

        System.out.println("Total steps executed: " + tm.steps);
        System.out.println("─────────────────────────────────────────────────────────────────");

        scanner.close();
    }
}