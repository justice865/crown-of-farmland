package edu.kit.kastel.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Evaluates the candidate fields for placing new enemy units on the board.
 * @author uqibg
 */
public class EnemyPlacementFieldEvaluator {
    private static final Direction[] CLOCKWISE = {
        Direction.RIGHT,
        Direction.LEFT,
        Direction.BOTTOM_RIGHT,
        Direction.DOWN,
        Direction.BOTTOM_LEFT,
        Direction.TOP_RIGHT,
        Direction.UP,
        Direction.TOP_LEFT
    };

    private static final Direction[] CARDINAL = {
        Direction.UP,
        Direction.RIGHT,
        Direction.DOWN,
        Direction.LEFT
    };

    private final Board board;
    private final TeamIndicator teamIndicator;
    private final Random rng;

    /**
     * Constructor for EnemyPlacementFieldEvaluator.
     * @param board the current state of the game board
     * @param teamIndicator the team for which we are evaluating placement fields (should be ENEMY)
     * @param rng a Random instance for tie-breaking between equally good candidate fields
     */
    public EnemyPlacementFieldEvaluator(Board board, TeamIndicator teamIndicator, Random rng) {
        this.board = board;
        this.teamIndicator = teamIndicator;
        this.rng = rng;
    }

    /**
     * Evaluates the fields around the enemy king and selects the best one for placing a new unit.
     * @param kingField the current position of the enemy king
     * @return the best field for placing a new unit, or null if no valid fields are available
     */
    public Field chooseBestPlacementField(Field kingField) {
        List<Field> candidates = new ArrayList<>();
        List<Integer> scores = new ArrayList<>();
        for (Direction direction : CLOCKWISE) {
            Field candidate = kingField.offset(direction);
            if (!isCandidate(candidate)) {
                continue;
            }
            candidates.add(candidate);
            scores.add(score(candidate));
        }
        if (candidates.isEmpty()) {
            return null;
        }
        int maxScore = Integer.MIN_VALUE;
        for (int s : scores) {
            if (s > maxScore) {
                maxScore = s;
            }
        }
        List<Integer> bestIndexes = new ArrayList<>();
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i) == maxScore) {
                bestIndexes.add(i);
            }
        }
        if (bestIndexes.size() == 1) {
            return candidates.get(bestIndexes.get(0));
        }
        if (bestIndexes.size() == 2) {
            Field first = candidates.get(bestIndexes.get(0));
            Field second = candidates.get(bestIndexes.get(1));
            int firstDistance = distanceToNearestOpponentUnit(first);
            int secondDistance = distanceToNearestOpponentUnit(second);
            if (firstDistance != secondDistance) {
                rng.nextInt(bestIndexes.size());
                return firstDistance < secondDistance ? first : second;
            }
        }
        int pick = rng.nextInt(bestIndexes.size());
        return candidates.get(bestIndexes.get(pick));
    }
    private boolean isCandidate(Field field) {
        if (!isInBounds(field)) {
            return false;
        }
        FieldPlaceable fieldPlaceable = board.getAt(field);
        if (fieldPlaceable.playerKing() || fieldPlaceable.cpuKing()) {
            return false;
        }
        Unit unit = fieldPlaceable.unit();
        return unit == null || unit.getTeamIndicator() == teamIndicator;
    }

    private int score(Field candidate) {
        int steps = candidate.distanceTo(getOpponentKingField());
        int enemies = countEnemiesCardinal(candidate);
        int fellows = countFellowsCardinal(candidate);
        return -steps + 2 * enemies - fellows;
    }
    private int countEnemiesCardinal(Field center) {
        int count = 0;
        for (Direction direction : CARDINAL) {
            Field neighbor = center.offset(direction);
            if (!isInBounds(neighbor)) {
                continue;
            }

            FieldPlaceable fieldPlaceable = board.getAt(neighbor);
            if (isOpponentKingField(fieldPlaceable)) {
                count++;
                continue;
            }
            Unit unit = fieldPlaceable.unit();
            if (unit != null && unit.getTeamIndicator() != teamIndicator) {
                count++;
            }
        }
        return count;
    }

    private int countFellowsCardinal(Field center) {
        int count = 0;
        for (Direction d : CARDINAL) {
            Field neighbor = center.offset(d);
            if (!isInBounds(neighbor)) {
                continue;
            }

            FieldPlaceable fieldPlaceable = board.getAt(neighbor);
            if (isFriendlyKingField(fieldPlaceable)) {
                count++;
                continue;
            }
            Unit unit = fieldPlaceable.unit();
            if (unit != null && unit.getTeamIndicator() == teamIndicator) {
                count++;
            }
        }
        return count;
    }

    private boolean isOpponentKingField(FieldPlaceable fieldPlaceable) {
        return teamIndicator == TeamIndicator.ENEMY ? fieldPlaceable.playerKing() : fieldPlaceable.cpuKing();
    }

    private boolean isFriendlyKingField(FieldPlaceable fieldPlaceable) {
        return teamIndicator == TeamIndicator.ENEMY ? fieldPlaceable.cpuKing() : fieldPlaceable.playerKing();
    }

    private Field getOpponentKingField() {
        return teamIndicator == TeamIndicator.ENEMY
            ? board.getPlayerKingPosition()
            : board.getCpuKingPosition();
    }

    private boolean isInBounds(Field field) {
        return field.row() >= 1 && field.row() <= 7 && field.column() >= 'A' && field.column() <= 'G';
    }

    private int distanceToNearestOpponentUnit(Field candidate) {
        int bestDistance = Integer.MAX_VALUE;
        for (int row = 1; row <= 7; row++) {
            for (char column = 'A'; column <= 'G'; column++) {
                Field field = new Field(row, column);
                Unit unit = board.getAt(field).unit();
                if (unit != null && unit.getTeamIndicator() != teamIndicator) {
                    bestDistance = Math.min(bestDistance, candidate.distanceTo(field));
                }
            }
        }
        return bestDistance;
    }
}
