package edu.kit.kastel;

import edu.kit.kastel.model.Verbosity;
import edu.kit.kastel.view.DeckConfigurationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Parses and validates startup arguments.
 * @author uqibg
 */
final class ArgumentParser {
    private final long seed;
    private final String boardPath;
    private final String unitsPath;
    private final String deckPath;
    private final String deck1Path;
    private final String deck2Path;
    private final String team1NameValue;
    private final String team2NameValue;
    private final String verbosityValue;
    private final boolean singleDeck;

    /**
     * Parses command line key-value arguments.
     * @param args startup arguments
     * @throws DeckConfigurationException if an argument is invalid
     */
    ArgumentParser(String[] args) throws DeckConfigurationException {
        Map<String, String> valuesByKey = collectValuesByKey(args);
        this.seed = parseSeed(valuesByKey);
        this.boardPath = valuesByKey.get("board");
        this.unitsPath = requireUnitsPath(valuesByKey);
        this.deckPath = valuesByKey.get("deck");
        this.deck1Path = valuesByKey.get("deck1");
        this.deck2Path = valuesByKey.get("deck2");
        this.team1NameValue = valuesByKey.get("team1");
        this.team2NameValue = valuesByKey.get("team2");
        this.verbosityValue = valuesByKey.get("verbosity");
        this.singleDeck = validateDeckMode();
    }

    private Map<String, String> collectValuesByKey(String[] args) throws DeckConfigurationException {
        Set<String> seenKeys = new HashSet<>();
        Map<String, String> valuesByKey = new HashMap<>();
        for (String arg : args) {
            String key = extractKey(arg);
            if (!isSupportedKey(key)) {
                throw new DeckConfigurationException("ERROR: Unknown argument key: " + key);
            }
            if (!seenKeys.add(key)) {
                throw new DeckConfigurationException("ERROR: Duplicate argument key: " + key);
            }
            valuesByKey.put(key, extractValue(arg));
        }
        return valuesByKey;
    }

    private long parseSeed(Map<String, String> valuesByKey) throws DeckConfigurationException {
        String seedValue = valuesByKey.get("seed");
        if (seedValue == null) {
            throw new DeckConfigurationException("ERROR: seed argument is required");
        }
        try {
            return Long.parseLong(seedValue);
        } catch (NumberFormatException e) {
            throw new DeckConfigurationException("ERROR: Invalid seed value");
        }
    }

    private String requireUnitsPath(Map<String, String> valuesByKey) throws DeckConfigurationException {
        String parsedUnitsPath = valuesByKey.get("units");
        if (parsedUnitsPath == null) {
            throw new DeckConfigurationException("ERROR: units argument is required");
        }
        return parsedUnitsPath;
    }

    private String parseTeamName(String value, String teamKey, String defaultName)
        throws DeckConfigurationException {
        if (value == null) {
            return defaultName;
        }
        if (value.length() > 14) {
            throw new DeckConfigurationException("ERROR: " + teamKey + " name exceeds 14 characters.");
        }
        return value;
    }

    private Verbosity parseVerbosity(String value) throws DeckConfigurationException {
        if (value == null) {
            return Verbosity.ALL;
        }
        if ("all".equalsIgnoreCase(value)) {
            return Verbosity.ALL;
        }
        if ("compact".equalsIgnoreCase(value)) {
            return Verbosity.COMPACT;
        }
        throw new DeckConfigurationException("ERROR: Invalid verbosity value.");
    }

    private boolean validateDeckMode() throws DeckConfigurationException {
        boolean hasDeck = this.deckPath != null;
        boolean hasDeck1 = this.deck1Path != null;
        boolean hasDeck2 = this.deck2Path != null;

        if (hasDeck && (hasDeck1 || hasDeck2)) {
            throw new DeckConfigurationException("ERROR: Cannot use both 'deck' and 'deck1'/'deck2' modes");
        }
        if ((hasDeck1 && !hasDeck2) || (!hasDeck1 && hasDeck2)) {
            throw new DeckConfigurationException("ERROR: Must provide both 'deck1' and 'deck2' or neither");
        }
        if (!hasDeck && !hasDeck1) {
            throw new DeckConfigurationException("ERROR: Must provide either 'deck' or both 'deck1' and 'deck2'");
        }
        return hasDeck;
    }

    private String extractKey(String arg) throws DeckConfigurationException {
        int eq = arg.indexOf('=');
        if (eq <= 0) {
            throw new DeckConfigurationException("ERROR: Invalid argument format: " + arg);
        }
        return arg.substring(0, eq);
    }

    private String extractValue(String arg) {
        int indexOfEquationSymbol = arg.indexOf('=');
        return arg.substring(indexOfEquationSymbol + 1);
    }

    private boolean isSupportedKey(String key) {
        return switch (key) {
            case "seed", "board", "units", "deck", "deck1", "deck2", "team1", "team2", "verbosity" -> true;
            default -> false;
        };
    }

    /**
     * Gets the seed for randomization.
     * @return seed value
     */
    long getSeed() {
        return seed;
    }

    /**
     * Gets the optional board path.
     * @return board path or null
     */
    String getBoardPath() {
        return boardPath;
    }

    /**
     * Gets the units file path.
     * @return units path
     */
    String getUnitsPath() {
        return unitsPath;
    }

    /**
     * Gets single-deck path.
     * @return deck path or null
     */
    String getDeckPath() {
        return deckPath;
    }

    /**
     * Gets first dual-deck path.
     * @return deck1 path or null
     */
    String getDeck1Path() {
        return deck1Path;
    }

    /**
     * Gets second dual-deck path.
     * @return deck2 path or null
     */
    String getDeck2Path() {
        return deck2Path;
    }

    /**
     * Resolves player team name and validates the value.
     * @return team1 name
     * @throws DeckConfigurationException if team1 value is invalid
     */
    String resolveTeam1Name() throws DeckConfigurationException {
        return parseTeamName(team1NameValue, "team1", "Player");
    }

    /**
     * Resolves enemy team name and validates the value.
     * @return team2 name
     * @throws DeckConfigurationException if team2 value is invalid
     */
    String resolveTeam2Name() throws DeckConfigurationException {
        return parseTeamName(team2NameValue, "team2", "Enemy");
    }

    /**
     * Resolves configured verbosity and validates the value.
     * @return verbosity mode
     * @throws DeckConfigurationException if verbosity value is invalid
     */
    Verbosity resolveVerbosity() throws DeckConfigurationException {
        return parseVerbosity(verbosityValue);
    }

    /**
     * Indicates whether startup uses one shared deck config.
     * @return true for single-deck mode
     */
    boolean hasSingleDeck() {
        return singleDeck;
    }
}
