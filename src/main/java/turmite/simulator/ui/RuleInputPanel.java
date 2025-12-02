package turmite.simulator.ui;

import turmite.simulator.models.Direction;
import turmite.simulator.models.Rule;
import turmite.simulator.utils.Ruleset;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RuleInputPanel extends JPanel {
    private boolean notSettingPanel = true;

    public RuleInputPanel() {
        super();
        setupGUI();
    }

    private void setupGUI() {
        JComboBox<Integer> cbI;
        JComboBox<Character> cbC;
        List<Character> turnDirs = Direction.getSquareGridTurnDirs();

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(3, 4, 3, 4);

        for (int row = 0; row < Ruleset.MAX_RULES; row++) {
            cbI = new JComboBox<>();
            cbI.setEnabled(false);
            for (int s = 0; s < Ruleset.MAX_STATES; s++) cbI.addItem(s);
            constraints.gridx = 0;
            constraints.gridy = row;
            add(cbI, constraints,-1);

            cbI = new JComboBox<>();
            cbI.setEnabled(false);
            for (int c = 0; c < Ruleset.MAX_COLORS; c++) cbI.addItem(c);
            constraints.gridx = 2;
            constraints.gridy = row;
            add(cbI, constraints);

            cbC = new JComboBox<>();
            for (int d = 0; d < Ruleset.SQUARE_GRID_MAX_TURN_DIRS; d++) cbC.addItem(turnDirs.get(d));
            constraints.gridx = 4;
            constraints.gridy = row;
            add(cbC, constraints);

            cbI = new JComboBox<>();
            for (int c = 0; c < Ruleset.MAX_COLORS; c++) cbI.addItem(c);
            constraints.gridx = 6;
            constraints.gridy = row;
            add(cbI, constraints);

            cbI = new JComboBox<>();
            for (int s = 0; s < Ruleset.MAX_STATES; s++) cbI.addItem(s);
            constraints.gridx = 8;
            constraints.gridy = row;
            add(cbI, constraints);
        }

        for (int row = 0; row < Ruleset.MAX_RULES; row++) {
            for (int col = 1; col < 8; col += 2) {
                constraints.gridx = col;
                constraints.gridy = row;
                add(new JLabel("-"), constraints);
            }
        }
    }

    // Using Rule.RuleCells insures that the returned JComboBox is of the desired type.
    // The ComboBox is not editable, but filled programmatically, so getSelectedItem() should not return null.
    @SuppressWarnings({"unchecked", "DataFlowIssue"})
    public Ruleset getRuleset() {
        Ruleset ruleset = new Ruleset();

        for (int row = 0; row < Ruleset.MAX_RULES; row++) {
            ruleset.addRule(
                    (int)((JComboBox<Integer>)getRuleCellFor(row, Rule.RuleCells.CURR_STATE)).getSelectedItem(),
                    (int)((JComboBox<Integer>)getRuleCellFor(row, Rule.RuleCells.CURR_COLOR)).getSelectedItem(),
                    (char)((JComboBox<Character>)getRuleCellFor(row, Rule.RuleCells.TURN_DIR)).getSelectedItem(),
                    (int)((JComboBox<Integer>) getRuleCellFor(row, Rule.RuleCells.NEW_COLOR)).getSelectedItem(),
                    (int)((JComboBox<Integer>) getRuleCellFor(row, Rule.RuleCells.NEW_STATE)).getSelectedItem()
            );
        }

        return ruleset;
    }

    // Using Rule.RuleCells insures that the returned JComboBox is of the desired type.
    @SuppressWarnings("unchecked")
    public void setPanelRuleset(Ruleset ruleset) {
        notSettingPanel = false;
        resetPanel();

        JComboBox<Integer> cbI;
        JComboBox<Character> cbC;
        int row = 0;
        for (Rule rule : ruleset.getRules()) {
            cbI = ((JComboBox<Integer>)getRuleCellFor(row, Rule.RuleCells.CURR_STATE));
            cbI.setSelectedIndex(indexOfItem(cbI, rule.getCurrState()));

            cbI = ((JComboBox<Integer>)getRuleCellFor(row, Rule.RuleCells.CURR_COLOR));
            cbI.setSelectedIndex(indexOfItem(cbI, rule.getCurrColor()));

            cbC = ((JComboBox<Character>)getRuleCellFor(row, Rule.RuleCells.TURN_DIR));
            cbC.setSelectedIndex(indexOfItem(cbC, rule.getTurnDir()));

            cbI = ((JComboBox<Integer>)getRuleCellFor(row, Rule.RuleCells.NEW_COLOR));
            cbI.setSelectedIndex(indexOfItem(cbI, rule.getNewColor()));

            cbI = ((JComboBox<Integer>)getRuleCellFor(row, Rule.RuleCells.NEW_STATE));
            cbI.setSelectedIndex(indexOfItem(cbI, rule.getNewState()));

            row++;
        }

        onlyEnableNeededRuleRows(ruleset.getNumOfRulesNeeded());
        notSettingPanel = true;
    }

    private void onlyEnableNeededRuleRows(int rulesNeeded) {
        for (int row = 0; row < rulesNeeded; row++) {
            getRuleCellFor(row, Rule.RuleCells.TURN_DIR).setEnabled(true);
            getRuleCellFor(row, Rule.RuleCells.NEW_COLOR).setEnabled(true);
            getRuleCellFor(row, Rule.RuleCells.NEW_STATE).setEnabled(true);
        }

        for (int row = Ruleset.MAX_RULES - 1; row >= rulesNeeded; row--) {
            getRuleCellFor(row, Rule.RuleCells.TURN_DIR).setEnabled(false);
            getRuleCellFor(row, Rule.RuleCells.NEW_COLOR).setEnabled(false);
            getRuleCellFor(row, Rule.RuleCells.NEW_STATE).setEnabled(false);

            resetRow(row);
        }
    }

    public Component getRuleCellFor(int ruleNum, Rule.RuleCells cell) {
        return getComponent(ruleNum * Rule.RuleCells.values().length + cell.ordinal());
    }

    private int indexOfItem(JComboBox<Character> cb, Direction dir) {
        for (int i = 0; i < cb.getModel().getSize(); i++)
            if (Direction.getTurnDirFromChar(cb.getItemAt(i)).equals(dir))
                return i;

        return -1;
    }

    private int indexOfItem(JComboBox<Integer> cb, int item) {
        for (int i = 0; i < cb.getModel().getSize(); i++)
            if (cb.getItemAt(i).equals(item))
                return i;

        return -1;
    }

    private void resetPanel() {
        for (int row = 0; row < Ruleset.MAX_RULES; row++) resetRow(row);
    }

    // Using Rule.RuleCells insures that the returned JComboBox is of the desired type.
    @SuppressWarnings("unchecked")
    private void resetRow(int row) {
        ((JComboBox<Integer>)getRuleCellFor(row, Rule.RuleCells.CURR_STATE)).setSelectedIndex(0);
        ((JComboBox<Integer>)getRuleCellFor(row, Rule.RuleCells.CURR_COLOR)).setSelectedIndex(0);
        ((JComboBox<Character>)getRuleCellFor(row, Rule.RuleCells.TURN_DIR)).setSelectedIndex(0);
        ((JComboBox<Integer>)getRuleCellFor(row, Rule.RuleCells.NEW_COLOR)).setSelectedIndex(0);
        ((JComboBox<Integer>)getRuleCellFor(row, Rule.RuleCells.NEW_STATE)).setSelectedIndex(0);
    }

    public boolean isNotSettingPanel() {
        return notSettingPanel;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (int row = 0; row < Ruleset.MAX_RULES; row++) {
            getRuleCellFor(row, Rule.RuleCells.TURN_DIR).setEnabled(enabled);
            getRuleCellFor(row, Rule.RuleCells.NEW_COLOR).setEnabled(enabled);
            getRuleCellFor(row, Rule.RuleCells.NEW_STATE).setEnabled(enabled);
        }

        notSettingPanel = false;
        if (enabled) onlyEnableNeededRuleRows(getRuleset().getNumOfRulesNeeded());
        notSettingPanel = true;
    }
}
