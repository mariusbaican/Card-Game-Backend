package util;

import fileio.Coordinates;

@FunctionalInterface
/**
 * This interface is used to store the cards' ability inside their instances.
 */
public interface Ability {
    /**
     * This method is used to run a card's ability.
     * @param coordinates The desired coordinates of the card/row to be attacked.
     */
    void run(Coordinates coordinates);
}
