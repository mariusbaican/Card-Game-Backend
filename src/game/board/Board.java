package game.board;

import game.cards.Card;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;

@Data
public class Board {
    private static Board board;
    private ArrayList<ArrayList<Card>> boardArray;

    private Board() {
        boardArray = new ArrayList<>(4);
        for (ArrayList<Card> row : boardArray) {
            row = new ArrayList<>(5);
        }
    }

    public static Board getInstance() {
        if (board == null) {
            board = new Board();
        }
        return board;
    }

    public void addCard(int row, Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        //TODO GET SMARTER
    }

    public void removeCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        //TODO GET SMARTER
    }

    public Card getCard(int row, int column) {
        return boardArray.get(row).get(column);
    }

    public ArrayList<Card> getRow(int row) {
        return boardArray.get(row);
    }
}
