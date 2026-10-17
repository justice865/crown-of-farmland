package edu.kit.kastel.view;

import edu.kit.kastel.FieldOrUnitNotExistingException;
import edu.kit.kastel.model.DuelResult;
import edu.kit.kastel.model.Field;
import edu.kit.kastel.model.MoveResult;
import edu.kit.kastel.model.Unit;
import edu.kit.kastel.model.UnitType;

/**
 * Command to move a unit to a specified field.
 * @author uqibg
 */
public class MoveCommand extends Command<Field> {
    private final BoardCommand boardCommand;
    private final ShowCommand showCommand;

    /**
     * Constructor for MoveCommand.
     * @param commandHandler the CommandHandler to use for executing the command
     */
    protected MoveCommand(CommandHandler commandHandler) {
        super(commandHandler);
        this.boardCommand = new BoardCommand(commandHandler);
        this.showCommand = new ShowCommand(commandHandler);
    }

    @Override
    public void execute(String[] args) {
        Field targetField;
        try {
            targetField = parseField(args);
        } catch (InvalidFieldFormatException e) {
            System.out.println("ERROR: " + e.getMessage());
            return;
        }

        execute(targetField);
    }

    private Field parseField(String[] args) throws InvalidFieldFormatException {
        if (args.length < 2) {
            throw new InvalidFieldFormatException("Invalid field format.");
        }

        String input = args[1].trim();
        if (input.length() < 2) {
            throw new InvalidFieldFormatException("Invalid field format.");
        }

        char column = Character.toUpperCase(input.charAt(0));
        if (column < 'A' || column > 'G') {
            throw new InvalidFieldFormatException("Invalid field format.");
        }

        try {
            int row = Integer.parseInt(input.substring(1));
            if (row < 1 || row > 7) {
                throw new InvalidFieldFormatException("Invalid field format.");
            }
            return new Field(row, column);
        } catch (NumberFormatException e) {
            throw new InvalidFieldFormatException("Invalid field format.");
        }
    }

    @Override
    protected void execute(Field targetField) {
        Field sourceField = commandHandler.getGame().getPlayer().getSelectedField();
        MoveResult result;

        try {
            result = commandHandler.getGame().getPlayer().move(targetField);
        } catch (FieldOrUnitNotExistingException e) {
            System.out.println(e.getMessage());
            return;
        }

        outputMoveResult(result, sourceField);
        applyDamageAndCheckGameOver(result);

        if (commandHandler.getGame().gameOver) {
            outputWinnerForCurrentState();
            boardCommand.execute((Void) null);
            showCommand.execute((Void) null);
            commandHandler.quit();
            return;
        }

        boardCommand.execute((Void) null);
        showCommand.execute((Void) null);
    }

    private void outputMoveResult(MoveResult result, Field sourceField) {
        if (result == null) {
            return;
        }
        if (result.movingUnit() == null) {
            if (!result.enPlace()) {
                System.out.println("Farmer King moves to " + fieldName(result.resultField()) + ".");
                if (result.eliminatedUnitName() != null) {
                    System.out.println(result.eliminatedUnitName() + " was eliminated!");
                }
            }
            return;
        }

        Unit movingUnit = result.movingUnit();
        UnitType movingType = movingUnit.getUnitType();
        String movingName = unitName(movingUnit);

        if (result.wasBlocked()) {
            System.out.println(movingName + " no longer blocks.");
        }

        if (result.enPlace()) {
            return;
        }

        if (result.mergeAttempted()) {
            outputMerge(result, movingName);
            return;
        }

        if (result.duelOccurred() && result.duelResult() != null) {
            outputDuel(result, movingUnit, movingType, sourceField);
            return;
        }

        if (result.resultField() != null) {
            System.out.println(movingName + " moves to " + fieldName(result.resultField()) + ".");
        }
    }

    private void outputMerge(MoveResult result, String movingName) {
        if (result.resultField() == null) {
            return;
        }

        String fieldName = fieldName(result.resultField());
        System.out.println(movingName + " moves to " + fieldName + ".");

        Unit targetUnit = result.targetUnit();
        if (targetUnit == null) {
            return;
        }

        String targetName = unitName(targetUnit);
        System.out.println(movingName + " and " + targetName + " on " + fieldName + " join forces!");

        if (result.mergeSuccessful()) {
            System.out.println("Success!");
        } else {
            System.out.println("Union failed. " + targetName + " was eliminated.");
        }
    }

