package edu.kit.kastel.view;

import edu.kit.kastel.model.Field;
import edu.kit.kastel.model.FieldPlaceable;
import edu.kit.kastel.model.TeamIndicator;
import edu.kit.kastel.model.Unit;
import edu.kit.kastel.model.UnitType;

/**
 * Command to show information about the currently selected field.
 * @author uqibg
 */
public class ShowCommand extends Command<Void> {
    /**
     * Constructor for ShowCommand.
     * @param commandHandler The CommandHandler that will handle the execution of this command
     */
    protected ShowCommand(CommandHandler commandHandler) {
        super(commandHandler);
    }

    @Override
    protected void execute(Void args) {
        Field selectedField = commandHandler.getGame().getPlayer().getSelectedField();
        if (selectedField == null) {
            System.out.println("ERROR: No field selected.");
            return;
        }
        FieldPlaceable fieldInfo = commandHandler.getGame().getPlayer().getBoard().getAt(selectedField);
        if (fieldInfo.playerKing()) {
            System.out.println(commandHandler.getGame().getPlayer().getName() + "'s Farmer King");
            return;
        }

        if (fieldInfo.cpuKing()) {
            System.out.println(commandHandler.getGame().getEnemy().getName() + "'s Farmer King");
            return;
        }
        Unit unit = fieldInfo.unit();
        if (unit == null) {
            System.out.println("<no unit>");
            return;
        }
        TeamIndicator team = unit.getTeamIndicator();
        String teamString = (team == TeamIndicator.PLAYER)
                ? commandHandler.getGame().getPlayer().getName()
                : commandHandler.getGame().getEnemy().getName();
        boolean isVisible = (team == TeamIndicator.PLAYER) || !unit.isFaceDown();

        if (isVisible) {
            UnitType unitType = unit.getUnitType();
            String name = unitType.qualifier() + " " + unitType.role();
            System.out.println(name + " (Team " + teamString + ")");
            System.out.println("ATK: " + unitType.attack());
            System.out.println("DEF: " + unitType.defense());
        } else {
            System.out.println("??? (Team " + teamString + ")");
            System.out.println("ATK: ???");
            System.out.println("DEF: ???");
        }
    }
}
