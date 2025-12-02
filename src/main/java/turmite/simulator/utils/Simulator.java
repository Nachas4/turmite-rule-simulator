package turmite.simulator.utils;

import turmite.simulator.ui.SquareGridPanel;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Simulator extends Thread {
    private final SquareGridPanel gridPanel;

    private final AtomicInteger interval;
    private final AtomicBoolean paused = new AtomicBoolean(true);

    public Simulator(SquareGridPanel gridPanel, int interval) {
        this.gridPanel = gridPanel;
        this.interval = new AtomicInteger(interval);
    }

    // An infinite loop is intended here.
    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public synchronized void run() {
        while (true) {
            try {
                if (paused.get()) continue;
                stepSimulation();
                wait(interval.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stepSimulation() {
        gridPanel.stepSimulation();
        gridPanel.repaint();
    }

    public void setInterval(int interval) {
        this.interval.set(interval);
    }

    public boolean toggleSimulation() {
        paused.set(!paused.get());
        return paused.get();
    }

    public void resetSimulation() {
        gridPanel.reset();
    }
}
