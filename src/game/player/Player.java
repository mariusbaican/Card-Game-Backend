package game.player;

import fileio.CardInput;
import fileio.Coordinates;
import fileio.DecksInput;
import game.board.Board;
import game.cards.HeroCard;
import game.cards.MinionCard;
import lombok.Data;

import java.lang.annotation.Target;
import java.util.ArrayList;

@Data
public class Player {

    private ArrayList<Deck> decks = new ArrayList<>();
    private Deck currentDeck = null;
    private ArrayList<MinionCard> hand;
    private HeroCard heroCard;
    private int currentMana;
    private int winCount;
    private int playerIndex;

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
        winCount = 0;
        playerIndex = 0;
    }

    public Player setHeroCard(HeroCard heroCard) {
        this.heroCard = heroCard;
        return this;
    }

    public Player selectDeck(int index, int shuffleSeed) {
        currentDeck = decks.get(index);
        currentDeck.shuffle(shuffleSeed);
        hand = currentDeck.getHand();
        return this;
    }

    public Player takeCard() {
        currentDeck.takeCard();
        return this;
    }

    public void placeCard(int handIndex) {
        //TODO ADD OUTPUTS
        MinionCard minionCard = hand.get(handIndex);
        if (currentMana < minionCard.getMana())
            return;

        int row = 0;
        switch (minionCard.getMinionType()) {
            case REGULAR, DRUID ->
                row = getBackRow();
            case TANK, LEGENDARY ->
                row = getFrontRow();
        }

        if (Board.getInstance().getGameBoard().get(row).size() == 5)
            return;

        Board.getInstance().getGameBoard().get(row).add(minionCard);
    }

    public void attackCard(Coordinates attackerCoordinates, Coordinates attackedCoordinates) {
        MinionCard attackerCard = Board.getInstance().getCard(attackerCoordinates);
        MinionCard attackedCard = Board.getInstance().getCard(attackedCoordinates);

        if (attackedCoordinates.getX() == getFrontRow() || attackedCoordinates.getX() == getBackRow())
            return; //TODO ADD OUTPUT
        if (!validAttacker(attackerCard))
            return;

        if (attackedCard.getMinionType() != MinionCard.Type.TANK) {
            if (checkForTank())
                return; //TODO ADD OUTPUT
        }

        attackedCard.setHealth(attackedCard.getHealth() - attackerCard.getAttackDamage());
        attackerCard.setHasAttacked(true);
        if (attackedCard.getHealth() <= 0)
            Board.getInstance().removeCard(attackedCoordinates);
    }

    public void useAbility(Coordinates attackerCoordinates, Coordinates attackedCoordinates) {
        MinionCard attackerCard = Board.getInstance().getCard(attackerCoordinates);
        MinionCard attackedCard = Board.getInstance().getCard(attackedCoordinates);

        if (!validAttacker(attackerCard))
            return;

        switch (attackerCard.getName()) {
            case "Disciple" -> {
                if (attackedCoordinates.getX() != getFrontRow() && attackedCoordinates.getX() != getBackRow())
                    return; //TODO ADD OUTPUT
            }
            case "The Ripper", "Miraj", "The Cursed One" -> {
                if (attackedCoordinates.getX() == getFrontRow() && attackedCoordinates.getX() == getBackRow())
                    return; //TODO ADD OUTPUT
                if (attackedCard.getMinionType() != MinionCard.Type.TANK)
                    if (checkForTank())
                        return; //TODO ADD OUTPUT
            }
        }

        attackerCard.getAbility().run(attackedCoordinates);
        attackerCard.setHasAttacked(true);
        if (attackedCard.getHealth() <= 0)
            Board.getInstance().removeCard(attackedCoordinates);
    }

    public void attackHero(Coordinates attackerCoordinates, HeroCard heroCard) {
        MinionCard attackerCard = Board.getInstance().getCard(attackerCoordinates);

        if (!validAttacker(attackerCard))
            return;

        if (checkForTank())
            return; //TODO ADD OUTPUT

        heroCard.setHealth(heroCard.getHealth() - attackerCard.getAttackDamage());
        attackerCard.setHasAttacked(true);
        if (heroCard.getHealth() <= 0)
            return; //TODO HANDLE GAME ENDING
    }

    public void useHeroAbility(int affectedRow) {
        if (currentMana < heroCard.getMana())
            return; //TODO ADD OUTPUT
        if (heroCard.hasAttacked())
            return; //TODO ADD OUTPUT

        switch (heroCard.getName()) {
            case "Lord Royce", "Empress Thorina" -> {
                if (affectedRow == getFrontRow() || affectedRow == getBackRow())
                    return; //TODO ADD OUTPUT
            }
            case "General Kocioraw", "King Mudface" -> {
                if (affectedRow != getFrontRow() || affectedRow != getBackRow())
                    return; //TODO ADD OUTPUT
            }
        }

        Coordinates attackedRow = new Coordinates();
        attackedRow.setX(affectedRow);
        attackedRow.setY(0);
        heroCard.getAbility().run(attackedRow);
        heroCard.setHasAttacked(true);
        //TODO HANDLE DEATHS
    }

    public boolean checkForTank() {
        int opposingFrontRow = getFrontRow() == 2 ? 1 : 2;
        for (MinionCard minionCard : Board.getInstance().getGameBoard().get(opposingFrontRow))
            if (minionCard.getMinionType() == MinionCard.Type.TANK)
                return true;
        return false;
    }

    public boolean validAttacker(MinionCard attackerCard) {
        if (attackerCard.isFrozen())
            return false; //TODO ADD OUTPUT
        if (attackerCard.hasAttacked())
            return false; //TODO ADD OUTPUT
        return true;
    }

    public int getFrontRow() {
        return playerIndex == 1 ? 2 : 1;
    }

    public int getBackRow() {
        return playerIndex == 1 ? 3 : 0;
    }
}
