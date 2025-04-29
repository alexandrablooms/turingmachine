package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main emulator class that handles user interaction for the Universal Turing Machine.
 * <p>
 * This class provides a command-line interface for users to:
 * - Input a Turing Machine encoding (directly, from a file, or use predefined TMs)
 * - Specify input for the Turing Machine
 * - Choose an execution mode (step-by-step or automatic run)
 * - View the machine's configuration and results
 * </p>
 */
public class TMEmulator {
    private static final String TM1 = "010010001010011000101010010110001001001010011000100010001010";
    private static final String TM2 = "1010010100100110101000101001100010010100100110001010010100";

    /**
     * Maximum step limit to prevent infinite loops.
     */
    private static final int STEP_LIMIT = 1000000;

    private static final int BINARY_INPUT_FORMAT = 1;
    private static final int DECIMAL_INPUT_FORMAT = 2;
    private static final String QUIT_COMMAND = "q";

    /**
     * Main method to run the Universal Turing Machine emulator.
     * Supports two modes:
     * 1. Step mode: executes one step at a time with user confirmation
     * 2. Run mode: executes all steps automatically and shows the result
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Universal Turing Machine Emulator");
        System.out.println("=================================");

        String tmEncoding = getTuringMachineEncoding(scanner);

        // Parse the Turing machine
        List<Transition> transitions = TMParser.parseTuringMachine(tmEncoding);

        TuringMachine tm = new TuringMachine(transitions);

        // Get and set the input
        String input = getInput(scanner);
        tm.initialize(input);

        // Choose and execute the selected mode
        int mode = selectExecutionMode(scanner);

        // Show initial configuration
        System.out.println("\nInitial configuration:");
        tm.printConfiguration();

        executeSelectedMode(tm, scanner, mode);

        // Print final result
        printFinalResult(tm);

        scanner.close();
    }

    /**
     * Prompts the user to provide the Turing Machine encoding.
     *
     * @param scanner The Scanner used for input.
     * @return The binary encoding of the Turing Machine.
     */
    private static String getTuringMachineEncoding(Scanner scanner) {
        System.out.println("\nSelect Turing Machine input method:");
        System.out.println("1. Enter binary encoding directly");
        System.out.println("2. Load from file");
        System.out.println("3. Use T₁ (" + TM1 + ")");
        System.out.println("4. Use T₂ (" + TM2 + ")");

        String tmEncoding = "";
        boolean validSelection = false;

        while (!validSelection) {
            System.out.print("\nEnter your choice (1-4): ");
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.println("\nEnter the binary encoding of the Turing machine:");
                    tmEncoding = scanner.nextLine().trim();
                    validSelection = true;
                    break;
                case 2:
                    boolean fileLoaded = false;
                    while (!fileLoaded) {
                        System.out.println("\nEnter the file name or 'cancel':");
                        String fileName = scanner.nextLine().trim();

                        if (fileName.equalsIgnoreCase("cancel")) {
                            System.out.println("File loading cancelled. Please select another option.");
                            break; // Go back to main selection
                        }

                        // Try multiple possible locations
                        List<String> possiblePaths = new ArrayList<>();
                        possiblePaths.add(fileName); // Direct filename
                        possiblePaths.add("src/resources/" + fileName); // Resources directory
                        possiblePaths.add("Emulator/src/resources/" + fileName); // From project root
                        possiblePaths.add("resources/" + fileName); // Another common pattern

                        boolean found = false;
                        for (String path : possiblePaths) {
                            try {
                                Path filePath = Paths.get(path);
                                if (Files.exists(filePath)) {
                                    tmEncoding = Files.readString(filePath).trim();
                                    System.out.println("Successfully loaded encoding from: " + filePath.toAbsolutePath());
                                    fileLoaded = true;
                                    validSelection = true;
                                    found = true;
                                    break;
                                }
                            } catch (IOException e) {
                                // Continue to next path
                            }
                        }

                        if (!found) {
                            System.out.println("File not found. Tried the following locations:");
                            for (String path : possiblePaths) {
                                System.out.println("- " + Paths.get(path).toAbsolutePath());
                            }
                            System.out.println("Please specify the complete path including filename or type 'cancel':");
                            String fullPath = scanner.nextLine().trim();

                            if (fullPath.equalsIgnoreCase("cancel")) {
                                System.out.println("File loading cancelled. Please select another option.");
                                break; // Go back to main selection
                            }

                            try {
                                Path filePath = Paths.get(fullPath);

                                // Check if it's a directory instead of a file
                                if (Files.isDirectory(filePath)) {
                                    System.out.println("The specified path is a directory, not a file. Please provide a complete file path.");
                                    continue;
                                }

                                // Check if file exists
                                if (!Files.exists(filePath)) {
                                    System.out.println("File does not exist: " + filePath.toAbsolutePath());
                                    continue;
                                }

                                // Try to read the file
                                tmEncoding = Files.readString(filePath).trim();
                                System.out.println("Successfully loaded encoding from: " + filePath.toAbsolutePath());
                                fileLoaded = true;
                                validSelection = true;
                            } catch (IOException e) {
                                System.out.println("Error reading file: " + e.getMessage());
                                System.out.println("Please try again or type 'cancel'.");
                            }
                        }
                    }
                    break;
                case 3:
                    tmEncoding = TM1;
                    System.out.println("Using T₁.");
                    validSelection = true;
                    break;
                case 4:
                    tmEncoding = TM2;
                    System.out.println("Using T₂.");
                    validSelection = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please select 1-4.");
                    break;
            }
        }

