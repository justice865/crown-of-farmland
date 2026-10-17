package edu.kit.kastel.view;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.SymbolSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Handles user input commands and interacts with the Game and SymbolSet.
 * @author uqibg
 */
public class CommandHandler {
    private final Map<String, Command<?>> commands = new HashMap<>();
    private final Game game;
    private final SymbolSet symbolSet;
    private final Scanner scanner = new Scanner(System.in);

    private boolean quit = false;

    /**
     * Constructor for CommandHandler.
     * @param game The Game instance that this CommandHandler will interact with to execute commands.
     * @param symbolSet The SymbolSet that is utilized to interpret symbols
     */
    public CommandHandler(Game game, SymbolSet symbolSet) {
        this.game = game;
        this.symbolSet = symbolSet;

        registerCommand("place", new PlaceCommand(this));
        registerCommand("board", new BoardCommand(this));
        registerCommand("move", new MoveCommand(this));
        registerCommand("quit", new QuitCommand(this));
        registerCommand("show", new ShowCommand(this));
        registerCommand("select", new SelectCommand(this));
        registerCommand("block", new BlockCommand(this));
        registerCommand("flip", new FlipCommand(this));
        registerCommand("hand", new HandCommand(this));
        registerCommand("state", new StateCommand(this));
        registerCommand("yield", new YieldCommand(this));
    }

    private void registerCommand(String name, Command<?> command) {
        commands.put(name, command);
    }

    /**
     * Starts the command handling loop, continuously reading user input and executing commands until the quit flag is set.
     */
    public void handle() {
        while (!quit && scanner.hasNextLine()) {
            String input = scanner.nextLine();
            handleInput(input);
        }
        scanner.close();
    }

    private void handleInput(String input) {
        String[] parts = input.split(" ", 2);
        String commandName = parts[0].toLowerCase();

        Command<?> command = commands.get(commandName.toLowerCase());

        if (command == null) {
            System.out.println("ERROR: Unknown command");
            return;
        }

        command.execute(parts);

        if (quit) {
            return;
        }

        if (!game.isPlayerTurn() && !game.gameOver) {
            game.simulateEnemyTurn();
        }

        if (game.gameOver) {
            outputWinner();
            if (game.getPlayer() != null && game.getEnemy() != null && game.getPlayer().getLifePoints() <= 0) {
                game.getPlayer().setSelectedField(game.getEnemy().getSelectedField());
            }
            new BoardCommand(this).execute((Void) null);
            new ShowCommand(this).execute((Void) null);
            quit = true;
        }
    }

    private void outputWinner() {
        if (game.getPlayer() != null && game.getEnemy() != null) {
            String playerName = game.getPlayer().getName();
            String enemyName = game.getEnemy().getName();

            if (game.getPlayer().getLifePoints() <= 0) {
                System.out.println(enemyName + " wins!");
            } else if (game.getEnemy().getLifePoints() <= 0) {
                System.out.println(playerName + " wins!");
            } else if (game.getPlayer().getDeck().isEmpty()) {
                System.out.println(enemyName + " wins!");
            } else if (game.getEnemy().getDeck().isEmpty()) {
                System.out.println(playerName + " wins!");
            } else {
                System.out.println("Game over!");
            }
        }
    }

    /**
     * Gets the Game instance.
     * @return The Game instance
     */
    public Game getGame() {
        return game;
    }

    /**
     * Sets the quit flag to true, which will cause the command handling loop to exit.
     */
    public void quit() {
        this.quit = true;
    }

    /**
     * Gets the SymbolSet instance.
     * @return The SymbolSet instance
     */
    public SymbolSet getSymbolSet() {
        return symbolSet;
    }
}
