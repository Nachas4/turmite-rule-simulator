package turmite.simulator.models;

public class Turmite {
    private final GridPos pos = new GridPos(0, 0);

    public int getX() {
        return pos.getX();
    }

    public int getY() {
        return pos.getY();
    }

    public void moveUp() {
        pos.moveUp();
    }

    public void moveDown() {
        pos.moveDown();
    }

    public void moveLeft() {
        pos.moveLeft();
    }

    public void moveRight() {
        pos.moveRight();
    }
}