        return tmEncoding;
    }

    /**
     * Gets the input format and input for the Turing Machine.
     *
     * @param scanner The Scanner used for input.
     * @return The user input converted to appropriate format.
     */
    private static String getInput(Scanner scanner) {
        System.out.println("\nSelect input format:");
        System.out.println("1. Unary number");
        System.out.println("2. Decimal number (will be converted to unary)");

        System.out.print("\nEnter your choice (1-2): ");
        int inputFormat;
        try {
            inputFormat = Integer.parseInt(scanner.nextLine().trim());
            if (inputFormat < 1 || inputFormat > 2) {
                System.out.println("Invalid choice. Using binary input mode.");
                inputFormat = BINARY_INPUT_FORMAT;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Using binary input mode.");
            inputFormat = BINARY_INPUT_FORMAT;
        }

        System.out.println("\nEnter the input for the Turing machine:");
        String input = scanner.nextLine().trim();

        // Process input based on selected format
        if (inputFormat == DECIMAL_INPUT_FORMAT) {
            try {
                int decimal = Integer.parseInt(input);
                // Convert decimal to unary (n consecutive 1s)
                StringBuilder unaryBuilder = new StringBuilder();
                for (int i = 0; i < decimal; i++) {
                    unaryBuilder.append('1');
                }
                input = unaryBuilder.toString();
                System.out.println("Converted to unary: " + input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid decimal number. Using '0' instead.");
                input = "0";
            }
        }

        return input;
    }

    /**
     * Prompts the user to select an execution mode.
     *
     * @param scanner The Scanner used for input.
     * @return The selected execution mode (1 for step mode, 2 for run mode).
     */
    private static int selectExecutionMode(Scanner scanner) {
        System.out.println("\nSelect execution mode:");
        System.out.println("1. Step mode (press Enter to proceed with each step)");
        System.out.println("2. Run mode (execute all steps and show final result)");

        System.out.print("\nEnter your choice (1-2): ");
        int mode;
        try {
            mode = Integer.parseInt(scanner.nextLine().trim());
            if (mode < 1 || mode > 2) {
                System.out.println("Invalid choice. Using run mode.");
                mode = 2;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Using run mode.");
            mode = 2;
        }

        return mode;
    }

    /**
     * Executes the selected mode (step or run).
     *
     * @param tm      The Turing Machine to execute.
     * @param scanner The Scanner used for input.
     * @param mode    The execution mode (1 for step mode, 2 for run mode).
     */
    private static void executeSelectedMode(TuringMachine tm, Scanner scanner, int mode) {
        if (mode == 1) {
            System.out.println("\nSTEP MODE: Press Enter to proceed with each step (or 'q' to quit)...");

            boolean running = true;
            while (running && tm.getSteps() < STEP_LIMIT) {
                String command = scanner.nextLine();
                if (command.equals(QUIT_COMMAND)) {
                    System.out.println("Execution cancelled by user.");
                    break;
                }

                boolean stepped = tm.step();
                System.out.println("\nAfter step " + tm.getSteps() + ":");
                tm.printConfiguration();

                if (!stepped) {
                    System.out.println("Machine halted!");
                    running = false;
                }
            }

            if (tm.getSteps() >= STEP_LIMIT) {
                System.out.println("Execution terminated - step limit reached!");
            }
        } else {
            System.out.println("\nRUN MODE: Executing all steps automatically...");

            long startTime = System.currentTimeMillis();

            // Execute with a step limit to prevent infinite loops
            while (tm.step() && tm.getSteps() < STEP_LIMIT) {
                if (tm.getSteps() % 1000 == 0) {
                    System.out.println("Processed " + tm.getSteps() + " steps...");
                }
            }

            long endTime = System.currentTimeMillis();

            if (tm.getSteps() >= STEP_LIMIT) {
                System.out.println("Execution terminated - step limit reached!");
            } else {
                System.out.println("Execution completed in " + tm.getSteps() + " steps (" +
                        (endTime - startTime) + "ms)!");
            }

            tm.printConfiguration();
        }
    }

    /**
     * Prints the final result of the Turing Machine execution.
     *
     * @param tm The Turing Machine whose results should be printed.
     */
    private static void printFinalResult(TuringMachine tm) {
        System.out.println("\nFINAL RESULT:");
        System.out.println("─────────────────────────────────────────────────────────────────");

        String resultContent = tm.getTapeContent();
        System.out.println("Tape content: " + (resultContent.isEmpty() ? "(empty)" : resultContent));

        // Check if the result looks like a binary number
        if (tm.isBinaryContent()) {
            int binaryDecimalValue = tm.binaryToDecimal();
            if (binaryDecimalValue >= 0) {
                System.out.println("Binary-to-decimal value: " + binaryDecimalValue);
            }
        }

        // Check for unary representation (either zeros or ones) and display decimal value
        int unaryZerosValue = tm.getUnaryZerosValue();
        int unaryOnesValue = tm.getUnaryOnesValue();

        // Remove any remaining spaces for pattern matching
        String contentNoSpaces = resultContent.replaceAll("␣", "").trim();

        if (unaryZerosValue > 0 && contentNoSpaces.matches("0+")) {
            System.out.println("Unary decimal value (count of consecutive zeros): " + unaryZerosValue);
        }

        if (unaryOnesValue > 0 && contentNoSpaces.matches("1+")) {
            System.out.println("Unary decimal value (count of consecutive ones): " + unaryOnesValue);
        } else if (contentNoSpaces.contains("1")) {
            // Count all ones in the result if they're not consecutive at the beginning
            long oneCount = contentNoSpaces.chars().filter(ch -> ch == '1').count();
            System.out.println("Total count of all ones: " + oneCount);
        }

        if (tm.isAccepted()) {
            System.out.println("Status: ACCEPTED (ended in accepting state q" + tm.getAcceptState() + ")");
        } else {
            System.out.println("Status: NOT ACCEPTED (ended in non-accepting state q" + tm.getCurrentState() + ")");
        }

        System.out.println("Total steps executed: " + tm.getSteps());
        System.out.println("─────────────────────────────────────────────────────────────────");
    }
}