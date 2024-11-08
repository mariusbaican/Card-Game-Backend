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
/**
 * This class stores a player's data.
 */
public final class Player {

    private ArrayList<Deck> decks;
    private Deck currentDeck = null;
    private ArrayList<MinionCard> hand;
    private HeroCard heroCard;
    private int currentMana;
    private int winCount;
    private int playerIndex;

    /**
     * This constructor creates a Player object
     * @param playerIndex The index of the Player to be created.
     */
    public Player(final int playerIndex) {
        currentMana = 0;
        winCount = 0;
        this.playerIndex = playerIndex;
        hand = new ArrayList<>();
    }

    /**
     * This method initializes a player's decks based on a given deckInput.
     * @param decksInput The list of available decks for the player.
     */
    public void initDecks(final DecksInput decksInput) {
        decks = new ArrayList<>();
        for (ArrayList<CardInput> deck : decksInput.getDecks()) {
            Deck tempDeck = new Deck();
            for (CardInput card : deck) {
                tempDeck.addCard(new MinionCard(card));
            }
            decks.add(tempDeck);
        }
    }

    /**
     * This method resets a player's data.
     */
    public void reset() {
        currentMana = 0;
        heroCard = null;
        currentDeck = null;
        hand = new ArrayList<>();
    }

    /**
     * This method selects the player's current deck from the deck list and shuffles it.
     * @param index The desired deck index.
     * @param shuffleSeed The desired shuffleSeed.
     * @return The player instance.
     */
    public Player selectDeck(final int index, final int shuffleSeed) {
        currentDeck = new Deck(decks.get(index));
        currentDeck.shuffle(shuffleSeed);
        return this;
    }

    /**
     * This method adds the first card from the currentDeck to the player's hand.
     */
    public void takeCard() {
        if (currentDeck.getCards().isEmpty()) {
            return;
        }

        MinionCard temp = currentDeck.getCards().get(0);
        hand.add(temp);
        currentDeck.getCards().remove(0);
    }

    /**
     * This method handles placing a card on the board.
     * @param handIndex The index of the desired card from the player's hand.
     */
    public void placeCard(final int handIndex) {
        if (hand.isEmpty()) {
            return;
        }

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

        int row;
        switch (minionCard.getMinionType()) {
            case REGULAR, DRUID ->
                row = getBackRow();
            case TANK, LEGENDARY ->
                row = getFrontRow();
            default ->
                throw new IllegalArgumentException("Invalid minionType" + minionCard.getMinionType());
        }

        if (Board.getInstance().getGameBoard().get(row).size() >= Board.getInstance().getColumnCount()) {
            placeCardOutput.put("error", "Cannot place card on table since row is full.");
            Game.getInstance().getOutput().add(placeCardOutput);
            return;
        }

        Board.getInstance().getGameBoard().get(row).add(minionCard);
        hand.remove(handIndex);
        currentMana -= minionCard.getMana();
    }

    /**
     * This method handles attacking a card.
     * @param attackerCoordinates The coordinates of the desired attacker.
     * @param attackedCoordinates The coordinates of the desired card to be attacked.
     */
    public void attackCard(final Coordinates attackerCoordinates, final Coordinates attackedCoordinates) {
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
        if (attackedCard.getHealth() <= 0) {
            Board.getInstance().removeCard(attackedCoordinates);
        }
    }

