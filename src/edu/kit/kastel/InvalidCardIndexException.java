package edu.kit.kastel;

/**
 * Exception thrown when an invalid card index is accessed in the game.
 * @author uqibg
 */
public class InvalidCardIndexException extends Exception {
    /**
     * Constructs a new InvalidCardIndexException with the specified detail message.
     * @param message the detail message explaining the reason for the exception
     */
    public InvalidCardIndexException(String message) {
        super(message);
    }
}

