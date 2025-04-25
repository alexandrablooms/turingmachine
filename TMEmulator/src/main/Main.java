package main;

import java.util.Scanner;

/**
 * Main class for the Universal Turing Machine Emulator
 * This class handles user interaction and runs the TM emulator
 */
 public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Main method to run the Universal TM Emulator
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("===== Universal Turing Machine Emulator =====");
        System.out.println("=========================================");

        // Get the TM encoding
        String tmEncoding = getTuringMachineEncoding();

        // Get the input
        String input = getInput();

        // Create the emulator
        UniversalTMEmulator emulator = new UniversalTMEmulator(tmEncoding, input);

        // Get execution mode
        UniversalTMEmulator.Mode mode = getMode();

        // Run the emulator
        emulator.run(mode);

        // Display final result
        System.out.println("\nExecution completed.");
        if (emulator.isAccepted()) {
            System.out.println("The input was ACCEPTED.");
        } else {
            System.out.println("The input was REJECTED.");
        }
    }

    /**
     * Method to get TM encoding (from file, direct binary input, or decimal input)
     * @return The binary encoding of the Turing Machine
     */
    private static String getTuringMachineEncoding() {
        while (true) {
            System.out.println("\nSelect input method for TM encoding:");
            System.out.println("1. Direct binary input");
            System.out.println("2. From file");
            System.out.println("3. Decimal number (will be converted to binary)");
            System.out.print("Choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        System.out.print("Enter binary encoding: ");
                        String binaryEncoding = scanner.nextLine().trim();
                        if (isValidBinaryString(binaryEncoding)) {
                            return binaryEncoding;
                        } else {
                            System.out.println("Invalid binary string. Please use only 0s and 1s.");
                        }
                        break;
                    case 2:
                        System.out.print("Enter filename: ");
                        String filename = scanner.nextLine().trim();
                        try {
                            String fileContent = readFromFile(filename);
                            if (fileContent != null && isValidBinaryString(fileContent)) {
                                return fileContent;
                            } else {
                                System.out.println("Invalid binary string in file. Please check the file content.");
                            }
                        } catch (Exception e) {
                            System.out.println("Error reading file: " + e.getMessage());
                            System.out.println("Please try again.");
                        }
                        break;
                    case 3:
                        System.out.print("Enter decimal number: ");
                        try {
                            long decimalNumber = Long.parseLong(scanner.nextLine().trim());
                            String binaryString = convertDecimalToBinary(decimalNumber);
                            System.out.println("Converted to binary: " + binaryString);
                            return binaryString;
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid decimal number. Please try again.");
                        }
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    /**
     * Checks if a string contains only 0s and 1s
     * @param str The string to check
     * @return true if the string is a valid binary string, false otherwise
     */
    private static boolean isValidBinaryString(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.matches("[01]+");
    }

    /**
     * Converts a decimal number to its binary representation
     * @param decimal The decimal number to convert
     * @return The binary representation as a string
     */
    private static String convertDecimalToBinary(long decimal) {
        return Long.toBinaryString(decimal);
    }

    /**
     * Method to get input for the TM
     * @return The input string for the Turing Machine
     */
    private static String getInput() {
        System.out.print("\nEnter input for the Turing Machine: ");
        return scanner.nextLine().trim();
    }

    /**
     * Method to select execution mode
     * @return The selected execution mode
     */
    private static UniversalTMEmulator.Mode getMode() {
        while (true) {
            System.out.println("\nSelect execution mode:");
            System.out.println("1. Step-by-step mode");
            System.out.println("2. Run mode");
            System.out.print("Choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice == 1) {
                    return UniversalTMEmulator.Mode.STEP_BY_STEP;
                } else if (choice == 2) {
                    return UniversalTMEmulator.Mode.RUN_MODE;
                } else {
                    System.out.println("Invalid choice. Please enter 1 or 2.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    /**
     * Reads the contents of a file and returns it as a string
     * @param filename The name of the file to read
     * @return The contents of the file as a string, or null if an error occurred
     */
    private static String readFromFile(String filename) {
        try {
            // Create a path to the file
            java.nio.file.Path path = java.nio.file.Paths.get(filename);

            // Read all bytes from the file and convert to string
            byte[] bytes = java.nio.file.Files.readAllBytes(path);
            String content = new String(bytes).trim();

            System.out.println("Successfully read " + content.length() +
                    " characters from file: " + filename);
            return content;
        } catch (java.io.IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return null;
        }
    }
}
