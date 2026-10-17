package edu.kit.kastel.view;

/**
 * Abstract base class for commands that can be executed in the application.
 * @param <T> The type of arguments that the command takes after parsing the input string array.
 * @author uqibg
 */
public abstract class Command<T> {
    protected CommandHandler commandHandler;

    /**
     * Constructor for Command.
     * @param commandHandler The CommandHandler that will handle the execution of this command.
     */
    protected Command(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    /**
     * Executes the command with the given arguments.
     * @param args The raw string array of arguments to be parsed and executed by the command.
     */
    public void execute(String[] args) {
        execute(parseArgs(args));
    }

    /**
     * Parses the raw string array of arguments into the appropriate type for execution.
     * @param args The raw string array of arguments to be parsed.
     * @return The parsed arguments of type T to be used for command execution.
     */
    protected T parseArgs(String[] args) {
        return null;
    }

    /**
     * Executes the command with the given parsed arguments.
     * @param args The already parsed arguments of type T to be used for command execution.
     */
    protected abstract void execute(T args);
}
