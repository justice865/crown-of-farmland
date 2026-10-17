package edu.kit.kastel.model;

/**
 * The MoveResult record represents the result of a move in the game.
 * @param resultField the field that is the result of the move
 * @param enPlace indicates whether the move was an en place move
 * @param damageToEnemy the amount of damage dealt to the enemy as a result of the move
 * @param damageToSelf the amount of damage dealt to the player's own unit as a result of the move
 * @param duelOccurred indicates whether a duel occurred as a result of the move
 * @param eliminatedUnitName the name of the unit that was eliminated as a result of the move, if any
 * @param wasBlocked indicates whether the move was blocked by an enemy unit
 * @param mergeAttempted indicates whether a merge was attempted as a result of the move
 * @param mergeSuccessful indicates whether the merge was successful, if a merge was attempted
 * @param duelResult the result of the duel, if a duel occurred
 * @param movingUnit the unit that was moved
 * @param targetUnit the unit that was the target of the move, if any
 * @param sourceField the field from which the unit was moved
 * @param isBlock indicates whether the move was a blocking move
 * @author uqibg
 */
public record MoveResult(Field resultField, boolean enPlace, int damageToEnemy, int damageToSelf,
                         boolean duelOccurred, String eliminatedUnitName, boolean wasBlocked,
                         boolean mergeAttempted, boolean mergeSuccessful, DuelResult duelResult,
                         Unit movingUnit, Unit targetUnit, Field sourceField, boolean isBlock) {
    /**
     * Constructs a MoveResult with the specified result field and en place status, and default values for all other parameters.
     * @param resultField the field that is the result of the move
     * @param enPlace indicates whether the move was an en place move
     */
    public MoveResult(Field resultField, boolean enPlace) {
        this(resultField, enPlace, 0, 0, false, null, false, false, false, null, null, null, null, false);
    }

    /**
     * Constructs a MoveResult with the specified result field, en place status,
     * and eliminated unit name, and default values for all other parameters.
     * @param resultField the field that is the result of the move
     * @param enPlace indicates whether the move was an en place move
     * @param eliminatedUnitName the name of the unit that was eliminated as a result of the move, if any
     */
    public MoveResult(Field resultField, boolean enPlace, String eliminatedUnitName) {
        this(resultField, enPlace, 0, 0, false, eliminatedUnitName, false, false, false,
                null, null, null, null, false);
    }
}
