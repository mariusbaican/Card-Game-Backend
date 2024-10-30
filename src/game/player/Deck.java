package game.player;

import fileio.DecksInput;
import game.cards.Card;
import game.cards.MinionCard;
import lombok.Data;

import java.util.*;

@Data
public class Deck {
    private ArrayList<MinionCard> cards;
    private ArrayList<MinionCard> hand;

    public Deck (List<MinionCard> cards) {
        this.cards = new ArrayList<>(cards);
        this.hand = new ArrayList<>();
    }

    public Deck(MinionCard... cards) {
        this(Arrays.asList(cards));
    }

    public Deck addCard(MinionCard card) {
        cards.add(card);
        return this;
    }

    public void shuffleDeck(int shuffleSeed) {
        Random rand = new Random(shuffleSeed);
        Collections.shuffle(cards, rand);
    }

    public void takeCard() {
        if (cards.isEmpty())
            return;
        MinionCard temp = cards.get(0);
        cards.remove(0);
        hand.add(temp);
    }

}
