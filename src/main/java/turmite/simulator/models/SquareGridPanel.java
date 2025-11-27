package turmite.simulator.models;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.HashMap;
import java.util.Map;

public class SquareGridPanel extends JPanel {
    private final Map<Grid, Color> grids = new HashMap<>();
    private final transient Turmite turmite;

    private double zoom = 2;
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
                int gridPanelMouseX = e.getX();
                int gridPanelMouseY = e.getY();

                offsetX += gridPanelMouseX - lastMouseX;
                offsetY += gridPanelMouseY - lastMouseY;

                lastMouseX = gridPanelMouseX;
                lastMouseY = gridPanelMouseY;

                repaint();
            }
        });
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

        for (Map.Entry<Grid, Color> entry : grids.entrySet()) {
            g2.setColor(entry.getValue());
            Grid grid = entry.getKey();
            g2.fillRect(grid.getX(), grid.getY(), gridSize, gridSize);
        }

        g2.setColor(Color.red);
        g2.fillOval(turmite.getX() + turmitePosModifier, turmite.getY() + turmitePosModifier, turmiteSize, turmiteSize);

        g2.dispose();
    }

    /*
     * Main Methods
     */

    public void stepSimulation() {
        turmite.move();
        double rand = Math.random();
        if (rand < 0.3) turmite.turnLeft();
        if (Math.random() > 0.7) turmite.turnRight();
        setColorAt(new Grid(turmite.getPos()), Color.black);
    }

    public void setColorAt(Grid grid, Color color) {
        grids.put(grid, color);
    }

    public void centerMap() {
        offsetX = ((double) getWidth() / 2) - (gridSize * 1.5);
        offsetY = ((double) getHeight() / 2) - (gridSize * 1.5);
    }
}
