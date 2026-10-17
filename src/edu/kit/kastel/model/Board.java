package edu.kit.kastel.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The class representing the board that contains the units on the fields, including farmer kings.
 * @author uqibg
 */
public class Board {

    private Field playerKing = new Field(1, 'D');
    private Field cpuKing = new Field(7, 'D');
    private boolean playerKingHasMoved = false;
    private boolean cpuKingHasMoved = true;

    private final Map<Field, Unit> board = new HashMap<>();

    /**
     * Gets the information of a possible unit on the given field or if the field is occupied by a king.
     * @param field the row and column of the position to check
     * @return a FieldPlaceable record that keeps the information needed
     */
    public FieldPlaceable getAt(Field field) {
        if (field.equals(playerKing)) {
            return new FieldPlaceable(null, true, false);
        } else if (field.equals(cpuKing)) {
            return new FieldPlaceable(null, false, true);
        } else {
            return new FieldPlaceable(this.board.get(field), false, false);
        }
    }

    /**
     * A getter for the position of the player's king.
     * @return the position of the player's king
     */
    public Field getPlayerKingPosition() {
        return playerKing;
    }

    /**
     * A getter for the position of the CPU's king.
     * @return the position of the CPU's king
     */
    public Field getCpuKingPosition() {
        return cpuKing;
    }

    /**
     * A setter for the position of the player's king.
     * @param newPosition the new position of the player's king
     */
    public void setPlayerKingPosition(Field newPosition) {
        this.playerKing = newPosition;
    }

    /**
     * A setter for the position of the CPU's king.
     * @param newPosition the new position of the CPU's king
     */
    public void setCpuKingPosition(Field newPosition) {
        this.cpuKing = newPosition;
    }

    /**
     * Appoints a unit to a field on the board.
     * @param field the row and column of the position to place the unit on
     * @param unit the unit to be placed on the field
     */
    public void placeUnit(Field field, Unit unit) {
        board.put(field, unit);
    }

    /**
     * Checks if the player's king has moved this turn.
     * @return true if the player's king has moved this turn, false otherwise
     */
    public boolean isPlayerKingHasMoved() {
        return playerKingHasMoved;
    }

    /**
     * Sets whether the player's king has moved this turn.
     * @param hasMoved true if the player's king has moved this turn, false otherwise
     */
    public void setPlayerKingHasMoved(boolean hasMoved) {
        this.playerKingHasMoved = hasMoved;
    }

    /**
     * Checks if the CPU's king has moved this turn.
     * @return true if the CPU's king has moved this turn, false otherwise
     */
    public boolean isCpuKingHasMoved() {
        return cpuKingHasMoved;
    }

    /**
     * Sets whether the CPU's king has moved this turn.
     * @param hasMoved true if the CPU's king has moved this turn, false otherwise
     */
    public void setCpuKingHasMoved(boolean hasMoved) {
        this.cpuKingHasMoved = hasMoved;
    }

    /**
     * Gets a set of all fields that have units placed on them.
     * @return a set of all fields that have units placed on them
     */
    public java.util.Set<Field> getAllFields() {
        return board.keySet();
    }

}
