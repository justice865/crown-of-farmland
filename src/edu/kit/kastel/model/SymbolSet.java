package edu.kit.kastel.model;

/**
 * The SymbolSet class represents a set of symbols used for rendering the board.
 * @author uqibg
 */
public class SymbolSet {
    private static final String STANDARD_SYMBOLS = "+-|=N#";

    private final String symbols;
    private final boolean isStandard;

    /**
     * Creates the standard symbol set.
     */
    public SymbolSet() {
        this.symbols = STANDARD_SYMBOLS;
        this.isStandard = true;
    }

    /**
     * Creates a custom symbol set. Custom sets must have exactly 29 symbols.
     * @param symbols the string containing the symbols for the custom set
     * @throws InvalidSymbolSetFormatException if the provided symbol set is null or does not have exactly 29 characters
     */
    public SymbolSet(String symbols) throws InvalidSymbolSetFormatException {
        if (symbols == null) {
            throw new InvalidSymbolSetFormatException("ERROR: Symbol set cannot be null");
        }
        if (symbols.length() != 29) {
            throw new InvalidSymbolSetFormatException("ERROR: Symbol set must be exactly 29 characters.");
        }
        this.symbols = symbols;
        this.isStandard = false;
    }

    private char standardCorner(boolean selected) {
        return selected ? symbols.charAt(5) : symbols.charAt(0);
    }

    private char standardHLine(boolean selected) {
        return selected ? symbols.charAt(3) : symbols.charAt(1);
    }

    private char standardVLine(boolean selected) {
        return selected ? symbols.charAt(4) : symbols.charAt(2);
    }

    /**
     * Gets the symbol for the corner of a field, depending on whether it is selected or not.
     * @param selected indicates whether the field is selected or not
     * @return the symbol for the corner of the field
     */
    public char cornerTopLeft(boolean selected) {
        if (isStandard) {
            return standardCorner(selected);
        }
        return selected ? symbols.charAt(11) : symbols.charAt(0);
    }

    /**
     * Gets the symbol for the corner of a field, depending on whether it is selected or not.
     * @param selected indicates whether the field is selected or not
     * @return the symbol for the corner of the field
     */
    public char cornerTopRight(boolean selected) {
        if (isStandard) {
            return standardCorner(selected);
        }
        return selected ? symbols.charAt(12) : symbols.charAt(1);
    }

    /**
     * Gets the symbol for the corner of a field, depending on whether it is selected or not.
     * @param selected indicates whether the field is selected or not
     * @return the symbol for the corner of the field
     */
    public char cornerBottomLeft(boolean selected) {
        if (isStandard) {
            return standardCorner(selected);
        }
        return selected ? symbols.charAt(13) : symbols.charAt(2);
    }

    /**
     * Gets the symbol for the corner of a field, depending on whether it is selected or not.
     * @param selected indicates whether the field is selected or not
     * @return the symbol for the corner of the field
     */
    public char cornerBottomRight(boolean selected) {
        if (isStandard) {
            return standardCorner(selected);
        }
        return selected ? symbols.charAt(14) : symbols.charAt(3);
    }

    /**
     * Gets the symbol for the edge of a field, depending on the adjacency and whether it is selected or not.
     * @param adjacency the direction of adjacency for the edge
     * @return the symbol for the edge of the field
     */
    public char midEdgeTop(Direction adjacency) {
        if (isStandard) {
            return standardCorner(adjacency != Direction.NONE);
        }
        return switch (adjacency) {
            case LEFT -> symbols.charAt(15);
            case RIGHT -> symbols.charAt(16);
            case NONE -> symbols.charAt(4);
            default -> symbols.charAt(4);
        };
    }

    /**
     * Gets the symbol for the edge of a field, depending on the adjacency and whether it is selected or not.
     * @param adjacency the direction of adjacency for the edge
     * @return the symbol for the edge of the field
     */
    public char midEdgeRight(Direction adjacency) {
        if (isStandard) {
            return standardCorner(adjacency != Direction.NONE);
        }
        return switch (adjacency) {
            case UP -> symbols.charAt(17);
            case DOWN -> symbols.charAt(18);
            case NONE -> symbols.charAt(5);
            default -> symbols.charAt(5);
        };
    }

    /**
     * Gets the symbol for the edge of a field, depending on the adjacency and whether it is selected or not.
     * @param adjacency the direction of adjacency for the edge
     * @return the symbol for the edge of the field
     */
    public char midEdgeBottom(Direction adjacency) {
        if (isStandard) {
            return standardCorner(adjacency != Direction.NONE);
        }
        return switch (adjacency) {
            case LEFT -> symbols.charAt(19);
            case RIGHT -> symbols.charAt(20);
            case NONE -> symbols.charAt(6);
            default -> symbols.charAt(6);
        };
    }

    /**
     * Gets the symbol for the edge of a field, depending on the adjacency and whether it is selected or not.
     * @param adjacency the direction of adjacency for the edge
     * @return the symbol for the edge of the field
     */
    public char midEdgeLeft(Direction adjacency) {
        if (isStandard) {
            return standardCorner(adjacency != Direction.NONE);
        }
        return switch (adjacency) {
            case UP -> symbols.charAt(21);
            case DOWN -> symbols.charAt(22);
            case NONE -> symbols.charAt(7);
            default -> symbols.charAt(7);
        };
    }

    /**
     * Gets the symbol for the central junction of a field,
     * depending on the adjacency and whether it is selected or not.
     * @param adjacency the direction of adjacency for the junction
     * @return the symbol for the central junction of the field
     */
    public char centralJunction(Direction adjacency) {
        if (isStandard) {
            return standardCorner(adjacency != Direction.NONE);
        }
        return switch (adjacency) {
            case TOP_LEFT -> symbols.charAt(25);
            case TOP_RIGHT -> symbols.charAt(26);
            case BOTTOM_LEFT -> symbols.charAt(27);
            case BOTTOM_RIGHT -> symbols.charAt(28);
            case NONE -> symbols.charAt(10);
            default -> symbols.charAt(10);
        };
    }

    /**
     * Gets the symbol for a horizontal line, depending on whether it is selected or not.
     * @param selected indicates whether the line is selected or not
     * @return the symbol for the horizontal line
     */
    public char hLine(boolean selected) {
        if (isStandard) {
            return standardHLine(selected);
        }
        return selected ? symbols.charAt(23) : symbols.charAt(8);
    }

    /**
     * Gets the symbol for a vertical line, depending on whether it is selected or not.
     * @param selected indicates whether the line is selected or not
     * @return the symbol for the vertical line
     */
    public char verticalLine(boolean selected) {
        if (isStandard) {
            return standardVLine(selected);
        }
        return selected ? symbols.charAt(24) : symbols.charAt(9);
    }
}
