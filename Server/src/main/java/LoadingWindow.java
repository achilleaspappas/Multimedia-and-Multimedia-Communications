import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

public class LoadingWindow extends JFrame {

    static Logger log;
    private JPanel mainPanel, bottomPanel;

    LoadingWindow() {

        /* Setting title. */
        super("MitchTV Server");

        /* Enable log4j logging. */
        log = LogManager.getLogger(LoadingWindow.class);

        /* Setting up GUI ans JFrame */
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

    public void loadingWindowTermination() {

        /* Terminates the loading window */
        this.setVisible(false);
        dispose();
        log.debug("Server: Loading window stopped.");
    }

    class initGUI {

        /* initGUI is setting up all styling elements of the loading window. */

        initGUI() {

            /* Initialization of the GUI. */
            panelsSetup();
            organizingPanels();
            log.debug("Server: Loading window started.");
        }

        private void panelsSetup() {

            /* Initializing panels */
            mainPanel = new JPanel(new BorderLayout());
            bottomPanel = new JPanel(new BorderLayout());
        }

        private void organizingPanels() {

            /* Organizing panels. */
            bottomPanel.add(new JLabel("Please wait, making sure all videos are available...", JLabel.CENTER), BorderLayout.CENTER);
            bottomPanel.add(new JLabel(new ImageIcon("src/images/loading.gif")), BorderLayout.SOUTH);
            bottomPanel.setBackground(new Color(211, 86, 194));
            mainPanel.add(new JLabel(new ImageIcon("src/images/mitch_logo_1.png")), BorderLayout.CENTER);
            mainPanel.setBackground(new Color(211, 86, 194));
            mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        }
    }
}
