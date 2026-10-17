package edu.kit.kastel.view;

import edu.kit.kastel.model.Board;
import edu.kit.kastel.model.Direction;
import edu.kit.kastel.model.Field;
import edu.kit.kastel.model.FieldPlaceable;
import edu.kit.kastel.model.SymbolSet;
import edu.kit.kastel.model.TeamIndicator;
import edu.kit.kastel.model.Unit;
import edu.kit.kastel.model.Verbosity;

/**
 * The BoardRenderer class is responsible for rendering the game board as a string representation.
 * @author uqibg
 */
public class BoardRenderer {
    /**
     * Renders the board as a string based on the provided parameters.
     * @param board the board to render
     * @param selectedField the currently selected field
     * @param verbosity the verbosity level (ALL or COMPACT) that determines how much detail to show in the rendering
     * @param symbols the symbol set to use for rendering the board and units
     * @return a string representation of the board based on the provided parameters
     */
    public String render(Board board, Field selectedField, Verbosity verbosity, SymbolSet symbols) {
        StringBuilder output = new StringBuilder();
        boolean isCompact = verbosity == Verbosity.COMPACT;
        for (int row = 7; row >= 1; row--) {
            if (!isCompact) {
                output.append(renderSeparatorLine(row, selectedField, symbols));
                output.append("\n");
            }
            output.append(renderContentLine(row, board, selectedField, symbols));
            output.append("\n");
        }
        if (!isCompact) {
            output.append(renderSeparatorLine(0, selectedField, symbols));
            output.append("\n");
        }
        output.append(renderCoordinateLine());

        return output.toString();
    }

    private String renderSeparatorLine(int row, Field selectedField, SymbolSet symbols) {
        StringBuilder line = new StringBuilder();
        line.append("  ");
        for (int col = 0; col <= 7; col++) {
            Direction adjacency = getSelectedAdjacency(row, col, selectedField);
            char symbol = getIntersectionSymbol(row, col, adjacency, symbols);
            line.append(symbol);
            if (col < 7) {
                boolean isSelectedEdge = isHorizontalEdgeSelected(row, col, selectedField);
                char horizontalLine = symbols.hLine(isSelectedEdge);
                line.append(horizontalLine).append(horizontalLine).append(horizontalLine);
            }
        }

        return line.toString();
    }

    private String renderContentLine(int row, Board board, Field selectedField, SymbolSet symbols) {
        StringBuilder line = new StringBuilder();
        line.append(String.format("%d", row));
        line.append(" ");
        boolean isLeftEdgeSelected = isVerticalEdgeSelected(row, 0, selectedField);
        line.append(symbols.verticalLine(isLeftEdgeSelected));
        for (int col = 1; col <= 7; col++) {
            Field field = new Field(row, (char) ('A' + col - 1));
            String token = getCellToken(board, field);
            line.append(token);
            boolean isRightEdgeSelected = isVerticalEdgeSelected(row, col, selectedField);
            line.append(symbols.verticalLine(isRightEdgeSelected));
        }

        return line.toString();
    }

    private String getCellToken(Board board, Field field) {
        FieldPlaceable fieldPlaceable = board.getAt(field);
        if (fieldPlaceable.isFriendlyKing()) {
            return board.isPlayerKingHasMoved() ? " X " : "*X ";
        }
        if (fieldPlaceable.isEnemyKing()) {
            return board.isCpuKingHasMoved() ? " Y " : "*Y ";
        }
        Unit unit = fieldPlaceable.unit();
        if (unit != null) {
            StringBuilder token = new StringBuilder();
            if (!unit.hasMoved()) {
                token.append("*");
            } else {
                token.append(" ");
            }
            token.append(unit.getTeamIndicator() == TeamIndicator.PLAYER ? "x" : "y");
            if (unit.isBlocked()) {
                token.append("b");
            } else {
                token.append(" ");
            }

            return token.toString();
        }

        return "   ";
    }

    private Direction getSelectedAdjacency(int row, int col, Field selectedField) {
        if (selectedField == null) {
            return Direction.NONE;
        }
        int selRow = selectedField.row();
        int selCol = selectedField.column() - 'A' + 1;
        boolean topLeft = row == selRow && col == selCol - 1;
        boolean topRight = row == selRow && col == selCol;
        boolean bottomLeft = row == selRow - 1 && col == selCol - 1;
        boolean bottomRight = row == selRow - 1 && col == selCol;

        if (row == 0 || row == 7) {
            if (row == 0) {
                if (bottomLeft) {
                    return Direction.RIGHT;
                }
                if (bottomRight) {
                    return Direction.LEFT;
                }
            } else {
                if (topLeft) {
                    return Direction.RIGHT;
                }
                if (topRight) {
                    return Direction.LEFT;
                }
            }
        } else if (col == 0 || col == 7) {
            if (col == 0) {
                if (topLeft) {
                    return Direction.DOWN;
                }
                if (bottomLeft) {
                    return Direction.UP;
                }
            } else {
                if (topRight) {
                    return Direction.DOWN;
                }
                if (bottomRight) {
                    return Direction.UP;
                }
            }
        } else {
            if (topLeft) {
                return Direction.BOTTOM_RIGHT;
            }
            if (topRight) {
                return Direction.BOTTOM_LEFT;
            }
            if (bottomLeft) {
                return Direction.TOP_RIGHT;
            }
            if (bottomRight) {
                return Direction.TOP_LEFT;
            }
        }

        return Direction.NONE;
    }

    private char getIntersectionSymbol(int row, int col, Direction adjacency, SymbolSet symbols) {
        if (row == 0 && col == 0) {
            return symbols.cornerBottomLeft(adjacency != Direction.NONE);
        } else if (row == 0 && col == 7) {
            return symbols.cornerBottomRight(adjacency != Direction.NONE);
        } else if (row == 7 && col == 0) {
            return symbols.cornerTopLeft(adjacency != Direction.NONE);
        } else if (row == 7 && col == 7) {
            return symbols.cornerTopRight(adjacency != Direction.NONE);
        } else if (row == 0) {
            return symbols.midEdgeBottom(adjacency);
        } else if (row == 7) {
            return symbols.midEdgeTop(adjacency);
        } else if (col == 0) {
            return symbols.midEdgeLeft(adjacency);
        } else if (col == 7) {
            return symbols.midEdgeRight(adjacency);
        } else {
            return symbols.centralJunction(adjacency);
        }
    }

    private boolean isHorizontalEdgeSelected(int row, int col, Field selectedField) {
        if (selectedField == null) {
            return false;
        }

        int selRow = selectedField.row();
        int selCol = selectedField.column() - 'A' + 1;
        return col == selCol - 1 && (row == selRow || row == selRow - 1);
    }

    private boolean isVerticalEdgeSelected(int row, int col, Field selectedField) {
        if (selectedField == null) {
            return false;
        }

        int selRow = selectedField.row();
        int selCol = selectedField.column() - 'A' + 1;
        return (col == selCol - 1 || col == selCol) && row == selRow;
    }

    private String renderCoordinateLine() {
        return "    A   B   C   D   E   F   G";
    }
}
