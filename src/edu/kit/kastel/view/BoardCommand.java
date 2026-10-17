package edu.kit.kastel.view;

import edu.kit.kastel.model.Board;
import edu.kit.kastel.model.Field;
import edu.kit.kastel.model.SymbolSet;
import edu.kit.kastel.model.Verbosity;

/**
 * The BoardCommand class is responsible for showing the current state of the board for the user.
 * @author uqibg
 */
public class BoardCommand extends Command<Void> {
    /**
     * Constructs a new BoardCommand with the given CommandHandler.
     * @param commandHandler the CommandHandler that provides access to the game state
     */
    public BoardCommand(CommandHandler commandHandler) {
        super(commandHandler);
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 1) {
            System.out.println("ERROR: The 'board' command does not take any arguments.");
        } else {
            execute((Void) null);
        }
    }

    @Override
    protected void execute(Void args) {
        Board board = commandHandler.getGame().getPlayer().getBoard();
        Field selectedField = commandHandler.getGame().getPlayer().getSelectedField();

        Verbosity verbosity = commandHandler.getGame().getVerbosity();
        SymbolSet symbolSet = commandHandler.getSymbolSet();

        BoardRenderer renderer = new BoardRenderer();
        String boardOutput = renderer.render(board, selectedField, verbosity, symbolSet);
        System.out.println(boardOutput);
    }
}
