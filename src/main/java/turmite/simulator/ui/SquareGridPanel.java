package turmite.simulator.ui;

import turmite.simulator.models.Grid;
import turmite.simulator.models.Rule;
import turmite.simulator.models.Turmite;
import turmite.simulator.utils.Ruleset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

public class SquareGridPanel extends JPanel {
    private static final double DEFAULT_ZOOM = 2;

    private final Map<Grid, Integer> grids = new HashMap<>();
    private final transient Turmite turmite;
    private RuleInputPanel attachedRuleInputPanel = null;

    private double zoom = DEFAULT_ZOOM;
    private double lastMouseX = 0;
    private double lastMouseY = 0;
    private double offsetX = 0;
    private double offsetY = 0;

    private final int gridSize = (int)(zoom * 4);

    private final int turmitePosModifier = (int)zoom;
    private final int turmiteSize = (int)(zoom * 2);

    public SquareGridPanel() {
        super();
        turmite = new Turmite(new Grid(0, 0, gridSize));
        setupEventListeners();
    }

    private void setupEventListeners() {
        addMouseWheelListener(this::calculateZoom);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMouseX = e.getX();
                lastMouseY = e.getY();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                calculatePan(e);
            }
        });
    }

    private void calculatePan(MouseEvent e) {
        int gridPanelMouseX = e.getX();
        int gridPanelMouseY = e.getY();

        offsetX += gridPanelMouseX - lastMouseX;
        offsetY += gridPanelMouseY - lastMouseY;

        lastMouseX = gridPanelMouseX;
        lastMouseY = gridPanelMouseY;

        repaint();
    }

    private void calculateZoom(MouseWheelEvent e) {
        // Calculate zoom
        double prevZoom = zoom;
        double direction = (e.getWheelRotation() < 0) ? 1.1 : 0.9;
        zoom *= direction;

        // Calculate new offsets, so the grid under the cursor before zooming stays under it after zooming.
        int gridPanelMouseX = e.getX();
        int gridPanelMouseY = e.getY();

        double gridWorldX = (gridPanelMouseX - offsetX) / prevZoom;
        double gridWorldY = (gridPanelMouseY - offsetY) / prevZoom;

        double newGridPanelMouseX = gridWorldX * zoom + offsetX;
        double newGridMousePanelY = gridWorldY * zoom + offsetY;

        offsetX -= newGridPanelMouseX - gridPanelMouseX;
        offsetY -= newGridMousePanelY - gridPanelMouseY;

        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        // Pan + Zoom
        g2.translate(offsetX, offsetY);
        g2.scale(zoom, zoom);

        try {
            for (Map.Entry<Grid, Integer> entry : grids.entrySet()) {
                g2.setColor(Ruleset.numToColor(entry.getValue()));
                Grid grid = entry.getKey();
                g2.fillRect(grid.getX(), grid.getY(), gridSize, gridSize);
            }
        } catch (ConcurrentModificationException ignored) {
            // Painting and modifying the map at the same time can throw this,
            // but no actual error happens in either methods.
        }

        g2.setColor(Color.red);
        g2.fillOval(turmite.getX() + turmitePosModifier, turmite.getY() + turmitePosModifier, turmiteSize, turmiteSize);

        g2.dispose();
    }

    /*
     * Main Methods
     */

    public void stepSimulation() {
        Grid currTurmiteGrid = turmite.getGrid();
        int gridColor = grids.getOrDefault(currTurmiteGrid, 0);

        setColorAt(new Grid(currTurmiteGrid), turmite.calculateNextColor(gridColor));
        turmite.move(gridColor);
    }

    private void setColorAt(Grid grid, int color) {
        grids.put(grid, color);
    }

    public void centerMap() {
        offsetX = ((double) getWidth() / 2) - (gridSize * 1.5);
        offsetY = ((double) getHeight() / 2) - (gridSize * 1.5);
    }

    public void loadSelectedRuleset(String src) throws FileNotFoundException {
        turmite.getRuleset().readRulesetFromFile(src);
        if (attachedRuleInputPanel != null) attachedRuleInputPanel.setPanelRuleset(turmite.getRuleset());
    }

    // Using Rule.RuleCells insures that the returned JComboBox is of the desired type.
    @SuppressWarnings("unchecked")
    public void attachRuleInputPanel(RuleInputPanel rp) {
        attachedRuleInputPanel = rp;
        for (int row = 0; row < Ruleset.MAX_RULES; row++) {
            int intRow = row;

            JComboBox<Integer> cbI1 = ((JComboBox<Integer>)attachedRuleInputPanel.getRuleCellFor(row, Rule.RuleCells.CURR_STATE));
            cbI1.addItemListener(e -> updateRuleset(intRow, Rule.RuleCells.CURR_STATE, cbI1));

            JComboBox<Integer> cbI2 = ((JComboBox<Integer>)attachedRuleInputPanel.getRuleCellFor(row, Rule.RuleCells.CURR_COLOR));
            cbI2.addItemListener(e -> updateRuleset(intRow, Rule.RuleCells.CURR_COLOR, cbI2));

            JComboBox<Character> cbC = ((JComboBox<Character>)attachedRuleInputPanel.getRuleCellFor(row, Rule.RuleCells.TURN_DIR));
            cbC.addItemListener(e -> updateRuleset(intRow, Rule.RuleCells.TURN_DIR, cbC));

            JComboBox<Integer> cbI3 = ((JComboBox<Integer>)attachedRuleInputPanel.getRuleCellFor(row, Rule.RuleCells.NEW_COLOR));
            cbI3.addItemListener(e -> updateRuleset(intRow, Rule.RuleCells.NEW_COLOR, cbI3));

            JComboBox<Integer> cbI4 = ((JComboBox<Integer>)attachedRuleInputPanel.getRuleCellFor(row, Rule.RuleCells.NEW_STATE));
            cbI4.addItemListener(e -> updateRuleset(intRow, Rule.RuleCells.NEW_STATE, cbI4));
        }
    }

    private void updateRuleset(int intRow, Rule.RuleCells cell, JComboBox<?> cb) {
        if (attachedRuleInputPanel.isNotSettingPanel()) turmite.getRuleset().changeRuleCell(intRow, cell, cb.getSelectedItem());
    }

    public void reset() {
        grids.clear();
        turmite.resetPos();
        centerMap();
        zoom = DEFAULT_ZOOM;
        repaint();
    }
}
