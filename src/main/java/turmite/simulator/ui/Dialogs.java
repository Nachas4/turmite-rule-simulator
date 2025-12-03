package turmite.simulator.ui;

import javax.swing.*;
import java.awt.*;

public class Dialogs {
    private Dialogs() {}

    public static void showInfoDialog(Component frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showErrorDialog(Component frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
