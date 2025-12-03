package turmite.simulator.ui;

import turmite.simulator.TurmiteJFrame;

import javax.swing.*;
import java.io.File;

/**
 * A JComboBox of type String that manages the names of loaded Rulesets.
 */
public class RuleSelectorComboBox extends JComboBox<String> {
    public static final String NEW_RULESET_STR = "NewRuleset";

    private transient Object prevItem = null;

    public RuleSelectorComboBox(String ext) {
        super();
        readFileNamesIntoDropdown(ext);
    }

    /**
     * Signal that the currently loaded Ruleset has been modified, and so it's not a saved Ruleset but a new one.
     */
    public void signalNewRuleset() {
        if (notContainsNewRulesetStr()) {
            addItem(NEW_RULESET_STR);
            setSelectedItem(RuleSelectorComboBox.NEW_RULESET_STR);
        }
    }

    /**
     * Signal that a Ruleset has been selected and read.
     */
    public void signalRuleRead(String ruleName) {
        setSelectedItem(ruleName);
        removeItem(NEW_RULESET_STR);
    }

    /**
     * @return Whether the dropdown does not contain NEW_RULESET_STR;
     */
    private boolean notContainsNewRulesetStr() {
        for (int i = 0; i < getItemCount(); i++) if (this.getItemAt(i).equals(NEW_RULESET_STR)) return false;
        return true;
    }

    /**
     * Reads the name of files which have an extension of {@code fileExt}.
     * The rule name cannot be NEW_RULESET_STR.
     *
     * @param fileExt The accepted extension of the files.
     */
    private void readFileNamesIntoDropdown(String fileExt) {
        File rulesetDir = new File(TurmiteJFrame.RULESET_DIR);
        File[] files = rulesetDir.listFiles((dir, name) -> name.endsWith(fileExt));

        if (files != null && files.length > 0) {
            for (File ruleFile : files) {
                String ruleName = ruleFile.getName().replace(fileExt, "");
                if (!ruleName.equals(NEW_RULESET_STR)) addItem(ruleName);
                else Dialogs.showInfoDialog(null, "Rule name cannot be NewRuleset.");
            }
        } else {
            addItem(NEW_RULESET_STR);
            setSelectedItem(NEW_RULESET_STR);
        }
    }

    public Object getPrevItem() {
        return prevItem;
    }

    @Override
    public void setSelectedItem(Object anItem) {
        prevItem = getSelectedItem();
        super.setSelectedItem(anItem);
    }
}
