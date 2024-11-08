package game.util;

import fileio.Coordinates;

@FunctionalInterface
/**
 * This interface is used to store a card's ability directly in its instance.
 */
public interface Ability {
    /**
     * This method runs the ability of a card.
     * @param coordinates The desired coordinates/row to be attacked.
     */
    void run(Coordinates coordinates);
}
