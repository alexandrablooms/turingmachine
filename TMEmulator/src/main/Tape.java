package main;

import java.util.HashMap;

/**
 * Tape class represents the tape of a Turing machine.
 * It allows reading and writing symbols, moving the head, and getting the content of the tape.
 */
public class Tape {
    private HashMap<Integer, Character> tapeContents; // position -> symbol mapping
    private int headPosition; // current position of the head
    private char blankSymbol; // symbol for blank spaces ('_')

    /**
     * Constructor for the Tape class to create a new tape with initial content
     * @param initialContent The initial content of the tape
     * @param blankSymbol The blank symbol to use for empty cells
     */
    public Tape(String initialContent, char blankSymbol) {
        this.tapeContents = new HashMap<>();
        this.headPosition = 0;
        this.blankSymbol = blankSymbol;

        // Initialize the tape with the initial content
        for (int i = 0; i < initialContent.length(); i++) {
            tapeContents.put(i, initialContent.charAt(i));
        }
    }

    /**
     * Read the symbol at the current head position
     * @return The symbol at the current head position
     */
    public char readSymbol() {
        return tapeContents.getOrDefault(headPosition, blankSymbol);
    }

    /**
     * Write a symbol at the current head position
     * @param symbol The symbol to write
     */
    public void writeSymbol(char symbol) {
        tapeContents.put(headPosition, symbol);
    }

    /**
     * Move the head to the left.
     */
    public void moveLeft() {
        headPosition--;
    }

    /**
     * Move the head to the right.
     */
    public void moveRight() {
        headPosition++;
    }

    /**
     * Get tape content with a specified number of symbols before and after the head position
     * @param symbolsBefore Number of symbols to include before the head
     * @param symbolsAfter Number of symbols to include after the head
     * @return A string containing the tape content
     */
    public String getTapeContent(int symbolsBefore, int symbolsAfter) {
        StringBuilder content = new StringBuilder();

        // Add symbols before head position
        for (int i = headPosition - symbolsBefore; i < headPosition; i++) {
            content.append(tapeContents.getOrDefault(i, blankSymbol));
        }

        // Add current symbol
        content.append(tapeContents.getOrDefault(headPosition, blankSymbol));

        // Add symbols after head position
        for (int i = headPosition + 1; i <= headPosition + symbolsAfter; i++) {
            content.append(tapeContents.getOrDefault(i, blankSymbol));
        }

        return content.toString();
    }

    /**
     * Get tape content before the head position
     * @param symbolsBefore Number of symbols to include before the head
     * @return A string containing the tape content before the head
     */
    public String getTapeContentBefore(int symbolsBefore) {
        StringBuilder content = new StringBuilder();

        // Add symbols before head position
        for (int i = headPosition - symbolsBefore; i < headPosition; i++) {
            content.append(tapeContents.getOrDefault(i, blankSymbol));
        }

        return content.toString();
    }

    /**
     * Get tape content after the head position (including the current symbol)
     * @param symbolsAfter Number of symbols to include after the head
     * @return A string containing the current symbol and the tape content after the head
     */
    public String getTapeContentAfter(int symbolsAfter) {
        StringBuilder content = new StringBuilder();

        // Add symbols after head position (excluding current)
        for (int i = headPosition + 1; i <= headPosition + symbolsAfter; i++) {
            content.append(tapeContents.getOrDefault(i, blankSymbol));
        }

        return content.toString();
    }

    /**
     * Get the current head position
     * @return The current head position
     */
    public int getHeadPosition() {
        return headPosition;
    }

    /**
     * Get the smallest position on the tape that contains a non-blank symbol
     * @return The smallest position with a non-blank symbol
     */
    public int getMinPosition() {
        if (tapeContents.isEmpty()) {
            return 0;
        }
        return tapeContents.keySet().stream().min(Integer::compare).orElse(0);
    }

    /**
     * Get the largest position on the tape that contains a non-blank symbol
     * @return The largest position with a non-blank symbol
     */
    public int getMaxPosition() {
        if (tapeContents.isEmpty()) {
            return 0;
        }
        return tapeContents.keySet().stream().max(Integer::compare).orElse(0);
    }

    /**
     * Create a copy of the tape
     * @return A new Tape object with the same content and head position
     */
    public Tape copyTape() {
        Tape newTape = new Tape("", blankSymbol);
        newTape.headPosition = this.headPosition;
        newTape.tapeContents = new HashMap<>(this.tapeContents);
        return newTape;
    }
}



