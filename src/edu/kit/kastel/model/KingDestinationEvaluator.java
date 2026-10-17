package edu.kit.kastel.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class evaluates potential destination fields for the king.
 * @author uqibg
 */
public class KingDestinationEvaluator {
    private final Board board;
    private final TeamIndicator teamIndicator;
    private final Random rng;

    /**
     * Constructor for KingDestinationEvaluator.
     * @param board the current state of the game board
     * @param teamIndicator the team for which we are evaluating king destinations (should be PLAYER)
     * @param rng a Random instance for tie-breaking situations
     */
    public KingDestinationEvaluator(Board board, TeamIndicator teamIndicator, Random rng) {
        this.board = board;
        this.teamIndicator = teamIndicator;
        this.rng = rng;
    }

    /**
     * Evaluates the legal destination fields for the king and selects the best one based on a scoring system.
     * @param currentKingPosition the current position of the player's king
     * @return the best destination field for the king, or null if no legal moves are available
     */
    public Field selectBestDestination(Field currentKingPosition) {
        Direction[] directions = {Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT, Direction.NONE};
        List<Field> candidates = new ArrayList<>();
        List<Integer> scores = new ArrayList<>();
        for (Direction dir : directions) {
            Field candidate = currentKingPosition.offset(dir);
            if (isLegal(candidate)) {
                candidates.add(candidate);
                scores.add(scoreDestination(candidate, currentKingPosition));
            }
        }
        int maxScore = Integer.MIN_VALUE;
        for (int score : scores) {
            if (score > maxScore) {
                maxScore = score;
            }
        }
        List<Integer> weights = new ArrayList<>();
        int bestCount = 0;
        int bestIndex = -1;
        for (int i = 0; i < candidates.size(); i++) {
            if (scores.get(i) == maxScore) {
                weights.add(1);
                bestCount++;
                bestIndex = i;
            } else {
                weights.add(0);
            }
        }
        if (bestCount == 1) {
            return candidates.get(bestIndex);
        }
        int selectedIndex = WeightedRandom.pickIndex(weights, rng);
        return candidates.get(selectedIndex);
    }

    private boolean isLegal(Field field) {
        if (field.row() < 1 || field.row() > 7 || field.column() < 'A' || field.column() > 'G') {
            return false;
        }
        FieldPlaceable info = board.getAt(field);
        if (info.playerKing()) {
            return false;
        }
        return !(info.unit() != null && info.unit().getTeamIndicator() == teamIndicator);
    }

    private int scoreDestination(Field destination, Field currentPosition) {
        int distance = destination.equals(currentPosition) ? 0 : 1;
        int fellows = countFellows(destination);
        int enemies = countEnemies(destination);
        int fellowPresent = hasFellowUnit(destination) ? 1 : 0;

        return fellows - 2 * enemies - distance - 3 * fellowPresent;
    }

    private int countFellows(Field field) {
        int count = 0;
        for (Unit unit : getNeighbors(field)) {
            if (unit.getTeamIndicator() == teamIndicator) {
                count++;
            }
        }
        return count;
    }

    private int countEnemies(Field field) {
        int count = 0;
        for (Unit unit : getNeighbors(field)) {
            if (unit.getTeamIndicator() != teamIndicator) {
                count++;
            }
        }
        if (hasEnemyKing(field)) {
            count++;
        }
        return count;
    }

    private List<Unit> getNeighbors(Field field) {
        List<Unit> neighbors = new ArrayList<>();
        Direction[] dirs = {Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT,
                            Direction.TOP_LEFT, Direction.TOP_RIGHT, Direction.BOTTOM_LEFT, Direction.BOTTOM_RIGHT};

        for (Direction dir : dirs) {
            Field neighbor = field.offset(dir);
            if (isInBounds(neighbor) && !isKing(neighbor)) {
                FieldPlaceable info = board.getAt(neighbor);
                if (info.unit() != null) {
                    neighbors.add(info.unit());
                }
            }
        }
        return neighbors;
    }

    private boolean hasEnemyKing(Field field) {
        Direction[] dirs = {Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT,
                            Direction.TOP_LEFT, Direction.TOP_RIGHT, Direction.BOTTOM_LEFT, Direction.BOTTOM_RIGHT};

        for (Direction dir : dirs) {
            Field neighbor = field.offset(dir);
            if (isInBounds(neighbor) && board.getAt(neighbor).playerKing()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasFellowUnit(Field field) {
        FieldPlaceable info = board.getAt(field);
        return info.unit() != null && info.unit().getTeamIndicator() == teamIndicator;
    }

    private boolean isInBounds(Field field) {
        return field.row() >= 1 && field.row() <= 7 && field.column() >= 'A' && field.column() <= 'G';
    }

    private boolean isKing(Field field) {
        FieldPlaceable info = board.getAt(field);
        return info.playerKing() || info.cpuKing();
    }
}

