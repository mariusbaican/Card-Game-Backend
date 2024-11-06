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
        for (ArrayList<MinionCard> cards : gameBoard)
            for (MinionCard minionCard : cards) {
                ObjectNode currentCard = objectMapper.createObjectNode();
                currentCard.put("mana", minionCard.getMana());
                currentCard.put("attackDamage", minionCard.getAttackDamage());
                currentCard.put("health", minionCard.getHealth());
                currentCard.put("description", minionCard.getDescription());
                ArrayNode colors = objectMapper.createArrayNode();
                for (String color : minionCard.getColors()) {
                    colors.add(color);
                }
                currentCard.put("colors", colors);
                currentCard.put("name", minionCard.getName());
                boardCards.add(currentCard);
            }
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
                ObjectNode currentCard = objectMapper.createObjectNode();
                currentCard.put("mana", minionCard.getMana());
                currentCard.put("attackDamage", minionCard.getAttackDamage());
                currentCard.put("health", minionCard.getHealth());
                currentCard.put("description", minionCard.getDescription());
                ArrayNode colors = objectMapper.createArrayNode();
                for (String color : minionCard.getColors()) {
                    colors.add(color);
                }
                currentCard.put("colors", colors);
                currentCard.put("name", minionCard.getName());
                boardCards.add(currentCard);
            }
        Game.getInstance().getOutput().add(boardOutput);
    }

    public void getCardAtPosition(int x, int y) {
        /*Coordinates coordinates = new Coordinates();
        coordinates.setX(x);
        coordinates.setY(y);

        MinionCard minionCard = getCard(coordinates);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode cardOutput = objectMapper.createObjectNode();
        cardOutput.put("command", "getCardAtPosition");
        cardOutput.put("x", x);
        cardOutput.put("y", y);

        ObjectNode cardStats = objectMapper.createObjectNode();
        cardStats.put("mana", minionCard.getMana());
        cardStats.put("attackDamage", minionCard.getAttackDamage());
        cardStats.put("health", minionCard.getHealth());
        cardStats.put("description", minionCard.getDescription());
        ArrayNode colors = objectMapper.createArrayNode();
        for (String color : minionCard.getColors()) {
            colors.add(color);
        }
        cardStats.put("colors", colors);
        cardStats.put("name", minionCard.getName());

        cardOutput.put("output", cardStats);
        Game.getInstance().getOutput().add(cardOutput);*/
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
