package game.player;

import game.cards.MinionCard;
import lombok.Data;

import java.util.*;

@Data
public class Deck {
    private ArrayList<MinionCard> cards;

    public Deck(Deck deck) {
        cards = new ArrayList<>();
        for (MinionCard card : deck.getCards()) {
            cards.add(new MinionCard(card));
        }
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
