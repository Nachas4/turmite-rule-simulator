package turmite.simulator.ui;

import javax.swing.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class IntervalSlider extends JSlider {
    public IntervalSlider(int min, int max, int value) {
        super(min, max, value);
        setPaintTrack(true);
        setPaintTicks(true);
        setPaintLabels(true);
        setSnapToTicks(true);
        setMajorTickSpacing(100);
        Map<Integer, JComponent> customLabelTable = new HashMap<>();
        customLabelTable.put(3, new JLabel("3"));
        customLabelTable.put(100, new JLabel("100"));
        customLabelTable.put(200, new JLabel("200"));
        customLabelTable.put(300, new JLabel("300"));
        customLabelTable.put(400, new JLabel("400"));
        customLabelTable.put(500, new JLabel("500"));
        customLabelTable.put(600, new JLabel("600"));
        customLabelTable.put(700, new JLabel("700"));
        customLabelTable.put(800, new JLabel("800"));
        customLabelTable.put(900, new JLabel("900"));
        customLabelTable.put(1000, new JLabel("1000"));
        setLabelTable(new Hashtable<>(customLabelTable));
    }
}
