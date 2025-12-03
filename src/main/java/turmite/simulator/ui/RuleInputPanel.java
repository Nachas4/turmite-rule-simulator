package turmite.simulator.ui;

import turmite.simulator.TurmiteJFrame;
import turmite.simulator.models.Direction;
import turmite.simulator.models.Rule;
import turmite.simulator.utils.Ruleset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * A class that manages Rule input, import and export.
 */
public class RuleInputPanel extends JPanel implements ActionListener {
    private final RuleSelectorComboBox ruleSelectorComboBox;
    private final transient Ruleset ruleset = new Ruleset();

    private boolean notSettingPanel = true;

    public RuleInputPanel(String rulesetExt) {
        super();
        ruleSelectorComboBox = new RuleSelectorComboBox(rulesetExt);
        setupGUI();
        setupEventListeners();
        loadSelectedRuleset();
    }

    private void setupGUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(3, 4, 3, 4);

        // Rule Input ComboBoxes
        int col = 0;
        for (int row = 1; row < Ruleset.MAX_RULES + 1; row++) {
            addInputComboBox(Integer.class, constraints, row, col, false);
            col += 2;

            addInputComboBox(Integer.class, constraints, row, col, false);
            col += 2;

            addInputComboBox(Character.class, constraints, row, col, true);
            col += 2;

            addInputComboBox(Integer.class, constraints, row, col, true);
            col += 2;

            addInputComboBox(Integer.class, constraints, row, col, true);
            col = 0;
        }

        // Separators
        col = 1;
        for (int row = 1; row < Ruleset.MAX_RULES + 1; row++) {
            for (; col < 8; col += 2) {
                constraints.gridx = col;
                constraints.gridy = row;
                add(new JLabel("-"), constraints);
            }
            col = 1;
        }

