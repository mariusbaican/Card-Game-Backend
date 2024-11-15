package game.cards;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;
import fileio.Coordinates;
import game.board.Board;
import game.util.Constants;
import lombok.Data;

/**
 * This class extends the Card super class, to add data specific to heroCards.
 */
@Data
public class HeroCard extends Card {
    /**
     * This constructor creates a heroCard object based on a given cardInput.
     * @param cardInput The desired heroCard information.
     */
    public HeroCard(final CardInput cardInput) {
        super(cardInput);
        health = Constants.HERO_BASE_HEALTH;
        hasAttacked = false;

        initAbilities(cardInput.getName());
    }

    /**
     * This method initializes the abilities specific to each card.
     * @param name The name of the desired card.
     */
    @Override
    protected void initAbilities(final String name) {
        switch (name) {
            // My beloved lambda expressions
            case "Lord Royce" -> this.setAbility((Coordinates coordinates) -> {
                for (MinionCard minionCard : Board.getInstance().getGameBoard().get(coordinates.getX())) {
                    minionCard.setFrozen(true);
                }
            });
            case "Empress Thorina" -> this.setAbility((Coordinates coordinates) -> {
                MinionCard healthiestCard = Board.getInstance().getGameBoard().get(coordinates.getX()).get(0);
                for (MinionCard minionCard : Board.getInstance().getGameBoard().get(coordinates.getX())) {
                    if (minionCard.getHealth() > healthiestCard.getHealth()) {
                        healthiestCard = minionCard;
                    }
                }
                healthiestCard.setHealth(0);
            });
            case "King Mudface" -> this.setAbility((Coordinates coordinates) -> {
                for (MinionCard minionCard : Board.getInstance().getGameBoard().get(coordinates.getX())) {
                    minionCard.setHealth(minionCard.getHealth() + 1);
                }
            });
            case "General Kocioraw" -> this.setAbility((Coordinates coordinates) -> {
                for (MinionCard minionCard : Board.getInstance().getGameBoard().get(coordinates.getX())) {
                    minionCard.setAttackDamage(minionCard.getAttackDamage() + 1);
                }
            });
            default ->
                    throw new IllegalStateException("Illegal Hero card name: " + name);
        }
    }

    /**
     * This method is used to convert a card to an objectNode, with the goal of adding it to output.
     * @param objectMapper The object mapper used to create objectNodes and arrayNodes.
     * @return The object node containing the card's information.
     */
    @Override
    public ObjectNode outputCard(final ObjectMapper objectMapper) {
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
