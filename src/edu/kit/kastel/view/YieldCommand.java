package edu.kit.kastel.view;

import edu.kit.kastel.InvalidCardIndexException;
import edu.kit.kastel.YieldConditionsHaveNotMetException;
import edu.kit.kastel.model.UnitType;

import java.util.List;

/**
 * Command for yielding the turn. Optionally,
 * the player can discard a card from their hand to meet the yield conditions.
 * @author uqibg
 */
public class YieldCommand extends Command<Integer> {
    /**
     * Creates a new YieldCommand with the given CommandHandler.
     * @param commandHandler The CommandHandler that will handle the execution of this command
     */
    protected YieldCommand(CommandHandler commandHandler) {
        super(commandHandler);
    }

    @Override
    public void execute(String[] args) {
        Integer discardIndexOrNull;
        try {
            discardIndexOrNull = parseDiscardIndex(args);
        } catch (InvalidFieldFormatException e) {
            System.out.println("ERROR: " + e.getMessage());
            return;
        }

        execute(discardIndexOrNull);
    }

    private Integer parseDiscardIndex(String[] args) throws InvalidFieldFormatException {
        if (args.length < 2 || args[1].trim().isEmpty()) {
            return null;
        }

        String token = args[1].trim();
        int oneBased;
        try {
            oneBased = Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new InvalidFieldFormatException("Invalid discard index.");
        }

        if (oneBased <= 0) {
            throw new InvalidFieldFormatException("Invalid discard index.");
        }

        return oneBased - 1;
    }

    @Override
    protected void execute(Integer discardIndexOrNull) {
        List<UnitType> cards = commandHandler.getGame().getPlayer().getCards();
        UnitType discardedCard = null;

        if (discardIndexOrNull != null) {
            if (cards == null || discardIndexOrNull < 0 || discardIndexOrNull >= cards.size()) {
                System.out.println("ERROR: Invalid discard index.");
                return;
            }
            discardedCard = cards.get(discardIndexOrNull);
        }

        try {
            if (discardIndexOrNull == null) {
                commandHandler.getGame().getPlayer().yield();
            } else {
                commandHandler.getGame().getPlayer().yield(discardIndexOrNull);
            }
        } catch (YieldConditionsHaveNotMetException | InvalidCardIndexException e) {
            System.out.println(e.getMessage());
            return;
        }

        String playerName = commandHandler.getGame().getPlayer().getName();
        String enemyName = commandHandler.getGame().getEnemy().getName();

        if (discardedCard != null) {
            String cardName = discardedCard.qualifier() + " " + discardedCard.role();
            System.out.println(playerName + " discarded " + cardName + " (" + discardedCard.attack() + "/"
                    + discardedCard.defense() + ").");
        }

        String currentTurnTeam = commandHandler.getGame().isPlayerTurn() ? playerName : enemyName;
        System.out.println("It is " + currentTurnTeam + "'s turn!");

        if (commandHandler.getGame().getEnemy().getDeck().isEmpty()) {
            System.out.println(enemyName + " has no cards left in the deck!");
        }
    }
}
