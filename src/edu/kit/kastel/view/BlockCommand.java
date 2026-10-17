package edu.kit.kastel.view;

import edu.kit.kastel.FieldOrUnitNotExistingException;
import edu.kit.kastel.model.Field;
import edu.kit.kastel.model.FieldPlaceable;
import edu.kit.kastel.model.Unit;
import edu.kit.kastel.model.UnitType;

/**
 * The BlockCommand class represents the command to block a unit on the board.
 * @author uqibg
 */
public class BlockCommand extends Command<Field> {
    private final BoardCommand boardCommand;
    private final ShowCommand showCommand;

    /**
     * Constructor for the BlockCommand class.
     * @param commandHandler the CommandHandler instance to handle the command execution
     */
    protected BlockCommand(CommandHandler commandHandler) {
        super(commandHandler);
        this.boardCommand = new BoardCommand(commandHandler);
        this.showCommand = new ShowCommand(commandHandler);
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            System.out.println("ERROR: 'block' command takes no arguments.");
            return;
        }

        Field selectedField = commandHandler.getGame().getPlayer().getSelectedField();
        execute(selectedField);
    }

    @Override
    protected void execute(Field targetField) {
        try {
            commandHandler.getGame().getPlayer().block(targetField);
        } catch (FieldOrUnitNotExistingException e) {
            System.out.println(e.getMessage());
            return;
        }
        FieldPlaceable fieldInfo = commandHandler.getGame().getPlayer().getBoard().getAt(targetField);
        Unit unit = fieldInfo.unit();

        if (unit != null) {
            UnitType unitType = unit.getUnitType();
            String unitName = unitType.qualifier() + " " + unitType.role();
            String fieldName = "" + targetField.column() + targetField.row();
            System.out.println(unitName + " (" + fieldName + ") blocks!");
        }

        boardCommand.execute((Void) null);
        showCommand.execute((Void) null);
    }
}
