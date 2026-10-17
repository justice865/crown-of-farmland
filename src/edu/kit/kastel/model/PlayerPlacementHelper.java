package edu.kit.kastel.model;

import edu.kit.kastel.FieldOrUnitNotExistingException;
import edu.kit.kastel.InvalidCardIndexException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper class to manage player placements on the board.
 * It validates the placement and handles the logic for placing units based on the player's cards and the target field.
 * @author uqibg
 */
public class PlayerPlacementHelper {
    private final PlayerTeam player;

    /**
     * Constructor for PlayerPlacementHelper.
     * @param player the player for whom the placement is being managed
     */
    public PlayerPlacementHelper(PlayerTeam player) {
        this.player = player;
    }

    /**
     * Places the specified cards on the board.
     * @param cardIndices the indices of the cards in the player's hand that are to be placed
     * @return a list of PlaceResult objects representing the outcome of each placement
     * @throws FieldOrUnitNotExistingException if the target field is invalid for placement
     * @throws InvalidCardIndexException if any of the provided card indices are invalid or if there are duplicates
     */
    public List<PlaceResult> place(int... cardIndices)
            throws FieldOrUnitNotExistingException, InvalidCardIndexException {
        validateCardIndices(cardIndices);
        Field targetField = validatePlacementTarget();

        List<UnitType> selectedCards = collectSelectedCards(cardIndices);
        removeSelectedCardsFromHand(cardIndices);

        List<PlaceResult> results = new ArrayList<>();
        for (UnitType cardType : selectedCards) {
            placeSingleCard(cardType, targetField, results);
        }

        player.setAlreadyPlaced(true);
        return results;
    }

    private void validateCardIndices(int[] cardIndices) throws InvalidCardIndexException {
        if (cardIndices == null || cardIndices.length == 0) {
            throw new InvalidCardIndexException("ERROR: No card indices provided.");
        }

        int handSize = player.getCards().size();
        for (int i = 0; i < cardIndices.length; i++) {
            if (cardIndices[i] < 0 || cardIndices[i] >= handSize) {
                throw new InvalidCardIndexException("ERROR: Invalid card index: " + cardIndices[i]);
            }
            for (int j = i + 1; j < cardIndices.length; j++) {
                if (cardIndices[i] == cardIndices[j]) {
                    throw new InvalidCardIndexException("ERROR: Duplicate card index: " + cardIndices[i]);
                }
            }
        }
    }

    private Field validatePlacementTarget() throws FieldOrUnitNotExistingException {
        Field targetField = player.getSelectedField();
        if (targetField == null) {
            throw new FieldOrUnitNotExistingException("ERROR: No field selected.");
        }

        FieldPlaceable fieldInfo = player.getBoard().getAt(targetField);
        if (fieldInfo.playerKing() || fieldInfo.cpuKing()) {
            throw new FieldOrUnitNotExistingException("ERROR: Cannot place on king field.");
        }

        if (!targetField.isAdjacentIncludingDiagonals(player.getBoard().getPlayerKingPosition())) {
            throw new FieldOrUnitNotExistingException("ERROR: edu.kit.kastel.model.Field not adjacent to king.");
        }

        Unit existingUnit = fieldInfo.unit();
        if (existingUnit != null && existingUnit.getTeamIndicator() != player.getTeamIndicator()) {
            throw new FieldOrUnitNotExistingException("ERROR: Cannot place on enemy unit.");
        }

        return targetField;
    }

    private int[] sortIndicesDescending(int[] cardIndices) {
        int[] sorted = Arrays.copyOf(cardIndices, cardIndices.length);
        for (int i = 1; i < sorted.length; i++) {
            int key = sorted[i];
            int j = i - 1;
            while (j >= 0 && sorted[j] < key) {
                sorted[j + 1] = sorted[j];
                j--;
            }
            sorted[j + 1] = key;
        }
        return sorted;
    }

    private List<UnitType> collectSelectedCards(int[] cardIndices) {
        List<UnitType> selectedCards = new ArrayList<>(cardIndices.length);
        for (int cardIndex : cardIndices) {
            selectedCards.add(player.getCards().get(cardIndex));
        }
        return selectedCards;
    }

    private void removeSelectedCardsFromHand(int[] cardIndices) {
        for (int cardIndex : sortIndicesDescending(cardIndices)) {
            player.getCards().remove(cardIndex);
        }
    }

    private void placeSingleCard(UnitType cardType, Field targetField, List<PlaceResult> results) {
        Unit currentUnit = player.getBoard().getAt(targetField).unit();

        if (currentUnit != null && currentUnit.getTeamIndicator() == player.getTeamIndicator()) {
            handlePlacementOnFriendlyUnit(cardType, currentUnit, targetField, results);
            return;
        }

        handlePlacementOnEmptyField(cardType, targetField, results);
    }

    private void handlePlacementOnFriendlyUnit(UnitType cardType, Unit currentUnit,
                                               Field targetField, List<PlaceResult> results) {
        UnitType existingType = currentUnit.getUnitType();
        UnitType mergedType = cardType.mergeWith(existingType);

        if (!mergedType.equals(cardType)) {
            Unit newUnit = new Unit(mergedType, player.getTeamIndicator());
            player.getBoard().placeUnit(targetField, newUnit);
            boolean mergeSuccess = !mergedType.equals(cardType);
            results.add(new PlaceResult(cardType, existingType, true, mergeSuccess, !mergeSuccess, targetField));
            return;
        }

        results.add(new PlaceResult(cardType, existingType, true, false, true, targetField));
    }

    private void handlePlacementOnEmptyField(UnitType cardType, Field targetField,
                                             List<PlaceResult> results) {
        if (player.getBoardUnitCount() >= 5) {
            results.add(new PlaceResult(cardType, null, false, false, true, targetField));
            return;
        }

        Unit newUnit = new Unit(cardType, player.getTeamIndicator());
        player.getBoard().placeUnit(targetField, newUnit);
        player.incrementBoardUnitCount();
        results.add(new PlaceResult(cardType, targetField));
    }
}
