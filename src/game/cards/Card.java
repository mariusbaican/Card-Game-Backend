package game.cards;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;
import lombok.Data;
import util.Ability;

import java.util.ArrayList;


@Data
/**
 * This is the super class of the two present card types, minionCard and heroCard
 * with its goal being storing common data between the two types.
 */
public class Card {

    /**
     * This enum is used to specify if a card is a MINION or a HERO.
     */
    public enum Type {
        MINION,
        HERO
    }

    protected int mana;
    protected int health;
    protected String  description;
    protected ArrayList<String> colors = new ArrayList<>();
    protected String name;
    protected boolean isFrozen;
    protected boolean hasAttacked;
    protected Ability ability;
    protected Type cardType;

    /**
     * This constructor created a Card object from a given cardInput.
     * @param cardInput The information of the card to be created.
     */
    public Card(final CardInput cardInput) {
        this.mana = cardInput.getMana();
        this.health = cardInput.getHealth();
        this.description = cardInput.getDescription();
        this.colors = cardInput.getColors();
        this.name = cardInput.getName();
        cardType = null;
        isFrozen = false;
        hasAttacked = false;
        ability = null;
    }

    /**
     * This is an empty constructor to create a Card object.
     */
    public Card() { }

    /**
     * This method is used to determine whether a card is frozen or not.
     * @return True if card is frozen.
     */
    public boolean isFrozen() {
        return isFrozen;
    }

    /**
     * This method is used to determine whether a card has attacked this turn or not.
     * @return True if card has attacked.
     */
    public boolean hasAttacked() {
        return hasAttacked;
    }

    /**
     * This method is used to convert a card to an objectNode, with the goal of adding it to output.
     * @param objectMapper The object mapper used to create objectNodes and arrrayNodes.
     * @return The object node containing the card's information.
     */
    public ObjectNode outputCard(final ObjectMapper objectMapper) {
        return null;
    }
}
