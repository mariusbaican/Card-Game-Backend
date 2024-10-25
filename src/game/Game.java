package game;

import game.board.Board;
import game.player.Player;
import lombok.Data;

@Data
public class Game {
    private static Game game;
    private Board board = Board.getInstance();

    private Player player1 = new Player();
    private Player player2 = new Player();

    private Game() {}

    public static Game getInstance() {
        if(game == null) {
            game = new Game();
        }
        return game;
    }
}
