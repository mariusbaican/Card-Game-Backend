package game.board;

import fileio.Coordinates;
import game.cards.HeroCard;
import game.cards.MinionCard;
import lombok.Data;

import java.util.ArrayList;

@Data
public class Board {

    private static Board board = new Board();
    private ArrayList<ArrayList<MinionCard>> gameBoard;

    private Board() {
        gameBoard = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            gameBoard.add(new ArrayList<>(5));
        }
    }

    public static Board getInstance() {
        return board;
    }

    public MinionCard getCard(Coordinates coordinates) {
        return gameBoard.get(coordinates.getX()).get(coordinates.getY());
    }

    public void handleAttack(Coordinates attackerCoordinates, Coordinates attackedCoordinates) {

    }

    public void handleAbility(Coordinates attackerCoordinates, Coordinates attackedCoordinates) {

    }

    public void handleHeroAttack(Coordinates attackerCoordinates, HeroCard heroCard) {

    }

    public void handleHeroAbility(HeroCard heroCard, int affectedRow) {
        Coordinates targetRow = new Coordinates();
        targetRow.setX(affectedRow);
        targetRow.setY(0);
        heroCard.getAbility().run(targetRow);
    }
}
