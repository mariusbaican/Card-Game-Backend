package game.cards;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;
import fileio.Coordinates;
import game.board.Board;
import lombok.Data;

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

    public MinionCard(CardInput cardInput) {
        super(cardInput);
        this.attackDamage = cardInput.getAttackDamage();
        isFrozen = true;
        hasAttacked = false;

        initAbilities(cardInput.getName());
    }

    public MinionCard(MinionCard minionCard) {
        mana = minionCard.mana;
        health = minionCard.health;
        description = minionCard.description;
        colors = minionCard.colors;
        name = minionCard.name;
        isFrozen = false;
        hasAttacked = false;
        cardType = minionCard.cardType;
        attackDamage = minionCard.attackDamage;
        minionType = minionCard.minionType;

        initAbilities(name);
    }

    private void initAbilities (String name) {
        switch (name) {
            case "Sentinel", "Berserker" -> {
                minionType = Type.REGULAR;
                this.setAbility(null);
            }
            case "Goliath", "Warden" -> {
                minionType = Type.TANK;
                this.setAbility(null);
            }
            case "The Cursed One" -> {
                minionType = Type.DRUID;
                this.setAbility((Coordinates coordinates) -> {
                    int newAttackDamage = Board.getInstance().getCard(coordinates).getHealth();
                    int newHealth = Board.getInstance().getCard(coordinates).getAttackDamage();
                    Board.getInstance().getCard(coordinates).setAttackDamage(newAttackDamage);
                    Board.getInstance().getCard(coordinates).setHealth(newHealth);
                });
            }
            case "Disciple" -> {
                minionType = Type.DRUID;
                this.setAbility((Coordinates coordinates) -> {
                    int newHealth = Board.getInstance().getCard(coordinates).getHealth() + 2;
                    Board.getInstance().getCard(coordinates).setHealth(newHealth);
                });
            }
            case "The Ripper" -> {
                minionType = Type.LEGENDARY;
                this.setAbility((Coordinates coordinates) -> {
                    int newAttackDamage = Math.max(Board.getInstance().getCard(coordinates).getAttackDamage() - 2, 0);
                    Board.getInstance().getCard(coordinates).setAttackDamage(newAttackDamage);
                });
            }
            case "Miraj" -> {
                minionType = Type.LEGENDARY;
                this.setAbility((Coordinates coordinates) -> {
                    int newMirajHealth = Board.getInstance().getCard(coordinates).getHealth();
                    int newOpponentHealth = this.getHealth();
                    this.setHealth(newMirajHealth);
                    Board.getInstance().getCard(coordinates).setHealth(newOpponentHealth);
                });
            }
            default -> System.out.println("Invalid Minion name");
        }
    }

    @Override
    public ObjectNode outputCard(ObjectMapper objectMapper) {
        ObjectNode currentCard = objectMapper.createObjectNode();
        currentCard.put("mana", mana);
        currentCard.put("attackDamage", attackDamage);
        currentCard.put("health", health);
        currentCard.put("description", description);
        ArrayNode colorArray = objectMapper.createArrayNode();
        for (String color : colors) {
            colorArray.add(color);
        }
        currentCard.put("colors", colorArray);
        currentCard.put("name", name);
        return currentCard;
    }

}
