import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class DataAdmin {

    static Logger log;
    private ArrayList<Video> videoList;
    private ArrayList<String> usedNames;
    private ArrayList<Integer> usedNamesIndex;
    private ArrayList<Integer> prototypeResolutionList;
    private ArrayList<String> prototypeFormatList;
    private SocketServer ss;

    DataAdmin() {

        /* Enable log4j logging. */
        log = LogManager.getLogger(DataAdmin.class);

        /* Loading window start up. */
        LoadingWindow lw = new LoadingWindow();

        /* Setting up all lists, videoList contains video objects, usedNames and usedNamesIndex are used to fins optimal values.
        * prototype lists contain information about all resolutions and formats. */
        videoList = new ArrayList<>();
        usedNames = new ArrayList<>();
        usedNamesIndex = new ArrayList<>();
        prototypeResolutionList = new ArrayList<>();
        prototypeFormatList = new ArrayList<>();

        /* Fill prototype lists. */
        createPrototypeResolutionList();
        createPrototypeFormatList();

        /* Read all videos in folder "videos". */
        collectFiles();

        /* Find videos with the best resolution. */
        findBiggerResolution();

        /* Locates missing resolutions and formats for every video. */
        findMissing();

        /* Terminates loading video because all videos have been created. */
        lw.loadingWindowTermination();

        /* Start a new main window. */
        new MainWindow();

        /* Setting up communication. */
        ss = new SocketServer();
        ss.dataExchange();
    }

    private void createPrototypeResolutionList() {

        /* Prototype lists for all resolutions. */
        prototypeResolutionList.add(240);
        prototypeResolutionList.add(480);
        prototypeResolutionList.add(720);
        prototypeResolutionList.add(1080);
        log.debug("Server: Prototype list containing resolutions has been created.");
    }

    private void createPrototypeFormatList() {

        /* Prototype lists for all formats. */
        prototypeFormatList.add("mkv");
        prototypeFormatList.add("mp4");
        prototypeFormatList.add("avi");
        log.debug("Server: Prototype list containing formats has been created.");
    }

    private void collectFiles() {

        /* This method reads all videos in folder "videos" and add them to videoList. */
        String tempName;
        Integer tempResolution;
        String tempFormat;

        File f = new File(System.getProperty("user.dir") + "/videos/");
        String[] fileNames = f.list();
        log.debug("Server: List of available files in folder \"videos\" has been obtained.");

        for (String fileName : fileNames) {
            tempName = String.valueOf(fileName.split("-")[0]);
            tempResolution = Integer.parseInt(fileName.split("-")[1].split("p.")[0]);
            tempFormat = String.valueOf(fileName.split("[.]")[1]);
            videoList.add(new Video(fileName, tempName, tempResolution, tempFormat));
            log.debug("Server: " + fileName + " added from folder \"videos\" to videoList.");
        }
    }

    private void findBiggerResolution() {

        /* This method finds the biggest resolution for every video in videoList. Note that it always prefers
        *  the format mkv over everything else. For example if it finds 720p.mp4 and 720.mkv, it uses the 720p.mkv
        * because mkv is lossless and mp4 and avi are lossy. But if it finds 1080.mp4 and 720.mkv it uses the mp4
        * because the resolutions is bigger. All elected videos go in usedNames and their indexes on videoList go
        * in usedNamesIndex. */
        Integer maxRes = 0;
        boolean state = false;
        String temp1 = null;
        Integer temp2 = null;
        for (Video item : videoList) {
            if (!usedNames.contains(item.getName())) {
                for (Video item1 : videoList) {
                    if (item.getName().equals(item1.getName()) && item1.getResolution() >= maxRes) {
                        if (maxRes == 0) {
                            maxRes = item1.getResolution();
                            temp1 = item1.getName();
                            temp2 = videoList.indexOf(item1);
                            state = true;
                        } else if (item1.getResolution() > maxRes) {
                            maxRes = item1.getResolution();
                            temp1 = item1.getName();
                            temp2 = videoList.indexOf(item1);
                            state = true;
                        } else if (item1.getResolution().equals(maxRes) && item1.getFormat().equals("mkv")) {
                            maxRes = item1.getResolution();
                            temp1 = item1.getName();
                            temp2 = videoList.indexOf(item1);
                            state = true;
                        }
                    }
                }
                if (state) {
                    usedNames.add(temp1);
                    usedNamesIndex.add(temp2);
                    state = false;
                    log.debug("Server: Biggest resolution found for video " + temp1 + ".");
                }
                maxRes = 0;
            }
        }
    }

    private void findMissing() {

        /* This method uses the usedNames and usedNamesIndex to find all resolutions and formats missing for every video
        * in videoList.  */
        ArrayList<Video> tempVideo = new ArrayList<>();
        ArrayList<String> tempFullNameList = new ArrayList<>();
        int counter = 0;
        Integer maxRes;

        for (Video name0 : videoList) {
            tempFullNameList.add(name0.getFullName());
        }

        for (String name : usedNames) {
            maxRes = videoList.get(usedNamesIndex.get(counter)).getResolution();
            for (String format : prototypeFormatList) {
                for (Integer resolution : prototypeResolutionList) {
                    if (resolution > maxRes) {
                        continue;
                    }
                    if (!tempFullNameList.contains(name + "-" + resolution + "p." + format)) {
                        log.debug("Server: Missing video found " + name + "-" + resolution + "p." + format + ".");
                        createVideo(videoList.get(usedNamesIndex.get(counter)), name + "-" + resolution + "p." + format);
                        tempVideo.add(new Video(name + "-" + resolution + "p." + format, name, resolution, format));
                        log.debug("Server: Missing video created " + name + "-" + resolution + "p." + format + ".");
                    }
                }
            }
            counter++;
        }
        videoList.addAll(tempVideo);
    }

    private void createVideo(Video inputObj, String outputName) {

        /* This method creates a video with ffmpeg. */
        String dir = System.getProperty("user.dir") + "/videos/";
        FFmpeg ffmpeg = null;
        FFprobe ffprobe = null;
        String inputName = inputObj.getFullName();
        try {
            log.debug("Server: Initializing FFMpegClient.");
            ffmpeg = new FFmpeg("C:\\ffmpeg\\bin\\ffmpeg.exe");
            ffprobe = new FFprobe("C:\\ffmpeg\\bin\\ffprobe.exe");
        } catch (IOException e) {
            log.fatal("Server: Error an exception happened.");
            e.printStackTrace();
        }

        log.debug("Server: Creating the transcoding.");
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(dir + inputName)
                .addOutput(dir + outputName)
                .done();

        log.debug("Server: Creating the executor.");
        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

        log.debug("Server: Starting the transcoding.");
        executor.createJob(builder).run();
        log.debug("Server: Transcoding finished for video " + outputName + ".");
    }

    class SocketServer {

        /* This class is setting up the server side of the communication. It opens a socket at port 5000. It talks to
         * the client and firstly receives the wanted format and the available bitrate. After, it sends the
         * available videos. At the end, it receives a specific video title and protocol and starts a command line
         * process for ffmpeg. */
        private Socket socket;
        private ServerSocket server;
        private ObjectOutputStream outputStream;
        private ObjectInputStream inputStream;

        SocketServer() {
            try {
                log.debug("Server: Initializing ServerSocket.");
                server = new ServerSocket(5000);
            } catch (IOException e) {
                log.fatal("Server: Error an exception happened.");
                e.printStackTrace();
            }
        }

        private void dataExchange() {

            try {
                log.debug("Server: Initialising Socket.");
                socket = server.accept();
                inputStream = new ObjectInputStream(socket.getInputStream());
                outputStream = new ObjectOutputStream(socket.getOutputStream());

                ArrayList<String> receivedRequestOne = (ArrayList<String>) inputStream.readObject();
                float selectedBitrate = Float.parseFloat(receivedRequestOne.get(0));
                String selectedFormat = receivedRequestOne.get(1);

                ArrayList<String> availableVideos = new ArrayList<>();
                for (Video item : videoList) {
                    if (item.getFormat().equals(selectedFormat)) {
                        if (selectedBitrate >= 4500 && item.getResolution() <= 1080) {
                            availableVideos.add(item.getFullName());
                        } else if (selectedBitrate < 4500 && selectedBitrate >= 2500 && item.getResolution() <= 720) {
                            availableVideos.add(item.getFullName());
                        } else if (selectedBitrate < 2500 && selectedBitrate >= 1000 && item.getResolution() <= 480) {
                            availableVideos.add(item.getFullName());
                        } else if (selectedBitrate < 1000 && selectedBitrate >= 750 && item.getResolution() <= 360) {
                            availableVideos.add(item.getFullName());
                        } else if (selectedBitrate < 750 && item.getResolution() <= 240) {
                            availableVideos.add(item.getFullName());
                        }
                    }
                }

                outputStream.writeObject(availableVideos);

                ArrayList<String> receivedRequestTwo = (ArrayList<String>) inputStream.readObject();

                String selectedProtocol = receivedRequestTwo.get(0);
                String selectedVideo = receivedRequestTwo.get(1);

                String dir = System.getProperty("user.dir") + "/videos/";
                ArrayList<String> commandLineArgs = new ArrayList<>();
                commandLineArgs.add("ffmpeg");
                String selectedVideoResolution = selectedVideo.split("-")[1].split("[.p]")[0];
                if (selectedProtocol.equals("UDP") | (selectedProtocol.equals("DEFAULT") && (selectedVideoResolution.equals("360") | selectedVideoResolution.equals("480")))) {
                    commandLineArgs.add("-i");
                    commandLineArgs.add(dir + selectedVideo);
                    commandLineArgs.add("-f");
                    commandLineArgs.add("mpegts");
                    commandLineArgs.add("udp://127.0.0.1:6000");
                } else if (selectedProtocol.equals("TCP") | (selectedProtocol.equals("DEFAULT") && selectedVideoResolution.equals("240"))) {
                    commandLineArgs.add("-i");
                    commandLineArgs.add(dir + selectedVideo);
                    commandLineArgs.add("-f");
                    commandLineArgs.add("mpegts");
                    commandLineArgs.add("tcp://127.0.0.1:5100?listen");
                } else if (selectedProtocol.equals("RTP/UDP") | (selectedProtocol.equals("DEFAULT") && (selectedVideoResolution.equals("720") | selectedVideoResolution.equals("1080")))) {
                    commandLineArgs.add("-re");
                    commandLineArgs.add("-i");
                    commandLineArgs.add(dir + selectedVideo);
                    commandLineArgs.add("-vcodec");
                    commandLineArgs.add("copy");
                    commandLineArgs.add("-f");
                    commandLineArgs.add("rtp_mpegts");
                    commandLineArgs.add("rtp://127.0.0.1:5004");
                }

                ProcessBuilder processBuilder = new ProcessBuilder(commandLineArgs);
                Process streamServer = processBuilder.start();

                log.debug("Server: Closing connection.");
                outputStream.close();
                inputStream.close();
                socket.close();
            } catch (IOException | ClassNotFoundException e) {
                log.fatal("Server: Error an exception happened.");
                e.printStackTrace();
            }
            log.debug("Server: Connection closed.");

            /* Program termination. */
            log.debug("Server: Program terminated.");
            System.exit(0);

        }
    }


}