    /**
     * This method handles using the ability of a minionCard.
     * @param attackerCoordinates The coordinates of the desired attacker.
     * @param attackedCoordinates The coordinates of the desired card to be attacked.
     */
    public void useAbility(final Coordinates attackerCoordinates, final Coordinates attackedCoordinates) {
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
                if (attackedCard.getMinionType() != MinionCard.Type.TANK) {
                    if (checkForTank()) {
                        useAbilityOutput.put("error", "Attacked card is not of type 'Tank'.");
                        Game.getInstance().getOutput().add(useAbilityOutput);
                        return;
                    }
                }
            }
            default ->
                throw new IllegalStateException("Unexpected attackerName: " + attackerCard.getName());
        }

        attackerCard.getAbility().run(attackedCoordinates);
        attackerCard.setHasAttacked(true);
        if (attackedCard.getHealth() <= 0) {
            Board.getInstance().removeCard(attackedCoordinates);
        }
    }

    /**
     * This method handles attacking the heroCard of the opposing player.
     * @param attackerCoordinates The coordinates of the desired attacker.
     * @param attackedHeroCard The heroCard to be attacked.
     */
    public void attackHero(final Coordinates attackerCoordinates, final HeroCard attackedHeroCard) {
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

        attackedHeroCard.setHealth(attackedHeroCard.getHealth() - attackerCard.getAttackDamage());
        attackerCard.setHasAttacked(true);
        if (attackedHeroCard .getHealth() <= 0) {
            Game.getInstance().end();
        }
    }

    /**
     * This method handles using the hero's ability.
     * @param affectedRow The row to be affected by the ability.
     */
    public void useHeroAbility(final int affectedRow) {
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
            default ->
                throw new IllegalStateException("Unexpected heroCard name: " + heroCard.getName());
        }

        Coordinates attackedRow = new Coordinates();
        attackedRow.setX(affectedRow);
        attackedRow.setY(0);
        heroCard.getAbility().run(attackedRow);
        heroCard.setHasAttacked(true);
        currentMana -= heroCard.getMana();

        for (int i = 0; i < Board.getInstance().getGameBoard().get(affectedRow).size(); i++) {
            if (Board.getInstance().getGameBoard().get(affectedRow).get(i).getHealth() <= 0) {
                Board.getInstance().getGameBoard().get(affectedRow).remove(i);
                i--;
            }
        }
    }

    /**
     * This method adds the player's hand to output.
     */
    public void getCardsInHand() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode handOutput = objectMapper.createObjectNode();
        handOutput.put("command", "getCardsInHand");
        handOutput.put("playerIdx", playerIndex);
        ArrayNode handCards = objectMapper.createArrayNode();
        for (MinionCard minionCard : hand) {
            handCards.add(minionCard.outputCard(objectMapper));
        }

        handOutput.put("output", handCards);
        Game.getInstance().getOutput().add(handOutput);
    }

    /**
     * This method adds the player's currentDeck to output.
     */
    public void getPlayerDeck() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode deckOutput = objectMapper.createObjectNode();
        deckOutput.put("command", "getPlayerDeck");
        deckOutput.put("playerIdx", playerIndex);
        ArrayNode deckCards = objectMapper.createArrayNode();
        for (MinionCard minionCard : currentDeck.getCards()) {
            deckCards.add(minionCard.outputCard(objectMapper));
        }

        deckOutput.put("output", deckCards);
        Game.getInstance().getOutput().add(deckOutput);
    }

    /**
     * This method adds the player's heroCard to output.
     */
    public void getPlayerHero() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode heroCardOutput = objectMapper.createObjectNode();
        heroCardOutput.put("command", "getPlayerHero");
        heroCardOutput.put("playerIdx", playerIndex);
        heroCardOutput.put("output", heroCard.outputCard(objectMapper));
        Game.getInstance().getOutput().add(heroCardOutput);
    }

    /**
     * This method adds the player's mana to output.
     */
    public void getPlayerMana() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode manaOutput = objectMapper.createObjectNode();
        manaOutput.put("command", "getPlayerMana");
        manaOutput.put("playerIdx", playerIndex);
        manaOutput.put("output", currentMana);
        Game.getInstance().getOutput().add(manaOutput);
    }

    /**
     * This method checks if there is a TANK present on the opponent's front row.
     * @return True if a tank is present.
     */
    public boolean checkForTank() {
        int opposingFrontRow = getFrontRow() == 2 ? 1 : 2;
        for (MinionCard minionCard : Board.getInstance().getGameBoard().get(opposingFrontRow)) {
            if (minionCard.getMinionType() == MinionCard.Type.TANK) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method provides the frontRow of the current player.
     * @return Row index of the frontRow.
     */
    public int getFrontRow() {
        if (playerIndex == 1) {
            return Board.getInstance().getPlayerOneFrontRow();
        } else if (playerIndex == 2) {
            return Board.getInstance().getPlayerTwoFrontRow();
        } else {
            throw new IllegalArgumentException("Invalid playerIndex: " + playerIndex);
        }
    }

    /**
     * This method provides the backRow of the current player.
     * @return Row index of the backRow.
     */
    public int getBackRow() {
        if (playerIndex == 1) {
            return Board.getInstance().getPlayerOneBackRow();
        } else if (playerIndex == 2) {
            return Board.getInstance().getPlayerTwoBackRow();
        } else {
            throw new IllegalArgumentException("Invalid playerIndex: " + playerIndex);
        }
    }

    /**
     * This method adds the winCount of the player to output.
     */
    public void getPlayerWins() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode winOutput = objectMapper.createObjectNode();
        if (playerIndex == 1) {
            winOutput.put("command", "getPlayerOneWins");
        } else if (playerIndex == 2) {
            winOutput.put("command", "getPlayerTwoWins");
        } else {
            throw new IllegalArgumentException("Invalid playerIndex");
        }
        winOutput.put("output", winCount);
        Game.getInstance().getOutput().add(winOutput);
    }

    /**
     * This method provides the player's name based on their index.
     * @return A string containing either "Player one" or "Player two".
     */
    public String getPlayerName() {
        if (playerIndex == 1) {
            return "Player one";
        } else if (playerIndex == 2) {
            return "Player two";
        } else {
            throw new IllegalArgumentException("Invalid playerIndex");
        }
    }
}
