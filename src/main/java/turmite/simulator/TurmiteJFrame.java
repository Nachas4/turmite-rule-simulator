package turmite.simulator;

import turmite.simulator.ui.Dialogs;
import turmite.simulator.ui.RuleInputPanel;
import turmite.simulator.ui.SquareGridPanel;
import turmite.simulator.utils.FileHandler;
import turmite.simulator.utils.Simulator;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TurmiteJFrame extends JFrame {
    private static final SquareGridPanel gridPanel = new SquareGridPanel();
    private static final JPanel leftPanel = new JPanel(new GridBagLayout());
    private static final JPanel rightPanel = new JPanel(new GridBagLayout());
    private static final JPanel mainPanel = new JPanel(new GridBagLayout());

    private static final JComboBox<String> ruleSelector = new JComboBox<>();
    private static final RuleInputPanel ruleInputPanel = new RuleInputPanel();
    private static final Button importButton = new Button("Import");
    private static final Button exportButton = new Button("Export");

    private static final String RULESET_DIR = "rulesets";
    private static final String RULESET_EXT = ".json";

    public TurmiteJFrame() {
        super("Turmite Simulator");
        setupGUI();
        setupEventListeners();
    }

    private void setupGUI() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1024, 720);
        setLocationRelativeTo(null);
        setResizable(false);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;

        Insets insets = new Insets(0, 0, 0, 0);

        leftPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        constraints.weightx = 0.8;
        constraints.weighty = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        mainPanel.add(leftPanel, constraints);

        rightPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        constraints.weightx = 0.2;
        constraints.weighty = 1;
        constraints.gridx = 1;
        constraints.gridy = 0;
        mainPanel.add(rightPanel, constraints);

        gridPanel.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        insets.set(20, 20, 100, 20);
        constraints.insets = insets;
        leftPanel.add(gridPanel, constraints);

        fillRuleSelectorDropdown();
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 0;
        insets.set(0, 50, 50, 50);
        rightPanel.add(ruleSelector, constraints);

        insets.set(0, 0, 0, 0);

        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 1;
        rightPanel.add(ruleInputPanel, constraints);

        insets.set(0, 50, 0, 50);

        JPanel buttonHolderPanel = new JPanel(new GridBagLayout());

        constraints.weightx = 0.5;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 0;
        buttonHolderPanel.add(importButton, constraints);

        constraints.weightx = 0.5;
        constraints.weighty = 0;
        constraints.gridx = 1;
        constraints.gridy = 0;
        buttonHolderPanel.add(exportButton, constraints);

        insets.set(50, 0, 0, 0);

        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 2;
        rightPanel.add(buttonHolderPanel, constraints);

        add(mainPanel);
    }

    private void setupEventListeners() {
        ruleSelector.addActionListener(e -> loadSelectedRulesetFromRuleSelector());
        importButton.addActionListener(e -> importRulesetFromFileDialog());
        exportButton.addActionListener(e -> exportRulesetWithFileDialog());
    }

    private void loadSelectedRulesetFromRuleSelector() {
        try {
            gridPanel.loadSelectedRuleset(String.format("%s\\%s%s", RULESET_DIR, ruleSelector.getSelectedItem(), RULESET_EXT));
            ruleInputPanel.setPanelRuleset(gridPanel.getLoadedRuleset());
        } catch (FileNotFoundException e) {
            Dialogs.showErrorDialog(this, String.format("File not found: %s%s", ruleSelector.getSelectedItem(), RULESET_EXT));
        }
    }

    private void importRulesetFromFileDialog() {
        String fileName = null;
        try {
            fileName = FileHandler.importRuleset(this, RULESET_EXT, RULESET_DIR);
        } catch (IOException ex) {
            Dialogs.showErrorDialog(this, "An error happened while copying the selected file: " + ex.getMessage());
        }

        if (fileName == null) return;

        try {
            String ruleName = fileName.replace(RULESET_DIR, "").replace("\\", "").replace(RULESET_EXT, "");
            ruleSelector.removeItem(ruleName);
            ruleSelector.addItem(ruleName);
            ruleSelector.setSelectedIndex(ruleSelector.getItemCount() - 1);

            gridPanel.loadSelectedRuleset(fileName);
            ruleInputPanel.setPanelRuleset(gridPanel.getLoadedRuleset());

            Dialogs.showInfoDialog(this, "Ruleset imported successfully!");
        } catch (FileNotFoundException ex) {
            Dialogs.showErrorDialog(this, String.format("File not found: %s.json", fileName));
        }
    }

    private void exportRulesetWithFileDialog() {
        try {
            FileHandler.exportRuleset(this, ruleInputPanel.getRuleset(), RULESET_DIR, RULESET_EXT);
            Dialogs.showInfoDialog(this, "Ruleset exported successfully!");
        } catch (FileNotFoundException e) {
            Dialogs.showErrorDialog(this, String.format("Error while writing file: %s", e.getMessage()));
        }
    }

    private static void fillRuleSelectorDropdown() {
        FileHandler.readFileNamesIntoDropdown(ruleSelector, RULESET_DIR, RULESET_EXT);
    }

    public static void main(String[] args) {
        TurmiteJFrame frame = new TurmiteJFrame();
        frame.setVisible(true);
        gridPanel.centerMap();

        try {
            gridPanel.loadSelectedRuleset(RULESET_DIR + "\\" + ruleSelector.getItemAt(0) + RULESET_EXT);
            // TODO: Selecting a new Ruleset from ruleSelector breaks the Simulator right now
            //Simulator simulator = new Simulator(gridPanel);
            //simulator.start();
        } catch (FileNotFoundException e) {
            Dialogs.showErrorDialog(frame, String.format("File not found: %s.json", ruleSelector.getItemAt(0)));
        }
    }
}
