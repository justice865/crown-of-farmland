package edu.kit.kastel;

import edu.kit.kastel.model.UnitType;
import java.util.List;

/**
 * Bundles player and enemy deck lists for startup setup.
 * @author uqibg
 * @param enemyDeck the enemy's deck list
 * @param playerDeck the player's deck list
 */
record DeckLists(List<UnitType> playerDeck, List<UnitType> enemyDeck) {
    @Override
    public List<UnitType> playerDeck() {
        return playerDeck;
    }
    @Override
    public List<UnitType> enemyDeck() {
        return enemyDeck;
    }
}
