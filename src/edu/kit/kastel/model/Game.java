package edu.kit.kastel.model;
/**
 * The Game class represents the overall state and logic of the game,
 * including the board, player and enemy teams, turn management, and game over conditions.
 * @author uqibg
 */
public class Game {
    /**
     * Indicates whether the game is over.
     */
    public boolean gameOver = false;
    Turn playerTurn = Turn.PLAYER_TEAM;
    private Board board;
    private PlayerTeam player;
    private EnemyTeam enemy;
    private Verbosity verbosity = Verbosity.ALL;
    private SymbolSet symbolSet = new SymbolSet();
    private boolean enemyFirstTurnDrawPending = true;

    /**
     * Simulates the enemy's turn by invoking the doMove method of the enemy team.
     */
    public void simulateEnemyTurn() {
        this.enemy.doMove();
    }

    private boolean gameOverByDeck() {
        Team currentTeam = (playerTurn == Turn.PLAYER_TEAM) ? player : enemy;
        return currentTeam.getDeck().isEmpty();
    }

    /**
     * Checks whose turn it is.
     * @return true if it's the player's turn, false if it's the enemy's turn
     */
    public boolean isPlayerTurn() {
        return playerTurn == Turn.PLAYER_TEAM;
    }

    /**
     * Switches the turn to the other team, clears selected fields,
     * checks for game over conditions,
     * and draws a card for the team whose turn just started.
     */
    public void switchTurn() {
        clearSelectedFields();
        if (playerTurn == Turn.PLAYER_TEAM) {
            playerTurn = Turn.ENEMY_TEAM;
        } else {
            playerTurn = Turn.PLAYER_TEAM;
        }

        if (gameOverByDeck()) {
            gameOver = true;
            return;
        }

        Team activeTeam = (playerTurn == Turn.PLAYER_TEAM) ? player : enemy;
        if (playerTurn == Turn.ENEMY_TEAM && enemyFirstTurnDrawPending) {
            enemyFirstTurnDrawPending = false;
        } else {
            activeTeam.draw();
        }

        resetActiveTeamMoveState();
    }

    private void resetActiveTeamMoveState() {
        if (board == null || player == null || enemy == null) {
            return;
        }

        Team activeTeam = (playerTurn == Turn.PLAYER_TEAM) ? player : enemy;
        TeamIndicator activeIndicator = activeTeam.getTeamIndicator();

        for (Field field : board.getAllFields()) {
            Unit unit = board.getAt(field).unit();
            if (unit != null && unit.getTeamIndicator() == activeIndicator) {
                unit.setHasMoved(false);
            }
        }

        if (playerTurn == Turn.PLAYER_TEAM) {
            board.setPlayerKingHasMoved(false);
        } else {
            board.setCpuKingHasMoved(false);
        }
    }

    private void clearSelectedFields() {
        if (player != null) {
            player.setSelectedField(null);
        }
        if (enemy != null) {
            enemy.setSelectedField(null);
        }
    }

    /**
     * Gets the player team.
     * @return the player team
     */
    public PlayerTeam getPlayer() {
        return player;
    }

    /**
     * Gets the enemy team.
     * @return the enemy team
     */
    public EnemyTeam getEnemy() {
        return enemy;
    }

    /**
     * Sets the player team.
     * @param player the player team to set
     */
    public void setPlayer(PlayerTeam player) {
        this.player = player;
    }

    /**
     * Sets the enemy team.
     * @param enemy the enemy team to set
     */
    public void setEnemy(EnemyTeam enemy) {
        this.enemy = enemy;
    }

    /**
     * Sets the board for the game.
     * @param board the board to set
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Gets the verbosity level for game output.
     * @return the verbosity level for game output
     */
    public Verbosity getVerbosity() {
        return verbosity;
    }

    /**
     * Sets the verbosity level for game output.
     * If the provided verbosity is null, it defaults to Verbosity.ALL.
     * @param verbosity the verbosity level to set for game output
     */
    public void setVerbosity(Verbosity verbosity) {
        this.verbosity = (verbosity == null) ? Verbosity.ALL : verbosity;
    }

    /**
     * Gets the symbol set used for rendering the game board and units.
     * @return the symbol set used for rendering the game board and units
     */
    public SymbolSet getSymbolSet() {
        return symbolSet;
    }

    /**
     * Sets the symbol set used for rendering the game board and units.
     * @param symbolSet the symbol set to set for rendering the game board and units.
     */
    public void setSymbolSet(SymbolSet symbolSet) {
        this.symbolSet = (symbolSet == null) ? new SymbolSet() : symbolSet;
    }
}
