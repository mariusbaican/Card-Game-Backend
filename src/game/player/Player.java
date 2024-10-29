package game.player;

import fileio.CardInput;
import fileio.DecksInput;
import game.cards.HeroCard;
import game.cards.MinionCard;

import java.util.ArrayList;
import java.util.Arrays;

public class Player {

    private ArrayList<Deck> decks = new ArrayList<>();
    private Deck currentDeck = null;
    private HeroCard heroCard;
    private int currentMana;

    public void initDecks(DecksInput decksInput) {
        for (ArrayList<CardInput> deck : decksInput.getDecks()) {
            Deck tempDeck = new Deck();
            for (CardInput card : deck) {
                tempDeck.addCard(new MinionCard(card));
            }
            decks.add(tempDeck);
        }
    }

    public Player() {
        currentMana = 10;
    }

    public Player setHeroCard(HeroCard heroCard) {
        this.heroCard = heroCard;
        return this;
    }

    public Player addDeck(Deck deck) {
        this.decks.add(deck);
        return this;
    }

    public Player selectDeck(int index) {
        currentDeck = decks.get(index);
        return this;
    }


}
