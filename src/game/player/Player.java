package game.player;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;
import fileio.Coordinates;
import fileio.DecksInput;
import game.Game;
import game.board.Board;
import game.cards.HeroCard;
import game.cards.MinionCard;
import lombok.Data;

import java.util.ArrayList;

@Data
public class Player {

    private ArrayList<Deck> decks;
    private Deck currentDeck = null;
    private ArrayList<MinionCard> hand;
    private HeroCard heroCard;
    private int currentMana;
    private int winCount;
    private int playerIndex;
    private String playerName;

    public void initDecks(DecksInput decksInput) {
        decks = new ArrayList<>();
        for (ArrayList<CardInput> deck : decksInput.getDecks()) {
            Deck tempDeck = new Deck();
            for (CardInput card : deck) {
                tempDeck.addCard(new MinionCard(card));
            }
            decks.add(tempDeck);
        }
    }

    public Player(int playerIndex, String playerName) {
        currentMana = 0;
        winCount = 0;
        this.playerIndex = playerIndex;
        hand = new ArrayList<>();
        this.playerName = playerName;
    }

    public void reset() {
        currentMana = 0;
        heroCard = null;
        currentDeck = null;
        hand = new ArrayList<>();
    }

    public Player setHeroCard(HeroCard heroCard) {
        this.heroCard = heroCard;
        return this;
    }

    public Player selectDeck(int index, int shuffleSeed) {
        currentDeck = new Deck(decks.get(index));
        currentDeck.shuffle(shuffleSeed);
        return this;
    }

    public void takeCard() {
        if (currentDeck.getCards().isEmpty())
            return;

        MinionCard temp = currentDeck.getCards().get(0);
        hand.add(temp);
        currentDeck.getCards().remove(0);
    }

    public void placeCard(int handIndex) {
        if (hand.isEmpty())
            return;

        MinionCard minionCard = hand.get(handIndex);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode placeCardOutput = objectMapper.createObjectNode();
        placeCardOutput.put("command", "placeCard");
        placeCardOutput.put("handIdx", handIndex);

        if (currentMana < minionCard.getMana()) {
            placeCardOutput.put("error", "Not enough mana to place card on table.");

            Game.getInstance().getOutput().add(placeCardOutput);
            return;
        }

        int row = 0;
        switch (minionCard.getMinionType()) {
            case REGULAR, DRUID ->
                row = getBackRow();
            case TANK, LEGENDARY ->
                row = getFrontRow();
        }

        if (Board.getInstance().getGameBoard().get(row).size() >= 5) {
            placeCardOutput.put("error", "Cannot place card on table since row is full.");
            Game.getInstance().getOutput().add(placeCardOutput);
            return;
        }

        Board.getInstance().getGameBoard().get(row).add(minionCard);
        hand.remove(handIndex);
        currentMana -= minionCard.getMana();
    }

    public void attackCard(Coordinates attackerCoordinates, Coordinates attackedCoordinates) {
        MinionCard attackerCard = Board.getInstance().getCard(attackerCoordinates);
        MinionCard attackedCard = Board.getInstance().getCard(attackedCoordinates);

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode attackCardOutput = objectMapper.createObjectNode();
        attackCardOutput.put("command", "cardUsesAttack");

        ObjectNode attackerCoords = objectMapper.createObjectNode();
        attackerCoords.put("x", attackerCoordinates.getX());
        attackerCoords.put("y", attackerCoordinates.getY());
        attackCardOutput.put("cardAttacker", attackerCoords);

        ObjectNode attackedCoords = objectMapper.createObjectNode();
        attackedCoords.put("x", attackedCoordinates.getX());
        attackedCoords.put("y", attackedCoordinates.getY());
        attackCardOutput.put("cardAttacked", attackedCoords);

        if (attackedCoordinates.getX() == getFrontRow() || attackedCoordinates.getX() == getBackRow()) {
            attackCardOutput.put("error", "Attacked card does not belong to the enemy.");
            Game.getInstance().getOutput().add(attackCardOutput);
            return;
        }

        if (attackerCard.isFrozen()) {
            attackCardOutput.put("error", "Attacker card is frozen.");
            Game.getInstance().getOutput().add(attackCardOutput);
            return;
        }

        if (attackerCard.hasAttacked()) {
            attackCardOutput.put("error", "Attacker card has already attacked this turn.");
            Game.getInstance().getOutput().add(attackCardOutput);
            return;
        }


        if (attackedCard.getMinionType() != MinionCard.Type.TANK) {
            if (checkForTank()) {
                attackCardOutput.put("error", "Attacked card is not of type 'Tank'.");
                Game.getInstance().getOutput().add(attackCardOutput);
                return;
            }
        }

        attackedCard.setHealth(attackedCard.getHealth() - attackerCard.getAttackDamage());
        System.out.println("damage " + attackerCard.getAttackDamage());
        attackerCard.setHasAttacked(true);
        if (attackedCard.getHealth() <= 0)
            Board.getInstance().removeCard(attackedCoordinates);
    }

