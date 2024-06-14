import ui.Panelv2;

import javax.swing.*;

public class app {
    public static void main(String [] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

        Panelv2 p = new Panelv2();
        p.setVisible(true);

    }
}
