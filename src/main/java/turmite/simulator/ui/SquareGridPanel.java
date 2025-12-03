package turmite.simulator.ui;

import turmite.simulator.models.Grid;
import turmite.simulator.models.Rule;
import turmite.simulator.models.Turmite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that represents a Square Grid Panel with a Turmite.
 */
public class SquareGridPanel extends JPanel {
    private static final double DEFAULT_ZOOM = 2;

    private final Map<Grid, Integer> grids = new HashMap<>();
    private final transient Turmite turmite;

    private double zoom = DEFAULT_ZOOM;
    private double lastMouseX = 0;
    private double lastMouseY = 0;
    private double offsetX = 0;
    private double offsetY = 0;

    private final int gridSize = (int)(zoom * 4);

    private final int turmitePosModifier = (int)zoom;
    private final int turmiteSize = (int)(zoom * 2);

    public SquareGridPanel(RuleInputPanel ruleInputPanel) {
        super();
        turmite = new Turmite(new Grid(0, 0, gridSize), ruleInputPanel.getRuleset());
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

    /**
     * Calculates the offsets for panning the panel.
     *
     * @param e The MouseEvent data.
     */
    private void calculatePan(MouseEvent e) {
        int gridPanelMouseX = e.getX();
        int gridPanelMouseY = e.getY();

        offsetX += gridPanelMouseX - lastMouseX;
        offsetY += gridPanelMouseY - lastMouseY;

        lastMouseX = gridPanelMouseX;
        lastMouseY = gridPanelMouseY;

        repaint();
    }

    /**
     * Calculates the zoom and offsets for zooming the panel.
     * Whatever Point was under the mouse pointer is kept under it.
     *
     * @param e The MouseWheelEvent data.
     */
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
                g2.setColor(Rule.numToColor(entry.getValue()));
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
     * Simulation Methods
     */

    /**
     * Paints the Grid to the Color based on the Turmite's Ruleset, and moves it.
     */
    public void stepSimulation() {
        Grid currTurmiteGrid = turmite.getGrid();
        int gridColor = grids.getOrDefault(currTurmiteGrid, 0);

        setColorAt(new Grid(currTurmiteGrid), turmite.calculateNextColor(gridColor));
        turmite.move(gridColor);
    }

    /**
     * Colors the Grid with the given Color.
     *
     * @param grid The grid to color.
     * @param color The color to use.
     */
    private void setColorAt(Grid grid, int color) {
        grids.put(grid, color);
    }

    /**
     * Centers the panel on.
     */
    public void centerMap() {
        offsetX = ((double) getWidth() / 2) - (gridSize * 1.5);
        offsetY = ((double) getHeight() / 2) - (gridSize * 1.5);
    }

    /**
     * Resets the panel. The Turmite and panel zoom are reset, all grids are cleared and the map is centered.
     */
    public void reset() {
        grids.clear();
        turmite.reset();
        centerMap();
        zoom = DEFAULT_ZOOM;
        repaint();
    }
}
