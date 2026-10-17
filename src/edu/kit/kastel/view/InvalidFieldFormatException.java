package edu.kit.kastel.view;

/**
 * Exception thrown when a field format is invalid, such as when parsing user input for field coordinates.
 * @author uqibg
 */
public class InvalidFieldFormatException extends Exception {
    /**
     * Constructor for InvalidFieldFormatException.
     * @param message The error message describing the reason for the exception.
     */
    public InvalidFieldFormatException(String message) {
        super(message);
    }
}
