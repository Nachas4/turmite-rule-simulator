package turmite.simulator.ui;

import turmite.simulator.models.Rule;
import turmite.simulator.utils.Ruleset;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class RuleSelectorDropDown extends JComboBox<String> {
    public static final String NEW_RULESET_STR = "NewRuleset";
    private boolean notContainsNewRulesetStr = true;

    public RuleSelectorDropDown(String dir, String ext) {
        super();
        readFileNamesIntoDropdown(dir, ext);
    }

    // Using Rule.RuleCells insures that the returned JComboBox is of the desired type.
    @SuppressWarnings("unchecked")
    public void attachRuleInputPanel(RuleInputPanel rp) {
        for (int row = 0; row < Ruleset.MAX_RULES; row++) {
            ((JComboBox<Integer>)rp.getRuleCellFor(row, Rule.RuleCells.CURR_STATE)).addActionListener(e -> handleRuleChange(rp));
            ((JComboBox<Integer>)rp.getRuleCellFor(row, Rule.RuleCells.CURR_COLOR)).addActionListener(e -> handleRuleChange(rp));
            ((JComboBox<Character>)rp.getRuleCellFor(row, Rule.RuleCells.TURN_DIR)).addActionListener(e -> handleRuleChange(rp));
            ((JComboBox<Integer>)rp.getRuleCellFor(row, Rule.RuleCells.NEW_COLOR)).addActionListener(e -> handleRuleChange(rp));
            ((JComboBox<Integer>)rp.getRuleCellFor(row, Rule.RuleCells.NEW_STATE)).addActionListener(e -> handleRuleChange(rp));
        }
    }

    private void handleRuleChange(RuleInputPanel rp) {
        if (rp.isNotSettingPanel() && notContainsNewRulesetStr) {
            addItem(NEW_RULESET_STR);
            setSelectedItem(NEW_RULESET_STR);
            notContainsNewRulesetStr = false;
        }
    }

    public void signalFileLoaded() {
        removeItem(NEW_RULESET_STR);
        notContainsNewRulesetStr = true;
    }

    private void readFileNamesIntoDropdown(String fileDirPath, String fileExt) {
        File rulesetDir = new File(fileDirPath);
        File[] files = rulesetDir.listFiles((dir, name) -> name.endsWith(fileExt));

        if (files != null)
            for (File ruleFile : files) {
                String ruleName = ruleFile.getName().replace(fileExt, "");
                if (!ruleName.equals(NEW_RULESET_STR)) addItem(ruleName);
                else Dialogs.showInfoDialog(null, "Rule name cannot be NewRuleset.");
            }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        if (!NEW_RULESET_STR.equals(getSelectedItem())) {
            removeItem(NEW_RULESET_STR);
            notContainsNewRulesetStr = true;
        }
    }
}
