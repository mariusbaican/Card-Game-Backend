package game.cards.special;

@FunctionalInterface
public interface SpecialAbility {
    void run(int row, int column);
}
