package edu.kit.kastel.view;

/**
 * Command to quit the game.
 * @author uqibg
 */
public class QuitCommand extends Command<Void> {
    /**
     * Constructor for QuitCommand.
     * @param commandHandler The CommandHandler that will handle the execution of this command.
     */
    protected QuitCommand(CommandHandler commandHandler) {
        super(commandHandler);
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            System.out.println("ERROR: 'quit' command takes no arguments.");
            return;
        }
        execute((Void) null);
    }

    @Override
    protected void execute(Void args) {
        commandHandler.quit();
    }
}
