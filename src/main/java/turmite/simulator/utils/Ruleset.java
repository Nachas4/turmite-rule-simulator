package turmite.simulator.utils;

import turmite.simulator.models.Direction;
import turmite.simulator.models.Rule;

import javax.json.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Ruleset {
    public static final int MAX_STATES = 3;
    public static final int MAX_COLORS = 3;
    public static final int MAX_RULES = MAX_STATES * MAX_COLORS;

    public static final int SQUARE_GRID_MAX_TURN_DIRS = 4;

    private int highestState;
    private int highestColor;

    private final List<Rule> rules = new ArrayList<>(MAX_RULES);

    public void readRulesetFromFile(String src) throws FileNotFoundException {
        clearRules();

        InputStream fileStream = new FileInputStream(src);
        JsonReader reader = Json.createReader(fileStream);
        JsonObject rulesetObj = reader.readObject();

        JsonArray ruleset = rulesetObj.getJsonArray("ruleset");

        for (JsonValue rule : ruleset) {
            JsonObject ruleObj = (JsonObject) rule;
            addRule(
                    ruleObj.getInt("currState"),
                    ruleObj.getInt("currColor"),
                    ruleObj.getString("turnDir").charAt(0),
                    ruleObj.getInt("newColor"),
                    ruleObj.getInt("newState")
            );
        }

        reader.close();

        calculateNumOfStatesAndColors();
    }

    public void addRule(int currState, int currColor, char dirChar, int newColor, int newState) {
        if (rules.size() < MAX_RULES) {
            Rule rule = validateRuleCells(currState, currColor, dirChar, newColor, newState);
            calculateNumOfStatesAndColors();
            rules.add(rule);
        }
    }

    public void changeRuleCell(int ruleNum, Rule.RuleCells cell, Object newValue) throws IllegalArgumentException {
        Rule rule = rules.get(ruleNum);
        Rule newRule = switch (cell) {
            case CURR_STATE ->
                    validateRuleCells((int)newValue, rule.getCurrColor(), Direction.getCharFromTurnDir(rule.getTurnDir()), rule.getNewColor(), rule.getNewState());
            case CURR_COLOR ->
                    validateRuleCells(rule.getCurrState(), (int)newValue, Direction.getCharFromTurnDir(rule.getTurnDir()), rule.getNewColor(), rule.getNewState());
            case TURN_DIR ->
                    validateRuleCells(rule.getCurrState(), rule.getCurrColor(), (char)newValue, rule.getNewColor(), rule.getNewState());
            case NEW_COLOR ->
                    validateRuleCells(rule.getCurrState(), rule.getCurrColor(), Direction.getCharFromTurnDir(rule.getTurnDir()), (int)newValue, rule.getNewState());
            case NEW_STATE ->
                    validateRuleCells(rule.getCurrState(), rule.getCurrColor(), Direction.getCharFromTurnDir(rule.getTurnDir()), rule.getNewColor(), (int)newValue);
        };

        rules.set(rules.indexOf(rule), newRule);
        calculateNumOfStatesAndColors();
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

    private Rule validateRuleCells(int currState, int currColor, char dirChar, int newColor, int newState) throws IllegalArgumentException {
        if (currState >= MAX_STATES || currState < 0)
            throw new IllegalArgumentException(String.format("State cannot be negative or higher than %d (got %d).", MAX_STATES - 1, currState));
        if (newState >= MAX_STATES || newState < 0)
            throw new IllegalArgumentException(String.format("State cannot be negative or higher than %d (got %d).", MAX_STATES - 1, newState));

        if (newColor >= MAX_COLORS || newColor < 0)
            throw new IllegalArgumentException(String.format("Color cannot be negative or higher than %d (got %d).", MAX_COLORS - 1, newColor));
        if (currColor >= MAX_COLORS || currColor < 0)
            throw new IllegalArgumentException(String.format("Color cannot be negative or higher than %d (got %d).", MAX_COLORS - 1, currColor));

        Direction dir = switch (dirChar) {
            case 'L' -> Direction.LEFT;
            case 'R' -> Direction.RIGHT;
            case 'N' -> Direction.NO_TURN;
            case 'U' -> Direction.U_TURN;
            default -> throw new IllegalArgumentException(String.format("The provided Direction is not valid: %s", dirChar));
        };

        return new Rule(currState, currColor, dir, newColor, newState);
    }

    public static Color numToColor(int num) {
        return switch (num) {
            case 0 -> Color.WHITE;
            case 1 -> Color.BLACK;
            case 2 -> Color.YELLOW;
            default -> throw new IllegalArgumentException("No Color found for value: " + num);
        };
    }

    public int getNumOfRulesNeeded() {
        return (highestState + 1) * (highestColor + 1);
    }

    public List<Rule> getRules() {
        return rules;
    }

    private void clearRules() {
        rules.clear();
    }
}
