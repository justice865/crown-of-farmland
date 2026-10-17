package edu.kit.kastel.model;

/**
 * The record FieldPlaceable represents the content of a field on the board.
 * It can contain a unit, and it can indicate whether the player's king or the CPU's king is present on that field.
 * @author uqibg
 * @param unit
 * @param playerKing
 * @param cpuKing
 */
public record FieldPlaceable(
        Unit unit,
        boolean playerKing,
        boolean cpuKing
) {
    /**
     * Checks if the field is the friendly king's position.
     * @return true if the field is the friendly king's position, false otherwise
     */
    public boolean isFriendlyKing() {
        return playerKing;
    }

    /**
     * Checks if the field is the enemy king's position.
     * @return true if the field is the enemy king's position, false otherwise
     */
    public boolean isEnemyKing() {
        return cpuKing;
    }
}