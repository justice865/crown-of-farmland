package edu.kit.kastel.view;

/**
 * Exception thrown when an invalid symbol set is encountered in the view layer.
 * @author uqibg
 */
public class InvalidSymbolSetException extends Exception {
    /**
     * Constructs a new InvalidSymbolSetException with the specified detail message.
     * @param message The detail message explaining the reason for the exception
     */
    public InvalidSymbolSetException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidSymbolSetException with the specified detail message and cause.
     * @param message The detail message explaining the reason for the exception
     * @param cause The cause of the exception
     */
    public InvalidSymbolSetException(String message, Throwable cause) {
        super(message, cause);
    }
}

