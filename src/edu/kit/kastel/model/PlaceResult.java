package edu.kit.kastel.model;

/**
 * The PlaceResult record encapsulates the result of a placement action in the game.
 * @param placedUnit the unit that was placed on the field
 * @param existingUnit the unit that was already on the field before the placement, if any
 * @param mergeAttempted indicates whether a merge was attempted as a result of the placement
 * @param mergeSuccessful indicates whether the merge was successful, if a merge was attempted
 * @param unitEliminated indicates whether a unit was eliminated as a result of the placement
 * @param targetField the field on which the unit was placed
 * @author uqibg
 */
public record PlaceResult(UnitType placedUnit, UnitType existingUnit, boolean mergeAttempted,
                          boolean mergeSuccessful, boolean unitEliminated, Field targetField) {
    /**
     * Constructor for PlaceResult record that initializes all fields with the provided values.
     * @param placedUnit the unit that was placed on the field
     * @param targetField the field on which the unit was placed
     */
    public PlaceResult(UnitType placedUnit, Field targetField) {
        this(placedUnit, null, false, false, false, targetField);
    }
}

