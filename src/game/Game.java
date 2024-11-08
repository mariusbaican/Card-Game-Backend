package game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import fileio.DecksInput;
import fileio.Input;
import fileio.GameInput;
import fileio.StartGameInput;
import game.board.Board;
import game.cards.HeroCard;
import game.player.Player;
import lombok.Data;

@Data
/**
 * This class stores all parts of the game and runs through the gameInputs.
 */
public final class Game {
    private static Game game = new Game();

    private Player player1 = new Player(1);
    private Player player2 = new Player(2);

    private ArrayNode output;

    private int gameCount = 0;

    /**
     * This empty constructor creates a Game instance.
     */
    private Game() { }

    /**
     * This method returns the singleton Game object.
     * @return The unique Game instance.
     */
    public static Game getInstance() {
        return game;
    }

    /**
     * This method calls the reset methods of each game component.
     */
    private void resetGame() {
        player1.reset();
        player2.reset();

        ActionHandler.getInstance().reset();
        Board.getInstance().reset();
    }

    /**
     * This method runs though all of the games in the input.
     * @param input The games to be played.
     * @param gameOutput The output to be written to.
     */
    public void runGames(final Input input, final ArrayNode gameOutput) {
        this.output = gameOutput;
        gameCount = 0;
        initPlayer(player1, input.getPlayerOneDecks());
        initPlayer(player2, input.getPlayerTwoDecks());

        for (GameInput gameInput : input.getGames()) {
            resetGame();
            initGame(gameInput.getStartGame());
            ActionHandler.getInstance().startRound();
            for (ActionsInput actionsInput : gameInput.getActions()) {
                ActionHandler.getInstance().run(actionsInput);
            }
        }
    }

    /**
     * This method initializes the game based on the given input. It handles deck selection
     * and heroCard selection.
     * @param startGameInput The desired start input.
     */
    public void initGame(final StartGameInput startGameInput) {
        gameCount++;
        player1.selectDeck(startGameInput.getPlayerOneDeckIdx(), startGameInput.getShuffleSeed())
                .setHeroCard(new HeroCard(startGameInput.getPlayerOneHero()));

        player2.selectDeck(startGameInput.getPlayerTwoDeckIdx(), startGameInput.getShuffleSeed())
                .setHeroCard(new HeroCard(startGameInput.getPlayerTwoHero()));

        ActionHandler.getInstance().addPlayers(player1, player2, startGameInput.getStartingPlayer());
    }

    /**
     * This method is used to initialize a player's decks.
     * @param player The target player.
     * @param decksInput The desired deck list.
     */
    public void initPlayer(final Player player, final DecksInput decksInput) {
        player.initDecks(decksInput);
        player.setWinCount(0);
    }

    /**
     * This method handles the game end, writing the winner to the output.
     */
    public void end() {
        Player winner = ActionHandler.getInstance().getCurrentPlayer();
        winner.setWinCount(winner.getWinCount() + 1);

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode endOutput = objectMapper.createObjectNode();
        endOutput.put("gameEnded", ActionHandler.getInstance().getCurrentPlayer().getPlayerName() + " killed the enemy hero.");
        output.add(endOutput);
    }

    /**
     * This method returns a player based on his index.
     * @param playerIndex The desired playerIndex.
     * @return The Player instance with the given index.
     */
    public Player getPlayer(final int playerIndex) {
        if (playerIndex == 1) {
            return player1;
        } else if (playerIndex == 2) {
            return player2;
        } else {
            throw new IllegalArgumentException("Invalid playerIndex: " + playerIndex);
        }
    }

    /**
     * This method adds to output the index of the player who has the current turn;
     */
    public void getPlayerTurn() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode turnOutput = objectMapper.createObjectNode();
        turnOutput.put("command", "getPlayerTurn");
        turnOutput.put("output", ActionHandler.getInstance().getCurrentPlayer().getPlayerIndex());
        output.add(turnOutput);
    }

    /**
     * This method adds to output the number of totalGamesPlayed.
     */
    public void getTotalGamesPlayed() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode totalGamesPlayed = objectMapper.createObjectNode();
        totalGamesPlayed.put("command", "getTotalGamesPlayed");
        totalGamesPlayed.put("output", gameCount);
        output.add(totalGamesPlayed);
    }
}
