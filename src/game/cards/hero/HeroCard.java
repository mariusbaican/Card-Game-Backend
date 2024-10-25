package game.cards.hero;

import game.cards.Card;
import lombok.Data;

import java.util.ArrayList;

@Data
public class HeroCard extends Card {
    private HeroAbility ability;

    public HeroCard(int mana, int health, int attackDamage, String description, ArrayList color, String name) {
        super(mana, health, attackDamage, description, color, name);
        ability = HeroAbilities.getAbility(name);
    }

    public HeroCard() {}
}
