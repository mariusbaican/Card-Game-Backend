package game.cards;

import lombok.Data;

import java.util.ArrayList;


@Data
public class Card {
    protected int mana;
    protected int health;
    protected int attackDamage;
    protected String  description;
    protected ArrayList<String> color = new ArrayList<>();
    protected String name;
    protected boolean isFrozen;

    public Card(int mana, int health, String description, ArrayList color, String name) {
        this.mana = mana;
        this.health = health;
        this.description = description;
        this.color = color;
        this.name = name;
        attackDamage = 0;
        isFrozen = false;
    }

    public Card(int mana, int health, int attackDamage, String description, ArrayList color, String name) {
        this.mana = mana;
        this.health = health;
        this.attackDamage = attackDamage;
        this.description = description;
        this.color = color;
        this.name = name;
        isFrozen = false;
    }

    public Card() {}
}
