package turmite.simulator.models;

import turmite.simulator.utils.Ruleset;

public final class Turmite {
    private final Ruleset ruleset = new Ruleset();

    private final Grid pos;
    private Direction dir;
    private int state;

    public Turmite(Grid pos) {
        this.pos = pos;
        dir = Direction.UP;
        state = 0;
    }

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

    public Grid getGrid() {
        return pos;
    }

    public int getX() {
        return pos.getX();
    }

    public int getY() {
        return pos.getY();
    }

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

    private void calculateNextState(int currColor) {
        for (Rule rule : ruleset.getRules())
            if (rule.getCurrState() == state && rule.getCurrColor() == currColor)
                state = rule.getNewState();
    }

    public int calculateNextColor(int currColor) {
        int newColor = -1;

        for (Rule rule : ruleset.getRules())
            if (rule.getCurrState() == state && rule.getCurrColor() == currColor)
                newColor = rule.getNewColor();

        return newColor;
    }

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

    private void moveUp() {
        pos.moveUp();
    }

    private void moveDown() {
        pos.moveDown();
    }

    private void moveLeft() {
        pos.moveLeft();
    }

    private void moveRight() {
        pos.moveRight();
    }

    public void resetPos() {
        pos.reset();
    }

    public Ruleset getRuleset() {
        return ruleset;
    }
}
