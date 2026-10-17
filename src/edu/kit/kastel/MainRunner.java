package edu.kit.kastel;

import edu.kit.kastel.model.Board;
import edu.kit.kastel.model.EnemyTeam;
import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.PlayerTeam;
import edu.kit.kastel.model.SymbolSet;
import edu.kit.kastel.model.TeamIndicator;
import edu.kit.kastel.model.UnitType;
import edu.kit.kastel.model.Verbosity;
import edu.kit.kastel.view.BoardSymbolParser;
import edu.kit.kastel.view.CommandHandler;
import edu.kit.kastel.view.DeckAndUnitParser;
import edu.kit.kastel.view.DeckConfigurationException;
import edu.kit.kastel.view.FileLoader;
import edu.kit.kastel.view.InvalidSymbolSetException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Executes game startup flow outside of Main to keep the entrypoint concise.
 * @author uqibg
 */
public final class MainRunner {
    /**
     * Starts the game.
     * @param args Command line arguments
     * @throws DeckConfigurationException if there is an error in loading the decks
     *                                    or symbol set from the provided file paths
     */
    public void run(String[] args) throws DeckConfigurationException {
        ArgumentParser argParser = new ArgumentParser(args);
        SymbolSet symbolSet = loadSymbolSet(argParser.getBoardPath());
        DeckAndUnitParser parser = new DeckAndUnitParser();
        List<UnitType> units = parser.parseUnits(argParser.getUnitsPath());
        Random rng = new Random(argParser.getSeed());
        DeckLists deckLists = buildShuffledDecks(argParser, parser, units, rng);
        String team1Name = argParser.resolveTeam1Name();
        String team2Name = argParser.resolveTeam2Name();
        Verbosity verbosity = argParser.resolveVerbosity();
        Game game = createGame(symbolSet, rng, deckLists.playerDeck(), deckLists.enemyDeck(),
            team1Name, team2Name, verbosity);
        startCommandLoop(game, symbolSet);
    }

    private SymbolSet loadSymbolSet(String boardPath) throws DeckConfigurationException {
        if (boardPath == null) {
            return new SymbolSet();
        }
        try {
            List<String> lines = new FileLoader().loadAndEcho(boardPath);
            return new BoardSymbolParser().parse(lines);
        } catch (IOException e) {
            throw new DeckConfigurationException("ERROR: Could not read file: " + boardPath);
        } catch (InvalidSymbolSetException e) {
            throw new DeckConfigurationException(e.getMessage());
        }
    }

    private DeckLists buildShuffledDecks(ArgumentParser argParser, DeckAndUnitParser parser,
                                         List<UnitType> units, Random rng) throws DeckConfigurationException {
        List<Integer> playerDeckCounts;
        List<Integer> enemyDeckCounts;
        if (argParser.hasSingleDeck()) {
            playerDeckCounts = parser.parseDeck(argParser.getDeckPath(), units.size());
            enemyDeckCounts = new ArrayList<>(playerDeckCounts);
        } else {
            playerDeckCounts = parser.parseDeck(argParser.getDeck1Path(), units.size());
            enemyDeckCounts = parser.parseDeck(argParser.getDeck2Path(), units.size());
        }

        List<UnitType> playerDeck = parser.buildDeck(units, playerDeckCounts);
        Collections.shuffle(playerDeck, rng);
        List<UnitType> enemyDeck = parser.buildDeck(units, enemyDeckCounts);
        Collections.shuffle(enemyDeck, rng);
        return new DeckLists(playerDeck, enemyDeck);
    }

    private Game createGame(SymbolSet symbolSet, Random rng,
                            List<UnitType> playerDeck, List<UnitType> enemyDeck,
                            String team1Name, String team2Name, Verbosity verbosity) {
        Game game = new Game();
        Board board = new Board();
        PlayerTeam player = new PlayerTeam(TeamIndicator.PLAYER);
        EnemyTeam enemy = new EnemyTeam(TeamIndicator.ENEMY);

        player.setName(team1Name);
        enemy.setName(team2Name);

        player.setBoard(board);
        player.setGame(game);
        player.setDeck(playerDeck);
        player.setCards(new ArrayList<>());

        enemy.setBoard(board);
        enemy.setGame(game);
        enemy.setDeck(enemyDeck);
        enemy.setCards(new ArrayList<>());
        enemy.setRng(rng);

        game.setPlayer(player);
        game.setEnemy(enemy);
        game.setBoard(board);
        game.setVerbosity(verbosity);
        game.setSymbolSet(symbolSet);

        board.setPlayerKingHasMoved(false);
        player.draw();
        enemy.drawOpeningHandOnly();
        return game;
    }

    private void startCommandLoop(Game game, SymbolSet symbolSet) {
        CommandHandler handler = new CommandHandler(game, symbolSet);
        System.out.println("Use one of the following commands: select,"
                + " board, move, flip, block, hand, place, show, yield, state, quit.");
        handler.handle();
    }
}
