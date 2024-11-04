package game;

import fileio.ActionsInput;
import game.board.Board;
import game.cards.MinionCard;
import game.player.Player;
import lombok.Data;

@Data
public class ActionHandler {

    private static ActionHandler actionHandler = new ActionHandler();

    private Player currentPlayer;
    private Player awaitingPlayer;

    private int roundNumber;
    private int turnNumber;
    private int gameCount;
    private boolean isStarted = false;

    private ActionHandler() {
        gameCount = 0;
        roundNumber = 0;
        turnNumber = 1;
    }

    public static ActionHandler getInstance() {
        return actionHandler;
    }

    public void reset() {
        currentPlayer = null;
        awaitingPlayer = null;
        turnNumber = 1;
        roundNumber = 0;
        gameCount++;
    }

    public void addPlayers(Player player1, Player player2, int startingPlayer) {
        if (startingPlayer == 1) {
            currentPlayer = player1;
            awaitingPlayer = player2;
        } else {
            currentPlayer = player2;
            awaitingPlayer = player1;
        }
        currentPlayer.setPlayerIndex(1);
        awaitingPlayer.setPlayerIndex(2);
    }

    public void run(ActionsInput actionsInput) {
        switch (actionsInput.getCommand()) {
            case "endPlayerTurn" -> {
                if (turnNumber % 2 == 0)
                    startRound();
                endTurn();
            }
            case "placeCard" ->
                currentPlayer.placeCard(actionsInput.getHandIdx());

            case "cardUsesAttack" ->
                currentPlayer.attackCard(actionsInput.getCardAttacker(), actionsInput.getCardAttacked());

            case "cardUsesAbility" ->
                currentPlayer.useAbility(actionsInput.getCardAttacker(), actionsInput.getCardAttacked());

            case "useAttackHero" ->
                currentPlayer.attackHero(actionsInput.getCardAttacker(), awaitingPlayer.getHeroCard());

            case "useHeroAbility" ->
                currentPlayer.useHeroAbility(actionsInput.getAffectedRow());
            default -> {
                return;
            }
        }
    }

    public void startRound() {
        roundNumber++;
        currentPlayer.setCurrentMana(currentPlayer.getCurrentMana() + Math.min(roundNumber, 10));
        awaitingPlayer.setCurrentMana(awaitingPlayer.getCurrentMana() + Math.min(roundNumber, 10));
        currentPlayer.takeCard();
        awaitingPlayer.takeCard();

        isStarted = true;
    }

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
        swapPlayers();
    }

    public void swapPlayers() {
        Player temp = currentPlayer;
        currentPlayer = awaitingPlayer;
        awaitingPlayer = temp;
    }
}
