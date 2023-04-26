import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.ArrayList;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.model.SpeedTestError;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;


public class MainWindow extends JFrame {

    static Logger log;
    private JPanel mainPanel, bottomPanel;
    private JMenuBar menuBar;
    private JMenu logo, dummy, info, profile, format;
    private JMenuItem mkv, mp4, avi;
    private ActionHandler handler;
    private JComboBox videoBoxSelector, protocolSelector;
    private JButton videoBoxSelectorButton;
    private SocketClient sc;
    private String strBitrate;

    MainWindow() {

        /* Setting title. */
        super("MitchTV Client");

        /* Enable log4j logging. */
        log = LogManager.getLogger(SocketClient.class);

        /* Running speed test with JSpeedTest. */
        new SpeedTestExecutor();

        /* Setting up GUI and Jframe. */
        new initGUI();
        frameSetup();

        /* Setting up communication. */
        sc = new SocketClient();
    }

    private void frameSetup() {

        /* Setting up Jframe. */
        this.add(mainPanel);
        this.setSize(1280, 720);
        this.setVisible(true);
        this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
    }

    class initGUI {

        /* initGUI is in charge of creating all GUI elements. GUI consist of a main panel, a menu bar and a bottom panel.
         * The menu bar has styling elements and the option "Set Format" to choose from mkv, mp4, avi.
         * The main panel has styling elements and hosts the bottom panel.
         * The bottom panel has the protocol and video dropdown menu and a button that submit these two options. */

        initGUI() {

            /* Initialization of the GUI. */
            panelsSetup();
            menuSetup();
            selectorSetup();
            organizingPanels();
            implementBehaviors();
            log.debug("Client: GUI ready.");
        }

        private void panelsSetup() {

            /* Initializing main and bottom panel. */
            mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(new Color(25, 25, 25));
            bottomPanel = new JPanel(new FlowLayout());
            bottomPanel.setBackground(new Color(25, 25, 25));

        }

        private void menuSetup() {

            /* Initializing menubar. */
            menuBar = new JMenuBar();
            menuBar.setBackground(new Color(25, 25, 25));

            /* Setting up elements on menu bar and also options for format. */
            logo = new JMenu();
            dummy = new JMenu("   ");
            info = new JMenu("Client Running");
            format = new JMenu();
            profile = new JMenu();
            mkv = new JMenuItem("mkv");
            mp4 = new JMenuItem("mp4");
            avi = new JMenuItem("avi");
            mkv.setBackground(new Color(25, 25, 25));
            mkv.setForeground(Color.WHITE);
            mp4.setBackground(new Color(25, 25, 25));
            mp4.setForeground(Color.WHITE);
            avi.setBackground(new Color(25, 25, 25));
            avi.setForeground(Color.WHITE);
            logo.setIcon(new ImageIcon("src/images/mitch_icon.png"));
            dummy.setEnabled(false);
            info.setFont(new Font("Roobert", Font.PLAIN, 20));
            info.setForeground(Color.WHITE);
            format.setIcon(new ImageIcon("src/images/set_format.png"));
            profile.setIcon(new ImageIcon("src/images/profile.png"));
            menuBar.add(logo);
            menuBar.add(dummy);
            menuBar.add(info);
            menuBar.add(Box.createHorizontalGlue());
            menuBar.add(format);
            format.add(mkv);
            format.add(mp4);
            format.add(avi);
            menuBar.add(profile);
        }

        private void selectorSetup() {

            /* Setting up options for protocol and video. */
            videoBoxSelector = new JComboBox();
            videoBoxSelector.setFont(new Font("Roobert", Font.PLAIN, 15));
            videoBoxSelector.setBackground(new Color(25, 25, 25));
            videoBoxSelector.setForeground(Color.WHITE);
            videoBoxSelectorButton = new JButton("Submit");
            videoBoxSelectorButton.setFont(new Font("Roobert", Font.PLAIN, 15));
            videoBoxSelectorButton.setBackground(new Color(25, 25, 25));
            videoBoxSelectorButton.setForeground(Color.WHITE);
            videoBoxSelector.setVisible(false);
            videoBoxSelectorButton.setVisible(false);
            protocolSelector = new JComboBox();
            protocolSelector.setFont(new Font("Roobert", Font.PLAIN, 15));
            protocolSelector.setBackground(new Color(25, 25, 25));
            protocolSelector.setForeground(Color.WHITE);
            protocolSelector.addItem("DEFAULT");
            protocolSelector.addItem("TCP");
            protocolSelector.addItem("UDP");
            protocolSelector.addItem("RTP/UDP");
            protocolSelector.setVisible(false);
        }

        private void organizingPanels() {

            /* Organizing panels. */
            bottomPanel.add(protocolSelector);
            bottomPanel.add(videoBoxSelector);
            bottomPanel.add(videoBoxSelectorButton);
            mainPanel.add(new JLabel(new ImageIcon("src/images/mitch_logo_2.png")), BorderLayout.CENTER);
            mainPanel.add(bottomPanel, BorderLayout.SOUTH);
            mainPanel.add(menuBar, BorderLayout.NORTH);
        }

        private void implementBehaviors() {

            /* Creating behaviors */
            handler = new ActionHandler();
            mkv.addActionListener(handler);
            mp4.addActionListener(handler);
            avi.addActionListener(handler);
            videoBoxSelectorButton.addActionListener(handler);
        }
    }

    class ActionHandler extends Component implements ActionListener {

