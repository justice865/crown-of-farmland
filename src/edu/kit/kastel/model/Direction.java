package edu.kit.kastel.model;

/**
 * The Enum here represents possible directions on the board related to fields.
 * @author uqibg
 *
 */
public enum Direction {
    /**
     * The direction that represents not moving.
     */
    NONE,
    /**
     * The direction that represents moving left.
     */
    LEFT,
    /**
     * The direction that represents moving right.
     */
    RIGHT,
    /**
     * The direction that represents moving up.
     */
    UP,
    /**
     * The direction that represents moving down.
     */
    DOWN,
    /**
     * The direction that represents moving diagonally up and left.
     */
    TOP_LEFT,
    /**
     * The direction that represents moving diagonally up and right.
     */
    TOP_RIGHT,
    /**
     * The direction that represents moving diagonally down and left.
     */
    BOTTOM_LEFT,
    /**
     * The direction that represents moving diagonally down and right.
     */
    BOTTOM_RIGHT
}

