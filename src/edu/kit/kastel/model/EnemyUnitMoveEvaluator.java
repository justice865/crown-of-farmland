package edu.kit.kastel.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class evaluates possible moves for enemy units and selects the best move based on a scoring system.
 * @author uqibg
 */
public class EnemyUnitMoveEvaluator {
    private static final Direction[] MOVE_DIRECTIONS = {Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT};
    private static final int ILLEGAL_MOVE_SCORE = Integer.MIN_VALUE / 2;

    private final Board board;
    private final TeamIndicator teamIndicator;
    private final Random rng;

    /**
     * Constructor for EnemyUnitMoveEvaluator.
     * @param board the current state of the game board
     * @param teamIndicator the team for which we are evaluating moves (should be ENEMY)
     * @param rng a Random instance for tie-breaking situations
     */
    public EnemyUnitMoveEvaluator(Board board, TeamIndicator teamIndicator, Random rng) {
        this.board = board;
        this.teamIndicator = teamIndicator;
        this.rng = rng;
    }

    /**
     * Compares the total scores of all placed units and selects the one with the highest score for moving.
     * @param placedUnits the list of currently placed units for the enemy team
     * @return the unit that should be moved, or null if there are no units to move
     */
    public Unit chooseMoveUnit(List<Unit> placedUnits) {
        if (placedUnits == null || placedUnits.isEmpty()) {
            return null;
        }

        List<Integer> unitTotalScores = new ArrayList<>();
        for (Unit unit : placedUnits) {
            unitTotalScores.add(calculateUnitTotalScore(unit));
        }

        int maxScore = Integer.MIN_VALUE;
        for (int score : unitTotalScores) {
            if (score > maxScore) {
                maxScore = score;
            }
        }

        List<Integer> weights = new ArrayList<>();
        int bestCount = 0;
        int bestIndex = -1;
        for (int i = 0; i < unitTotalScores.size(); i++) {
            if (unitTotalScores.get(i) == maxScore) {
                weights.add(1);
                bestCount++;
                bestIndex = i;
            } else {
                weights.add(0);
            }
        }
        if (bestCount == 1) {
            return placedUnits.get(bestIndex);
        }

        WeightedRandom.buildIntervals(weights);
        int selectedIndex = WeightedRandom.pickIndex(weights, rng);

        if (selectedIndex < 0 || selectedIndex >= placedUnits.size()) {
            return placedUnits.get(0);
        }
        return placedUnits.get(selectedIndex);
    }

    /**
     * Evaluates the possible moves for the given unit and selects the best one based on a scoring system.
     * @param unit the unit for which to choose a move
     * @return a MoveDecision object representing the chosen move, or null if no valid moves are available
     */
    public MoveDecision chooseMoveForUnit(Unit unit) {
        Field currentField = findUnitField(unit);
        if (currentField == null) {
            return null;
        }

        List<Integer> scores = new ArrayList<>();
        for (Direction dir : MOVE_DIRECTIONS) {
            scores.add(scoreMove(unit, currentField.offset(dir)));
        }
        scores.add(scoreBlock(unit, currentField));
        scores.add(scoreEnPlace(unit, currentField));

        boolean allNonPositive = true;
        for (int score : scores) {
            if (score > 0) {
                allNonPositive = false;
                break;
            }
        }

        if (allNonPositive) {
            return new MoveDecision(unit, currentField, true, false, 4);
        }
        List<Integer> weights = new ArrayList<>();
        for (int score : scores) {
            weights.add(Math.max(0, score));
        }
        int selectedIndex = WeightedRandom.pickIndex(weights, rng);

        if (selectedIndex < 0) {
            selectedIndex = 4;
        }

        if (selectedIndex < 4) {
            return new MoveDecision(unit, currentField.offset(MOVE_DIRECTIONS[selectedIndex]), false, false, selectedIndex);
        } else if (selectedIndex == 4) {
            return new MoveDecision(unit, currentField, true, false, 4);
        } else {
            return new MoveDecision(unit, currentField, false, true, 5);
        }
    }

    private int calculateUnitTotalScore(Unit unit) {
        Field currentField = findUnitField(unit);
        if (currentField == null) {
            return 0;
        }

        int totalScore = 0;
        for (Direction dir : MOVE_DIRECTIONS) {
            int moveScore = scoreMove(unit, currentField.offset(dir));
            if (moveScore != ILLEGAL_MOVE_SCORE) {
                totalScore += moveScore;
            }
        }
        totalScore += scoreBlock(unit, currentField);
        totalScore += scoreEnPlace(unit, currentField);
        return totalScore;
    }

