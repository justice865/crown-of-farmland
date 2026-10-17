package edu.kit.kastel.view;

import edu.kit.kastel.FieldOrUnitNotExistingException;
import edu.kit.kastel.model.Field;

/**
 * Command to select a unit on the board.
 * @author uqibg
 */
public class SelectCommand extends Command<Field> {
    private final BoardCommand boardCommand;
    private final ShowCommand showCommand;

    /**
     * Constructor for SelectCommand.
     * @param commandHandler The CommandHandler that will handle the execution of this command
     */
    protected SelectCommand(CommandHandler commandHandler) {
        super(commandHandler);
        this.boardCommand = new BoardCommand(commandHandler);
        this.showCommand = new ShowCommand(commandHandler);
    }

    @Override
    public void execute(String[] args) {
        Field field;
        try {
            field = parseField(args);
        } catch (InvalidFieldFormatException e) {
            System.out.println("ERROR: " + e.getMessage());
            return;
        }

        execute(field);
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
    protected void execute(Field field) {
        try {
            commandHandler.getGame().getPlayer().select(field);
        } catch (FieldOrUnitNotExistingException e) {
            System.out.println(e.getMessage());
            return;
        }

        boardCommand.execute((Void) null);
        showCommand.execute((Void) null);
    }
}
