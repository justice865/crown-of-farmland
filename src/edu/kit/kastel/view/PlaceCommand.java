package edu.kit.kastel.view;

import edu.kit.kastel.FieldOrUnitNotExistingException;
import edu.kit.kastel.InvalidCardIndexException;
import edu.kit.kastel.model.PlaceResult;
import edu.kit.kastel.model.UnitType;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the "place" command, allowing the player to place one or more cards from their hand onto the board.
 * @author uqibg
 */
public class PlaceCommand extends Command<int[]> {
    private final BoardCommand boardCommand;
    private final ShowCommand showCommand;

    /**
     * Constructor for PlaceCommand.
     * @param commandHandler The CommandHandler instance that this PlaceCommand will interact with
     */
    public PlaceCommand(CommandHandler commandHandler) {
        super(commandHandler);
        this.boardCommand = new BoardCommand(commandHandler);
        this.showCommand = new ShowCommand(commandHandler);
    }

    @Override
    public void execute(String[] args) {
        int[] indices;
        try {
            indices = parseIndices(args);
        } catch (InvalidFieldFormatException e) {
            System.out.println("ERROR: " + e.getMessage());
            return;
        }

        execute(indices);
    }

    private int[] parseIndices(String[] args) throws InvalidFieldFormatException {
        if (args.length < 2 || args[1].trim().isEmpty()) {
            throw new InvalidFieldFormatException("No card indices provided.");
        }

        String[] tokens = args[1].trim().split("\\s+");
        List<Integer> parsed = new ArrayList<>();

        for (String token : tokens) {
            int oneBased;
            try {
                oneBased = Integer.parseInt(token);
            } catch (NumberFormatException e) {
                throw new InvalidFieldFormatException("Invalid card index.");
            }

            if (oneBased <= 0) {
                throw new InvalidFieldFormatException("Invalid card index.");
            }
            parsed.add(oneBased - 1);
        }

        int[] indices = new int[parsed.size()];
        for (int i = 0; i < parsed.size(); i++) {
            indices[i] = parsed.get(i);
        }
        return indices;
    }

    @Override
    protected void execute(int[] indices) {
        List<PlaceResult> results;
        try {
            results = commandHandler.getGame().getPlayer().place(indices);
        } catch (FieldOrUnitNotExistingException | InvalidCardIndexException e) {
            System.out.println(e.getMessage());
            return;
        }

        String team = commandHandler.getGame().getPlayer().getName();
        for (PlaceResult result : results) {
            outputPlaceResult(team, result);
        }

        boardCommand.execute((Void) null);
        showCommand.execute((Void) null);
    }

    private void outputPlaceResult(String team, PlaceResult result) {
        String placedName = unitName(result.placedUnit());
        String fieldName = fieldName(result.targetField());

        System.out.println(team + " places " + placedName + " on " + fieldName + ".");

        if (result.mergeAttempted()) {
            String existingName = unitName(result.existingUnit());
            System.out.println(placedName + " and " + existingName + " on " + fieldName + " join forces!");

            if (result.mergeSuccessful()) {
                System.out.println("Success!");
            } else {
                System.out.println("Union failed. " + existingName + " was eliminated.");
            }
            return;
        }

        if (result.unitEliminated()) {
            System.out.println(placedName + " was eliminated!");
        }
    }

    private String unitName(UnitType type) {
        if (type == null) {
            return "Unknown";
        }
        return type.qualifier() + " " + type.role();
    }

    private String fieldName(edu.kit.kastel.model.Field field) {
        return "" + field.column() + field.row();
    }
}
