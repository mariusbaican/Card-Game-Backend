package game.cards;

import lombok.Data;

import java.util.ArrayList;

@Data
public class MinionCard extends Card {

    public MinionCard(int mana, int health, int attackDamage, String description, ArrayList color, String name) {
        super(mana, health, attackDamage, description, color, name);
    }

    public MinionCard() {}
}
