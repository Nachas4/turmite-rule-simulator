package turmite.simulator.models;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that defines one Rule of a Ruleset.
 */
public class Rule {
    private int currState;
    private int currColor;
    private final Direction turnDir;
    private final int newColor;
    private final int newState;

    /**
     * The enum representation of a Rule's properties, called its cells.
     * Also includes a method to get the String names of all Rule cells.
     */
    public enum RuleCells {
        CURR_STATE,
        CURR_COLOR,
        TURN_DIR,
        NEW_COLOR,
        NEW_STATE;

        /**
         * Get the name of all Rule cells.
         *
         * @return The list of cell names.
         */
        public static List<String> getRuleCellNames() { return new ArrayList<>(List.of("currState", "currColor", "turnDir", "newColor", "newState")); }
    }

    public Rule() {
        this.currState = 0;
        this.currColor = 0;
        this.turnDir = Direction.LEFT;
        this.newColor = 0;
        this.newState = 0;
    }

    public Rule(int currState, int currColor, Direction turnDir, int newColor, int newState) {
        this.currState = currState;
        this.currColor = currColor;
        this.turnDir = turnDir;
        this.newColor = newColor;
        this.newState = newState;
    }

    /**
     * @return The "currState" value of the Rule.
     */
    public int getCurrState() {
        return currState;
    }

    /**
     * Sets "currState" value of the Rule.
     */
    public void setCurrState(int newValue) {
        currState = newValue;
    }

    /**
     * @return The "currColor" value of the Rule.
     */
    public int getCurrColor() {
        return currColor;
    }

    /**
     * Sets "currColor" value of the Rule.
     */
    public void setCurrColor(int newValue) {
        currColor = newValue;
    }

    /**
     * @return The "turnDir" value of the Rule.
     */
    public Direction getTurnDir() {
        return turnDir;
    }

    /**
     * @return The "newColor" value of the Rule.
     */
    public int getNewColor() {
        return newColor;
    }

    /**
     * @return The "newState" value of the Rule.
     */
    public int getNewState() {
        return newState;
    }

    /**
     * A Rule's "colors" are saved as integer values.
     * This method returns Graphics usable Colors for the numbers a Rule can use.
     *
     * @param num The number to be converted.
     * @return The Color associated with the number.
     */
    public static Color numToColor(int num) {
        return switch (num) {
            case 0 -> Color.WHITE;
            case 1 -> Color.BLACK;
            case 2 -> Color.YELLOW;
            default -> throw new IllegalArgumentException("No Color found for value: " + num);
        };
    }
}
