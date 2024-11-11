package game.cards;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;
import fileio.Coordinates;
import game.board.Board;
import lombok.Data;

/**
 * This class extends the Card super class, to add data specific to minionCards.
 */
@Data
public class MinionCard extends Card {
    /**
     * This enum is used to determine which row cards should be placed on.
     */
    public enum Type {
        REGULAR,
        TANK,
        DRUID,
        LEGENDARY
    }

    private int attackDamage;
    private Type minionType;

    /**
     * This constructor creates a minionCard object based on a given cardInput.
     * @param cardInput The information to be passed to the minionCard.
     */
    public MinionCard(final CardInput cardInput) {
        super(cardInput);
        this.attackDamage = cardInput.getAttackDamage();
        isFrozen = true;
        hasAttacked = false;

        initAbilities(cardInput.getName());
    }

    /**
     * This constructor creates a copy of a given minionCard.
     * @param minionCard The minionCard desired to be copied.
     */
    public MinionCard(final MinionCard minionCard) {
        mana = minionCard.mana;
        health = minionCard.health;
        description = minionCard.description;
        colors = minionCard.colors;
        name = minionCard.name;
        isFrozen = false;
        hasAttacked = false;
        attackDamage = minionCard.attackDamage;
        minionType = minionCard.minionType;

        // This is why the ability interface made debugging hell
        // Copying the ability over kept it attached to the original card, not the copy
        initAbilities(name);
    }

    /**
     * This method initializes the abilities specific to each card.
     * @param name The name of the desired card.
     */
    private void initAbilities(final String name) {
        switch (name) {
            case "Sentinel", "Berserker" -> {
                minionType = Type.REGULAR;
                this.setAbility(null);
            }
            case "Goliath", "Warden" -> {
                minionType = Type.TANK;
                this.setAbility(null);
            }
            case "The Cursed One" -> {
                minionType = Type.DRUID;
                this.setAbility((Coordinates coordinates) -> {
                    int newAttackDamage = Board.getInstance().getCard(coordinates).getHealth();
                    int newHealth = Board.getInstance().getCard(coordinates).getAttackDamage();
                    Board.getInstance().getCard(coordinates).setAttackDamage(newAttackDamage);
                    Board.getInstance().getCard(coordinates).setHealth(newHealth);
                });
            }
            case "Disciple" -> {
                minionType = Type.DRUID;
                this.setAbility((Coordinates coordinates) -> {
                    int newHealth = Board.getInstance().getCard(coordinates).getHealth() + 2;
                    Board.getInstance().getCard(coordinates).setHealth(newHealth);
                });
            }
            case "The Ripper" -> {
                minionType = Type.LEGENDARY;
                this.setAbility((Coordinates coordinates) -> {
                    int newAttackDamage = Math.max(Board.getInstance().getCard(coordinates).getAttackDamage() - 2, 0);
                    Board.getInstance().getCard(coordinates).setAttackDamage(newAttackDamage);
                });
            }
            case "Miraj" -> {
                minionType = Type.LEGENDARY;
                this.setAbility((Coordinates coordinates) -> {
                    int newMirajHealth = Board.getInstance().getCard(coordinates).getHealth();
                    int newOpponentHealth = this.getHealth();
                    this.setHealth(newMirajHealth);
                    Board.getInstance().getCard(coordinates).setHealth(newOpponentHealth);
                });
            }
            default -> throw new IllegalStateException("Unexpected minionCard name: " + name);
        }
    }

    /**
     * This method is used to convert a card to an objectNode, with the goal of adding it to output.
     * @param objectMapper The object mapper used to create objectNodes and arrrayNodes.
     * @return The object node containing the card's information.
     */
    @Override
    public ObjectNode outputCard(final ObjectMapper objectMapper) {
        ObjectNode currentCard = objectMapper.createObjectNode();
        currentCard.put("mana", mana);
        currentCard.put("attackDamage", attackDamage);
        currentCard.put("health", health);
        currentCard.put("description", description);
        ArrayNode colorArray = objectMapper.createArrayNode();
        for (String color : colors) {
            colorArray.add(color);
        }
        currentCard.put("colors", colorArray);
        currentCard.put("name", name);
        return currentCard;
    }

}
