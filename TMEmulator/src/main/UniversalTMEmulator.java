package main;

public class UniversalTMEmulator {
    private TuringMachine turingMachine; // currrently loaded turing machine
    private TMConfiguration currentConfig; // the current configuration

    enum Mode {
        STEP_BY_STEP, RUN_MODE
    }

    public UniversalTMEmulator(String binaryEncodedTM, String input) {
        turingMachine = TMParser.parseTuringMachine(binaryEncodedTM);
        currentConfig = turingMachine.createInitialConfiguration(input);
    }

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

    public int executeRun() {
        int stepCount = 0;
        while (executeStep()) {
            stepCount++;
        }
        return stepCount;
    }

    public void displayState() {
        // current state
        System.out.println("Current state: " + currentConfig.getCurrentState());

        // tape content
        Tape tape = currentConfig.getTape();
        String tapeContent = tape.getTapeContent(15, 15);
        System.out.println("Tape: " + tapeContent);

        // head position
        // print state q before the symbol it's reading
        int headPosition = tape.getHeadPosition();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < headPosition; i++) {
            sb.append(tape.readSymbol());
        }
        sb.append("q");
        sb.append(currentConfig.getCurrentState());
        sb.append(tape.readSymbol());
        for (int i = headPosition + 1; i < tapeContent.length(); i++) {
            sb.append(tape.readSymbol());
        }
        System.out.println("Head position: " + sb.toString());

        // step count
        System.out.println("Steps executed: " + currentConfig.getStepCount());

        // show results if halted
        if (turingMachine.isHalted(currentConfig)) {
            if (turingMachine.isAccepting(currentConfig)) {
                System.out.println("Result: ACCEPTED");
            } else {
                System.out.println("Result: REJECTED");
            }
        }
    }

    public void run(Mode mode) {
        switch (mode) {
            case STEP_BY_STEP:
                // Execute steps one at a time with display after each
                System.out.println("Initial configuration:");
                displayState();

                java.util.Scanner scanner = new java.util.Scanner(System.in);
                while (executeStep()) {
                    displayState();
                    System.out.println("Press Enter for next step...");
                    scanner.nextLine(); // Wait for user input
                }

                // Show final state
                System.out.println("Final configuration:");
                displayState();
                break;

            case RUN_MODE:
                // Run until halt
                int steps = executeRun();
                System.out.println("Execution completed in " + steps + " steps.");
                displayState();
                break;
        }
    }

    public boolean isAccepted() {
        return turingMachine.isAccepting(currentConfig);
    }

    public boolean isHalted() {
        return turingMachine.isHalted(currentConfig);
    }
}
