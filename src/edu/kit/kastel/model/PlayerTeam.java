package edu.kit.kastel.model;

import edu.kit.kastel.FieldOrUnitNotExistingException;
import edu.kit.kastel.InvalidCardIndexException;
import edu.kit.kastel.YieldConditionsHaveNotMetException;

import java.util.List;

/**
 * The PlayerTeam class represents the player's team in the game.
 * @author uqibg
 */
public class PlayerTeam extends Team {
    private boolean yieldLocked;
    private final PlayerPlacementHelper placementHelper;
    private final PlayerMoveHelper moveHelper;

    /**
     * Constructor for PlayerTeam.
     * @param team the team indicator for the player team
     */
    public PlayerTeam(TeamIndicator team) {
        super(team);
        this.yieldLocked = false;
        this.placementHelper = new PlayerPlacementHelper(this);
        this.moveHelper = new PlayerMoveHelper(this);
    }

    /**
     * Selects a field on the board for the player team.
     * @param field the field to select
     * @throws FieldOrUnitNotExistingException if the field does not exist on the board
     */
    public void select(Field field) throws FieldOrUnitNotExistingException {
        if (field == null) {
            throw new FieldOrUnitNotExistingException("ERROR: Field does not exist.");
        }

        if (field.row() < 1 || field.row() > 7 || field.column() < 'A' || field.column() > 'G') {
            throw new FieldOrUnitNotExistingException("ERROR: Field does not exist.");
        }

        setSelectedField(field);
    }

    /**
     * Places a card from the player's hand onto the board at the selected field.
     * @param cardIndices the indices of the cards in the player's hand to place
     * @return a list of PlaceResult objects representing the results of the placement
     * @throws FieldOrUnitNotExistingException if the selected field does not exist or is invalid for placement
     * @throws InvalidCardIndexException if any of the provided card indices are invalid
     */
    public List<PlaceResult> place(int... cardIndices)
            throws FieldOrUnitNotExistingException, InvalidCardIndexException {
        if (yieldLocked) {
            if (getCards().size() == 5) {
                throw new FieldOrUnitNotExistingException("ERROR: you must discard!");
            } else {
                throw new FieldOrUnitNotExistingException("ERROR: cannot place a card.");
            }
        }

        if (isAlreadyPlaced()) {
            throw new FieldOrUnitNotExistingException("ERROR: Can only place once per turn.");
        }

        return placementHelper.place(cardIndices);
    }

    /**
     * Moves a unit from the selected field to the target field.
     * @param targetField the field to move the unit to
     * @return a MoveResult object representing the result of the move
     * @throws FieldOrUnitNotExistingException if the selected field or target field does not exist, or if the move is invalid
     */
    public MoveResult move(Field targetField) throws FieldOrUnitNotExistingException {
        if (yieldLocked) {
            if (getCards().size() == 5) {
                throw new FieldOrUnitNotExistingException("ERROR: cannot move a unit, you must discard!");
            } else {
                throw new FieldOrUnitNotExistingException("ERROR: cannot move a unit.");
            }
        }

        MoveResult result = moveHelper.move(targetField);
        if (result != null && result.mergeAttempted() && result.mergeSuccessful() && result.resultField() != null) {
            Unit mergedUnit = getBoard().getAt(result.resultField()).unit();
            if (mergedUnit != null && mergedUnit.getTeamIndicator() == this.getTeamIndicator()) {
                mergedUnit.setHasMoved(false);
            }
        }
        return result;
    }

