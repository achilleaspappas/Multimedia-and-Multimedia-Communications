# Multimedia and Multimedia Communications

The purpose of this project is to develop two Java applications to implement a system for processing and transmitting video streams using a client-server model. This project was part of a course I did in my 8th semester.
The system to be implemented consists of two basic entities (applications):
- Streaming Server: Responsible for managing a list of available files that can be transmitted, converting the original file into a set of files with different formats and resolutions, communicating with the client, and transmitting the files to the client. This application will be implemented in Java.
- Streaming Client: Responsible for communicating with the server, receiving and playing the video on the client side. This application will also be implemented in Java.

## Prerequisites

To use the files in this repository, you will need the following:

- [Java JDK](https://adoptium.net/) in order to build and run the project. 
- A Java IDE like [Intellij](https://www.jetbrains.com/idea/) which was used to make this project.

## Getting Started

To get started with this project, follow these steps:

1. Clone this repository to your local machine.
2. Open the IDE and import the Java project files from the cloned repository.
3. Build and run the project to test the functionality.

## Contents

This repository contains the following files:
1. Server folder: This folder contains all the server files.
2. Client folder: This folder contains all the client files.

## Specifications

### GUI Appearance
The GUI is developed using JavaFX. It consists of two main screens:
1. Streaming Server screen: This screen is used by the server to monitor the streaming process and display the current status.
2. Streaming Client screen: This screen is used by the client to select the video format, resolution, transmission protocol and start the streaming process.

### Functionality

1. The Streaming Server scans the "videos" folder for available video files in different formats (avi, mp4, mkv) and resolutions (240p, 360p, 480p, 720p, 1080p).
2. For each video file, the Streaming Server creates the missing formats and resolutions using FFMPEG and FFMPEG Wrapper in Java.
3. The Streaming Server creates a list of all available video files and their corresponding formats and resolutions.
4. The Streaming Client estimates the download connection speed of the user's computer using a download test with JSpeedTest library.
5. The Streaming Client sends the user's chosen video format to the Streaming Server.
6. The Streaming Server creates a list of available video files for the user based on their chosen format and the estimated download speed.
7. The Streaming Client presents the available video files to the user for selection.
8. The Streaming Client asks the user to choose the desired protocol for video transmission (UDP, TCP, RTP/UDP).
9. If the user does not choose a protocol, the Streaming Client automatically selects one based on the resolution of the chosen video file.
10. The Streaming Client sends the chosen video file and protocol to the Streaming Server.
11. The Streaming Server initiates the video transmission using FFMPEG as Video Server and notifies the Streaming Client to start receiving the video using FFMPEG as Video Client.
12. Both the Streaming Server and Streaming Client have appropriate logging using Logger and a Graphical User Interface (GUI) using Java Swings.

## Contributing

This is a university project so you can not contribute.

## Authors

* **[University of West Attica]** - *Provided the exersice*
* **[Achilleas Pappas]** - *Made the app*

## License

This project is licensed by University of West Attica as is a part of a university course. Do not redistribute.

## Acknowledgments

Thank you to **University of West Attica** and my professors for providing the resources and knowledge to complete this project.
