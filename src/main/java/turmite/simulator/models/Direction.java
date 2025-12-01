package turmite.simulator.models;

import java.util.List;

public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    NO_TURN,
    U_TURN;

    public static List<Character> getSquareGridTurnDirs() {
        return List.of('L', 'R', 'N', 'U');
    }

    public static Direction getTurnDirFromChar(Character character) {
        return switch (character) {
            case 'L' -> LEFT;
            case 'R' -> RIGHT;
            case 'N' -> NO_TURN;
            case 'U' -> U_TURN;
            default -> throw new IllegalArgumentException("Turn Direction Character not recognized: " + character);
        };
    }

    public static Character getCharFromTurnDir(Direction dir) {
        return switch (dir) {
            case LEFT -> 'L';
            case RIGHT -> 'R';
            case U_TURN -> 'U';
            case NO_TURN -> 'N';
            default -> throw new IllegalArgumentException("Turn Direction not recognized: " + dir);
        };
    }
}
