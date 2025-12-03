package turmite.simulator.ui;

import javax.swing.*;
import java.awt.*;

/**
 * A class which handles simple UI Dialogs. This class cannot be instantiated,
 * all its methods are static.
 */
public class Dialogs {
    private Dialogs() {}

    /**
     * Show an information Dialog.
     *
     * @param frame The parent frame.
     * @param message The message to show.
     */
    public static void showInfoDialog(Component frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show an error Dialog.
     *
     * @param frame The parent frame.
     * @param message The message to show.
     */
    public static void showErrorDialog(Component frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