    /**
     * Blocks a unit on the target field, preventing it from moving for the rest of the turn.
     * @param targetField the field of the unit to block
     * @throws FieldOrUnitNotExistingException if the target field does not exist, does not have a unit,
     *                                          has a king, has an enemy unit,
     *                                          or if the unit has already moved this turn
     */
    public void block(Field targetField) throws FieldOrUnitNotExistingException {
        if (targetField == null) {
            throw new FieldOrUnitNotExistingException("ERROR: Field does not exist.");
        }

        FieldPlaceable fieldInfo = getBoard().getAt(targetField);

        if (fieldInfo.playerKing() || fieldInfo.cpuKing()) {
            throw new FieldOrUnitNotExistingException("ERROR: Kings cannot block.");
        }

        Unit targetUnit = fieldInfo.unit();
        if (targetUnit == null) {
            throw new FieldOrUnitNotExistingException("ERROR: Field does not have a unit.");
        }

        if (targetUnit.getTeamIndicator() != this.getTeamIndicator()) {
            throw new FieldOrUnitNotExistingException("ERROR: Cannot block enemy units.");
        }

        if (targetUnit.hasMoved()) {
            throw new FieldOrUnitNotExistingException("ERROR: Unit has already moved this turn.");
        }

        targetUnit.setBlocked(true);
        targetUnit.setHasMoved(true);
    }

    /**
     * Flips a unit on the target field, revealing its stats and allowing it to move and attack as normal.
     * @param targetField the field of the unit to flip
     * @return the Unit that was flipped
     * @throws FieldOrUnitNotExistingException if the target field does not exist, does not have a unit,
     *                                          has a king, has an enemy unit,
     */
    public Unit flip(Field targetField) throws FieldOrUnitNotExistingException {
        if (targetField == null) {
            throw new FieldOrUnitNotExistingException("ERROR: Field does not exist.");
        }

        FieldPlaceable fieldInfo = getBoard().getAt(targetField);

        if (fieldInfo.playerKing() || fieldInfo.cpuKing()) {
            throw new FieldOrUnitNotExistingException("ERROR: Kings cannot be flipped.");
        }

        Unit targetUnit = fieldInfo.unit();
        if (targetUnit == null) {
            throw new FieldOrUnitNotExistingException("ERROR: Field does not have a unit.");
        }

        if (targetUnit.getTeamIndicator() != this.getTeamIndicator()) {
            throw new FieldOrUnitNotExistingException("ERROR: Cannot flip enemy units.");
        }

        if (targetUnit.hasMoved()) {
            throw new FieldOrUnitNotExistingException("ERROR: Unit has already moved this turn.");
        }

        if (!targetUnit.isFaceDown()) {
            throw new FieldOrUnitNotExistingException("ERROR: Unit is already face up.");
        }

        targetUnit.setFaceDown(false);
        return targetUnit;
    }

    /**
     * Yields the turn to the enemy team. If the player's hand is full, the player must discard a card before yielding.
     * @throws YieldConditionsHaveNotMetException if the player cannot yield due to having a full hand without discarding,
     *                                              or if the player tries to discard when not required
     */
    public void yield() throws YieldConditionsHaveNotMetException {
        if (getCards().size() == 5) {
            yieldLocked = true;
            throw new YieldConditionsHaveNotMetException("ERROR: Player's hand is full!");
        }
        performYield();
    }

    /**
     * Yields the turn to the enemy team by discarding a card from the player's hand.
     * This method should only be called if the player's hand is full.
     * @param discardIndex the index of the card in the player's hand to discard
     * @throws YieldConditionsHaveNotMetException if the player's hand is not full, or if the player tries to discard when not required
     * @throws InvalidCardIndexException if the provided discard index is invalid
     */
    public void yield(int discardIndex) throws YieldConditionsHaveNotMetException, InvalidCardIndexException {
        if (getCards().size() < 5) {
            yieldLocked = true;
            throw new YieldConditionsHaveNotMetException("ERROR: Cannot discard when hand is not full.");
        }
        discard(discardIndex);
        performYield();
    }

    private void performYield() {
        yieldLocked = false;
        setAlreadyPlaced(false);
        setSelectedField(null);

        for (Field field : getBoard().getAllFields()) {
            Unit unit = getBoard().getAt(field).unit();
            if (unit != null && unit.getTeamIndicator() == this.getTeamIndicator()) {
                unit.setHasMoved(true);
            }
        }
        getBoard().setPlayerKingHasMoved(true);
        getGame().switchTurn();
    }
}
