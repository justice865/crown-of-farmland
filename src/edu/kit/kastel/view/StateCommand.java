package edu.kit.kastel.view;

import edu.kit.kastel.model.EnemyTeam;
import edu.kit.kastel.model.Field;
import edu.kit.kastel.model.PlayerTeam;

/**
 * Command to display the current state of the game, including player and enemy information,
 * as well as the board state. It also shows details of the selected field if applicable.
 * @author uqibg
 */
public class StateCommand extends Command<Void> {
    private static final String LINE_PREFIX = "  ";
    private static final int NAME_LINE_WIDTH = 29;
    private static final int LP_LEFT_AREA_WIDTH = 17;
    private static final int DC_LEFT_AREA_WIDTH = 20;
    private static final int BC_LEFT_AREA_WIDTH = 22;

    private final BoardCommand boardCommand;
    private final ShowCommand showCommand;

    /**
     * Constructor for StateCommand.
     * @param commandHandler The CommandHandler that will handle the execution of this command and provide access to the game state
     */
    protected StateCommand(CommandHandler commandHandler) {
        super(commandHandler);
        this.boardCommand = new BoardCommand(commandHandler);
        this.showCommand = new ShowCommand(commandHandler);
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            System.out.println("ERROR: 'state' command takes no arguments.");
            return;
        }
        execute((Void) null);
    }

    @Override
    protected void execute(Void args) {
        PlayerTeam player = commandHandler.getGame().getPlayer();
        EnemyTeam enemy = commandHandler.getGame().getEnemy();

        String line1Left = player.getName();
        String line1Right = enemy.getName();

        String line2Left = String.format("%d/8000 LP", player.getLifePoints());
        String line2Right = String.format("%d/8000 LP", enemy.getLifePoints());

        String line3Left = String.format("DC: %d/40", player.getDeck().size());
        String line3Right = String.format("DC: %d/40", enemy.getDeck().size());

        String line4Left = String.format("BC: %d/5", player.getBoardUnitCount());
        String line4Right = String.format("BC: %d/5", enemy.getBoardUnitCount());

        System.out.println(formatHeaderAligned(line1Left, line1Right));
        System.out.println(formatAligned(line2Left, line2Right, LP_LEFT_AREA_WIDTH));
        System.out.println(formatAligned(line3Left, line3Right, DC_LEFT_AREA_WIDTH));
        System.out.println(formatAligned(line4Left, line4Right, BC_LEFT_AREA_WIDTH));

        boardCommand.execute((Void) null);

        Field selectedField = player.getSelectedField();
        if (selectedField != null) {
            showCommand.execute((Void) null);
        }
    }

    private String formatHeaderAligned(String left, String right) {
        int spaces = NAME_LINE_WIDTH - left.length() - right.length();
        if (spaces < 1) {
            spaces = 1;
        }
        return LINE_PREFIX + left + " ".repeat(spaces) + right;
    }

    private String formatAligned(String left, String right, int leftAreaWidth) {
        return LINE_PREFIX + String.format("%-" + leftAreaWidth + "s", left) + right;
    }
}
