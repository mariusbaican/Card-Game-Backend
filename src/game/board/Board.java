package game.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.Coordinates;
import game.Game;
import game.cards.MinionCard;
import game.util.Constants;
import lombok.Data;

import java.util.ArrayList;

/**
 * This class stores the cards on the game board.
 */
@Data
public final class Board {

    private static Board board = new Board();
    private ArrayList<ArrayList<MinionCard>> gameBoard;

    /**
     * This constructor instantiates the rows and columns of the board.
     */
    private Board() {
        gameBoard = new ArrayList<>(Constants.ROW_COUNT);
        for (int i = 0; i < Constants.ROW_COUNT; i++) {
            gameBoard.add(new ArrayList<>(Constants.COLUMN_COUNT));
        }
    }

    /**
     * This method returns the singleton Board.
     * @return The unique instance of Board.
     */
    public static Board getInstance() {
        return board;
    }

    /**
     * This method resets the rows and columns of the board.
     */
    public void reset() {
        board = new Board();
    }



    /**
     * This method returns the card present at a desired set of coordinates.
     * @param coordinates The coordinates of the desired card.
     * @return The card present at the provided coordinates.
     */
    public MinionCard getCard(final Coordinates coordinates) {
        return gameBoard.get(coordinates.getX()).get(coordinates.getY());
    }

    /**
     * THis method removes the card present at a desired set of coordinates.
     * @param coordinates The coordinates of the desired card to be removed.
     */
    public void removeCard(final Coordinates coordinates) {
        if (gameBoard.get(coordinates.getX()).get(coordinates.getY()) == null) {
            return;
        }
        gameBoard.get(coordinates.getX()).remove(coordinates.getY());
    }

    /**
     * This method adds the cards on the board to the output.
     */
    public void getCardsOnTable() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode boardOutput = objectMapper.createObjectNode();
        boardOutput.put("command", "getCardsOnTable");
        ArrayNode boardCards = objectMapper.createArrayNode();
        for (ArrayList<MinionCard> cards : gameBoard) {
            ArrayNode rowCards = objectMapper.createArrayNode();
            for (MinionCard minionCard : cards) {
                rowCards.add(minionCard.outputCard(objectMapper));
            }
            boardCards.add(rowCards);
        }
        boardOutput.put("output", boardCards);
        Game.getInstance().getOutput().add(boardOutput);
    }

    /**
     * This method adds the frozen cards on the board to the output.
     */
    public void getFrozenCardsOnTable() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode boardOutput = objectMapper.createObjectNode();
        boardOutput.put("command", "getFrozenCardsOnTable");
        ArrayNode boardCards = objectMapper.createArrayNode();
        for (ArrayList<MinionCard> cards : gameBoard) {
            for (MinionCard minionCard : cards) {
                if (!minionCard.isFrozen()) {
                    continue;
                }
                boardCards.add(minionCard.outputCard(objectMapper));
            }
        }
        boardOutput.put("output", boardCards);
        Game.getInstance().getOutput().add(boardOutput);
    }

    /**
     * This method adds the card on row x, column y to the output.
     * @param x The row of the desired card to be added to output.
     * @param y The column of the desired card to be added to output.
     */
    public void getCardAtPosition(final int x, final int y) {
        // I blame the lack of a Coordinates(int x, int y) constructor for this
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
}
