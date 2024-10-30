package game.cards;

import fileio.CardInput;
import fileio.Coordinates;
import game.board.Board;
import lombok.Data;

@Data
public class HeroCard extends Card {

    public HeroCard(CardInput cardInput) {
        super(cardInput);
        cardType = Type.HERO;

        initAbilities(cardInput.getName());
    }

    private void initAbilities(String name) {
        switch (name) {
            case "Lord Royce" -> this.setAbility((Coordinates coordinates) -> {
                for (MinionCard minionCard : Board.getInstance().getGameBoard().get(coordinates.getX()))
                    minionCard.setFrozen(true);
            });
            case "Empress Thorina" -> this.setAbility((Coordinates coordinates) -> {
                MinionCard healthiestCard = Board.getInstance().getGameBoard().get(coordinates.getX()).get(0);
                for (MinionCard minionCard : Board.getInstance().getGameBoard().get(coordinates.getX()))
                    if (minionCard.getHealth() > healthiestCard.getHealth())
                        healthiestCard = minionCard;
                healthiestCard.setHealth(0);
            });
            case "King Mudface" -> this.setAbility((Coordinates coordinates) -> {
                for (MinionCard minionCard : Board.getInstance().getGameBoard().get(coordinates.getX()))
                    minionCard.setHealth(minionCard.getHealth() + 1);
            });
            case "General Kocioraw" -> this.setAbility((Coordinates coordinates) -> {
                for (MinionCard minionCard : Board.getInstance().getGameBoard().get(coordinates.getX()))
                    minionCard.setAttackDamage(minionCard.getAttackDamage() + 1);
            });
            default -> System.out.println("Invalid Hero name");
        }
    }
}
