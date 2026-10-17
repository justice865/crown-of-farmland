package edu.kit.kastel.model;

import edu.kit.kastel.FieldOrUnitNotExistingException;

/**
 * The PlayerMoveHelper class is responsible for handling the logic of moving a unit on the board for a player.
 * It validates the move, checks for duels, merges, and updates the game state accordingly.
 * @author uqibg
 */
public class PlayerMoveHelper {
    private final PlayerTeam player;

    /**
     * Constructor for PlayerMoveHelper.
     * @param player the player for whom the move is being processed
     */
    public PlayerMoveHelper(PlayerTeam player) {
        this.player = player;
    }

    /**
     * Processes a move to the target field, validating the move and updating the game state accordingly.
     * @param targetField the field to which the player wants to move
     * @return a MoveResult object containing the outcome of the move
     * @throws FieldOrUnitNotExistingException if a move is invalid due to reasons such as no selected field,
     *                                         moving a unit that doesn't belong to the player,
     *                                         moving to a non-adjacent field,
     *                                         or other invalid move conditions
     */
    public MoveResult move(Field targetField) throws FieldOrUnitNotExistingException {
        Field selectedField = player.getSelectedField();
        if (selectedField == null) {
            throw new FieldOrUnitNotExistingException("ERROR: Not a valid selected field.");
        }
        if (selectedField.equals(player.getBoard().getCpuKingPosition())) {
            throw new FieldOrUnitNotExistingException("ERROR: Not a valid selected field.");
        }
        boolean movingKing = selectedField.equals(player.getBoard().getPlayerKingPosition());
        if (!movingKing) {
            Unit selectedUnit = player.getBoard().getAt(selectedField).unit();
            if (selectedUnit == null || selectedUnit.getTeamIndicator() != player.getTeamIndicator()) {
                throw new FieldOrUnitNotExistingException("ERROR: Not a valid selected field.");
            }
        }
        if (targetField.distanceTo(player.getSelectedField()) != 1 && !targetField.equals(player.getSelectedField())) {
            throw new FieldOrUnitNotExistingException("ERROR: Units can only move to adjacent fields.");
        }
        if (movingKing) {
            return handleKingMove(selectedField, targetField);
        }

        Unit movingUnit = player.getBoard().getAt(selectedField).unit();
        if (movingUnit.isBlocked()) {
            return handleBlockedUnitMove(selectedField, movingUnit);
        }
        if (movingUnit.hasMoved()) {
            throw new FieldOrUnitNotExistingException("ERROR: edu.kit.kastel.model.Unit has already moved this turn.");
        }
        FieldPlaceable targetFieldInfo = player.getBoard().getAt(targetField);
        Unit targetUnit = targetFieldInfo.unit();
        boolean targetIsEnemyKing = targetFieldInfo.cpuKing();
        boolean targetIsFriendlyKing = targetFieldInfo.playerKing();
        if (targetField.equals(selectedField)) {
            return handleInPlaceMove(selectedField, movingUnit);
        }
        if (targetIsFriendlyKing) {
            throw new FieldOrUnitNotExistingException("ERROR: Cannot move to friendly king field.");
        }
        if (targetIsEnemyKing) {
            return handleEnemyKingDuel(selectedField, targetField, movingUnit);
        }
        if (targetUnit != null && targetUnit.getTeamIndicator() == TeamIndicator.ENEMY) {
            return handleEnemyUnitDuel(selectedField, targetField, movingUnit, targetUnit);
        }
        if (targetUnit != null && targetUnit.getTeamIndicator() == player.getTeamIndicator()) {
            return handleFriendlyMerge(selectedField, targetField, movingUnit, targetUnit);
        }
        if (targetUnit == null) {
            return handleEmptyFieldMove(selectedField, targetField, movingUnit);
        }
        return new MoveResult(selectedField, true, 0, 0, false, null, false, false, false,
                null, movingUnit, null, null, false);
    }
    private MoveResult handleBlockedUnitMove(Field sourceField, Unit movingUnit) {
        movingUnit.setBlocked(false);
        movingUnit.setHasMoved(true);
        return new MoveResult(sourceField, true, 0, 0, false, null, true, false, false,
                null, movingUnit, null, null, false);
    }
    private MoveResult handleInPlaceMove(Field sourceField, Unit movingUnit) {
        movingUnit.setHasMoved(true);
        return new MoveResult(sourceField, true, 0, 0, false, null, false, false, false,
                null, movingUnit, null, null, false);
    }

    private MoveResult handleEnemyKingDuel(Field sourceField, Field targetField, Unit movingUnit) {
        DuelResult duelResult = movingUnit.duelWith(null, true);
        int damageToEnemy = movingUnit.getUnitType().attack();
        movingUnit.setHasMoved(true);
        player.setSelectedField(null);
        return new MoveResult(targetField, false, damageToEnemy, 0, true, null, false, false, false,
                duelResult, movingUnit, null, sourceField, false);
    }

