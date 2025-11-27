package turmite.simulator.models;

public class Grid {
    private int x;
    private int y;
    private final int size;

    public Grid(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Grid(Grid pos) {
        this.x = pos.x;
        this.y = pos.y;
        this.size = pos.size;
    }

    public void moveUp() {
        y -= size;
    }

    public void moveDown() {
        y += size;
    }

    public void moveLeft() {
        x -= size;
    }

    public void moveRight() {
        x += size;
    }
}
