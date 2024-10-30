package game.player;

import fileio.CardInput;
import fileio.Coordinates;
import fileio.DecksInput;
import game.board.Board;
import game.cards.HeroCard;
import game.cards.MinionCard;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;

@Data
public class Player {

    private ArrayList<Deck> decks = new ArrayList<>();
    private Deck currentDeck = null;
    private ArrayList<MinionCard> hand;
    private ArrayList<MinionCard> playedCards;
    private HeroCard heroCard;
    private int currentMana;
    private int winCount;

    public void initDecks(DecksInput decksInput) {
        for (ArrayList<CardInput> deck : decksInput.getDecks()) {
            Deck tempDeck = new Deck();
            for (CardInput card : deck) {
                tempDeck.addCard(new MinionCard(card));
            }
            decks.add(tempDeck);
        }
        playedCards = new ArrayList<>();
    }

    public Player() {
        currentMana = 10;
        winCount = 0;
    }

    public Player setHeroCard(HeroCard heroCard) {
        this.heroCard = heroCard;
        return this;
    }

    public Player selectDeck(int index) {
        currentDeck = decks.get(index);
        hand = currentDeck.getHand();
        return this;
    }

    public Player takeCard() {
        currentDeck.takeCard();
        return this;
    }

    public void placeCard(int handIndex) {
        if (hand.get(handIndex) == null)
            return;
        if (hand.get(handIndex).getMana() >= currentMana)
            return;
        //TODO ADD PLACE CARD METHOD IN BOARD
        //TODO ADD OUTPUT
        //Board.getInstance().placeCard(hand.get(handIndex));
    }

    public void attackCard(Coordinates attackerCoordinates, Coordinates attackedCoordinates) {
        Board.getInstance().handleAttack(attackerCoordinates, attackedCoordinates);
    }

    public void useAbility(Coordinates attackerCoordinates, Coordinates attackedCoordinates) {
        Board.getInstance().handleAbility(attackerCoordinates, attackedCoordinates);
    }

    public void attackHero(Coordinates attackerCoordinates, HeroCard heroCard) {
        Board.getInstance().handleHeroAttack(attackerCoordinates, heroCard);
    }

    public void useHeroAbility(int affectedRow) {
        Board.getInstance().handleHeroAbility(heroCard, affectedRow);
    }




}
