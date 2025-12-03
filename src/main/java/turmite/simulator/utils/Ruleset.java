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

/**
 * A class that manages a Ruleset. This includes model and validation.
 */
public class Ruleset {
    public static class InvalidRulesetException extends Exception {
        public InvalidRulesetException(String message) {
            super(message);
        }
    }

    public static final int MAX_STATES = 3;
    public static final int MAX_COLORS = 3;
    public static final int MAX_RULES = MAX_STATES * MAX_COLORS;

    public static final int SQUARE_GRID_MAX_TURN_DIRS = 4;

    private int highestState;
    private int highestColor;

    private final List<Rule> rules = new ArrayList<>(MAX_RULES);

    public Ruleset() {}

    public Ruleset(List<Rule> rules) {
        this.rules.addAll(rules);
    }

    /**
     * Read a Ruleset from the given file. If a Ruleset is invalid for any reason,
     * no modification is done to one already loaded.
     *
     * @param fileName The name of the Ruleset file.
     * @throws FileNotFoundException If the file does not exist, is a directory rather than a regular file,
     * or for some other reason cannot be opened for reading.
     * @throws IllegalArgumentException If a Rule from the file is not valid.
     *
     * @see #validateRuleCells(int, int, char, int, int)
     */
    public void readRulesetFromFile(String fileName) throws FileNotFoundException, IllegalArgumentException, InvalidRulesetException {
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
                resetToSnapshot(rulesSnapshot);
                throw e;
            }
        }

        reader.close();

        if (!validateRuleset(rulesSnapshot))
            throw new InvalidRulesetException("The Ruleset does not cover every {currState-currColor} combination and/or does not have the number of Rules needed (highestState * highestColor).");
    }

    /**
     * Change the cell of a Rule to a new value.
     * <p>
     * Changing a Rule cell can affect the number of rules needed to cover all {@code currState-currColor} combinations.
     *
     * @param ruleRow The row of the Rule ranging from 0-{@code MAX_RULES}.
     * @param cell The cell of the Rule to change.
     * @param newValue The new value of the Rule cell.
     * @throws IllegalArgumentException If the Rule would not be valid with the given {@code newValue}.
     *
     * @see #validateRuleCells(int, int, char, int, int)
     * @see #recalculateRulesetTable()
     */
    public void changeRuleCell(int ruleRow, Rule.RuleCells cell, Object newValue) throws IllegalArgumentException {
        Rule rule = rules.get(ruleRow);
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

    /**
     * This method makes sure that enough Rules are available in the Ruleset to cover all {@code currState-currColor}
     * combinations.
     * <p>
     * Unneeded Rule rows are removed, and new ones are added if necessary.
     */
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

    /**
     * Recalculate how many states and colors this Ruleset has.
     */
    private void recalculateNumOfStatesAndColors() {
        highestState = 0;
        highestColor = 0;
        for (Rule rule : rules) {
            if (rule.getNewState() > highestState) highestState = rule.getNewState();
            if (rule.getNewColor() > highestColor) highestColor = rule.getNewColor();
        }
    }

    /**
     * Adds a Rule to the Ruleset, if it's valid, and the Ruleset is not full.
     *
     * @param currState The currState value.
     * @param currColor The currColor value.
     * @param dirChar The dirChar value.
     * @param newColor The newColor value.
     * @param newState The newState value.
     * @throws IllegalArgumentException If the Rule is invalid.
     */
    private void addRule(int currState, int currColor, char dirChar, int newColor, int newState) throws IllegalArgumentException {
        if (rules.size() == MAX_RULES) return;
        rules.add(validateRuleCells(currState, currColor, dirChar, newColor, newState));
    }

    /**
     * Validates a Rule so it doesn't contain invalid values.
     * <ul>
     *     <li>{@code currState} and {@code newState} must be in the range {@code 0-MAX_STATES}.</li>
     *     <li>{@code currColor} and {@code newColor} must be in the range {@code 0-MAX_COLORS}.</li>
     *     <li>{@link Direction#getSquareGridTurnDirs()} must contain {@code turnDir}.</li>
     * </ul>
     *
     * @param currState The currState value.
     * @param currColor The currColor value.
     * @param dirChar The dirChar value.
     * @param newColor The newColor value.
     * @param newState The newState value.
     * @return The validated Rule object.
     * @throws IllegalArgumentException If the new Rule would be invalid.
     */
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

    /**
     * Validates the loaded Ruleset. If it is, then no action is needed.
     * <p>
     * But if the Ruleset is invalid, it is reset to a snapshot.
     *
     * @param rulesSnapshot The snapshot to reset to if the Ruleset is invalid.
     * @return Whether the Ruleset is valid.
     */
    private boolean validateRuleset(List<Rule> rulesSnapshot) {
        recalculateNumOfStatesAndColors();
        int needed = getNumOfRulesNeeded();
        if (needed != rules.size()) {
            resetToSnapshot(rulesSnapshot);
            return false;
        }

        Ruleset rulesetSnapshot = new Ruleset(rules);
        recalculateRulesetTable();

        for (int i = 0; i < rules.size(); i++)
            if (!rules.get(i).equals(rulesetSnapshot.getRule(i))) {
                resetToSnapshot(rulesSnapshot);
                return false;
            }

        return true;
    }

    /**
     * Reverts the Ruleset to a snapshot state.
     *
     * @param rulesSnapshot The snapshot.
     */
    private void resetToSnapshot(List<Rule> rulesSnapshot) {
        rules.clear();
        rules.addAll(rulesSnapshot);
        recalculateNumOfStatesAndColors();
        recalculateRulesetTable();
    }

    /**
     * @return The number of rules needed to cover all {@code currState-currColor} combinations.
     */
    public int getNumOfRulesNeeded() {
        return (highestState + 1) * (highestColor + 1);
    }

    /**
     * @return The List of Rules in the Ruleset.
     */
    public List<Rule> getRules() {
        return rules;
    }

    /**
     * @param ruleRow The row of the Rule ranging from 0-{@code MAX_RULES}.
     * @return The Rule at the given position.
     *
     * @throws IndexOutOfBoundsException If no Rule is found at the given index.
     */
    public Rule getRule(int ruleRow) {
        return rules.get(ruleRow);
    }
}
