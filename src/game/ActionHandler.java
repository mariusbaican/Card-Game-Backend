package game;

import fileio.ActionsInput;
import game.board.Board;
import game.cards.MinionCard;
import game.player.Player;
import game.util.Constants;
import lombok.Data;

@Data
/**
 * This class is used to handle turns, rounds and actionInputs.
 */
public final class ActionHandler {

    private static ActionHandler actionHandler = new ActionHandler();

    private Player currentPlayer;
    private Player awaitingPlayer;

    private int roundNumber;
    private int turnNumber;
    private int gameCount;

    /**
     * This construction instantiates an actionHandler object.
     */
    private ActionHandler() {
        gameCount = 0;
        roundNumber = 0;
        turnNumber = 1;
    }

    /**
     * This method returns the singleton actionHandler instance..
     * @return Unique actionHandler instance.
     */
    public static ActionHandler getInstance() {
        return actionHandler;
    }

    /**
     * This method resets the members of the actionHandler class.
     */
    public void reset() {
        currentPlayer = null;
        awaitingPlayer = null;
        turnNumber = 1;
        roundNumber = 0;
        gameCount++;
    }

    /**
     * This method adds the players to the actionHandler.
     * @param player1 Player1 instance.
     * @param player2 Player2 instance.
     * @param startingPlayer StartingPlayer's index.
     */
    public void addPlayers(final Player player1, final Player player2, final int startingPlayer) {
        if (startingPlayer == 1) {
            currentPlayer = player1;
            awaitingPlayer = player2;
        } else if (startingPlayer == 2) {
            currentPlayer = player2;
            awaitingPlayer = player1;
        } else {
            throw new IllegalArgumentException("Invalid starting player index: " + startingPlayer);
        }
    }

    /**
     * This method handles all given actionInputs.
     * @param actionsInput The current action to be completed.
     */
    public void run(final ActionsInput actionsInput) {
        switch (actionsInput.getCommand()) {
            case "endPlayerTurn" -> {
                if (turnNumber % 2 == 0) {
                    startRound();
                }
                endTurn();
            }
            case "placeCard" ->
                currentPlayer.placeCard(actionsInput.getHandIdx());
            case "cardUsesAttack" ->
                /* I will choose to use my 30 coding style errors for lines >100 characters as this
                   currentPlayer.attackCard(actionsInput.getCardAttacker(),
                                            actionsInput.getCardAttacked());
                   is worse than having it >100 characters. */
                currentPlayer.attackCard(actionsInput.getCardAttacker(), actionsInput.getCardAttacked());
            case "cardUsesAbility" ->
                currentPlayer.useAbility(actionsInput.getCardAttacker(), actionsInput.getCardAttacked());
            case "useAttackHero" ->
                currentPlayer.attackHero(actionsInput.getCardAttacker(), awaitingPlayer.getHeroCard());
            case "useHeroAbility" ->
                currentPlayer.useHeroAbility(actionsInput.getAffectedRow());
            case "getCardsInHand" ->
                Game.getInstance().getPlayer(actionsInput.getPlayerIdx()).getCardsInHand();
            case "getPlayerDeck" ->
                Game.getInstance().getPlayer(actionsInput.getPlayerIdx()).getPlayerDeck();
            case "getCardsOnTable" ->
                Board.getInstance().getCardsOnTable();
            case "getPlayerTurn" ->
                Game.getInstance().getPlayerTurn();
            case "getPlayerHero" ->
                Game.getInstance().getPlayer(actionsInput.getPlayerIdx()).getPlayerHero();
            case "getCardAtPosition" ->
                Board.getInstance().getCardAtPosition(actionsInput.getX(), actionsInput.getY());
            case "getPlayerMana" ->
                Game.getInstance().getPlayer(actionsInput.getPlayerIdx()).getPlayerMana();
            case "getFrozenCardsOnTable" ->
                Board.getInstance().getFrozenCardsOnTable();
            case "getTotalGamesPlayed" ->
                Game.getInstance().getTotalGamesPlayed();
            case "getPlayerOneWins" ->
                Game.getInstance().getPlayer(1).getPlayerWins();
            case "getPlayerTwoWins" ->
                Game.getInstance().getPlayer(2).getPlayerWins();
            default ->
                throw new IllegalArgumentException("Unknown command" + actionsInput.getCommand());
        }
    }

    /**
     * This method handles the start of a round, each player gaining mana and taking a card.
     */
    public void startRound() {
        roundNumber++;
        currentPlayer.setCurrentMana(currentPlayer.getCurrentMana()
                + Math.min(roundNumber, Constants.MAX_MANA_PER_ROUND));
        awaitingPlayer.setCurrentMana(awaitingPlayer.getCurrentMana()
                + Math.min(roundNumber, Constants.MAX_MANA_PER_ROUND));
        currentPlayer.takeCard();
        awaitingPlayer.takeCard();
    }

    /**
     * This method handles the end of a turn, cards getting unfrozen and removing their hasAttacked status.
     * It also swaps the current and awaiting player.
     */
    public void endTurn() {
        turnNumber++;
        for (MinionCard minionCard : Board.getInstance().getGameBoard().get(currentPlayer.getFrontRow())) {
                minionCard.setFrozen(false);
                minionCard.setHasAttacked(false);
        }
        for (MinionCard minionCard : Board.getInstance().getGameBoard().get(currentPlayer.getBackRow())) {
                minionCard.setFrozen(false);
                minionCard.setHasAttacked(false);
        }
        currentPlayer.getHeroCard().setHasAttacked(false);
        swapPlayers();
    }

    /**
     * This method swaps the current and awaiting player, used at the end of a turn.
     */
    private void swapPlayers() {
        Player temp = currentPlayer;
        currentPlayer = awaitingPlayer;
        awaitingPlayer = temp;
    }
}
