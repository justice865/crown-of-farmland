package edu.kit.kastel;

/**
 * Exception thrown when the conditions for yielding have not been met.
 * @author uqibg
 */
public class YieldConditionsHaveNotMetException extends Exception {
    /**
     * Constructs a new YieldConditionsHaveNotMetException with the specified detail message.
     * @param message the detail message explaining why the yield conditions have not been met
     */
    public YieldConditionsHaveNotMetException(String message) {
        super(message);
    }
}