        /* This class is setting up behaviors for all available options in the GUI. */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == mkv) {
                protocolSelector.setVisible(true);
                videoBoxSelector.setVisible(true);
                videoBoxSelectorButton.setVisible(true);
                sc.sendFormatBitrate(strBitrate, "mkv");
            } else if (e.getSource() == mp4) {
                protocolSelector.setVisible(true);
                videoBoxSelector.setVisible(true);
                videoBoxSelectorButton.setVisible(true);
                sc.sendFormatBitrate(strBitrate, "mp4");
            } else if (e.getSource() == avi) {
                protocolSelector.setVisible(true);
                videoBoxSelector.setVisible(true);
                videoBoxSelectorButton.setVisible(true);
                sc.sendFormatBitrate(strBitrate, "avi");
            } else if (e.getSource() == videoBoxSelectorButton) {
                protocolSelector.setVisible(false);
                videoBoxSelector.setVisible(false);
                videoBoxSelectorButton.setVisible(false);
                sc.sendProtocolSelectedVideo(protocolSelector.getSelectedItem().toString(), videoBoxSelector.getSelectedItem().toString());
                videoBoxSelector.removeAllItems();
            }
        }
    }

    class SpeedTestExecutor {

        /* This class is doing the speed test. We use the JSpeedTest library. When the speed test is done it updates the
         * strBitrate value. */
        SpeedTestExecutor() {
            speedTest();
        }

        private void speedTest() {
            SpeedTestSocket speedTestSocket = new SpeedTestSocket();

            speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

                @Override
                public void onCompletion(SpeedTestReport report) {
                    // called when download/upload is complete.
                    BigDecimal tempBitrateBD = report.getTransferRateOctet();
                    float tempBitrate = tempBitrateBD.floatValue();
                    tempBitrate /= 1000;
                    strBitrate = String.valueOf(tempBitrate);
                    log.debug("Client: Speed test completed: " + strBitrate + " kbps");
                }

                @Override
                public void onError(SpeedTestError speedTestError, String errorMessage) {
                    // called when a download/upload error occur.
                }

                @Override
                public void onProgress(float percent, SpeedTestReport report) {
                    // called to notify download/upload progress.
                }
            });

            speedTestSocket.startDownload("http://ipv4.ikoula.testdebit.info/1M.iso");
        }
    }


    class SocketClient {

        /* This class is setting up the client side of the communication. It opens a socket at port 5000. It talks to
         * the server and firstly informs it for the wanted format and the available bitrate. After, it receives the
         * available videos. At the end, it asks for a specific video and protocol and starts a command line process for
         * ffplay. */
        private Socket socket;
        private ObjectOutputStream outputStream;
        private ObjectInputStream inputStream;

        SocketClient() {
            try {
                log.debug("Client: Initialising Socket.");
                socket = new Socket("127.0.0.1", 5000);
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                log.fatal("Client: Error an exception happened.");
                e.printStackTrace();
            }
        }

        private void sendFormatBitrate(String bitrate, String format) {
            try {
                ArrayList<String> dataFormatBitrate = new ArrayList<>();
                dataFormatBitrate.add(bitrate);
                dataFormatBitrate.add(format);

                log.debug("Client: Sending format and bitrate.");
                outputStream.writeObject(dataFormatBitrate);

                log.debug("Client: Received available videos.");
                ArrayList<String> availableVideos = (ArrayList<String>) inputStream.readObject();

                log.debug("Client: Sending list to GUI.");
                for (String currentVideo : availableVideos) {
                    videoBoxSelector.addItem(currentVideo);
                }
            } catch (IOException | ClassNotFoundException e) {
                log.fatal("Client: Error an exception happened.");
                e.printStackTrace();
            }
        }

        private void sendProtocolSelectedVideo(String protocol, String selectedVideo) {

            try {
                /* Sending selected protocol and selected video from GUI to server. */
                ArrayList<String> dataProtocolSelectedVideo = new ArrayList<>();
                dataProtocolSelectedVideo.add(protocol);
                dataProtocolSelectedVideo.add(selectedVideo);

                log.debug("Client: Sending selected protocol \"" + protocol + "\" and selected video \"" + selectedVideo + "\".");
                outputStream.writeObject(dataProtocolSelectedVideo);

                /* Creating command for command line to play streaming video. */
                ArrayList<String> commandLineArgs = new ArrayList<>();
                commandLineArgs.add("ffplay");
                String selectedVideoResolution = selectedVideo.split("-")[1].split("[.p]")[0];
                if (protocol.equals("UDP") | (protocol.equals("DEFAULT") && (selectedVideoResolution.equals("360") | selectedVideoResolution.equals("480")))) {
                    commandLineArgs.add("udp://127.0.0.1:6000");
                } else if (protocol.equals("TCP") | (protocol.equals("DEFAULT") && selectedVideoResolution.equals("240"))) {
                    commandLineArgs.add("tcp://127.0.0.1:5100");
                } else if (protocol.equals("RTP/UDP") | (protocol.equals("DEFAULT") && (selectedVideoResolution.equals("720") | selectedVideoResolution.equals("1080")))) {
                    commandLineArgs.add("-protocol_whitelist");
                    commandLineArgs.add("\"file,rtp,udp\"");
                    commandLineArgs.add("-i");
                    commandLineArgs.add("rtp://127.0.0.1:5004");
                }

                log.debug("Client: Streaming process started");
                ProcessBuilder processBuilder = new ProcessBuilder(commandLineArgs);
                Process streamClient = processBuilder.start();

            } catch (IOException e) {
                log.fatal("Client: Error an exception happened.");
                e.printStackTrace();
            }

            /* Program termination. */
            log.debug("Client: Program terminated.");
            System.exit(0);
        }
    }

}

