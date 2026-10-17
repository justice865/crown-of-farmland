package edu.kit.kastel.model;

import edu.kit.kastel.InvalidCardIndexException;

import java.util.List;

/**
 * The abstract class Team represents a team in the game, which can be either the player or the enemy.
 * @author uqibg
 */
public abstract class Team {
    String name;
    private int lifePoints = 8000;
    private List<UnitType> deck;
    private List<UnitType> cards;
    private Field selectedField;
    private final TeamIndicator team;
    private Board board;
    private Game game;
    private boolean initialTurn = true;
    private int boardUnitCount = 0;
    private boolean alreadyPlaced = false;

    /**
     * Constructs a Team with the specified team indicator and initializes the name based on the team.
     * @param team the indicator of the team, either PLAYER or ENEMY(AI)
     */
    public Team(TeamIndicator team) {
        this.team = team;
        this.name = (team == TeamIndicator.PLAYER) ? "Player" : "Enemy";
    }

    /**
     * The method that manages the drawing of card/s.
     */
    public void draw() {
        if (initialTurn) {
            setInitialTurn(false);
            getCards().add(getDeck().get(0));
            getDeck().remove(0);
            getCards().add(getDeck().get(0));
            getDeck().remove(0);
            getCards().add(getDeck().get(0));
            getDeck().remove(0);
            getCards().add(getDeck().get(0));
            getDeck().remove(0);
        }

        getCards().add(getDeck().get(0));
        getDeck().remove(0);
    }

    /**
     * Draws only the opening hand of 5 cards for the team, if it's the initial turn.
     */
    public void drawOpeningHandOnly() {
        if (!initialTurn) {
            return;
        }

        setInitialTurn(false);
        for (int i = 0; i < 5; i++) {
            getCards().add(getDeck().get(0));
            getDeck().remove(0);
        }
    }

    /**
     * Discards a card from the team's hand based on the provided index.
     * @param index the index of the card to be discarded from the hand
     * @throws InvalidCardIndexException if the index is out of bounds or if the cards list is null
     */
    protected void discard(int index) throws InvalidCardIndexException {
        if (getCards() == null) {
            throw new InvalidCardIndexException("Error: Cards list is null. Cannot discard card.");
        }

        if (index < 0 || index >= getCards().size()) {
            throw new InvalidCardIndexException("Error: Invalid card index: " + index + ". Valid range is 0 to " + (getCards().size() - 1));
        }

        getCards().remove(index);
    }

    /**
     * Reduces the team's life points by the specified amount of damage.
     * @return the new life points after taking damage
     */
    public int getLifePoints() {
        return lifePoints;
    }

    /**
     * Sets the team's life points to the specified value.
     * @param lifePoints the new life points to be set for the team
     */
    public void setLifePoints(int lifePoints) {
        this.lifePoints = lifePoints;
    }

    /**
     * Checks if it's the team's initial turn.
     * @param initialTurn true if it's the initial turn, false otherwise
     */
    public void setInitialTurn(boolean initialTurn) {
        this.initialTurn = initialTurn;
    }

    /**
     * Gets the current state of the game board.
     * @return the current game board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Sets the game board for the team.
     * @param board the game board to be set for the team
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Gets the game instance associated with the team.
     * @return the game instance associated with the team
     */
    public Game getGame() {
        return game;
    }

    /**
     * Sets the game instance for the team.
     * @param game the game instance to be set for the team
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Gets the deck of the team, which is a list of UnitType representing the cards available for drawing.
     * @return the deck of the team
     */
    public List<UnitType> getDeck() {
        return deck;
    }

    /**
     * Sets the deck for the team, which is a list of UnitType representing the cards available for drawing.
     * @param deck the deck to be set for the team
     */
    public void setDeck(List<UnitType> deck) {
        this.deck = deck;
    }

    /**
     * Gets the cards currently in the hand of the team.
     * @return the list of UnitType representing the cards currently in the hand of the team
     */
    public List<UnitType> getCards() {
        return cards;
    }

    /**
     * Sets the cards currently in the hand of the team.
     * @param cards the list of UnitType representing the cards to be set in the hand of the team
     */
    public void setCards(List<UnitType> cards) {
        this.cards = cards;
    }

    /**
     * Gets the indicator of the team, which can be either PLAYER or ENEMY(AI).
     * @return the indicator of the team
     */
    public TeamIndicator getTeamIndicator() {
        return team;
    }

    /**
     * Gets the currently selected field.
     * @return the currently selected field, or null if no field is selected
     */
    public Field getSelectedField() {
        return selectedField;
    }

    /**
     * Sets the currently selected field.
     * @param selectedField the field to be set as the currently selected field, or null to clear the selection
     */
    public void setSelectedField(Field selectedField) {
        this.selectedField = selectedField;
    }

    /**
     * Gets the number of units currently placed on the board for the team(farmer king excluded).
     * @return the number of units currently placed on the board for the team
     */
    public int getBoardUnitCount() {
        return boardUnitCount;
    }

    /**
     * Increments the count of units currently placed on the board for the team by 1.
     */
    public void incrementBoardUnitCount() {
        this.boardUnitCount++;
    }

    /**
     * Decrements the count of units currently placed on the board for the team by 1.
     */
    public void decrementBoardUnitCount() {
        this.boardUnitCount--;
    }

    /**
     * Checks if the team has already placed a unit on the board this turn.
     * @return true if the team has already placed a unit on the board this turn, false otherwise
     */
    public boolean isAlreadyPlaced() {
        return alreadyPlaced;
    }

    /**
     * Sets whether the team has already placed a unit on the board this turn.
     * @param alreadyPlaced true if the team has already placed a unit on the board this turn, false otherwise
     */
    public void setAlreadyPlaced(boolean alreadyPlaced) {
        this.alreadyPlaced = alreadyPlaced;
    }

    /**
     * Gets the name of the team.
     * @return the name of the team
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the team. If the provided name is null or blank,
     * it defaults to "Player" for the player team and "Enemy" for the enemy team.
     * @param name the name to be set for the team, or null/blank to reset to default based on the team indicator
     */
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            this.name = (team == TeamIndicator.PLAYER) ? "Player" : "Enemy";
            return;
        }
        this.name = name;
    }
}