    private int scoreMove(Unit unit, Field targetField) {
        if (!isInBounds(targetField)) {
            return ILLEGAL_MOVE_SCORE;
        }

        FieldPlaceable targetInfo = board.getAt(targetField);
        if (isFriendlyKingField(targetInfo)) {
            return ILLEGAL_MOVE_SCORE;
        }

        if (targetInfo.unit() != null && targetInfo.unit().getTeamIndicator() == teamIndicator) {
            return scoreMerge(unit, targetInfo.unit());
        }
        if (targetInfo.unit() != null && targetInfo.unit().getTeamIndicator() != teamIndicator) {
            return scoreAttackEnemyUnit(unit, targetInfo.unit());
        }
        if (targetInfo.playerKing()) {
            return scoreAttackKing(unit);
        }
        return scoreEmptyField(targetField);
    }

    private boolean isFriendlyKingField(FieldPlaceable fieldPlaceable) {
        return teamIndicator == TeamIndicator.ENEMY ? fieldPlaceable.cpuKing() : fieldPlaceable.playerKing();
    }

    private int scoreMerge(Unit mover, Unit friendly) {
        UnitType moverUnitType = mover.getUnitType();
        UnitType friendlyType = friendly.getUnitType();

        if (moverUnitType.canMergeWith(friendlyType)) {
            UnitType merged = moverUnitType.mergeWith(friendlyType);
            return merged.attack() + merged.defense() - moverUnitType.attack() - moverUnitType.defense();
        }
        return -friendlyType.attack() - friendlyType.defense();
    }

    private int scoreAttackEnemyUnit(Unit attacker, Unit enemy) {
        if (enemy.isFaceDown()) {
            return attacker.getUnitType().attack() - 500;
        }
        if (enemy.isBlocked()) {
            return attacker.getUnitType().attack() - enemy.getUnitType().defense();
        }
        return 2 * (attacker.getUnitType().attack() - enemy.getUnitType().attack());
    }

    private int scoreAttackKing(Unit attacker) {
        return attacker.getUnitType().attack();
    }

    private int scoreEmptyField(Field targetField) {
        int steps = targetField.distanceTo(getOpponentKingField());
        int enemies = countEnemiesCardinal(targetField);
        return 10 * steps - enemies;
    }

    private int scoreBlock(Unit unit, Field currentField) {
        int defense = unit.getUnitType().defense();
        int highestEnemyAttack = getHighestEnemyAttackCardinal(currentField);
        return Math.max(1, (defense - highestEnemyAttack) / 100);
    }

    private int scoreEnPlace(Unit unit, Field currentField) {
        int attack = unit.getUnitType().attack();
        int highestEnemyAttack = getHighestEnemyAttackCardinal(currentField);
        return Math.max(0, (attack - highestEnemyAttack) / 100);
    }

    private Field findUnitField(Unit unit) {
        for (Field field : board.getAllFields()) {
            if (board.getAt(field).unit() == unit) {
                return field;
            }
        }
        return null;
    }

    private int countEnemiesCardinal(Field center) {
        int count = 0;
        for (Direction direction : MOVE_DIRECTIONS) {
            Field neighbor = center.offset(direction);
            if (!isInBounds(neighbor)) {
                continue;
            }
            FieldPlaceable fieldPlaceable = board.getAt(neighbor);
            if (fieldPlaceable.unit() != null && fieldPlaceable.unit().getTeamIndicator() != teamIndicator) {
                count++;
            }
        }
        return count;
    }

    private int getHighestEnemyAttackCardinal(Field center) {
        int maxAttack = 0;
        for (Direction direction : MOVE_DIRECTIONS) {
            Field neighbor = center.offset(direction);
            if (!isInBounds(neighbor)) {
                continue;
            }
            FieldPlaceable fieldPlaceable = board.getAt(neighbor);
            if (fieldPlaceable.unit() != null && fieldPlaceable.unit().getTeamIndicator() != teamIndicator
                    && !fieldPlaceable.unit().isFaceDown()) {
                int attack = fieldPlaceable.unit().getUnitType().attack();
                if (attack > maxAttack) {
                    maxAttack = attack;
                }
            }
        }
        return maxAttack;
    }

    private Field getOpponentKingField() {
        return teamIndicator == TeamIndicator.ENEMY
                ? board.getPlayerKingPosition()
                : board.getCpuKingPosition();
    }

    private boolean isInBounds(Field field) {
        return field.row() >= 1 && field.row() <= 7 && field.column() >= 'A' && field.column() <= 'G';
    }
}