    private MoveResult handleEnemyUnitDuel(Field sourceField, Field targetField, Unit movingUnit,
                                           Unit targetUnit) {
        DuelResult duelResult = movingUnit.duelWith(targetUnit, false);

        String eliminatedName = null;
        int damageToEnemy = 0;
        int damageToSelf = 0;

        if (!duelResult.stalemate()) {
            int attackDiff = movingUnit.getUnitType().attack() - targetUnit.getUnitType().attack();
            if (attackDiff > 0) {
                damageToEnemy = attackDiff;
            } else if (attackDiff < 0) {
                damageToSelf = Math.abs(attackDiff);
            }

            if (!duelResult.attackerWins()) {
                eliminatedName = movingUnit.getUnitType().qualifier() + " " + movingUnit.getUnitType().role();
                player.getBoard().placeUnit(sourceField, null);
                player.decrementBoardUnitCount();
                player.setSelectedField(null);
            } else {
                eliminatedName = targetUnit.getUnitType().qualifier() + " " + targetUnit.getUnitType().role();
                player.getGame().getEnemy().decrementBoardUnitCount();
                player.getBoard().placeUnit(targetField, movingUnit);
                player.getBoard().placeUnit(sourceField, null);
                player.setSelectedField(targetField);
            }
        } else {
            player.getBoard().placeUnit(sourceField, null);
            player.getBoard().placeUnit(targetField, null);
            player.decrementBoardUnitCount();
            player.getGame().getEnemy().decrementBoardUnitCount();
            player.setSelectedField(null);
        }

        movingUnit.setHasMoved(true);
        return new MoveResult(targetField, false, damageToEnemy, damageToSelf, true, eliminatedName,
                false, false, false, duelResult, movingUnit, targetUnit, sourceField, false);
    }

    private MoveResult handleFriendlyMerge(Field sourceField, Field targetField, Unit movingUnit,
                                           Unit targetUnit) {
        UnitType movingType = movingUnit.getUnitType();
        UnitType targetType = targetUnit.getUnitType();

        if (movingType.canMergeWith(targetType)) {
            UnitType mergedType = movingType.mergeWith(targetType);
            Unit mergedUnit = new Unit(mergedType, player.getTeamIndicator());
            mergedUnit.setHasMoved(true);

            player.decrementBoardUnitCount();
            player.getBoard().placeUnit(targetField, mergedUnit);
            player.getBoard().placeUnit(sourceField, null);

            player.setSelectedField(targetField);
            return new MoveResult(targetField, false, 0, 0, false, null, false, true, true,
                    null, movingUnit, targetUnit, null, false);
        }

        player.decrementBoardUnitCount();
        player.getBoard().placeUnit(targetField, movingUnit);
        player.getBoard().placeUnit(sourceField, null);
        movingUnit.setHasMoved(true);

        player.setSelectedField(targetField);

        String eliminatedName = targetType.qualifier() + " " + targetType.role();
        return new MoveResult(targetField, false, 0, 0, false, eliminatedName, false, true, false,
                null, movingUnit, targetUnit, null, false);
    }

    private MoveResult handleEmptyFieldMove(Field sourceField, Field targetField, Unit movingUnit) {
        player.getBoard().placeUnit(targetField, movingUnit);
        player.getBoard().placeUnit(sourceField, null);

        movingUnit.setHasMoved(true);
        player.setSelectedField(targetField);
        return new MoveResult(targetField, false, 0, 0, false, null, false, false, false,
                null, movingUnit, null, null, false);
    }

    private MoveResult handleKingMove(Field sourceField, Field targetField) throws FieldOrUnitNotExistingException {
        if (player.getBoard().isPlayerKingHasMoved()) {
            throw new FieldOrUnitNotExistingException("ERROR: King has already moved this turn.");
        }

        FieldPlaceable targetFieldInfo = player.getBoard().getAt(targetField);
        boolean targetIsEnemyKing = targetFieldInfo.cpuKing();
        boolean targetIsFriendlyKing = targetFieldInfo.playerKing();
        if (targetField.equals(sourceField)) {
            player.getBoard().setPlayerKingHasMoved(true);
            return new MoveResult(sourceField, true);
        }
        if (targetIsFriendlyKing) {
            throw new FieldOrUnitNotExistingException("ERROR: Cannot move to friendly king field.");
        }
        if (targetIsEnemyKing) {
            throw new FieldOrUnitNotExistingException("ERROR: King cannot attack enemy king.");
        }

        Unit targetUnit = targetFieldInfo.unit();
        if (targetUnit == null) {
            player.getBoard().setPlayerKingPosition(targetField);
            player.getBoard().setPlayerKingHasMoved(true);
            player.setSelectedField(targetField);
            return new MoveResult(targetField, false);
        }
        if (targetUnit.getTeamIndicator() != player.getTeamIndicator()) {
            String eliminatedName = targetUnit.getUnitType().qualifier() + " " + targetUnit.getUnitType().role();
            player.getBoard().placeUnit(targetField, null);
            player.getBoard().setPlayerKingPosition(targetField);
            player.getBoard().setPlayerKingHasMoved(true);
            player.setSelectedField(targetField);
            player.getGame().getEnemy().decrementBoardUnitCount();
            return new MoveResult(targetField, false, eliminatedName);
        }
        throw new FieldOrUnitNotExistingException("ERROR: Cannot move king to friendly unit field.");
    }
}
