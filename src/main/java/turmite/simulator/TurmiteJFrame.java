package turmite.simulator;

import turmite.simulator.ui.*;
import turmite.simulator.utils.FileHandler;
import turmite.simulator.utils.Simulator;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TurmiteJFrame extends JFrame {
    private static final String START_STR = "Start";
    private static final String PAUSE_STR = "Pause";
    private static final String STEP_STR = "Step Once";
    private static final String RESET_STR = "Reset Grid";
    private static final String IMPORT_STR = "Import";
    private static final String EXPORT_STR = "Export";

    public static final String RULESET_DIR = "rulesets";
    public static final String RULESET_EXT = ".json";

    private static final JPanel leftPanel = new JPanel(new GridBagLayout());
    private static final JPanel rightPanel = new JPanel(new GridBagLayout());
    private static final JPanel mainPanel = new JPanel(new GridBagLayout());

    private static final RuleInputPanel ruleInputPanel = new RuleInputPanel(RULESET_DIR, RULESET_EXT);
    private static final SquareGridPanel gridPanel = new SquareGridPanel(ruleInputPanel);
    private static final JButton importButton = new JButton(IMPORT_STR);
    private static final JButton exportButton = new JButton(EXPORT_STR);

    private static final JButton toggleSimButton = new JButton(START_STR);
    private static final JButton stepSimButton = new JButton(STEP_STR);
    private static final JButton resetSimButton = new JButton(RESET_STR);
    private static final IntervalSlider intervalSlider = new IntervalSlider(3, 1000, 100);

    private static final Simulator simulator = new Simulator(gridPanel, intervalSlider.getValue());

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
        constraints.insets = insets;

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
        insets.set(20, 20, 20, 20);
        leftPanel.add(gridPanel, constraints);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 1;
        insets.set(0, 10, 50, 10);
        leftPanel.add(buttonPanel, constraints);

        insets.set(0, 20, 0, 20);

        toggleSimButton.setFocusPainted(false);
        constraints.weightx = 0.4;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 0;
        buttonPanel.add(toggleSimButton, constraints);

        stepSimButton.setFocusPainted(false);
        constraints.weightx = 0.4;
        constraints.weighty = 0;
        constraints.gridx = 1;
        constraints.gridy = 0;
        buttonPanel.add(stepSimButton, constraints);

        resetSimButton.setFocusPainted(false);
        constraints.weightx = 0.2;
        constraints.weighty = 0;
        constraints.gridx = 2;
        constraints.gridy = 0;
        buttonPanel.add(resetSimButton, constraints);

        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 3;
        insets.set(20, 20, 0, 20);
        buttonPanel.add(intervalSlider, constraints);

        constraints.gridwidth = 1;
        insets.set(0, 0, 0, 0);

        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 0;
        rightPanel.add(ruleInputPanel, constraints);

        insets.set(0, 50, 0, 50);

        JPanel buttonHolderPanel = new JPanel(new GridBagLayout());

        importButton.setFocusPainted(false);
        constraints.weightx = 0.5;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 0;
        buttonHolderPanel.add(importButton, constraints);

        exportButton.setFocusPainted(false);
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
        importButton.addActionListener(e -> importRulesetFromFileDialog());
        exportButton.addActionListener(e -> exportRulesetWithFileDialog());
        toggleSimButton.addActionListener(e -> toggleSimulation());
        stepSimButton.addActionListener(e -> stepSimulation());
        resetSimButton.addActionListener(e -> resetSimulation());
        intervalSlider.addChangeListener(e -> changeSimulationSpeed());
    }

    private void importRulesetFromFileDialog() {
        String fileName = null;
        try {
            fileName = FileHandler.importRuleset(this, RULESET_EXT, RULESET_DIR);
        } catch (IOException ex) {
            Dialogs.showErrorDialog(this, "An error happened while copying the selected file: " + ex.getMessage());
        }

        if (fileName == null) return;

        if (ruleInputPanel.readRulesetFromFile(fileName.replace(RULESET_DIR + "\\", ""))) Dialogs.showInfoDialog(this, "Ruleset imported successfully!");
    }

    private void exportRulesetWithFileDialog() {
        try {
            FileHandler.exportRuleset(this, ruleInputPanel.getRuleset(), RULESET_DIR, RULESET_EXT);
            Dialogs.showInfoDialog(this, "Ruleset exported successfully!");
        } catch (FileNotFoundException e) {
            Dialogs.showErrorDialog(this, String.format("Error while writing file: %s", e.getMessage()));
        } catch (IllegalArgumentException e) {
            Dialogs.showErrorDialog(this, e.getMessage());
        }
    }

    private void toggleSimulation() {
        if (simulator.toggleSimulation()) {
            toggleSimButton.setText(START_STR);
            importButton.setEnabled(true);
            exportButton.setEnabled(true);
            stepSimButton.setEnabled(true);
            resetSimButton.setEnabled(true);
        }
        else {
            toggleSimButton.setText(PAUSE_STR);
            importButton.setEnabled(false);
            exportButton.setEnabled(false);
            stepSimButton.setEnabled(false);
            resetSimButton.setEnabled(false);
        }

        ruleInputPanel.setEnabled(false);
    }

    private void stepSimulation() {
        simulator.stepSimulation();
        ruleInputPanel.setEnabled(false);
    }

    private void resetSimulation() {
        simulator.resetSimulation();
        ruleInputPanel.setEnabled(true);
    }

    private void changeSimulationSpeed() {
        simulator.setInterval(intervalSlider.getValue());
    }

    public static void main(String[] args) {
        TurmiteJFrame frame = new TurmiteJFrame();
        frame.setVisible(true);
        gridPanel.centerMap();
        simulator.start();
    }
}
