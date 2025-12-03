package turmite.simulator.models;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Rule {
    private int currState;
    private int currColor;
    private final Direction turnDir;
    private final int newColor;
    private final int newState;

    public enum RuleCells {
        CURR_STATE,
        CURR_COLOR,
        TURN_DIR,
        NEW_COLOR,
        NEW_STATE;

        public static List<String> getRuleCellNames() { return new ArrayList<>(List.of("currState", "currColor", "turnDir", "newColor", "newState")); }
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

    public void setCurrState(int newValue) {
        currState = newValue;
    }

    public int getCurrColor() {
        return currColor;
    }

    public void setCurrColor(int newValue) {
        currColor = newValue;
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

    public static Color numToColor(int num) {
        return switch (num) {
            case 0 -> Color.WHITE;
            case 1 -> Color.BLACK;
            case 2 -> Color.YELLOW;
            default -> throw new IllegalArgumentException("No Color found for value: " + num);
        };
    }
}
