package game.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.Coordinates;
import game.Game;
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

    public void reset() {
        gameBoard = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            gameBoard.add(new ArrayList<>(5));
        }
    }

    public void getCardsOnTable() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode boardOutput = objectMapper.createObjectNode();
        boardOutput.put("command", "getCardsOnTable");
        ArrayNode boardCards = objectMapper.createArrayNode();
        for (ArrayList<MinionCard> cards : gameBoard) {
            ArrayNode rowCards = objectMapper.createArrayNode();
            for (MinionCard minionCard : cards)
                rowCards.add(minionCard.outputCard(objectMapper));
            boardCards.add(rowCards);
        }
        boardOutput.put("output", boardCards);
        Game.getInstance().getOutput().add(boardOutput);
    }

    public void getFrozenCardsOnTable() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode boardOutput = objectMapper.createObjectNode();
        boardOutput.put("command", "getFrozenCardsOnTable");
        ArrayNode boardCards = objectMapper.createArrayNode();
        for (ArrayList<MinionCard> cards : gameBoard)
            for (MinionCard minionCard : cards) {
                if (!minionCard.isFrozen())
                    continue;
                boardCards.add(minionCard.outputCard(objectMapper));
            }
        boardOutput.put("output", boardCards);
        Game.getInstance().getOutput().add(boardOutput);
    }

    public void getCardAtPosition(int x, int y) {
        Coordinates coordinates = new Coordinates();
        coordinates.setX(x);
        coordinates.setY(y);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode cardOutput = objectMapper.createObjectNode();
        cardOutput.put("command", "getCardAtPosition");

        if (gameBoard.get(coordinates.getX()).size() - 1 < y) {
            cardOutput.put("output", "No card available at that position.");
            cardOutput.put("x", x);
            cardOutput.put("y", y);
            Game.getInstance().getOutput().add(cardOutput);
            return;
        }

        cardOutput.put("x", x);
        cardOutput.put("y", y);

        MinionCard minionCard = getCard(coordinates);

        cardOutput.put("output", minionCard.outputCard(objectMapper));
        Game.getInstance().getOutput().add(cardOutput);
    }

    public static Board getInstance() {
        return board;
    }

    public MinionCard getCard(Coordinates coordinates) {
          return gameBoard.get(coordinates.getX()).get(coordinates.getY());
    }

    public void removeCard(Coordinates coordinates) {
        if (gameBoard.get(coordinates.getX()).get(coordinates.getY()) == null)
            return;

        gameBoard.get(coordinates.getX()).remove(coordinates.getY());
    }
}
