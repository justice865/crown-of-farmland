package edu.kit.kastel.model;

/**
 * The MoveDecision record represents a decision to move a unit to a target field.
 * @author uqibg
 * @param unit the unit that is being moved
 * @param targetField the field to which the unit is being moved
 * @param isBlock indicates whether the move is a blocking move
 * @param isEnPlace indicates whether the move is an en place move
 * @param moveIndex the index of the move in a sequence of moves, used for ordering moves
 */
public record MoveDecision(Unit unit, Field targetField, boolean isBlock, boolean isEnPlace, int moveIndex) {
}

