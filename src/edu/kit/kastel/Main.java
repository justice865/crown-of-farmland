package edu.kit.kastel;
import edu.kit.kastel.view.DeckConfigurationException;

/**
 * Main class for the game.
 * @author uqibg
 */
public final class Main {
    private Main() {
    }

    /**
     * Entry point of the application.
     * @param args Command line arguments in the format of key=value pairs
     */
    public static void main(String[] args) {
        try {
            new edu.kit.kastel.MainRunner().run(args);
        } catch (DeckConfigurationException e) {
            System.out.println(e.getMessage());
        }
    }
}
