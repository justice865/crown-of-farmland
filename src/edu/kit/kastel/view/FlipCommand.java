package edu.kit.kastel.view;

import edu.kit.kastel.FieldOrUnitNotExistingException;
import edu.kit.kastel.model.Field;
import edu.kit.kastel.model.Unit;
import edu.kit.kastel.model.UnitType;

/**
 * Command to flip a unit on the board.
 * @author uqibg
 */
public class FlipCommand extends Command<Field> {
    private final BoardCommand boardCommand;
    private final ShowCommand showCommand;

    /**
     * Constructor for FlipCommand.
     * @param commandHandler The CommandHandler instance that this FlipCommand will interact with
     */
    protected FlipCommand(CommandHandler commandHandler) {
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
        Field selected = commandHandler.getGame().getPlayer().getSelectedField();
        if (selected != null && args.length == 1) {
            return selected;
        } else {
            throw new InvalidFieldFormatException("Flip doesnt need any arguments");
        }
    }

    @Override
    protected void execute(Field targetField) {
        Unit flippedUnit;
        try {
            flippedUnit = commandHandler.getGame().getPlayer().flip(targetField);
        } catch (FieldOrUnitNotExistingException e) {
            System.out.println(e.getMessage());
            return;
        }
        if (flippedUnit != null) {
            UnitType unitType = flippedUnit.getUnitType();
            String unitName = unitType.qualifier() + " " + unitType.role();
            String fieldName = "" + targetField.column() + targetField.row();
            System.out.println(unitName + " (" + unitType.attack() + "/" + unitType.defense() + ") was flipped on " + fieldName + "!");
        }

        boardCommand.execute((Void) null);
        showCommand.execute((Void) null);
    }
}
