package edu.kit.kastel.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The EnemyTeam class represents the logic and behavior of the enemy team in the game.
 * @author uqibg
 */
public class EnemyTeam extends Team {
    private Random rng;
    private KingDestinationEvaluator destinationEvaluator;
    private EnemyPlacementFieldEvaluator placementFieldEvaluator;
    private final List<Unit> placedUnitOrder = new ArrayList<>();
    private EnemyOutputHelper outputHelper;
    private int chosenUnitIndex = -1;

    /**
     * Constructor for EnemyTeam.
     * @param team the TeamIndicator for this team (should be ENEMY)
     */
    public EnemyTeam(TeamIndicator team) {
        super(team);
    }

    /**
     * Sets the Random instance to be used for decision-making in the enemy's move logic.
     * @param rng a Random instance for tie-breaking and random choices in the enemy's move logic
     */
    public void setRng(Random rng) {
        this.rng = rng;
    }

    /**
     * Initializes the evaluators and output helper for the enemy team.
     */
    public void initializeEvaluator() {
        this.destinationEvaluator = new KingDestinationEvaluator(getBoard(), this.getTeamIndicator(), rng);
        this.placementFieldEvaluator = new EnemyPlacementFieldEvaluator(getBoard(), this.getTeamIndicator(), rng);
        this.outputHelper = new EnemyOutputHelper(getBoard(), getGame());
    }

    private void ensureEvaluatorInitialized() {
        if (destinationEvaluator == null || placementFieldEvaluator == null || outputHelper == null) {
            initializeEvaluator();
        }
    }

    /**
     * Simulates the enemy's turn.
     */
    public void doMove() {
        ensureEvaluatorInitialized();
        MoveResult kingMoveResult = moveKing();
        outputHelper.outputKingMove(kingMoveResult);
        outputHelper.displayBoard(getSelectedField());
        Field chosenField = chooseField();
        if (chosenField != null) {
            UnitType chosenUnit = chooseUnit();
            PlaceResult placeResult = placeUnit(chosenField, chosenUnit);
            if (placeResult != null) {
                outputHelper.outputPlacement(placeResult);
                outputHelper.displayBoard(getSelectedField());
            }
        }
        moveUnits();
        maybeDiscard();
    }

    /**
     * Determines the best destination for the enemy king to move to.
     * @return the Field representing the best destination.
     */
    public Field kingDestination() {
        ensureEvaluatorInitialized();
        return destinationEvaluator.selectBestDestination(getBoard().getCpuKingPosition());
    }

    /**
     * Gets the best field that is chosen.
     * @return the Field representing the best placement field for a new unit, or null if no valid fields are available.
     */
    public Field chooseField() {
        ensureEvaluatorInitialized();
        Field chosen = placementFieldEvaluator.chooseBestPlacementField(getBoard().getCpuKingPosition());
        setSelectedField(chosen);
        return chosen;
    }

    private MoveResult moveKing() {
        Field currentKingPosition = getBoard().getCpuKingPosition();
        Field destination = kingDestination();
        if (destination.equals(currentKingPosition)) {
            setSelectedField(destination);
            getBoard().setCpuKingHasMoved(true);
            return new MoveResult(destination, true);
        }
        FieldPlaceable destinationInfo = getBoard().getAt(destination);
        Unit destinationUnit = destinationInfo.unit();
        if (destinationUnit == null) {
            getBoard().setCpuKingPosition(destination);
            getBoard().setCpuKingHasMoved(true);
            setSelectedField(destination);
            return new MoveResult(destination, false);
        }
        UnitType eliminatedType = destinationUnit.getUnitType();
        String eliminatedUnitName = eliminatedType.qualifier() + " " + eliminatedType.role();
        getBoard().placeUnit(destination, null);
        getBoard().setCpuKingPosition(destination);
        getBoard().setCpuKingHasMoved(true);
        setSelectedField(destination);
        if (destinationUnit.getTeamIndicator() == getTeamIndicator()) {
            this.decrementBoardUnitCount();
        } else {
            getGame().getPlayer().decrementBoardUnitCount();
        }
        return new MoveResult(destination, false, eliminatedUnitName);
    }

