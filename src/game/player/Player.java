package game.player;

import game.cards.Card;
import game.cards.hero.HeroCard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Player {
    private HeroCard heroCard;
    private ArrayList<Card> cards;
    private int currentMana;

    public Player() {
        currentMana = 0;
    }

    public Player setHeroCard(HeroCard heroCard) {
        this.heroCard = heroCard;
        return this;
    }

    public Player setCards(List<Card> cards) {
        this.cards = new ArrayList<>(cards);
        return this;
    }

    public Player setCards(Card... cards) {
        return this.setCards(Arrays.asList(cards));
    }

    public void playCard(int row, int column, Card card) {}
}
