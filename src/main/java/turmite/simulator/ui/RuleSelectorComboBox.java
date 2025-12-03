package turmite.simulator.ui;

import javax.swing.*;
import java.io.File;

public class RuleSelectorComboBox extends JComboBox<String> {
    public static final String NEW_RULESET_STR = "NewRuleset";

    public RuleSelectorComboBox(String dir, String ext) {
        super();
        readFileNamesIntoDropdown(dir, ext);
    }

    public void signalNewRuleset() {
        if (notContainsNewRulesetStr()) {
            addItem(NEW_RULESET_STR);
            setSelectedItem(RuleSelectorComboBox.NEW_RULESET_STR);
        }
    }

    public void signalRuleRead(String ruleName) {
        setSelectedItem(ruleName);
        removeItem(NEW_RULESET_STR);
    }

    private boolean notContainsNewRulesetStr() {
        for (int i = 0; i < getItemCount(); i++) if (this.getItemAt(i).equals(NEW_RULESET_STR)) return false;
        return true;
    }

    private void readFileNamesIntoDropdown(String fileDirPath, String fileExt) {
        File rulesetDir = new File(fileDirPath);
        File[] files = rulesetDir.listFiles((dir, name) -> name.endsWith(fileExt));

        if (files != null) {
            for (File ruleFile : files) {
                String ruleName = ruleFile.getName().replace(fileExt, "");
                if (!ruleName.equals(NEW_RULESET_STR)) addItem(ruleName);
                else Dialogs.showInfoDialog(null, "Rule name cannot be NewRuleset.");
            }
        }
    }
}
