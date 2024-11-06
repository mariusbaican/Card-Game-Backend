package game;

import com.fasterxml.jackson.databind.node.ArrayNode;
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
                throw new RuntimeException("Unknown command");
        }
    }

    public void startRound() {
        roundNumber++;
        currentPlayer.setCurrentMana(currentPlayer.getCurrentMana() + Math.min(roundNumber, 10));
        awaitingPlayer.setCurrentMana(awaitingPlayer.getCurrentMana() + Math.min(roundNumber, 10));
        currentPlayer.takeCard();
        awaitingPlayer.takeCard();
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
