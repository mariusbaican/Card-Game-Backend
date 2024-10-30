package game.cards;

import fileio.CardInput;
import lombok.Data;
import util.Ability;

import java.util.ArrayList;


@Data
public class Card {
    public enum Type {
        MINION,
        HERO
    }

    public enum PlacementRow {
        FRONT,
        BACK
    }

    protected int mana;
    protected int health;
    protected String  description;
    protected ArrayList<String> colors = new ArrayList<>();
    protected String name;
    protected boolean isFrozen;
    protected Ability ability;
    protected Type cardType;

    public Card(CardInput cardInput) {
        this.mana = cardInput.getMana();
        this.health = cardInput.getHealth();
        this.description = cardInput.getDescription();
        this.colors = cardInput.getColors();
        this.name = cardInput.getName();
        cardType = null;
        isFrozen = false;
        ability = null;
    }

    public Card(int mana, int health, String description, ArrayList colors, String name) {
        this.mana = mana;
        this.health = health;
        this.description = description;
        this.colors = colors;
        this.name = name;
        cardType = null;
        isFrozen = false;
        ability = null;
    }

    public Card() {}

    public boolean isFrozen() {
        return isFrozen;
    }
}
