package turmite.simulator;

import turmite.simulator.models.SquareGridPanel;

import javax.swing.*;
import java.awt.*;

public class TurmiteJFrame extends JFrame {
    private static final SquareGridPanel gridPanel = new SquareGridPanel();
    private static final JPanel leftPanel = new JPanel(new GridBagLayout());
    private static final JPanel rightPanel = new JPanel();
    private static final JPanel mainPanel = new JPanel(new GridBagLayout());

    public TurmiteJFrame() {
        super("Turmite Simulator");
        setup();
    }

    private void setup() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        GridBagConstraints constraints = new GridBagConstraints();

        leftPanel.setBorder(BorderFactory.createLineBorder(Color.green));
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.7;
        constraints.weighty = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        mainPanel.add(leftPanel, constraints);

        rightPanel.setBorder(BorderFactory.createLineBorder(Color.blue));
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.3;
        constraints.weighty = 1;
        constraints.gridx = 1;
        constraints.gridy = 0;
        mainPanel.add(rightPanel, constraints);

        gridPanel.setBorder(BorderFactory.createLineBorder(Color.orange));
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(20, 20, 100, 20);
        leftPanel.add(gridPanel, constraints);

        constraints.insets = new Insets(0, 0, 0, 0);

        add(mainPanel);
    }

    public static void main(String[] args) {
        TurmiteJFrame frame = new TurmiteJFrame();
        frame.setVisible(true);
        gridPanel.centerMap();
    }
}