    private void outputDuel(MoveResult result, Unit movingUnit, UnitType movingType, Field sourceField) {
        Field duelField = result.resultField();
        if (duelField == null) {
            return;
        }

        DuelResult duel = result.duelResult();
        Unit target = result.targetUnit();

        String attackerName = unitName(movingUnit);
        String attackerInfo = "(" + movingType.attack() + "/" + movingType.defense() + ")";

        String targetLabel;
        if (target == null) {
            targetLabel = "Farmer King";
        } else if (duel.defenderFlipped()) {
            targetLabel = "???";
        } else {
            UnitType targetType = target.getUnitType();
            targetLabel = unitName(target) + " (" + targetType.attack() + "/" + targetType.defense() + ")";
        }

        System.out.println(attackerName + " " + attackerInfo + " attacks " + targetLabel + " on " + fieldName(duelField) + "!");

        if (duel.attackerFlipped() && sourceField != null) {
            System.out.println(attackerName + " " + attackerInfo + " was flipped on " + fieldName(sourceField) + "!");
        }

        if (duel.defenderFlipped() && target != null) {
            UnitType targetType = target.getUnitType();
            System.out.println(unitName(target) + " (" + targetType.attack() + "/" + targetType.defense() + ") was flipped on "
                    + fieldName(duelField) + "!");
        }

        if (result.eliminatedUnitName() != null) {
            System.out.println(result.eliminatedUnitName() + " was eliminated!");
        }
    }

    private void applyDamageAndCheckGameOver(MoveResult result) {
        int playerDamage = Math.max(0, result.damageToSelf());
        int enemyDamage = Math.max(0, result.damageToEnemy());

        String playerName = commandHandler.getGame().getPlayer().getName();
        String enemyName = commandHandler.getGame().getEnemy().getName();
        boolean playerDroppedToZero = false;
        boolean enemyDroppedToZero = false;

        if (playerDamage > 0) {
            int lp = Math.max(0, commandHandler.getGame().getPlayer().getLifePoints() - playerDamage);
            commandHandler.getGame().getPlayer().setLifePoints(lp);
            System.out.println(playerName + " takes " + playerDamage + " damage!");
            playerDroppedToZero = lp == 0;
        }

        if (enemyDamage > 0) {
            int lp = Math.max(0, commandHandler.getGame().getEnemy().getLifePoints() - enemyDamage);
            commandHandler.getGame().getEnemy().setLifePoints(lp);
            System.out.println(enemyName + " takes " + enemyDamage + " damage!");
            enemyDroppedToZero = lp == 0;
        }

        if (result.duelOccurred() && result.duelResult() != null && result.duelResult().attackerMoves()
                && result.movingUnit() != null && result.resultField() != null) {
            System.out.println(unitName(result.movingUnit()) + " moves to " + fieldName(result.resultField()) + ".");
        }

        if (playerDroppedToZero) {
            System.out.println(playerName + "'s life points dropped to 0!");
            commandHandler.getGame().gameOver = true;
        }
        if (enemyDroppedToZero) {
            System.out.println(enemyName + "'s life points dropped to 0!");
            commandHandler.getGame().gameOver = true;
        }
    }

    private void outputWinnerForCurrentState() {
        String playerName = commandHandler.getGame().getPlayer().getName();
        String enemyName = commandHandler.getGame().getEnemy().getName();

        if (commandHandler.getGame().getEnemy().getLifePoints() <= 0) {
            System.out.println(playerName + " wins!");
            return;
        }
        if (commandHandler.getGame().getPlayer().getLifePoints() <= 0) {
            System.out.println(enemyName + " wins!");
            return;
        }
        if (commandHandler.getGame().getEnemy().getDeck().isEmpty()) {
            System.out.println(playerName + " wins!");
            return;
        }
        if (commandHandler.getGame().getPlayer().getDeck().isEmpty()) {
            System.out.println(enemyName + " wins!");
            return;
        }

        System.out.println("Game over!");
    }

    private String unitName(Unit unit) {
        return unit.getUnitType().qualifier() + " " + unit.getUnitType().role();
    }

    private String fieldName(Field field) {
        return "" + field.column() + field.row();
    }
}
