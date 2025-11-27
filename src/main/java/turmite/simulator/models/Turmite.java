package turmite.simulator.models;

public final class Turmite {
    private final Grid pos;
    private Direction dir;

    public Turmite(Grid pos) {
        dir = Direction.UP;
        this.pos = pos;
    }

    public int getX() {
        return pos.getX();
    }

    public int getY() {
        return pos.getY();
    }

    public void move() {
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

    public Grid getPos() {
        return pos;
    }

    public void turnLeft() {
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
        }
    }

    public void turnRight() {
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
        }
    }
}
