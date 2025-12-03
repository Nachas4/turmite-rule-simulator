package turmite.simulator.utils;

import turmite.simulator.ui.SquareGridPanel;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class manages a GridPanel's simulation. It uses a separate Thread
 * to run the simulation, so other UI elements are not affected.
 */
public class Simulator extends Thread {
    private final SquareGridPanel gridPanel;

    private final AtomicInteger interval;
    private final AtomicBoolean paused = new AtomicBoolean(true);

    public Simulator(SquareGridPanel gridPanel, int interval) {
        this.gridPanel = gridPanel;
        this.interval = new AtomicInteger(interval);
    }

    /**
     * Steps the simulation one time after the set interval (if it isn't paused) in an infinite loop.
     * Use {@code start()} to start the Thread, which will also call this method.
     */
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

    /**
     * Calls the GridPanel's {@code stepSimulation()} and {@code repaint()} methods.
     */
    public void stepSimulation() {
        gridPanel.stepSimulation();
        gridPanel.repaint();
    }

    /**
     * Sets the interval between simulation steps.
     *
     * @param interval The interval to set.
     */
    public void setInterval(int interval) {
        this.interval.set(interval);
    }

    /**
     * Toggles whether the simulation is paused or not.
     *
     * @return Whether the simulation is paused after this.
     */
    public boolean toggleSimulation() {
        paused.set(!paused.get());
        return paused.get();
    }

    /**
     * Resets the simulation by calling the GridPanel's {@code reset()} method.
     */
    public void resetSimulation() {
        gridPanel.reset();
    }
}
