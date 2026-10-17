package edu.kit.kastel.view;

/**
 * Exception thrown when there is an issue with the deck configuration, such as invalid format or missing required fields.
 * @author uqibg
 */
public class DeckConfigurationException extends Exception {
    /**
     * Constructor for DeckConfigurationException with a custom error message.
     * @param message The error message describing the issue with the deck configuration.
     */
    public DeckConfigurationException(String message) {
        super(message);
    }
}

