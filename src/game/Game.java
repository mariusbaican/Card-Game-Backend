package game;

import fileio.*;
import game.board.Board;
import game.cards.HeroCard;
import game.player.Player;
import lombok.Data;

@Data
public class Game {
    private static Game game = new Game();
    private Board board = Board.getInstance();

    private Player player1 = new Player();
    private Player player2 = new Player();

    private Game() {}

    public static Game getInstance() {
        return game;
    }

    public void runGames(Input input) {
        initPlayer(player1, input.getPlayerOneDecks());
        initPlayer(player2, input.getPlayerTwoDecks());

        for (GameInput gameInput : input.getGames()) {
            initGame(gameInput.getStartGame());

            ActionHandler.getInstance().reset();
            ActionHandler.getInstance().startRound();
            for (ActionsInput actionsInput : gameInput.getActions()) {
                ActionHandler.getInstance().run(actionsInput);
            }
        }
    }

    public void initGame(StartGameInput startGameInput) {
        player1.selectDeck(startGameInput.getPlayerOneDeckIdx())
                        .setHeroCard(new HeroCard(startGameInput.getPlayerOneHero()));

        player2.selectDeck(startGameInput.getPlayerTwoDeckIdx())
                .setHeroCard(new HeroCard(startGameInput.getPlayerTwoHero()));

        ActionHandler.getInstance().addPlayers(player1, player2, startGameInput.getStartingPlayer());
    }

    public void initPlayer(Player player, DecksInput decksInput) {
        player.initDecks(decksInput);
    }
}
