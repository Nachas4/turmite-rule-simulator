package turmite.simulator.ui;

import javax.swing.*;

public class Dialogs {
    private Dialogs() {}

    public static void showInfoDialog(JFrame frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showErrorDialog(JFrame frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
