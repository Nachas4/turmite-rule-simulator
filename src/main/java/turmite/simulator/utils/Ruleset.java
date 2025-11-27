package turmite.simulator.utils;

import turmite.simulator.models.Direction;
import turmite.simulator.models.Rule;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Ruleset {
    private static final int MAX_SIZE = 9;
    private static final int MAX_STATES = 3;
    private static final int MAX_COLORS = 3;

    private int highestState;
    private int highestColor;

    private final List<Rule> rules = new ArrayList<>(MAX_SIZE);

    public void addRule(int currState, int currColor, Character dirChar, int newColor, int newState) {
        if (rules.size() < MAX_SIZE) {
            Rule rule = validateRule(currState, currColor, dirChar, newColor, newState);
            calculateNumOfStatesAndColors();
            rules.add(rule);
        }
    }

    private void calculateNumOfStatesAndColors() {
        highestState = 0;
        highestColor = 0;
        for (Rule rule : rules) {
            if (rule.getCurrState() > highestState) highestState = rule.getCurrState();
            if (rule.getNewState() > highestState) highestState = rule.getNewState();
            if (rule.getCurrColor() > highestColor) highestColor = rule.getCurrColor();
            if (rule.getNewColor() > highestColor) highestColor = rule.getNewColor();
        }
    }

    private Rule validateRule(int currState, int currColor, Character dirChar, int newColor, int newState) throws IllegalArgumentException {
        if (currState >= MAX_STATES || currState < 0
            || newState >= MAX_STATES || newState < 0)
            throw new IllegalArgumentException(String.format("State cannot be negative or higher than %d.", MAX_STATES - 1));

        if (currColor >= MAX_COLORS || currColor < 0
            || newColor >= MAX_COLORS || newColor < 0)
            throw new IllegalArgumentException(String.format("Color cannot be negative or higher than %d.", MAX_COLORS - 1));

        Direction dir = switch (dirChar) {
            case 'L' -> Direction.LEFT;
            case 'R' -> Direction.RIGHT;
            case 'N' -> Direction.NO_TURN;
            case 'U' -> Direction.U_TURN;
            default -> throw new IllegalArgumentException("The provided Direction is not valid.");
        };

        return new Rule(currState, currColor, dir, newColor, newState);
    }

    public static Color numToColor(int num) {
        return switch (num) {
            case 0 -> Color.WHITE;
            case 1 -> Color.BLACK;
            case 2 -> Color.YELLOW;
            default -> throw new IllegalArgumentException("Unexpected value: " + num);
        };
    }

    public int getNumOfRulesNeeded() {
        return (highestState + 1) * (highestColor + 1);
    }

    public List<Rule> getRules() {
        return rules;
    }
}