    public void useAbility(Coordinates attackerCoordinates, Coordinates attackedCoordinates) {
        MinionCard attackerCard = Board.getInstance().getCard(attackerCoordinates);
        MinionCard attackedCard = Board.getInstance().getCard(attackedCoordinates);

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode useAbilityOutput = objectMapper.createObjectNode();
        useAbilityOutput.put("command", "cardUsesAbility");

        ObjectNode attackerCoords = objectMapper.createObjectNode();
        attackerCoords.put("x", attackerCoordinates.getX());
        attackerCoords.put("y", attackerCoordinates.getY());
        useAbilityOutput.put("cardAttacker", attackerCoords);

        ObjectNode attackedCoords = objectMapper.createObjectNode();
        attackedCoords.put("x", attackedCoordinates.getX());
        attackedCoords.put("y", attackedCoordinates.getY());
        useAbilityOutput.put("cardAttacked", attackedCoords);

        if (attackerCard.isFrozen()) {
            useAbilityOutput.put("error", "Attacker card is frozen.");
            Game.getInstance().getOutput().add(useAbilityOutput);
            return;
        }
        if (attackerCard.hasAttacked()) {
            useAbilityOutput.put("error", "Attacker card has already attacked this turn.");
            Game.getInstance().getOutput().add(useAbilityOutput);
            return;
        }

        switch (attackerCard.getName()) {
            case "Disciple" -> {
                if (attackedCoordinates.getX() != getFrontRow() && attackedCoordinates.getX() != getBackRow()) {
                    useAbilityOutput.put("error", "Attacked card does not belong to the current player.");
                    Game.getInstance().getOutput().add(useAbilityOutput);
                    return;
                }
            }
            case "The Ripper", "Miraj", "The Cursed One" -> {
                if (attackedCoordinates.getX() == getFrontRow() || attackedCoordinates.getX() == getBackRow()) {
                    useAbilityOutput.put("error", "Attacked card does not belong to the enemy.");
                    Game.getInstance().getOutput().add(useAbilityOutput);
                    return;
                }
                if (attackedCard.getMinionType() != MinionCard.Type.TANK)
                    if (checkForTank()) {
                        useAbilityOutput.put("error", "Attacked card is not of type 'Tank'.");
                        Game.getInstance().getOutput().add(useAbilityOutput);
                        return;
                    }
            }
        }

        attackerCard.getAbility().run(attackedCoordinates);
        attackerCard.setHasAttacked(true);
        if (attackedCard.getHealth() <= 0)
            Board.getInstance().removeCard(attackedCoordinates);
    }

    public void attackHero(Coordinates attackerCoordinates, HeroCard heroCard) {
        MinionCard attackerCard = Board.getInstance().getCard(attackerCoordinates);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode attackHeroOutput = objectMapper.createObjectNode();
        attackHeroOutput.put("command", "useAttackHero");
        ObjectNode attackerCoords = objectMapper.createObjectNode();
        attackerCoords.put("x", attackerCoordinates.getX());
        attackerCoords.put("y", attackerCoordinates.getY());
        attackHeroOutput.put("cardAttacker", attackerCoords);

        if (attackerCard.isFrozen()) {
            attackHeroOutput.put("error", "Attacker card is frozen.");
            Game.getInstance().getOutput().add(attackHeroOutput);
            return;
        }
        if (attackerCard.hasAttacked()) {
            attackHeroOutput.put("error", "Attacker card has already attacked this turn.");
            Game.getInstance().getOutput().add(attackHeroOutput);
            return;
        }

        if (checkForTank()) {
            attackHeroOutput.put("error", "Attacked card is not of type 'Tank'.");
            Game.getInstance().getOutput().add(attackHeroOutput);
            return;
        }

        heroCard.setHealth(heroCard.getHealth() - attackerCard.getAttackDamage());
        attackerCard.setHasAttacked(true);
        if (heroCard.getHealth() <= 0)
            Game.getInstance().end();
    }