        // Rule Selector ComboBox
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = Rule.RuleCells.values().length * 2 - 1;
        constraints.insets.set(0, 50, 50, 50);
        add(ruleSelectorComboBox, constraints);
    }

    /**
     * Adds a new JComboBox of the desired type to the panel.
     *
     * @param type The type of the new JComboBox.
     * @param constraints The GridBagConstraints to use.
     * @param row The GridBagConstraints row to put the JComboBox in.
     * @param col The GridBagConstraints column to put the JComboBox in.
     * @param enabled Whether the new JComboBox is enabled.
     */
    private <T> void addInputComboBox(T type, GridBagConstraints constraints, int row, int col, boolean enabled) {
        if (type.equals(Integer.class)) {
            JComboBox<Integer> cbI = new JComboBox<>();
            cbI.setEnabled(enabled);
            for (int s = 0; s < Ruleset.MAX_STATES; s++) cbI.addItem(s);
            constraints.gridx = col;
            constraints.gridy = row;
            add(cbI, constraints);
        } else if (type.equals(Character.class)) {
            List<Character> turnDirs = Direction.getSquareGridTurnDirs();
            JComboBox<Character> cbC = new JComboBox<>();
            cbC.setEnabled(enabled);
            for (int d = 0; d < Ruleset.SQUARE_GRID_MAX_TURN_DIRS; d++) cbC.addItem(turnDirs.get(d));
            constraints.gridx = col;
            constraints.gridy = row;
            add(cbC, constraints);
        }
    }

    private void setupEventListeners() {
        ruleSelectorComboBox.addActionListener(e -> loadSelectedRuleset());

        String cmdStr = "%d-%s";
        for (int row = 0; row < Ruleset.MAX_RULES; row++) {
            JComboBox<Integer> cbI1 = getIntComboBox(row, Rule.RuleCells.CURR_STATE);
            cbI1.setActionCommand(String.format(cmdStr, row, Rule.RuleCells.CURR_STATE));
            cbI1.addActionListener(this);

            JComboBox<Integer> cbI2 = getIntComboBox(row, Rule.RuleCells.CURR_COLOR);
            cbI2.setActionCommand(String.format(cmdStr, row, Rule.RuleCells.CURR_COLOR));
            cbI2.addActionListener(this);

            JComboBox<Character> cbC = getCharComboBox(row, Rule.RuleCells.TURN_DIR);
            cbC.setActionCommand(String.format(cmdStr, row, Rule.RuleCells.TURN_DIR));
            cbC.addActionListener(this);

            JComboBox<Integer> cbI3 = getIntComboBox(row, Rule.RuleCells.NEW_COLOR);
            cbI3.setActionCommand(String.format(cmdStr, row, Rule.RuleCells.NEW_COLOR));
            cbI3.addActionListener(this);

            JComboBox<Integer> cbI4 = getIntComboBox(row, Rule.RuleCells.NEW_STATE);
            cbI4.setActionCommand(String.format(cmdStr, row, Rule.RuleCells.NEW_STATE));
            cbI4.addActionListener(this);
        }
    }

    /**
     * Loads the Ruleset file with the name selected in the RuleSelectorComboBox.
     * The Ruleset name cannot be {@code RuleSelectorComboBox.NEW_RULESET_STR}.
     */
    private void loadSelectedRuleset() {
        Object selected = ruleSelectorComboBox.getSelectedItem();
        if (selected != null && selected != RuleSelectorComboBox.NEW_RULESET_STR) readRulesetFromFile(selected + TurmiteJFrame.RULESET_EXT);
    }

    /**
     * Reads the Ruleset file with the specified name.
     *
     * @param fileName The name of the Ruleset file.
     * @return Whether the read was successful.
     */
    public boolean readRulesetFromFile(String fileName) {
        try {
            ruleset.readRulesetFromFile(fileName);
            loadRulesetIntoPanel();
            ruleSelectorComboBox.signalRuleRead(fileName.replace(TurmiteJFrame.RULESET_EXT, ""));
            return true;
        } catch (FileNotFoundException e) {
            Dialogs.showErrorDialog(this, String.format("File not found: %s", fileName));
        } catch (IllegalArgumentException | Ruleset.InvalidRulesetException e) {
            ruleSelectorComboBox.setSelectedItem(ruleSelectorComboBox.getPrevItem());
            Dialogs.showErrorDialog(this, e.getMessage());
        }

        return false;
    }

    /**
     * Loads the loaded Ruleset into the Input Panel Dropdowns.
     */
    private void loadRulesetIntoPanel() {
        notSettingPanel = false;

        int neededRules = ruleset.getNumOfRulesNeeded();
        for (int row = 0; row < neededRules; row++) {
            JComboBox<Integer> cbI1 = getIntComboBox(row, Rule.RuleCells.CURR_STATE);
            cbI1.setSelectedItem(ruleset.getRule(row).getCurrState());

            JComboBox<Integer> cbI2 = getIntComboBox(row, Rule.RuleCells.CURR_COLOR);
            cbI2.setSelectedItem(ruleset.getRule(row).getCurrColor());

            JComboBox<Character> cbIC = getCharComboBox(row, Rule.RuleCells.TURN_DIR);
            cbIC.setSelectedItem(Direction.getCharFromTurnDir(ruleset.getRule(row).getTurnDir()));
            cbIC.setEnabled(true);

            JComboBox<Integer> cbI3 = getIntComboBox(row, Rule.RuleCells.NEW_COLOR);
            cbI3.setSelectedItem(ruleset.getRule(row).getNewColor());
            cbI3.setEnabled(true);

            JComboBox<Integer> cbI4 = getIntComboBox(row, Rule.RuleCells.NEW_STATE);
            cbI4.setSelectedItem(ruleset.getRule(row).getNewState());
            cbI4.setEnabled(true);
        }

        int diff = Ruleset.MAX_RULES - neededRules;
        for (int row = diff - 1; row >= 0; row--) resetRow(Ruleset.MAX_RULES - row - 1);

        notSettingPanel = true;
    }

    /**
     * Change the cell of a Rule to a new value and load the changed Ruleset into the Input Panel.
     *
     * @param ruleRow The row of the Rule ranging from 0-{@code Ruleset.MAX_RULES}.
     * @param cell The cell of the Rule to change.
     * @param newValue The new value of the Rule cell.
     */
    private void handleRuleChange(int ruleRow, Rule.RuleCells cell, Object newValue) {
        try {
            ruleset.changeRuleCell(ruleRow, cell, newValue);
            loadRulesetIntoPanel();
        } catch (IllegalArgumentException e) {
            Dialogs.showErrorDialog(this, e.getMessage());
        }
    }

    /**
     * Get the JComboBox of type Integer which is for the cell of the given Rule.
     *
     * @param ruleNum The row of the Rule ranging from 0-{@code Ruleset.MAX_RULES}.
     * @param cell The cell of the Rule to get.
     * @return A JComboBox of type Integer which is for the cell of the given Rule.
     *
     * @throws IllegalArgumentException If the Rule cell is not of type integer, or the JComboBox returned
     * would not be of type Integer.
     */
    // Using Rule.RuleCells insures that the returned JComboBox is of the desired type.
    @SuppressWarnings("unchecked")
    private JComboBox<Integer> getIntComboBox(int ruleNum, Rule.RuleCells cell) {
        int idx = ruleNum * Rule.RuleCells.values().length + cell.ordinal();
        Component component = getComponent(idx);
        if (!(component instanceof JComboBox<?>) ||
                cell != Rule.RuleCells.CURR_STATE && cell != Rule.RuleCells.CURR_COLOR &&
                cell != Rule.RuleCells.NEW_COLOR && cell != Rule.RuleCells.NEW_STATE)
            throw new IllegalArgumentException(String.format("The Component with index %d was not of type JComboBox<Integer>.", idx));

        return (JComboBox<Integer>) component;
    }

    /**
     * Get the JComboBox of type Character which is for the cell of the given Rule.
     *
     * @param ruleNum The row of the Rule ranging from 0-{@code Ruleset.MAX_RULES}.
     * @param cell The cell of the Rule to get.
     * @return A JComboBox of type Character which is for the cell of the given Rule.
     *
     * @throws IllegalArgumentException If the Rule cell is not of type char, or the JComboBox returned
     * would not be of type Character.
     */
    // Using Rule.RuleCells insures that the returned JComboBox is of the desired type.
    @SuppressWarnings({"unchecked", "SameParameterValue"})
    private JComboBox<Character> getCharComboBox(int ruleNum, Rule.RuleCells cell) {
        int idx = ruleNum * Rule.RuleCells.values().length + cell.ordinal();
        Component component = getComponent(idx);
        if (!(component instanceof JComboBox<?>) || cell != Rule.RuleCells.TURN_DIR)
            throw new IllegalArgumentException(String.format("The Component with index %d was not of type JComboBox<Character>.", idx));

        return (JComboBox<Character>) component;
    }

    /**
     * Sets the selected item of the JComboBoxes in the row to the first one,
     * and disables them.
     *
     * @param ruleRow The row of the Rule ranging from 0-{@code Ruleset.MAX_RULES}.
     */
    private void resetRow(int ruleRow) {
        getIntComboBox(ruleRow, Rule.RuleCells.CURR_STATE).setSelectedItem(0);
        getIntComboBox(ruleRow, Rule.RuleCells.CURR_COLOR).setSelectedItem(0);
        getCharComboBox(ruleRow, Rule.RuleCells.TURN_DIR).setSelectedItem(0);
        getIntComboBox(ruleRow, Rule.RuleCells.NEW_COLOR).setSelectedItem(0);
        getIntComboBox(ruleRow, Rule.RuleCells.NEW_STATE).setSelectedItem(0);

        getCharComboBox(ruleRow, Rule.RuleCells.TURN_DIR).setEnabled(false);
        getIntComboBox(ruleRow, Rule.RuleCells.NEW_COLOR).setEnabled(false);
        getIntComboBox(ruleRow, Rule.RuleCells.NEW_STATE).setEnabled(false);
    }

    /**
     * @return The Ruleset.
     */
    public Ruleset getRuleset() {
        return ruleset;
    }

    /**
     * Processes when a Rule cell is modified with a Rule Input JComboBox.
     *
     * @param e The event to be processed.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (notSettingPanel) {
            int ruleRow = Integer.parseInt(e.getActionCommand().split("-")[0]);
            Rule.RuleCells ruleCell = Rule.RuleCells.valueOf(e.getActionCommand().split("-")[1]);
            handleRuleChange(ruleRow, ruleCell, ((JComboBox<?>) e.getSource()).getSelectedItem());
            ruleSelectorComboBox.signalNewRuleset();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        ruleSelectorComboBox.setEnabled(enabled);

        int numOfRulesToChange = enabled ? ruleset.getNumOfRulesNeeded() : Ruleset.MAX_RULES;
        for (int row = 0; row < numOfRulesToChange; row++) {
            getCharComboBox(row, Rule.RuleCells.TURN_DIR).setEnabled(enabled);
            getIntComboBox(row, Rule.RuleCells.NEW_COLOR).setEnabled(enabled);
            getIntComboBox(row, Rule.RuleCells.NEW_STATE).setEnabled(enabled);
        }
    }
}
