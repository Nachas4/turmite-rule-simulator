package turmite.simulator.utils;

import turmite.simulator.TurmiteJFrame;
import turmite.simulator.models.Direction;
import turmite.simulator.models.Rule;

import javax.json.*;
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

    public void readRulesetFromFile(String fileName) throws FileNotFoundException, IllegalArgumentException {
        InputStream fileStream = new FileInputStream(String.format("%s/%s", TurmiteJFrame.RULESET_DIR, fileName));
        JsonReader reader = Json.createReader(fileStream);
        JsonObject rulesetObj = reader.readObject();

        JsonArray ruleset = rulesetObj.getJsonArray("ruleset");

        List<Rule> rulesSnapshot = new ArrayList<>(rules);
        rules.clear();

        for (JsonValue rule : ruleset) {
            JsonObject ruleObj = (JsonObject) rule;
            try {
                List<String> ruleNames = Rule.RuleCells.getRuleCellNames();
                addRule(
                        ruleObj.getInt(ruleNames.getFirst()),
                        ruleObj.getInt(ruleNames.get(1)),
                        ruleObj.getString(ruleNames.get(2)).charAt(0),
                        ruleObj.getInt(ruleNames.get(3)),
                        ruleObj.getInt(ruleNames.getLast())
                );
            } catch (IllegalArgumentException e) {
                rules.clear();
                rules.addAll(rulesSnapshot);
                throw e;
            }
        }

        reader.close();
        recalculateNumOfStatesAndColors();
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
        recalculateRulesetTable();
    }

    private void recalculateRulesetTable() {
        recalculateNumOfStatesAndColors();
        int needed = getNumOfRulesNeeded();
        int diff = rules.size() - needed;

        if (diff > 0) for (int i = 0; i < diff; i++) rules.removeLast();

        int row = 0;
        int currRuleSize = rules.size();
        for (int i = 0; i < highestState + 1; i++) {
            for (int j = 0; j < highestColor + 1; j++) {
                if (row < currRuleSize) {
                    rules.get(row).setCurrState(i);
                    rules.get(row).setCurrColor(j);
                }
                else rules.add(new Rule(i, j, Direction.LEFT, 0, 0));

                row++;
            }
        }
    }

    private void recalculateNumOfStatesAndColors() {
        highestState = 0;
        highestColor = 0;
        for (Rule rule : rules) {
            if (rule.getNewState() > highestState) highestState = rule.getNewState();
            if (rule.getNewColor() > highestColor) highestColor = rule.getNewColor();
        }
    }

    private void addRule(int currState, int currColor, char dirChar, int newColor, int newState) throws IllegalArgumentException {
        if (rules.size() == MAX_RULES) return;
        rules.add(validateRuleCells(currState, currColor, dirChar, newColor, newState));
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

    public int getNumOfRulesNeeded() {
        return (highestState + 1) * (highestColor + 1);
    }

    public List<Rule> getRules() {
        return rules;
    }

    public Rule getRule(int ruleNum) {
        return rules.get(ruleNum);
    }
}