    private UnitType chooseUnit() {
        if (getCards() == null || getCards().isEmpty()) {
            chosenUnitIndex = -1;
            return null;
        }
        List<Integer> weights = new ArrayList<>();
        for (UnitType card : getCards()) {
            weights.add(card.attack());
        }
        List<WeightedRandom.Interval> intervals = WeightedRandom.buildIntervals(weights);
        int selectedIndex = WeightedRandom.pickIndexFromIntervals(intervals, rng);
        chosenUnitIndex = (selectedIndex < 0 || selectedIndex >= getCards().size()) ? 0 : selectedIndex;
        return getCards().get(chosenUnitIndex);
    }
    private PlaceResult placeUnit(Field chosenField, UnitType chosenUnit) {
        if (chosenField == null || chosenUnit == null) {
            return null;
        }
        if (getCards() != null && chosenUnitIndex >= 0 && chosenUnitIndex < getCards().size()) {
            getCards().remove(chosenUnitIndex);
        }
        chosenUnitIndex = -1;
        Unit existing = getBoard().getAt(chosenField).unit();
        if (existing != null && existing.getTeamIndicator() == getTeamIndicator()) {
            UnitType existingType = existing.getUnitType();
            UnitType mergedType = chosenUnit.mergeWith(existingType);
            boolean mergeSuccess = !mergedType.equals(chosenUnit);
            if (mergeSuccess) {
                Unit mergedUnit = new Unit(mergedType, getTeamIndicator());
                getBoard().placeUnit(chosenField, mergedUnit);
                int idx = placedUnitOrder.indexOf(existing);
                if (idx >= 0) {
                    placedUnitOrder.set(idx, mergedUnit);
                } else {
                    placedUnitOrder.add(mergedUnit);
                }
            }
            setSelectedField(chosenField);
            return new PlaceResult(chosenUnit, existingType, true, mergeSuccess, !mergeSuccess, chosenField);
        }
        Unit placedUnit = new Unit(chosenUnit, getTeamIndicator());
        getBoard().placeUnit(chosenField, placedUnit);
        this.incrementBoardUnitCount();
        placedUnitOrder.add(placedUnit);
        setSelectedField(chosenField);
        return new PlaceResult(chosenUnit, chosenField);
    }
    private void clearPlacedUnitOrder() {
        placedUnitOrder.clear();
    }
    private void moveUnits() {
        if (rng == null || getBoard() == null) {
            return;
        }
        syncPlacedUnitOrderWithBoard();
        EnemyUnitMoveEvaluator moveEvaluator = new EnemyUnitMoveEvaluator(getBoard(), this.getTeamIndicator(), rng);
        List<Unit> remainingUnits = new ArrayList<>(placedUnitOrder);
        while (!remainingUnits.isEmpty() && !getGame().gameOver) {
            Unit unitToMove = moveEvaluator.chooseMoveUnit(remainingUnits);
            if (unitToMove == null) {
                break;
            }
            MoveDecision decision = moveEvaluator.chooseMoveForUnit(unitToMove);
            if (decision == null) {
                remainingUnits.remove(unitToMove);
                continue;
            }
            MoveResult moveResult = executeMoveForUnit(unitToMove, decision);
            if (moveResult != null) {
                outputHelper.outputUnitMove(moveResult);
                outputHelper.displayBoard(getSelectedField());
                if (moveResult.mergeAttempted() && moveResult.mergeSuccessful()) {
                    Unit targetUnit = moveResult.targetUnit();
                    Unit mergedUnit = getBoard().getAt(moveResult.resultField()).unit();
                    if (targetUnit != null) {
                        remainingUnits.remove(targetUnit);
                    }
                    if (mergedUnit != null && !remainingUnits.contains(mergedUnit)) {
                        remainingUnits.add(mergedUnit);
                    }
                    int attackerIndex = placedUnitOrder.indexOf(unitToMove);
                    int targetIndex = targetUnit == null ? -1 : placedUnitOrder.indexOf(targetUnit);
                    int insertIndex;
                    if (targetIndex >= 0) {
                        insertIndex = targetIndex;
                    } else if (attackerIndex >= 0) {
                        insertIndex = attackerIndex;
                    } else {
                        insertIndex = placedUnitOrder.size();
                    }
                    placedUnitOrder.remove(unitToMove);
                    if (targetUnit != null) {
                        placedUnitOrder.remove(targetUnit);
                    }
                    if (mergedUnit != null && !placedUnitOrder.contains(mergedUnit)) {
                        if (insertIndex > placedUnitOrder.size()) {
                            placedUnitOrder.add(mergedUnit);
                        } else {
                            placedUnitOrder.add(insertIndex, mergedUnit);
                        }
                    }
                }
            }
            remainingUnits.remove(unitToMove);
        }
    }
    private void syncPlacedUnitOrderWithBoard() {
        List<Unit> boardUnits = collectEnemyUnitsOnBoardStable();
        List<Unit> oldOrder = new ArrayList<>(placedUnitOrder);
        clearPlacedUnitOrder();
        for (Unit unit : oldOrder) {
            if (boardUnits.contains(unit)) {
                placedUnitOrder.add(unit);
            }
        }
        for (Unit unit : boardUnits) {
            if (!placedUnitOrder.contains(unit)) {
                placedUnitOrder.add(unit);
            }
        }
    }
    private List<Unit> collectEnemyUnitsOnBoardStable() {
        List<Unit> units = new ArrayList<>();
        for (int row = 1; row <= 7; row++) {
            for (char column = 'A'; column <= 'G'; column++) {
                Unit unit = getBoard().getAt(new Field(row, column)).unit();
                if (unit != null && unit.getTeamIndicator() == getTeamIndicator()) {
                    units.add(unit);
                }
            }
        }
        return units;
    }
    private MoveResult executeMoveForUnit(Unit unit, MoveDecision decision) {
        Field currentField = findUnitField(unit);
        if (currentField == null) {
            return null;
        }
        boolean wasBlocked = unit.isBlocked();
        unit.setBlocked(false);
        unit.setHasMoved(true);
        if (decision.isBlock()) {
            unit.setBlocked(true);
            setSelectedField(currentField);
            return new MoveResult(currentField, false, 0, 0, false, null, wasBlocked, false, false,
                    null, unit, null, null, true);
        }
        if (decision.isEnPlace()) {
            setSelectedField(currentField);
            return new MoveResult(currentField, true, 0, 0, false, null, wasBlocked, false, false,
                    null, unit, null, null, false);
        }
        Field targetField = decision.targetField();
        setSelectedField(targetField);
        FieldPlaceable targetInfo = getBoard().getAt(targetField);
        if (targetInfo.cpuKing()) {
            setSelectedField(currentField);
            return new MoveResult(currentField, true, 0, 0, false, null, wasBlocked, false, false,
                    null, unit, null, null, false);
        }
        if (targetInfo.unit() != null && targetInfo.unit().getTeamIndicator() == this.getTeamIndicator()) {
            return handleMerge(unit, targetInfo.unit(), targetField, currentField, wasBlocked);
        }
        if (targetInfo.unit() != null && targetInfo.unit().getTeamIndicator() != this.getTeamIndicator()) {
            return handleDuel(unit, targetInfo.unit(), targetField, currentField, false, wasBlocked);
        }
        if (targetInfo.playerKing()) {
            return handleDuel(unit, null, targetField, currentField, true, wasBlocked);
        }
        getBoard().placeUnit(currentField, null);
        getBoard().placeUnit(targetField, unit);
        return new MoveResult(targetField, false, 0, 0, false, null, wasBlocked, false, false,
                null, unit, null, null, false);
    }
    private MoveResult handleMerge(Unit attacker, Unit friendly, Field mergeField, Field attackerField, boolean wasBlocked) {
        UnitType attackerType = attacker.getUnitType();
        UnitType friendlyType = friendly.getUnitType();
        String friendlyName = friendlyType.qualifier() + " " + friendlyType.role();
        if (attackerType.canMergeWith(friendlyType)) {
            UnitType merged = attackerType.mergeWith(friendlyType);
            Unit mergedUnit = new Unit(merged, this.getTeamIndicator());
            getBoard().placeUnit(mergeField, mergedUnit);
            getBoard().placeUnit(attackerField, null);
            this.decrementBoardUnitCount();
            return new MoveResult(mergeField, false, 0, 0, false, null, wasBlocked, true, true,
                    null, attacker, friendly, null, false);
        } else {
            getBoard().placeUnit(mergeField, attacker);
            getBoard().placeUnit(attackerField, null);
            this.decrementBoardUnitCount();
            return new MoveResult(mergeField, false, 0, 0, false, friendlyName, wasBlocked, true, false,
                    null, attacker, friendly, null, false);
        }
    }
    private MoveResult handleDuel(Unit attacker, Unit defender, Field duelField, Field attackerField,
                                  boolean isKing, boolean wasBlocked) {
        if (isKing) {
            int damage = attacker.getUnitType().attack();
            DuelResult duelResult = attacker.duelWith(null, true);
            setSelectedField(attackerField);
            getGame().getPlayer().setLifePoints(Math.max(0, getGame().getPlayer().getLifePoints() - damage));
            return new MoveResult(duelField, false, damage, 0, true, null, wasBlocked, false, false,
                    duelResult, attacker, null, attackerField, false);
        }
        DuelResult duelResult = attacker.duelWith(defender, false);
        String eliminatedName = null;
        int damageToEnemy = 0;
        int damageToSelf = 0;
        if (duelResult.attackerWins()) {
            getBoard().placeUnit(duelField, attacker);
            getBoard().placeUnit(attackerField, null);
            eliminatedName = defender.getUnitType().qualifier() + " " + defender.getUnitType().role();
            getGame().getPlayer().decrementBoardUnitCount();
        } else if (duelResult.stalemate()) {
            getBoard().placeUnit(attackerField, null);
            getBoard().placeUnit(duelField, null);
            this.decrementBoardUnitCount();
            getGame().getPlayer().decrementBoardUnitCount();
        } else {
            getBoard().placeUnit(attackerField, null);
            eliminatedName = attacker.getUnitType().qualifier() + " " + attacker.getUnitType().role();
            this.decrementBoardUnitCount();
        }
        int attackDiff = attacker.getUnitType().attack() - defender.getUnitType().attack();
        if (attackDiff > 0) {
            damageToEnemy = attackDiff;
            getGame().getPlayer().setLifePoints(Math.max(0, getGame().getPlayer().getLifePoints() - damageToEnemy));
        } else if (attackDiff < 0) {
            damageToSelf = Math.abs(attackDiff);
            dealDamage(damageToSelf);
        }
        return new MoveResult(duelField, false, damageToEnemy, damageToSelf, true, eliminatedName,
                wasBlocked, false, false, duelResult, attacker, defender, attackerField, false);
    }
    private Field findUnitField(Unit unit) {
        for (Field field : getBoard().getAllFields()) {
            FieldPlaceable fp = getBoard().getAt(field);
            if (fp.unit() == unit) {
                return field;
            }
        }
        return null;
    }
    private void dealDamage(int amount) {
        this.setLifePoints(Math.max(0, this.getLifePoints() - amount)); }
    private void maybeDiscard() {
        if (getGame().gameOver) {
            return;
        }
        if (getCards() != null && getCards().size() == 5) {
            List<Integer> weights = new ArrayList<>();
            for (UnitType card : getCards()) {
                int weight = card.attack() + card.defense();
                weights.add(weight);
            }
            int discardIndex = WeightedRandom.pickIndexInverse(weights, rng);
            if (discardIndex >= 0 && discardIndex < getCards().size()) {
                UnitType discardedCard = getCards().get(discardIndex);
                String cardName = discardedCard.qualifier() + " " + discardedCard.role();
                System.out.println(getName() + " discarded " + cardName + " ("
                    + discardedCard.attack() + "/" + discardedCard.defense() + ").");
                getCards().remove(discardIndex);
            }
        }
        String playerName = getGame().getPlayer() != null ? getGame().getPlayer().getName() : "Player";
        System.out.println("It is " + playerName + "'s turn!");
        getGame().switchTurn();
    }
}
