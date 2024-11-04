package game.player;

import fileio.DecksInput;
import game.cards.Card;
import game.cards.MinionCard;
import lombok.Data;

import java.util.*;

@Data
public class Deck {
    private ArrayList<MinionCard> cards;

    public Deck(Deck deck) {
        cards = new ArrayList<>(deck.getCards());
    }

    public Deck() {
        cards = new ArrayList<>();
    }

    public Deck addCard(MinionCard card) {
        cards.add(card);
        return this;
    }

    public void shuffle(int shuffleSeed) {
        Random rand = new Random(shuffleSeed);
        Collections.shuffle(cards, rand);
    }
}
