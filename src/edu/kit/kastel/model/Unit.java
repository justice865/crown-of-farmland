package edu.kit.kastel.model;

/**
 * The Unit class represents a unit in the game.
 * @author uqibg
 */
public class Unit {
    private final UnitType unitType;
    private boolean faceDown;
    private boolean hasMoved;
    private boolean blocked;
    private final TeamIndicator indicator;

    /**
     * Constructor for the Unit class.
     * @param unitType the type of the unit, which determines its attack and defense values
     * @param indicator the team indicator of the unit
     */
    public Unit(UnitType unitType, TeamIndicator indicator) {
        this.unitType = unitType;
        this.indicator = indicator;
        this.faceDown = true;
        this.hasMoved = false;
        this.blocked = false;
    }

    /**
     * Gets the properties of the unit, including its attack and defense values.
     * @return the properties of the unit
     */
    public UnitType getUnitType() {
        return unitType;
    }

    /**
     * Checks if the unit is face down.
     * @return true if the unit is face down, false otherwise
     */
    public boolean isFaceDown() {
        return faceDown;
    }

    /**
     * Sets the face down status of the unit.
     * @param faceDown the new face down status of the unit
     */
    public void setFaceDown(boolean faceDown) {
        this.faceDown = faceDown;
    }

    /**
     * Checks if the unit is blocked.
     * @return true if the unit is blocked, false otherwise
     */
    public boolean isBlocked() {
        return blocked;
    }

    /**
     * Sets the blockade status of the unit.
     * @param blocked the new blockade status of the unit
     */
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    /**
     * Gets the team indicator of the unit.
     * @return the team indicator of the unit
     */
    public TeamIndicator getTeamIndicator() {
        return this.indicator;
    }

    /**
     * Checks if the unit has moved this turn.
     * @return true if the unit has moved this turn, false otherwise
     */
    public boolean hasMoved() {
        return this.hasMoved;
    }

    /**
     * Sets whether the unit has moved this turn.
     * @param hasMoved true if the unit has moved this turn, false otherwise
     */
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    /**
     * Resolves a duel between this unit (the attacker) and a defender unit.
     * @param defender the unit being attacked
     * @param defenderIsKing indicates whether the defender is the king or not
     * @return a DuelResult object that contains the outcomes of the duel
     */
    public DuelResult duelWith(Unit defender, boolean defenderIsKing) {
        boolean attackerWasFlipped = this.faceDown;
        boolean defenderWasFlipped = defender != null && defender.faceDown;
        this.setFaceDown(false);
        if (defender != null) {
            defender.setFaceDown(false);
        }
        if (defenderIsKing) {
            return new DuelResult(true, true, false, false, attackerWasFlipped, false);
        }
        if (defender != null && defender.isBlocked()) {
            int attackerAttack = this.unitType.attack();
            int defenderDefense = defender.unitType.defense();

            if (attackerAttack > defenderDefense) {
                return new DuelResult(true, true, false, true, attackerWasFlipped, defenderWasFlipped);
            } else if (attackerAttack == defenderDefense) {
                return new DuelResult(true, false, true, false, attackerWasFlipped, defenderWasFlipped);
            } else {
                return new DuelResult(true, false, false, false, attackerWasFlipped, defenderWasFlipped);
            }
        }
        if (defender != null) {
            int attackerAttack = this.unitType.attack();
            int defenderAttack = defender.unitType.attack();

            if (attackerAttack > defenderAttack) {
                return new DuelResult(true, true, false, true, attackerWasFlipped, defenderWasFlipped);
            } else if (attackerAttack < defenderAttack) {
                return new DuelResult(true, false, false, false, attackerWasFlipped, defenderWasFlipped);
            } else {
                return new DuelResult(true, false, false, false, attackerWasFlipped, defenderWasFlipped);
            }
        }

        return new DuelResult(true, false, false, false, attackerWasFlipped, false);
    }
}
