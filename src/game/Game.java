package game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.*;
import game.board.Board;
import game.cards.HeroCard;
import game.player.Player;
import lombok.Data;

@Data
public class Game {
    private static Game game = new Game();

    private Player player1 = new Player(1, "Player one");
    private Player player2 = new Player(2, "Player two");

    private ArrayNode output;

    private int gameCount = 0;

    private Game() {}

    private void resetGame() {
        player1.reset();
        player2.reset();

        ActionHandler.getInstance().reset();
        Board.getInstance().reset();
    }

    public static Game getInstance() {
        return game;
    }

    public void runGames(Input input, ArrayNode output) {
        this.output = output;
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

    public void initGame(StartGameInput startGameInput) {
        gameCount++;
        player1.selectDeck(startGameInput.getPlayerOneDeckIdx(), startGameInput.getShuffleSeed())
                .setHeroCard(new HeroCard(startGameInput.getPlayerOneHero()));

        player2.selectDeck(startGameInput.getPlayerTwoDeckIdx(), startGameInput.getShuffleSeed())
                .setHeroCard(new HeroCard(startGameInput.getPlayerTwoHero()));

        ActionHandler.getInstance().addPlayers(player1, player2, startGameInput.getStartingPlayer());
    }

    public void initPlayer(Player player, DecksInput decksInput) {
        player.initDecks(decksInput);
    }

    public void end() {
        Player winner = ActionHandler.getInstance().getCurrentPlayer();
        winner.setWinCount(winner.getWinCount() + 1);

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode endOutput = objectMapper.createObjectNode();
        endOutput.put("gameEnded", ActionHandler.getInstance().getCurrentPlayer().getPlayerName() + " killed the enemy hero.");
        output.add(endOutput);
    }

    public Player getPlayer(int playerIndex) {
        if (playerIndex == 1)
            return player1;
        else if (playerIndex == 2)
            return player2;
        return null;
    }

    public void getPlayerTurn() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode turnOutput = objectMapper.createObjectNode();
        turnOutput.put("command", "getPlayerTurn");
        turnOutput.put("turn", ActionHandler.getInstance().getCurrentPlayer().getPlayerIndex());
        output.add(turnOutput);
    }

    public void getTotalGamesPlayed() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode totalGamesPlayed = objectMapper.createObjectNode();
        totalGamesPlayed.put("command", "getTotalGamesPlayed");
        totalGamesPlayed.put("output", gameCount);
        output.add(totalGamesPlayed);
    }
}
