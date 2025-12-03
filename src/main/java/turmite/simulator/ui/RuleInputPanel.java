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

public class RuleInputPanel extends JPanel implements ActionListener {
    private final RuleSelectorComboBox ruleSelectorComboBox;
    private final transient Ruleset ruleset = new Ruleset();

    private boolean notSettingPanel = true;

    public RuleInputPanel(String rulesetDir, String rulesetExt) {
        super();
        ruleSelectorComboBox = new RuleSelectorComboBox(rulesetDir, rulesetExt);
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

    private  <T> void addInputComboBox(T type, GridBagConstraints constraints, int row, int col, boolean enabled) {
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

    private void loadSelectedRuleset() {
        Object selected = ruleSelectorComboBox.getSelectedItem();
        if (selected != null && selected != RuleSelectorComboBox.NEW_RULESET_STR) readRulesetFromFile(selected + TurmiteJFrame.RULESET_EXT);
    }

    public boolean readRulesetFromFile(String fileName) {
        try {
            ruleset.readRulesetFromFile(fileName);
            loadRulesetIntoPanel();
            ruleSelectorComboBox.signalRuleRead(fileName.replace(TurmiteJFrame.RULESET_EXT, ""));
            return true;
        } catch (FileNotFoundException e) {
            Dialogs.showErrorDialog(this, String.format("File not found: %s", fileName));
        } catch (IllegalArgumentException e) {
            Dialogs.showErrorDialog(this, e.getMessage());
        }

        return false;
    }

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

    private void handleRuleChange(int ruleRow, Rule.RuleCells cell, Object newValue) {
        try {
            ruleset.changeRuleCell(ruleRow, cell, newValue);
            loadRulesetIntoPanel();
        } catch (IllegalArgumentException e) {
            Dialogs.showErrorDialog(this, e.getMessage());
        }
    }

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

    // Using Rule.RuleCells insures that the returned JComboBox is of the desired type.
    @SuppressWarnings({"unchecked", "SameParameterValue"})
    private JComboBox<Character> getCharComboBox(int ruleNum, Rule.RuleCells cell) {
        int idx = ruleNum * Rule.RuleCells.values().length + cell.ordinal();
        Component component = getComponent(idx);
        if (!(component instanceof JComboBox<?>) || cell != Rule.RuleCells.TURN_DIR)
            throw new IllegalArgumentException(String.format("The Component with index %d was not of type JComboBox<Character>.", idx));

        return (JComboBox<Character>) component;
    }

    private void resetRow(int row) {
        getIntComboBox(row, Rule.RuleCells.CURR_STATE).setSelectedItem(0);
        getIntComboBox(row, Rule.RuleCells.CURR_COLOR).setSelectedItem(0);
        getCharComboBox(row, Rule.RuleCells.TURN_DIR).setSelectedItem(0);
        getIntComboBox(row, Rule.RuleCells.NEW_COLOR).setSelectedItem(0);
        getIntComboBox(row, Rule.RuleCells.NEW_STATE).setSelectedItem(0);

        getCharComboBox(row, Rule.RuleCells.TURN_DIR).setEnabled(false);
        getIntComboBox(row, Rule.RuleCells.NEW_COLOR).setEnabled(false);
        getIntComboBox(row, Rule.RuleCells.NEW_STATE).setEnabled(false);
    }

    public Ruleset getRuleset() {
        return ruleset;
    }

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
