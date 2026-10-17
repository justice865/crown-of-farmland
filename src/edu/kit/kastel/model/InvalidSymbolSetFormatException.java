package edu.kit.kastel.model;

/**
 * Exception thrown when the symbol set format is invalid.
 * @author uqibg
 */
public class InvalidSymbolSetFormatException extends Exception {
    /**
     * Constructs a new InvalidSymbolSetFormatException with the specified detail message.
     * @param message the detail message
     */
    public InvalidSymbolSetFormatException(String message) {
        super(message);
    }
}

