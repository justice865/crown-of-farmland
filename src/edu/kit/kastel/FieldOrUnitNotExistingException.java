package edu.kit.kastel;

/**
 * Exception thrown when a specified field or unit does not exist in the game.
 * @author uqibg
 */
public class FieldOrUnitNotExistingException extends Exception {
    /**
     * Constructs a new FieldOrUnitNotExistingException with the specified detail message.
     * @param message the detail message explaining the reason for the exception
     */
    public FieldOrUnitNotExistingException(String message) {
        super(message);
    }
}