    public void useHeroAbility(int affectedRow) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode heroAbilityOutput = objectMapper.createObjectNode();
        heroAbilityOutput.put("command", "useHeroAbility");
        heroAbilityOutput.put("affectedRow", affectedRow);

        if (currentMana < heroCard.getMana()) {
            heroAbilityOutput.put("error", "Not enough mana to use hero's ability.");
            Game.getInstance().getOutput().add(heroAbilityOutput);
            return;
        }
        if (heroCard.hasAttacked()) {
            heroAbilityOutput.put("error", "Hero has already attacked this turn.");
            Game.getInstance().getOutput().add(heroAbilityOutput);
            return;
        }

        switch (heroCard.getName()) {
            case "Lord Royce", "Empress Thorina" -> {
                if (affectedRow == getFrontRow() || affectedRow == getBackRow()) {
                    heroAbilityOutput.put("error", "Selected row does not belong to the enemy.");
                    Game.getInstance().getOutput().add(heroAbilityOutput);
                    return;
                }
            }
            case "General Kocioraw", "King Mudface" -> {
                if (affectedRow != getFrontRow() && affectedRow != getBackRow()) {
                    heroAbilityOutput.put("error", "Selected row does not belong to the current player.");
                    Game.getInstance().getOutput().add(heroAbilityOutput);
                    return;
                }
            }
        }

        Coordinates attackedRow = new Coordinates();
        attackedRow.setX(affectedRow);
        attackedRow.setY(0);
        heroCard.getAbility().run(attackedRow);
        heroCard.setHasAttacked(true);
        currentMana -= heroCard.getMana();

        for (int i = 0; i < Board.getInstance().getGameBoard().get(affectedRow).size(); i++)
            if (Board.getInstance().getGameBoard().get(affectedRow).get(i).getHealth() <= 0) {
                Board.getInstance().getGameBoard().get(affectedRow).remove(i);
                i--;
            }
    }

    public void getCardsInHand() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode handOutput = objectMapper.createObjectNode();
        handOutput.put("command", "getCardsInHand");
        handOutput.put("playerIdx", playerIndex);
        ArrayNode handCards = objectMapper.createArrayNode();
        for (MinionCard minionCard : hand)
            handCards.add(minionCard.outputCard(objectMapper));

        handOutput.put("output", handCards);
        Game.getInstance().getOutput().add(handOutput);
    }

    public void getPlayerDeck() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode deckOutput = objectMapper.createObjectNode();
        deckOutput.put("command", "getPlayerDeck");
        deckOutput.put("playerIdx", playerIndex);
        ArrayNode deckCards = objectMapper.createArrayNode();
        for (MinionCard minionCard : currentDeck.getCards())
            deckCards.add(minionCard.outputCard(objectMapper));

        deckOutput.put("output", deckCards);
        Game.getInstance().getOutput().add(deckOutput);
    }

    public void getPlayerHero() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode heroCardOutput = objectMapper.createObjectNode();
        heroCardOutput.put("command", "getPlayerHero");
        heroCardOutput.put("playerIdx", playerIndex);
        heroCardOutput.put("output", heroCard.outputCard(objectMapper));
        Game.getInstance().getOutput().add(heroCardOutput);
    }

    public void getPlayerMana() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode manaOutput = objectMapper.createObjectNode();
        manaOutput.put("command", "getPlayerMana");
        manaOutput.put("playerIdx", playerIndex);
        manaOutput.put("output", currentMana);
        Game.getInstance().getOutput().add(manaOutput);
    }

    public boolean checkForTank() {
        int opposingFrontRow = getFrontRow() == 2 ? 1 : 2;
        for (MinionCard minionCard : Board.getInstance().getGameBoard().get(opposingFrontRow))
            if (minionCard.getMinionType() == MinionCard.Type.TANK)
                return true;
        return false;
    }

    public int getFrontRow() {
        return playerIndex == 1 ? 2 : 1;
    }

    public int getBackRow() {
        return playerIndex == 1 ? 3 : 0;
    }

    public void getPlayerWins() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode winOutput = objectMapper.createObjectNode();
        if (playerIndex == 1)
            winOutput.put("command", "getPlayerOneWins");
        else if (playerIndex == 2)
            winOutput.put("command", "getPlayerTwoWins");
        else
            throw new IllegalArgumentException("Invalid playerIndex");
        winOutput.put("output", winCount);
        Game.getInstance().getOutput().add(winOutput);
    }
}
