package main;

import java.util.HashMap;

public class Tape {
    private HashMap<Integer, Character> tapeContents; // int = position, char = symbol
    private int headPosition; // current position of the head
    private char blankSymbol; // symbol for blank spaces ('_')

    /**
     * Constructor for the Tape class to create a new tape with initial content.
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
     * Read the symbol at the current head position.
     */
    public char readSymbol() {
        return tapeContents.getOrDefault(headPosition, blankSymbol);
    }

    /**
     * Write a symbol at the current head position.
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
     * Get a view of the tape content around the current head position.
     */
    public String getTapeContent(int symbolsBefore, int symbolsAfter) {
        StringBuilder content = new StringBuilder();
        int start = headPosition - symbolsBefore;
        int end = headPosition + symbolsAfter;
        for (int i = start; i <= end; i++) {
            content.append(tapeContents.getOrDefault(i, blankSymbol));
        }
        return content.toString();
    }

    /**
     * Get the current head position.
     */
    public int getHeadPosition() {
        return headPosition;
    }

    /**
     * A copy of the tape.
     */
    public Tape copy() {
        Tape newTape = new Tape("", blankSymbol);
        newTape.headPosition = this.headPosition;
        newTape.tapeContents = new HashMap<>(this.tapeContents);
        return newTape;
    }
}



