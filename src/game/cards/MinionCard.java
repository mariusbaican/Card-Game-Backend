package game.cards;

import fileio.CardInput;
import fileio.Coordinates;
import game.board.Board;
import lombok.Data;

import java.util.ArrayList;

@Data
public class MinionCard extends Card {
    public enum Type {
        REGULAR,
        TANK,
        DRUID,
        LEGENDARY
    }

    protected int attackDamage;
    protected Type minionType;

    public MinionCard(Coordinates coordinates, Type minionType) {}

    public MinionCard(CardInput cardInput) {
        super(cardInput);
        this.attackDamage = cardInput.getAttackDamage();

        initAbilities(cardInput.getName());
    }

    public MinionCard(int mana, int health, int attackDamage, String description, ArrayList colors, String name) {
        super(mana, health, description, colors, name);
        this.attackDamage = attackDamage;
        cardType = Card.Type.MINION;

        initAbilities(name);
    }

    private void initAbilities (String name) {
        switch (name) {
            case "Sentinel", "Berserker" -> {
                minionType = Type.REGULAR;
                setAbility(null);
            }
            case "Goliath", "Warden" -> {
                minionType = Type.TANK;
                setAbility(null);
            }
            case "The Cursed One" -> {
                minionType = Type.DRUID;
                setAbility((Coordinates coordinates) -> {
                    int newAttackDamage = Board.getInstance().getCard(coordinates).getHealth();
                    int newHealth = Board.getInstance().getCard(coordinates).getAttackDamage();
                    Board.getInstance().getCard(coordinates).setAttackDamage(newAttackDamage);
                    Board.getInstance().getCard(coordinates).setHealth(newHealth);
                });
            }
            case "Disciple" -> {
                minionType = Type.DRUID;
                setAbility((Coordinates coordinates) -> {
                    int newHealth = Board.getInstance().getCard(coordinates).getHealth() + 2;
                    Board.getInstance().getCard(coordinates).setHealth(newHealth);
                });
            }
            case "The Ripper" -> {
                minionType = Type.LEGENDARY;
                setAbility((Coordinates coordinates) -> {
                    int newAttackDamage = Math.max(Board.getInstance().getCard(coordinates).getAttackDamage() - 2, 0);
                    Board.getInstance().getCard(coordinates).setAttackDamage(newAttackDamage);
                });
            }
            case "Miraj" -> {
                minionType = Type.LEGENDARY;
                setAbility((Coordinates coordinates) -> {
                    int newMirajHealth = Board.getInstance().getCard(coordinates).getHealth();
                    int newOpponentHealth = this.getHealth();
                    this.setHealth(newMirajHealth);
                    Board.getInstance().getCard(coordinates).setHealth(newOpponentHealth);
                });
            }
            default -> System.out.println("Invalid Minion name");
        }
    }


}
