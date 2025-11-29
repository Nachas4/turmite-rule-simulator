package turmite.simulator.models;

public class Rule {
    private final Integer currState;
    private final int currColor;
    private final Direction turnDir;
    private final int newColor;
    private final int newState;

    public enum RuleCells {
        CURR_STATE,
        CURR_COLOR,
        TURN_DIR,
        NEW_COLOR,
        NEW_STATE
    }

    public Rule(int currState, int currColor, Direction turnDir, int newColor, int newState) {
        this.currState = currState;
        this.currColor = currColor;
        this.turnDir = turnDir;
        this.newColor = newColor;
        this.newState = newState;
    }

    public int getCurrState() {
        return currState;
    }

    public int getCurrColor() {
        return currColor;
    }

    public Direction getTurnDir() {
        return turnDir;
    }

    public int getNewColor() {
        return newColor;
    }

    public int getNewState() {
        return newState;
    }
}
