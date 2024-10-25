package game.cards.special;

import game.cards.MinionCard;
import lombok.Data;

import java.util.ArrayList;

@Data
public class SpecialCard extends MinionCard {

    private SpecialAbility ability;
    private enum SpecialCardType {
        DRUID,
        LEGENDARY_MINION
    }

    public SpecialCard(int mana, int health, int attackDamage, String description, ArrayList color, String name) {
        super(mana, health, attackDamage, description, color, name);
        ability = SpecialAbilities.getAbility(name);
    }

    public SpecialCard() {}
}
