package edu.kit.kastel.model;
import edu.kit.kastel.view.BoardRenderer;
/**
 * A helper class that provides methods necessary for enemy output generations.
 * @author uqibg
 */
public class EnemyOutputHelper {
    private final Board board;
    private final Game game;
    /**
     * Constructor for EnemyOutputHelper.
     * @param board the board to be used as a source of information for the outputs
     * @param game the game object to be used as a source of information for the outputs
     */
    public EnemyOutputHelper(Board board, Game game) {
        this.board = board;
        this.game = game;
    }
    /**
     * Provides output for the enemy king's move.
     * @param result the MoveResult of king, containing needed information for the output
     */
    public void outputKingMove(MoveResult result) {
        String fieldName = "" + result.resultField().column() + result.resultField().row();
        System.out.println("Farmer King moves to " + fieldName + ".");

        if (result.eliminatedUnitName() != null) {
            System.out.println(result.eliminatedUnitName() + " was eliminated!");
        }
    }
    /**
     * Provides output for the enemy's placement of a unit.
     * @param result the PlaceResult of the placement, containing needed information for the output
     */
    public void outputPlacement(PlaceResult result) {
        String unitName = result.placedUnit().qualifier() + " " + result.placedUnit().role();
        String fieldName = "" + result.targetField().column() + result.targetField().row();
        System.out.println(enemyName() + " places " + unitName + " on " + fieldName + ".");

        if (result.mergeAttempted()) {
            String existingName = result.existingUnit().qualifier() + " " + result.existingUnit().role();
            System.out.println(unitName + " and " + existingName + " on " + fieldName + " join forces!");
            if (result.mergeSuccessful()) {
                System.out.println("Success!");
            } else {
                System.out.println("Union failed. " + existingName + " was eliminated.");
            }
            return;
        }

        if (result.unitEliminated()) {
            System.out.println(unitName + " was eliminated!");
        }
    }
    /**
     * Provides output for the enemy's move of a unit, including possible merge or duel results.
     * @param result the MoveResult of the move, containing needed information for the output
     */
    public void outputUnitMove(MoveResult result) {
        if (result == null || result.movingUnit() == null) {
            return;
        }
        Unit unit = result.movingUnit();
        UnitType unitType = unit.getUnitType();
        String unitName = unitType.qualifier() + " " + unitType.role();
        String fieldName = "" + result.resultField().column() + result.resultField().row();
        if (result.wasBlocked()) {
            System.out.println(unitName + " no longer blocks.");
        }
        if (result.isBlock()) {
            System.out.println(unitName + " (" + fieldName + ") blocks!");
            return;
        }
        if (result.mergeAttempted()) {
            outputMerge(unitName, fieldName, result);
            return;
        }
        if (result.duelOccurred() && result.duelResult() != null) {
            outputDuel(unitType, unitName, fieldName, result);
            return;
        }
        System.out.println(unitName + " moves to " + fieldName + ".");
    }
    private void outputMerge(String unitName, String fieldName, MoveResult result) {
        System.out.println(unitName + " moves to " + fieldName + ".");
        Unit targetUnit = result.targetUnit();
        if (targetUnit != null) {
            String targetName = targetUnit.getUnitType().qualifier() + " " + targetUnit.getUnitType().role();
            System.out.println(unitName + " and " + targetName + " on " + fieldName + " join forces!");
            if (result.mergeSuccessful()) {
                System.out.println("Success!");
            } else {
                System.out.println("Union failed. " + targetName + " was eliminated.");
            }
        }
    }
    private void outputDuel(UnitType unitType, String unitName, String fieldName, MoveResult result) {
        Unit target = result.targetUnit();
        DuelResult duel = result.duelResult();
        String attackerInfo = "(" + unitType.attack() + "/" + unitType.defense() + ")";
        String targetName;
        String targetInfo;
        if (target != null && duel.defenderFlipped()) {
            targetName = "???";
            targetInfo = "";
        } else if (target != null) {
            UnitType targetType = target.getUnitType();
            targetName = targetType.qualifier() + " " + targetType.role();
            targetInfo = " (" + targetType.attack() + "/" + targetType.defense() + ")";
        } else {
            targetName = "Farmer King";
            targetInfo = "";
        }
        System.out.println(unitName + " " + attackerInfo + " attacks " + targetName + targetInfo + " on " + fieldName + "!");
        if (duel.attackerFlipped()) {
            String attackerFlipField = fieldName;
            if (result.sourceField() != null) {
                attackerFlipField = "" + result.sourceField().column() + result.sourceField().row();
            }
            System.out.println(unitName + " " + attackerInfo + " was flipped on " + attackerFlipField + "!");
        }
        if (duel.defenderFlipped() && target != null) {
            UnitType targetType = target.getUnitType();
            String realTargetName = targetType.qualifier() + " " + targetType.role();
            String realTargetInfo = "(" + targetType.attack() + "/" + targetType.defense() + ")";
            System.out.println(realTargetName + " " + realTargetInfo + " was flipped on " + fieldName + "!");
        }

        if (result.eliminatedUnitName() != null) {
            System.out.println(result.eliminatedUnitName() + " was eliminated!");
        }
        if (result.damageToSelf() > 0) {
            System.out.println(enemyName() + " takes " + result.damageToSelf() + " damage!");
            if (game.getEnemy() != null && game.getEnemy().getLifePoints() <= 0) {
                System.out.println(enemyName() + "'s life points dropped to 0!");
                game.gameOver = true;
            }
        }
        if (result.damageToEnemy() > 0) {
            System.out.println(playerName() + " takes " + result.damageToEnemy() + " damage!");
            if (game.getPlayer() != null && game.getPlayer().getLifePoints() <= 0) {
                System.out.println(playerName() + "'s life points dropped to 0!");
                game.gameOver = true;
            }
        }

        if (duel.attackerMoves() && !game.gameOver) {
            System.out.println(unitName + " moves to " + fieldName + ".");
        }
    }

    /**
     * Displays the current state of the board with showing the selected field info included.
     * @param selectedField the currently selected field, to be used as the source of its info
     */
    public void displayBoard(Field selectedField) {
        if (game.gameOver) {
            return;
        }
        BoardRenderer renderer = new BoardRenderer();
        String boardOutput = renderer.render(board, selectedField, game.getVerbosity(), game.getSymbolSet());
        System.out.println(boardOutput);
        displaySelectedFieldInfo(selectedField);
    }

    private void displaySelectedFieldInfo(Field selected) {
        if (selected == null) {
            return;
        }

        FieldPlaceable fp = board.getAt(selected);

        if (fp.cpuKing()) {
            System.out.println(enemyName() + "'s Farmer King");
            return;
        }

        if (fp.playerKing()) {
            System.out.println(playerName() + "'s Farmer King");
            return;
        }

        Unit unit = fp.unit();
        if (unit != null) {
            UnitType type = unit.getUnitType();
            String unitName = type.qualifier() + " " + type.role();
            String team = unit.getTeamIndicator() == TeamIndicator.ENEMY ? enemyName() : playerName();
            System.out.println(unitName + " (Team " + team + ")");
            System.out.println("ATK: " + type.attack());
            System.out.println("DEF: " + type.defense());
        }
    }

    private String enemyName() {
        if (game.getEnemy() == null || game.getEnemy().getName() == null || game.getEnemy().getName().isBlank()) {
            return "Enemy";
        }
        return game.getEnemy().getName();
    }

    private String playerName() {
        if (game.getPlayer() == null || game.getPlayer().getName() == null || game.getPlayer().getName().isBlank()) {
            return "Player";
        }
        return game.getPlayer().getName();
    }
}
