package edu.kit.kastel.view;

import edu.kit.kastel.model.UnitType;

import java.util.List;

/**
 * Command to display the cards in the player's hand.
 * @author uqibg
 */
public class HandCommand extends Command<Void> {
    /**
     * Constructor for HandCommand.
     * @param commandHandler The CommandHandler that will handle the execution of this command.
     */
    protected HandCommand(CommandHandler commandHandler) {
        super(commandHandler);
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            System.out.println("ERROR: 'hand' command takes no arguments.");
            return;
        }
        execute((Void) null);
    }

    @Override
    protected void execute(Void args) {
        List<UnitType> cards = commandHandler.getGame().getPlayer().getCards();
        if (cards == null || cards.isEmpty()) {
            return;
        }

        for (int i = 0; i < cards.size(); i++) {
            UnitType card = cards.get(i);
            String name = card.qualifier() + " " + card.role();
            System.out.println("[" + (i + 1) + "] " + name + " (" + card.attack() + "/" + card.defense() + ")");
        }
    }
}
