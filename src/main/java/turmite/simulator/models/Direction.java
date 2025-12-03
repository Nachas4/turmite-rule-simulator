package turmite.simulator.models;

import java.util.List;

/**
 * An enum that represents Direction, both absolute and relative.
 */
public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    NO_TURN,
    U_TURN;

    /**
     * @return The Chars of the directions one can turn on a square grid.
     */
    public static List<Character> getSquareGridTurnDirs() {
        return List.of('L', 'R', 'N', 'U');
    }

    /**
     * @param character The character to convert.
     * @return The Turn Direction the character describes.
     */
    public static Direction getTurnDirFromChar(char character) {
        return switch (character) {
            case 'L' -> LEFT;
            case 'R' -> RIGHT;
            case 'N' -> NO_TURN;
            case 'U' -> U_TURN;
            default -> throw new IllegalArgumentException("Turn Direction Character not recognized: " + character);
        };
    }

    /**
     * @param dir The direction to convert.
     * @return The character for the Turn Direction.
     */
    public static char getCharFromTurnDir(Direction dir) {
        return switch (dir) {
            case LEFT -> 'L';
            case RIGHT -> 'R';
            case U_TURN -> 'U';
            case NO_TURN -> 'N';
            default -> throw new IllegalArgumentException("Turn Direction not recognized: " + dir);
        };
    }
}
