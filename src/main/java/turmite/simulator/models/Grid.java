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

    public Grid(Grid grid) {
        this.x = grid.x;
        this.y = grid.y;
        this.size = grid.size;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Grid grid) {
            return this.x == grid.x && this.y == grid.y && this.size == grid.size;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        long bits = java.lang.Double.doubleToLongBits(x);
        bits ^= java.lang.Double.doubleToLongBits(y) * 31;
        return (((int) bits) ^ ((int) (bits >> 32)));
    }
}
