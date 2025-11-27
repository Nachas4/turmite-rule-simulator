package turmite.simulator.utils;

public class Simulator extends Thread {
    private final SquareGridPanel gridPanel;

    private int interval;

    public Simulator(SquareGridPanel gridPanel) {
        this.gridPanel = gridPanel;
        interval = 500;
    }

    @Override
    public synchronized void run() {
        while (true) {
            try {
                gridPanel.stepSimulation();
                gridPanel.repaint();

                wait(interval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
