package turmite.simulator.models;

/**
 * A class that represents a square Grid.
 */
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

    /**
     * Moves the position of this Grid up by one.
     */
    public void moveUp() {
        y -= size;
    }

    /**
     * Moves the position of this Grid down by one.
     */
    public void moveDown() {
        y += size;
    }

    /**
     * Moves the position of this Grid left by one.
     */
    public void moveLeft() {
        x -= size;
    }

    /**
     * Moves the position of this Grid right by one.
     */
    public void moveRight() {
        x += size;
    }

    /**
     * Reset the position to {@code (0, 0)}.
     */
    public void reset() {
        x = 0;
        y = 0;
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
