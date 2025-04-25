package main;

import java.util.Scanner;

/**
 * UniversalTMEmulator class implements a universal Turing Machine emulator
 * that can simulate any Turing Machine given its binary encoding
 */
 public class UniversalTMEmulator {
    private TuringMachine turingMachine; // currrently loaded turing machine
    private TMConfiguration currentConfig; // the current configuration
    private static final int DISPLAY_TAPE_WIDTH = 15; // number of symbols to display on each side of the head

    /**
     * Execution modes for the Turing Machine
     */
    enum Mode {
        STEP_BY_STEP, // Execute one step at a time with user interaction
        RUN_MODE // Execute all steps without interruption
    }

    /**
     * Constructor for the Universal TM Emulator
     * @param binaryEncodedTM The binary encoding of the Turing Machine
     * @param input The input string for the Turing Machine
     */
    public UniversalTMEmulator(String binaryEncodedTM, String input) {
        turingMachine = TMParser.parseTuringMachine(binaryEncodedTM);
        currentConfig = turingMachine.createInitialConfiguration(input);
    }

    /**
     * Execute a single step of the Turing Machine
     * @return true if the step was executed successfully, false if the machine has halted
     */
    public boolean executeStep() {
        if (turingMachine.isHalted(currentConfig)) {
            return false; // machine has halted
        }

        TMConfiguration nextConfig = turingMachine.step(currentConfig);

        // check if machine has halted
        if (turingMachine.isHalted(nextConfig)) {
            return false;
        }

        // update the current configuration
        currentConfig = nextConfig;
        return true;
    }

    /**
     * Execute the Turing Machine until it halts
     * @return The number of steps executed
     */
    public int executeRun() {
        int stepCount = 0;
        while (executeStep()) {
            stepCount++;

            // safety measure to prevent infinite loops
            if (stepCount > 1000000) {
                System.out.println("Warning: Execution limit reached (1,000,000 steps). The machine may be in an infinite loop.");
                return stepCount;
            }
        }
        return stepCount;
    }

    /**
     * Display the current state of the Turing Machine
     * This includes the current state, tape content, head position, and step count
     */
    public void displayState() {
        // a) Display the result if halted
        if (turingMachine.isHalted(currentConfig)) {
            if (turingMachine.isAccepting(currentConfig)) {
                System.out.println("Result: ACCEPTED");
            } else {
                System.out.println("Result: REJECTED");
            }
            System.out.println("Machine halted.");
        }

        // b) Display current state
        System.out.println("Current state: " + currentConfig.getCurrentState());

        // c) Display tape content with proper spacing around head
        Tape tape = currentConfig.getTape();
        int headPos = tape.getHeadPosition();
        String tapeContent = tape.getTapeContent(DISPLAY_TAPE_WIDTH, DISPLAY_TAPE_WIDTH);
        System.out.println("Tape: " + tapeContent);

        // d) Display head position with state marker
        StringBuilder headPositionIndicator = new StringBuilder();
        for (int i = 0; i < DISPLAY_TAPE_WIDTH; i++) {
            headPositionIndicator.append(" ");
        }
        headPositionIndicator.append("^");
        System.out.println("Head: " + headPositionIndicator);

        // Display the configuration in the format X₁...Xᵢ₋₁qXᵢXᵢ₊₁...Xₙ
        String beforeHead = tape.getTapeContentBefore(DISPLAY_TAPE_WIDTH);
        char currentSymbol = tape.readSymbol();
        String afterHead = tape.getTapeContentAfter(DISPLAY_TAPE_WIDTH);

        System.out.println("Configuration: " + beforeHead + currentConfig.getCurrentState() +
                currentSymbol + afterHead);

        // e) Display step count
        System.out.println("Steps executed: " + currentConfig.getStepCount());
    }

    /**
     * Run the Turing Machine in the specified mode
     * @param mode The execution mode (STEP_BY_STEP or RUN_MODE)
     */
    public void run(Mode mode) {
        switch (mode) {
            case STEP_BY_STEP:
                runStepByStep();
                break;
            case RUN_MODE:
                runContinuous();
                break;
        }
    }

    /**
     * Run the Turing Machine in step-by-step mode
     */
    private void runStepByStep() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nStarting step-by-step execution:");
        System.out.println("Initial configuration:");
        displayState();

        while (true) {
            System.out.print("Press ENTER for next step or 'q' to quit: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("q")) {
                System.out.println("Execution terminated by user.");
                break;
            }

            if (!executeStep()) {
                System.out.println("\nFinal configuration (machine halted):");
                displayState();
                break;
            }

            displayState();
        }
    }

    /**
     * Run the Turing Machine continuously until it halts
     */
    private void runContinuous() {
        System.out.println("\nStarting continuous execution...");
        System.out.println("Initial configuration:");
        displayState();

        int steps = executeRun();

        System.out.println("\nExecution completed in " + steps + " steps.");
        System.out.println("Final configuration:");
        displayState();
    }

    /**
     * Check if the current configuration is accepted
     * @return true if the current configuration is accepted, false otherwise
     */
    public boolean isAccepted() {
        return turingMachine.isAccepting(currentConfig);
    }

    /**
     * Check if the Turing Machine has halted
     * @return true if the Turing Machine has halted, false otherwise
     */
    public boolean isHalted() {
        return turingMachine.isHalted(currentConfig);
    }
}
