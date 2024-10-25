package game.cards.hero;

import game.board.Board;
import game.cards.Card;

import java.util.ArrayList;
import java.util.HashMap;

public class HeroAbilities {
    private static HashMap<String, HeroAbility> abilities;

    private static HashMap<String, HeroAbility> initAbilities() {
        //TODO IMPLEMENT HERO ABILITIES
        abilities = new HashMap<>();
        abilities.put("Lord Royce", (row) -> {
            ArrayList<Card> affectedRow = Board.getInstance().getRow(row);
            for (Card card : affectedRow) {
                card.setFrozen(true);
            }
        });
        abilities.put("Empress Thorina", (row) -> {
            ArrayList<Card> affectedRow = Board.getInstance().getRow(row);
            Card healthiestCard = null;
            for (Card card : affectedRow) {
                if (healthiestCard == null || healthiestCard.getHealth() < card.getHealth())
                    healthiestCard = card;
            }
            Board.getInstance().removeCard(healthiestCard);
        });
        abilities.put("King Mudface", (row) -> {
            ArrayList<Card> affectedRow = Board.getInstance().getRow(row);
            for (Card card : affectedRow) {
                card.setHealth(card.getHealth() + 1);
            }
        });
        abilities.put("General Kocioraw", (row) -> {
            ArrayList<Card> affectedRow = Board.getInstance().getRow(row);
            for (Card card : affectedRow) {
                card.setAttackDamage(card.getAttackDamage() + 1);
            }
        });
        return abilities;
    }

    public static HeroAbility getAbility(String name) {
        if (abilities == null) {
            abilities = initAbilities();
        }
        if (abilities.get(name) == null) {
            throw new IllegalArgumentException("Invalid HeroCard name");
        }
        return abilities.get(name);
    }
}
