package game.cards.special;

import game.board.Board;
import game.cards.Card;

import java.util.HashMap;

public class SpecialAbilities {
    private static HashMap<String, SpecialAbility> abilities;

    private static HashMap<String, SpecialAbility> initAbilities() {
        //TODO IMPLEMENT SPECIAL ABILITIES
        abilities = new HashMap<>();
        abilities.put("The Ripper", (row, column) -> {
            Card card = Board.getInstance().getCard(row, column);
            card.setAttackDamage(Math.max(card.getAttackDamage() - 2, 0));
        });
        abilities.put("Miraj", (row, column) -> {
            row = row;
            column = column;
        });
        abilities.put("The Cursed One", (row, column) -> {
            Card card = Board.getInstance().getCard(row, column);
            if (card.getAttackDamage() == 0) {
                Board.getInstance().removeCard(card);
            } else {
                int temp = card.getAttackDamage();
                card.setAttackDamage(card.getHealth());
                card.setHealth(temp);
            }
        });
        abilities.put("Disciple", (row, column) -> {
            Card card = Board.getInstance().getCard(row, column);
            card.setHealth(card.getHealth() + 2);
        });
        return abilities;
    }

    public static SpecialAbility getAbility(String name) {
        if (abilities == null) {
            abilities = initAbilities();
        }
        if (abilities.get(name) == null) {
            throw new IllegalArgumentException("Invalid SpecialCard name");
        }
        return abilities.get(name);
    }
}
