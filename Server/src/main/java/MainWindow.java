import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    static Logger log;
    private JPanel mainPanel;
    private JMenuBar menuBar;
    private JMenu logo, empty, info;

    MainWindow() {

        /* Setting title. */
        super("MitchTV Server");

        /* Enable log4j logging. */
        log = LogManager.getLogger(MainWindow.class);

        /* Setting up GUI and Jframe. */
        new initGUI();
        frameSetup();
    }

    private void frameSetup() {

        /* Setting up Jframe. */
        this.add(mainPanel);
        this.setSize(1280, 720);
        this.setVisible(true);
        this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
    }


    class initGUI {

        /* initGUI is setting up all styling elements of the main window. */
        initGUI() {
            panelSetup();
            menuSetup();
            organizingPanels();
            log.debug("Server: GUI ready.");
        }

        private void panelSetup() {

            /* Initializing main panel. */
            mainPanel = new JPanel(new BorderLayout());
        }

        private void menuSetup() {

            /* Initializing menubar. */
            menuBar = new JMenuBar();
            menuBar.setBackground(new Color(25, 25, 25));

            /* Setting up elements on menu bar and also options for format. */
            logo = new JMenu();
            empty = new JMenu("   ");
            info = new JMenu("Server Running");
            logo.setIcon(new ImageIcon("src/images/mitch_icon.png"));
            empty.setEnabled(false);
            info.setFont(new Font("Roobert", Font.PLAIN, 20));
            info.setForeground(Color.WHITE);

        }

        private void organizingPanels() {
            menuBar.add(logo);
            menuBar.add(empty);
            menuBar.add(info);
            mainPanel.setBackground(new Color(25, 25, 25));
            mainPanel.add(menuBar, BorderLayout.NORTH);
            mainPanel.add(new JLabel(new ImageIcon("src/images/mitch_logo_2.png")), BorderLayout.CENTER);
        }

    }

}
