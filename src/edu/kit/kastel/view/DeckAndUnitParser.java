package edu.kit.kastel.view;

import edu.kit.kastel.model.UnitType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for parsing unit and deck configuration files.
 * @author uqibg
 */
public class DeckAndUnitParser {

    /**
     * Parses a units file and returns a list of UnitType objects.
     * @param filePath the path to the units file
     * @return a list of UnitType objects representing the UnitType(card attributes) defined in the file
     * @throws DeckConfigurationException if the file is invalid or cannot be read
     */
    public List<UnitType> parseUnits(String filePath) throws DeckConfigurationException {
        List<String> lines;
        try {
            lines = new FileLoader().loadAndEcho(filePath);
        } catch (IOException e) {
            throw new DeckConfigurationException("ERROR: Could not read units file: " + filePath);
        }

        if (lines.isEmpty()) {
            throw new DeckConfigurationException("ERROR: Units file must not be empty.");
        }
        if (lines.size() > 80) {
            throw new DeckConfigurationException("ERROR: Units file must have at most 80 lines, but got " + lines.size());
        }

        List<UnitType> units = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.endsWith(";")) {
                throw new DeckConfigurationException(
                    "ERROR: Unit line " + (i + 1) + " has trailing semicolon: \"" + line + "\""
                );
            }

            String[] fields = line.split(";", -1);
            if (fields.length != 4) {
                throw new DeckConfigurationException(
                    "ERROR: Unit line " + (i + 1) + " must have exactly 4 fields: \"" + line + "\""
                );
            }

            String qualifier = fields[0];
            String role = fields[1];
            int attack;
            int defense;

            try {
                attack = Integer.parseInt(fields[2]);
                defense = Integer.parseInt(fields[3]);
            } catch (NumberFormatException e) {
                throw new DeckConfigurationException(
                    "ERROR: Unit line " + (i + 1)
                        + " has invalid attack or defense value: \"" + line + "\""
                );
            }

            if (attack < 0 || defense < 0) {
                throw new DeckConfigurationException(
                    "ERROR: Unit line " + (i + 1)
                        + " has negative attack or defense value: \"" + line + "\""
                );
            }

            units.add(new UnitType(qualifier, role, attack, defense));
        }

        return units;
    }

    /**
     * Parses a deck file and returns a list of card counts.
     * @param filePath the path to the deck file
     * @param expectedLineCount the expected number of lines in the deck file
     *                          (should match the number of unit types defined in the units file)
     * @return a list of integers representing the count of each unit type in the deck
     * @throws DeckConfigurationException if the file is invalid, cannot be read,
     *                                    or if the total count of cards does not equal 40
     */
    public List<Integer> parseDeck(String filePath, int expectedLineCount) throws DeckConfigurationException {
        List<String> lines;
        try {
            lines = new FileLoader().loadAndEcho(filePath);
        } catch (IOException e) {
            throw new DeckConfigurationException("ERROR: Could not read deck file: " + filePath);
        }

        if (lines.size() != expectedLineCount) {
            throw new DeckConfigurationException(
                "ERROR: Deck file line count must match units file line count (" + expectedLineCount
                    + "), but got " + lines.size() + " lines"
            );
        }

        List<Integer> counts = new ArrayList<>();
        int totalCount = 0;

        for (int i = 0; i < lines.size(); i++) {
            String rawLine = lines.get(i);
            String line = rawLine.trim();
            int count;
            try {
                count = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                throw new DeckConfigurationException(
                    "ERROR: Deck line " + (i + 1) + " is not a valid integer: \"" + rawLine + "\""
                );
            }

            if (count < 0) {
                throw new DeckConfigurationException(
                    "ERROR: Deck line " + (i + 1) + " has negative count: \"" + rawLine + "\""
                );
            }

            counts.add(count);
            totalCount += count;
        }

        if (totalCount != 40) {
            throw new DeckConfigurationException(
                "ERROR: Deck must contain exactly 40 cards, but got " + totalCount
                    + " (after line " + lines.size() + ")"
            );
        }

        return counts;
    }

    /**
     * Builds a deck list from unit types and their counts.
     * @param units the list of unit types defined in the units file
     * @param counts the list of counts for each unit type defined in the deck file
     * @return a list of UnitType objects representing the deck
     * @throws DeckConfigurationException if the sizes of the units and counts lists do not match
     */
    public List<UnitType> buildDeck(List<UnitType> units, List<Integer> counts) throws DeckConfigurationException {
        if (units.size() != counts.size()) {
            throw new DeckConfigurationException("ERROR: Unit count does not match deck count lines");
        }

        List<UnitType> deck = new ArrayList<>();
        for (int i = 0; i < units.size(); i++) {
            UnitType unit = units.get(i);
            int count = counts.get(i);
            for (int j = 0; j < count; j++) {
                deck.add(unit);
            }
        }

        return deck;
    }
}

