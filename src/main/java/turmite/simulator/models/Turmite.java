package turmite.simulator.models;

import turmite.simulator.utils.Ruleset;

/**
 * A class that represents a Turmite.
 */
public final class Turmite {
    private final Ruleset ruleset;

    private final Grid pos;
    private Direction dir;
    private int state;

    public Turmite(Grid pos, Ruleset ruleset) {
        this.ruleset = ruleset;
        this.pos = pos;
        dir = Direction.UP;
        state = 0;
    }

    /**
     * The method calculates the new State and Direction of the Turmite based on
     * its internal State and {@code currColor}, then moves it.
     *
     * @param currColor The Color the Turmite is currently standing on.
     */
    public void move(int currColor) {
        calculateNextDirection(currColor);
        calculateNextState(currColor);

        switch (dir) {
            case UP:
                moveUp();
                break;
            case DOWN:
                moveDown();
                break;
            case LEFT:
                moveLeft();
                break;
            case RIGHT:
                moveRight();
                break;
            default:
                break;
        }
    }

    /**
     * @return The Grid the Turmite is standing on.
     */
    public Grid getGrid() {
        return pos;
    }

    /**
     * @return The X coordinate of the Turmite.
     */
    public int getX() {
        return pos.getX();
    }

    /**
     * @return The Y coordinate of the Turmite.
     */
    public int getY() {
        return pos.getY();
    }

    /**
     * Turns the Turmite based on its internal State and the Color of the Grid it is currently
     * standing on using the Ruleset.
     *
     * @param currColor The Color the Turmite is currently standing on.
     */
    private void calculateNextDirection(int currColor) {
        for (Rule rule : ruleset.getRules()) {
            if (rule.getCurrState() == state && rule.getCurrColor() == currColor) {
                Direction turnDir = rule.getTurnDir();
                switch (turnDir) {
                    case Direction.LEFT -> turnLeft();
                    case Direction.RIGHT -> turnRight();
                    case Direction.U_TURN -> turnBack();
                    default -> {
                        if (!turnDir.equals(Direction.NO_TURN))
                            throw new IllegalArgumentException("A Rule has an invalid Direction set.");
                    }
                }
            }
        }
    }

    /**
     * Calculates the next State of the Turmite based on its internal State and the Color of the Grid it is currently
     * standing on using the Ruleset.
     *
     * @param currColor The Color the Turmite is currently standing on.
     */
    private void calculateNextState(int currColor) {
        for (Rule rule : ruleset.getRules()) {
            if (rule.getCurrState() == state && rule.getCurrColor() == currColor) {
                state = rule.getNewState();
                return;
            }
        }
    }

    /**
     * Calculates the Color to paint the current Grid based on the Turmite's internal State and the Color of
     * the Grid it is currently standing on using the Ruleset.
     *
     * @param currColor The Color the Turmite is currently standing on.
     * @return The Color to paint the Grid before the Turmite moves.
     */
    public int calculateNextColor(int currColor) {
        int newColor = -1;

        for (Rule rule : ruleset.getRules())
            if (rule.getCurrState() == state && rule.getCurrColor() == currColor) {
                newColor = rule.getNewColor();
                break;
            }

        return newColor;
    }

    /**
     * Turns the Turmite left.
     */
    private void turnLeft() {
        switch (dir) {
            case UP:
                dir = Direction.LEFT;
                break;
            case DOWN:
                dir = Direction.RIGHT;
                break;
            case LEFT:
                dir = Direction.DOWN;
                break;
            case RIGHT:
                dir = Direction.UP;
                break;
            default:
                throw new IllegalArgumentException("The Turmite has an invalid Direction set.");
        }
    }


    /**
     * Turns the Turmite right.
     */
    private void turnRight() {
        switch (dir) {
            case UP:
                dir = Direction.RIGHT;
                break;
            case DOWN:
                dir = Direction.LEFT;
                break;
            case LEFT:
                dir = Direction.UP;
                break;
            case RIGHT:
                dir = Direction.DOWN;
                break;
            default:
                throw new IllegalArgumentException("The Turmite has an invalid Direction set.");
        }
    }

    /**
     * Turns the Turmite back (180deg).
     */
    private void turnBack() {
        switch (dir) {
            case UP:
                dir = Direction.DOWN;
                break;
            case DOWN:
                dir = Direction.UP;
                break;
            case LEFT:
                dir = Direction.RIGHT;
                break;
            case RIGHT:
                dir = Direction.LEFT;
                break;
            default:
                throw new IllegalArgumentException("The Turmite has an invalid Direction set.");
        }
    }


    /**
     * Moves the Turmite up one Grid.
     */
    private void moveUp() {
        pos.moveUp();
    }

    /**
     * Moves the Turmite down one Grid.
     */
    private void moveDown() {
        pos.moveDown();
    }

    /**
     * Moves the Turmite left one Grid.
     */
    private void moveLeft() {
        pos.moveLeft();
    }

    /**
     * Moves the Turmite right one Grid.
     */
    private void moveRight() {
        pos.moveRight();
    }

    /**
     * Reset the Turmite to its default. Position, Direction and State are affected.
     */
    public void reset() {
        pos.reset();
        dir = Direction.UP;
        state = 0;
    }
}
