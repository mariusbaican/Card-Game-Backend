package game;

import game.player.Player;

public class RoundHandler {

    private static RoundHandler roundHandler = new RoundHandler();

    private Player playerOne;
    private Player playerTwo;
    private int roundNumber;

    private RoundHandler() {}

    public static RoundHandler getInstance() {
        return roundHandler;
    }

    public void reset() {
        playerOne = null;
        playerTwo = null;
        roundNumber = 0;
    }

    public RoundHandler addPlayers(Player player1, Player player2, int startingPlayer) {
        if (startingPlayer == 1) {
            this.playerOne = player1;
            this.playerTwo = player2;
        } else {
            this.playerOne = player2;
            this.playerTwo = player1;
        }
        return this;
    }

    public void runRound() {

    }
}
