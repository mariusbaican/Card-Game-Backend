package game.player;

import game.cards.MinionCard;
import lombok.Data;

import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

@Data
/**
 * This class stores the cards in a player's deck.
 */
public final class Deck {
    private ArrayList<MinionCard> cards;

    /**
     * This constructor copies a given deck.
     * @param deck The deck to be copied.
     */
    public Deck(final Deck deck) {
        cards = new ArrayList<>();
        for (MinionCard card : deck.getCards()) {
            cards.add(new MinionCard(card));
        }
    }

    /**
     * This constructor creates a new Deck object.
     */
    public Deck() {
        cards = new ArrayList<>();
    }

    /**
     * This method adds a card to a deck.
     * @param card The card to be added to the deck.
     */
    public void addCard(final MinionCard card) {
        cards.add(card);
    }

    /**
     * This method shuffles a deck based on a given shuffleSeed.
     * @param shuffleSeed The desired shuffleSeed.
     */
    public void shuffle(final int shuffleSeed) {
        Random rand = new Random(shuffleSeed);
        Collections.shuffle(cards, rand);
    }
}
