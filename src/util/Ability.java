package util;

import fileio.Coordinates;

@FunctionalInterface
public interface Ability
{
    void run(Coordinates coordinates);
}
