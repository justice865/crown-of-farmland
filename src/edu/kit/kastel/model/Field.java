package edu.kit.kastel.model;

/**
 * The record Field represents a field on the board. It has a row and a column.
 * @author uqibg
 * @param row the row of the field, with 1 being the bottom row and 7 being the top row
 * @param column the column of the field, with 'A' being the leftmost column and 'G' being the rightmost column
 */
public record Field(int row, char column) {
    /**
     * Calculates the distance to another field using Manhattan distance.
     * @param other the other field to calculate the distance to
     * @return the Manhattan distance between this field and the other field
     */
    public int distanceTo(Field other) {
        return Math.abs(row - other.row) + Math.abs(column - other.column);
    }

    /**
     * Checks if this field is adjacent to another field, including diagonals.
     * @param other the other field to check adjacency with
     * @return true if this field is adjacent to the other field (including diagonals), false otherwise
     */
    public boolean isAdjacentIncludingDiagonals(Field other) {
        return Math.abs(row - other.row()) <= 1 && Math.abs(column - other.column()) <= 1;
    }

    /**
     * Calculates the field that is in the given direction from this field.
     * If the direction is invalid, it returns this field.
     * @param direction the direction to calculate the offset for
     * @return the field that is in the given direction from this field.
     */
    public Field offset(Direction direction) {
        return switch (direction) {
            case DOWN    -> new Field(row - 1, column);
            case UP  -> new Field(row + 1, column);
            case LEFT  -> new Field(row, (char) (column - 1));
            case RIGHT -> new Field(row, (char) (column + 1));
            case BOTTOM_LEFT -> new Field(row - 1, (char) (column - 1));
            case BOTTOM_RIGHT -> new Field(row - 1, (char) (column + 1));
            case TOP_LEFT -> new Field(row + 1, (char) (column - 1));
            case TOP_RIGHT -> new Field(row + 1, (char) (column + 1));
            default -> this;
        };

    }

}
