package game.cards;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;
import fileio.Coordinates;
import game.Game;
import game.board.Board;
import lombok.Data;

@Data
public class HeroCard extends Card {

    public HeroCard(CardInput cardInput) {
        super(cardInput);
        health = 30;
        cardType = Type.HERO;
        hasAttacked = false;

        initAbilities(cardInput.getName());
    }

    private void initAbilities(String name) {
        switch (name) {
            case "Lord Royce" -> this.setAbility((Coordinates coordinates) -> {
                for (MinionCard minionCard : Board.getInstance().getGameBoard().get(coordinates.getX()))
                    minionCard.setFrozen(true);
            });
            case "Empress Thorina" -> this.setAbility((Coordinates coordinates) -> {
                MinionCard healthiestCard = Board.getInstance().getGameBoard().get(coordinates.getX()).get(0);
                for (MinionCard minionCard : Board.getInstance().getGameBoard().get(coordinates.getX()))
                    if (minionCard.getHealth() > healthiestCard.getHealth())
                        healthiestCard = minionCard;
                healthiestCard.setHealth(0);
            });
            case "King Mudface" -> this.setAbility((Coordinates coordinates) -> {
                for (MinionCard minionCard : Board.getInstance().getGameBoard().get(coordinates.getX()))
                    minionCard.setHealth(minionCard.getHealth() + 1);
            });
            case "General Kocioraw" -> this.setAbility((Coordinates coordinates) -> {
                for (MinionCard minionCard : Board.getInstance().getGameBoard().get(coordinates.getX()))
                    minionCard.setAttackDamage(minionCard.getAttackDamage() + 1);
            });
            default ->
                    throw new IllegalStateException("Illegal Hero card name: " + name);
        }
    }

    @Override
    public ObjectNode outputCard(ObjectMapper objectMapper) {
        ObjectNode heroCardStats = objectMapper.createObjectNode();
        heroCardStats.put("mana", mana);
        heroCardStats.put("description", description);
        ArrayNode colorArray = objectMapper.createArrayNode();
        for (String color : colors) {
            colorArray.add(color);
        }
        heroCardStats.put("colors", colorArray);
        heroCardStats.put("name", name);
        heroCardStats.put("health", health);
        return heroCardStats;
    }
}
