import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.media.*;
import javax.microedition.media.control.VideoControl;
import javax.microedition.media.control.VolumeControl;
import javax.microedition.io.*;
import javax.microedition.io.file.*;
import java.io.*;
import java.util.Vector;
import java.util.Enumeration;

/**
 * MediaPlayer Plus v3.3 - Barre de progression temps réel
 * Compatible J2ME MIDP 2.0 / CLDC 1.1
 */
public class MediaPlayerPlusMIDlet extends MIDlet implements CommandListener, PlayerListener {
    private Display display;
    // Ecrans
    private MainMenuScreen mainMenuScreen;
    private VideoDownloadScreen videoDownloadScreen;
    private AudioDownloadScreen audioDownloadScreen;
    private VideoQualityScreen videoQualityScreen;
    private AudioFormatScreen audioFormatScreen;
    private VideoLibraryScreen videoLibraryScreen;
    private AudioLibraryScreen audioLibraryScreen;
    private ConverterScreen converterScreen;
    private SettingsScreen settingsScreen;
    private RealTimeProgressScreen progressScreen;
    private VLCPlayerScreen videoPlayerScreen;
    private AudioPlayerScreen audioPlayerScreen;
    // Player
    private Player currentPlayer;
    private VideoControl videoControl;
    private VolumeControl volumeControl;
    private boolean isPlaying = false;
    private boolean isPaused = false;
    private boolean isVideoMode = false;
    private String currentFilePath = "";
    private String currentFileName = "";
    // Stockage
    private String rootPath = "";
    private String videosFolder = "";
    private String audiosFolder = "";
    private String tempFolder = "";
    private boolean hasFileAccess = false;
    // Configuration
    private int volume = 75;
    private boolean autoPlay = true;
    private boolean loopPlayback = false;
    // Playlist
    private Vector currentPlaylist = new Vector();
    private int currentTrackIndex = 0;
    // Variables de telechargement
    private String pendingVideoId = "";
    private String pendingAudioUrl = "";
    private String pendingPlatform = "";

    public void startApp() {
        display = Display.getDisplay(this);
        initializeStorage();
        mainMenuScreen = new MainMenuScreen();
        display.setCurrent(mainMenuScreen);
    }

    public void pauseApp() {
        if (isPlaying && currentPlayer != null) {
            try {
                currentPlayer.stop();
                isPaused = true;
                isPlaying = false;
            } catch (Exception e) {}
        }
    }

    public void destroyApp(boolean unconditional) {
        stopPlayer();
    }

    public void commandAction(Command c, Displayable d) {}

    // Gestion stockage
    private void initializeStorage() {
        try {
            Enumeration roots = FileSystemRegistry.listRoots();
            if (roots != null && roots.hasMoreElements()) {
                hasFileAccess = true;
                String bestRoot = null;
                long maxSpace = 0;

                while (roots.hasMoreElements()) {
                    String root = (String) roots.nextElement();
                    try {
                        String testPath = "file:///" + root;
                        FileConnection fc = (FileConnection) Connector.open(testPath);
                        long space = fc.availableSize();

                        if (fc.canWrite() && space > maxSpace) {
                            maxSpace = space;
                            bestRoot = testPath;
                        }
                        fc.close();
                    } catch (Exception e) {}
                }

                if (bestRoot != null) {
                    rootPath = bestRoot;
                    createFolderStructure();
                }
            }
        } catch (Exception e) {
            hasFileAccess = false;
        }
    }

    private void createFolderStructure() {
        try {
            String baseFolder = rootPath + "MediaPlayerPlus/";
            createFolder(baseFolder);
            videosFolder = baseFolder + "Videos/";
            createFolder(videosFolder);

            audiosFolder = baseFolder + "Audios/";
            createFolder(audiosFolder);

            tempFolder = baseFolder + "Temp/";
            createFolder(tempFolder);
        } catch (Exception e) {}
    }

    private void createFolder(String path) {
        try {
            FileConnection fc = (FileConnection) Connector.open(path);
            if (!fc.exists()) fc.mkdir();
            fc.close();
        } catch (Exception e) {}
    }

    // Gestion player
    private void stopPlayer() {
        if (currentPlayer != null) {
            try {
                currentPlayer.stop();
                currentPlayer.deallocate();
                currentPlayer.close();
            } catch (Exception e) {}
            currentPlayer = null;
            videoControl = null;
            volumeControl = null;
            isPlaying = false;
            isPaused = false;
        }
    }

    public void playerUpdate(Player player, String event, Object eventData) {
        if (event.equals(PlayerListener.STARTED)) {
            isPlaying = true;
            isPaused = false;
        } else if (event.equals(PlayerListener.STOPPED)) {
            isPlaying = false;
        } else if (event.equals(PlayerListener.END_OF_MEDIA)) {
            isPlaying = false;
            handleMediaEnd();
        }
    }

    private void handleMediaEnd() {
        if (loopPlayback) {
            playCurrentMedia();
        } else if (!isVideoMode && currentTrackIndex < currentPlaylist.size() - 1) {
            playNextTrack();
        } else {
            try { Thread.sleep(2000); } catch (Exception e) {}
            display.callSerially(new Runnable() {
                public void run() { display.setCurrent(mainMenuScreen); }
            });
        }
    }

    private void playNextTrack() {
        if (currentTrackIndex < currentPlaylist.size() - 1) {
            currentTrackIndex++;
            String fileName = (String) currentPlaylist.elementAt(currentTrackIndex);
            playAudio(audiosFolder + fileName, fileName);
        }
    }

    private void playPreviousTrack() {
        if (currentTrackIndex > 0) {
            currentTrackIndex--;
            String fileName = (String) currentPlaylist.elementAt(currentTrackIndex);
            playAudio(audiosFolder + fileName, fileName);
        }
    }

    // ============================================
    // MENU PRINCIPAL
    // ============================================
    class MainMenuScreen extends List implements CommandListener {
        private Command selectCmd, exitCmd;

        MainMenuScreen() {
            super("MediaPlayer Plus v3.3", List.IMPLICIT);

            append("Download Video", null);
            append("Download Audio", null);
            append("Video Library", null);
            append("Audio Library", null);
            append("Convert Video to Audio", null);
            append("Settings", null);
            append("About", null);

            selectCmd = new Command("Select", Command.OK, 0);
            exitCmd = new Command("Exit", Command.EXIT, 1);
            addCommand(selectCmd);
            addCommand(exitCmd);
            setCommandListener(this);
        }

        public void commandAction(Command c, Displayable d) {
            if (c == exitCmd) {
                stopPlayer();
                notifyDestroyed();
            } else if (c == selectCmd || c == List.SELECT_COMMAND) {
                int sel = getSelectedIndex();
                if (sel == 0) {
                    videoDownloadScreen = new VideoDownloadScreen();
                    display.setCurrent(videoDownloadScreen);
                } else if (sel == 1) {
                    audioDownloadScreen = new AudioDownloadScreen();
                    display.setCurrent(audioDownloadScreen);
                } else if (sel == 2) {
                    videoLibraryScreen = new VideoLibraryScreen();
                    display.setCurrent(videoLibraryScreen);
                } else if (sel == 3) {
                    audioLibraryScreen = new AudioLibraryScreen();
                    display.setCurrent(audioLibraryScreen);
                } else if (sel == 4) {
                    converterScreen = new ConverterScreen();
                    display.setCurrent(converterScreen);
                } else if (sel == 5) {
                    settingsScreen = new SettingsScreen();
                    display.setCurrent(settingsScreen);
                } else if (sel == 6) {
                    showAbout();
                }
            }
        }

        private void showAbout() {
            Alert about = new Alert("About",
                "MediaPlayer Plus v3.3\n\n" +
                "Download & play videos/audio\n" +
                "YouTube, TikTok, FB, Instagram\n" +
                "VLC-style player\n" +
                "MP3 converter\n" +
                "Real-time progress bar\n\n" +
                "Compatible: J2ME MIDP 2.0",
                null, AlertType.INFO);
            about.setTimeout(Alert.FOREVER);
            display.setCurrent(about, mainMenuScreen);
        }
    }

    // ============================================
    // TELECHARGEMENT VIDEO
    // ============================================
    class VideoDownloadScreen extends Form implements CommandListener {
        private TextField urlField;
        private ChoiceGroup platformChoice;
        private Command nextCmd, backCmd;

        VideoDownloadScreen() {
            super("Download Video");

            urlField = new TextField("Paste URL:", "", 256, TextField.URL);
            append(urlField);

            platformChoice = new ChoiceGroup("Platform:", Choice.EXCLUSIVE);
            platformChoice.append("YouTube", null);
            platformChoice.append("TikTok", null);
            platformChoice.append("Facebook", null);
            platformChoice.append("Instagram", null);
            platformChoice.append("Direct Link", null);
            platformChoice.setSelectedIndex(0, true);
            append(platformChoice);

            append(new StringItem("Info:",
                "Video will be saved to Video Library"));

            if (!hasFileAccess) {
                append(new StringItem("Warning:",
                    "File system not available!"));
            }

            nextCmd = new Command("Choose Quality", Command.OK, 0);
            backCmd = new Command("Back", Command.BACK, 1);
            addCommand(nextCmd);
            addCommand(backCmd);
            setCommandListener(this);
        }

        public void commandAction(Command c, Displayable d) {
            if (c == backCmd) {
                display.setCurrent(mainMenuScreen);
            } else if (c == nextCmd) {
                String url = urlField.getString().trim();
                if (url.length() == 0) {
                    showAlert("Error", "Please enter a URL");
                    return;
                }

                if (!hasFileAccess) {
                    showAlert("Error", "File system not available");
                    return;
                }

                int platIndex = platformChoice.getSelectedIndex();
                if (platIndex == 0) {
                    pendingPlatform = "YouTube";
                } else if (platIndex == 1) {
                    pendingPlatform = "TikTok";
                } else if (platIndex == 2) {
                    pendingPlatform = "Facebook";
                } else if (platIndex == 3) {
                    pendingPlatform = "Instagram";
                } else {
                    pendingPlatform = "Direct";
                }

                if (pendingPlatform.equals("YouTube")) {
                    String videoId = extractYouTubeId(url);
                    if (videoId != null) {
                        pendingVideoId = videoId;
                        videoQualityScreen = new VideoQualityScreen();
                        display.setCurrent(videoQualityScreen);
                    } else {
                        showAlert("Error", "Invalid YouTube URL");
                    }
                } else {
                    pendingVideoId = url;
                    videoQualityScreen = new VideoQualityScreen();
                    display.setCurrent(videoQualityScreen);
                }
            }
        }
    }

    // ============================================
    // SELECTION QUALITE VIDEO
    // ============================================
    class VideoQualityScreen extends List implements CommandListener {
        private Command selectCmd, backCmd;

        VideoQualityScreen() {
            super("Choose Quality", List.IMPLICIT);

            append("144p (Low - Fastest)", null);
            append("240p (Medium)", null);
            append("360p (High - Recommended)", null);
            append("480p (HD)", null);

            selectCmd = new Command("Download", Command.OK, 0);
            backCmd = new Command("Back", Command.BACK, 1);
            addCommand(selectCmd);
            addCommand(backCmd);
            setCommandListener(this);
        }

        public void commandAction(Command c, Displayable d) {
            if (c == backCmd) {
                display.setCurrent(videoDownloadScreen);
            } else if (c == selectCmd || c == List.SELECT_COMMAND) {
                int sel = getSelectedIndex();
                String quality = "360";
                if (sel == 0) quality = "144";
                else if (sel == 1) quality = "240";
                else if (sel == 2) quality = "360";
                else if (sel == 3) quality = "480";

                startVideoDownload(pendingPlatform, pendingVideoId, quality);
            }
        }
    }

    // ============================================
    // TELECHARGEMENT AUDIO
    // ============================================
    class AudioDownloadScreen extends Form implements CommandListener {
        private TextField urlField;
        private ChoiceGroup platformChoice;
        private Command nextCmd, backCmd;

        AudioDownloadScreen() {
            super("Download Audio");

            urlField = new TextField("Paste URL:", "", 256, TextField.URL);
            append(urlField);

            platformChoice = new ChoiceGroup("Platform:", Choice.EXCLUSIVE);
            platformChoice.append("YouTube", null);
            platformChoice.append("TikTok", null);
            platformChoice.append("Facebook", null);
            platformChoice.append("Instagram", null);
            platformChoice.append("Direct MP3 Link", null);
            platformChoice.setSelectedIndex(0, true);
            append(platformChoice);

            append(new StringItem("Info",
                "Audio will be downloaded directly to Audio Library.\n" +
                "Perfect for music, podcasts, and audio content."));

            if (!hasFileAccess) {
                append(new StringItem("Warning",
                    "File system not available!"));
            }

            nextCmd = new Command("Choose Format", Command.OK, 0);
            backCmd = new Command("Back", Command.BACK, 1);
            addCommand(nextCmd);
            addCommand(backCmd);
            setCommandListener(this);
        }

        public void commandAction(Command c, Displayable d) {
            if (c == backCmd) {
                display.setCurrent(mainMenuScreen);
            } else if (c == nextCmd) {
                String url = urlField.getString().trim();
                if (url.length() == 0) {
                    showAlert("Error", "Please enter a URL");
                    return;
                }

                if (!hasFileAccess) {
                    showAlert("Error", "File system not available");
                    return;
                }

                int platIndex = platformChoice.getSelectedIndex();
                if (platIndex == 0) {
                    pendingPlatform = "YouTube";
                } else if (platIndex == 1) {
                    pendingPlatform = "TikTok";
                } else if (platIndex == 2) {
                    pendingPlatform = "Facebook";
                } else if (platIndex == 3) {
                    pendingPlatform = "Instagram";
                } else {
                    pendingPlatform = "Direct";
                }

                pendingAudioUrl = url;
                audioFormatScreen = new AudioFormatScreen();
                display.setCurrent(audioFormatScreen);
            }
        }
    }

    // ============================================
    // SELECTION FORMAT AUDIO
    // ============================================
    class AudioFormatScreen extends List implements CommandListener {
        private Command selectCmd, backCmd;

        AudioFormatScreen() {
            super("Audio Format", List.IMPLICIT);

            append("MP3 (Best - Recommended)", null);
            append("AAC (Good)", null);
            append("WAV (Uncompressed)", null);

            selectCmd = new Command("Download", Command.OK, 0);
            backCmd = new Command("Back", Command.BACK, 1);
            addCommand(selectCmd);
            addCommand(backCmd);
            setCommandListener(this);
        }

        public void commandAction(Command c, Displayable d) {
            if (c == backCmd) {
                display.setCurrent(audioDownloadScreen);
            } else if (c == selectCmd || c == List.SELECT_COMMAND) {
                int sel = getSelectedIndex();
                String format = "mp3";
                if (sel == 0) format = "mp3";
                else if (sel == 1) format = "aac";
                else if (sel == 2) format = "wav";

                startAudioDownload(pendingPlatform, pendingAudioUrl, format);
            }
        }
    }

    // ============================================
    // BIBLIOTHEQUE VIDEO
    // ============================================
    class VideoLibraryScreen extends List implements CommandListener {
        private Command playCmd, deleteCmd, backCmd;
        private Vector videoFiles = new Vector();

        VideoLibraryScreen() {
            super("Video Library", List.IMPLICIT);

            playCmd = new Command("Play", Command.OK, 0);
            deleteCmd = new Command("Delete", Command.ITEM, 1);
            backCmd = new Command("Back", Command.BACK, 2);

            loadVideos();

            addCommand(playCmd);
            addCommand(deleteCmd);
            addCommand(backCmd);
            setCommandListener(this);
        }

        private void loadVideos() {
            deleteAll();
            videoFiles.removeAllElements();

            if (!hasFileAccess) {
                append("File system not available", null);
                return;
            }

            try {
                FileConnection fc = (FileConnection) Connector.open(videosFolder);
                if (fc.exists() && fc.isDirectory()) {
                    Enumeration files = fc.list("*.3gp;*.mp4;*.avi", false);

                    while (files.hasMoreElements()) {
                        String name = (String) files.nextElement();
                        if (!name.startsWith(".")) {
                            append(name, null);
                            videoFiles.addElement(name);
                        }
                    }

                    if (videoFiles.size() == 0) {
                        append("No videos found", null);
                        append("Use Download Video", null);
                    }
                }
                fc.close();
            } catch (Exception e) {
                append("Error: " + e.getMessage(), null);
            }
        }

        public void commandAction(Command c, Displayable d) {
            if (c == backCmd) {
                display.setCurrent(mainMenuScreen);
            } else if ((c == playCmd || c == List.SELECT_COMMAND) && videoFiles.size() > 0) {
                int sel = getSelectedIndex();
                if (sel >= 0 && sel < videoFiles.size()) {
                    String fileName = (String) videoFiles.elementAt(sel);
                    playVideo(videosFolder + fileName, fileName);
                }
            } else if (c == deleteCmd && videoFiles.size() > 0) {
                int sel = getSelectedIndex();
                if (sel >= 0 && sel < videoFiles.size()) {
                    String fileName = (String) videoFiles.elementAt(sel);
                    confirmDelete(fileName, true);
                }
            }
        }
    }

    // ============================================
    // BIBLIOTHEQUE AUDIO
    // ============================================
    class AudioLibraryScreen extends List implements CommandListener {
        private Command playCmd, deleteCmd, backCmd;
        private Vector audioFiles = new Vector();

        AudioLibraryScreen() {
            super("Audio Library", List.IMPLICIT);

            playCmd = new Command("Play", Command.OK, 0);
            deleteCmd = new Command("Delete", Command.ITEM, 1);
            backCmd = new Command("Back", Command.BACK, 2);

            loadAudios();

            addCommand(playCmd);
            addCommand(deleteCmd);
            addCommand(backCmd);
            setCommandListener(this);
        }

        private void loadAudios() {
            deleteAll();
            audioFiles.removeAllElements();

            if (!hasFileAccess) {
                append("File system not available", null);
                return;
            }

            try {
                FileConnection fc = (FileConnection) Connector.open(audiosFolder);
                if (fc.exists() && fc.isDirectory()) {
                    Enumeration files = fc.list("*.mp3;*.wav;*.amr;*.aac", false);

                    while (files.hasMoreElements()) {
                        String name = (String) files.nextElement();
                        if (!name.startsWith(".")) {
                            append(name, null);
                            audioFiles.addElement(name);
                        }
                    }

                    if (audioFiles.size() == 0) {
                        append("No audio files", null);
                        append("Use Download Audio", null);
                    }
                }
                fc.close();
            } catch (Exception e) {
                append("Error: " + e.getMessage(), null);
            }
        }

        public void commandAction(Command c, Displayable d) {
            if (c == backCmd) {
                display.setCurrent(mainMenuScreen);
            } else if ((c == playCmd || c == List.SELECT_COMMAND) && audioFiles.size() > 0) {
                int sel = getSelectedIndex();
                if (sel >= 0 && sel < audioFiles.size()) {
                    currentPlaylist.removeAllElements();
                    for (int i = sel; i < audioFiles.size(); i++) {
                        currentPlaylist.addElement(audioFiles.elementAt(i));
                    }
                    currentTrackIndex = 0;
                    String fileName = (String) audioFiles.elementAt(sel);
                    playAudio(audiosFolder + fileName, fileName);
                }
            } else if (c == deleteCmd && audioFiles.size() > 0) {
                int sel = getSelectedIndex();
                if (sel >= 0 && sel < audioFiles.size()) {
                    String fileName = (String) audioFiles.elementAt(sel);
                    confirmDelete(fileName, false);
                }
            }
        }
    }

    // ============================================
    // CONVERTISSEUR
    // ============================================
    class ConverterScreen extends List implements CommandListener {
        private Command convertCmd, backCmd;
        private Vector videoFiles = new Vector();

        ConverterScreen() {
            super("Convert to Audio", List.IMPLICIT);

            append("Select video:", null);
            append(" ", null);

            convertCmd = new Command("Convert", Command.OK, 0);
            backCmd = new Command("Back", Command.BACK, 1);

            loadVideoFiles();

            addCommand(convertCmd);
            addCommand(backCmd);
            setCommandListener(this);
        }

        private void loadVideoFiles() {
            if (!hasFileAccess) {
                append("File system not available", null);
                return;
            }

            try {
                FileConnection fc = (FileConnection) Connector.open(videosFolder);
                if (fc.exists() && fc.isDirectory()) {
                    Enumeration files = fc.list("*.3gp;*.mp4;*.avi", false);

                    while (files.hasMoreElements()) {
                        String name = (String) files.nextElement();
                        if (!name.startsWith(".")) {
                            append(name, null);
                            videoFiles.addElement(name);
                        }
                    }

                    if (videoFiles.size() == 0) {
                        append("No videos", null);
                    }
                }
                fc.close();
            } catch (Exception e) {
                append("Error: " + e.getMessage(), null);
            }
        }

        public void commandAction(Command c, Displayable d) {
            if (c == backCmd) {
                display.setCurrent(mainMenuScreen);
            } else if ((c == convertCmd || c == List.SELECT_COMMAND) && videoFiles.size() > 0) {
                int sel = getSelectedIndex() - 2;
                if (sel >= 0 && sel < videoFiles.size()) {
                    String fileName = (String) videoFiles.elementAt(sel);
                    convertToAudio(fileName);
                }
            }
        }

        private void convertToAudio(final String videoFileName) {
            Alert converting = new Alert("Converting",
                "Extracting audio from:\n" + videoFileName,
                null, AlertType.INFO);
            converting.setTimeout(Alert.FOREVER);
            display.setCurrent(converting);

            new Thread(new Runnable() {
                public void run() {
                    try {
                        String videoPath = videosFolder + videoFileName;
                        int dotIndex = videoFileName.lastIndexOf('.');
                        String audioFileName = videoFileName.substring(0, dotIndex) + ".mp3";
                        String audioPath = audiosFolder + audioFileName;

                        extractAudio(videoPath, audioPath);

                        final String finalName = audioFileName;
                        display.callSerially(new Runnable() {
                            public void run() {
                                Alert success = new Alert("Success",
                                    "Saved: " + finalName,
                                    null, AlertType.CONFIRMATION);
                                success.setTimeout(3000);
                                display.setCurrent(success, converterScreen);
                            }
                        });
                    } catch (Exception e) {
                        final String msg = e.getMessage();
                        display.callSerially(new Runnable() {
                            public void run() {
                                showAlert("Error", msg);
                            }
                        });
                    }
                }
            }).start();
        }
    }

    // ============================================
    // PARAMETRES
    // ============================================
    class SettingsScreen extends Form implements CommandListener {
        private Gauge volumeGauge;
        private ChoiceGroup autoPlayChoice;
        private ChoiceGroup loopChoice;
        private Command saveCmd, backCmd;

        SettingsScreen() {
            super("Settings");

            volumeGauge = new Gauge("Volume:", true, 100, volume);
            append(volumeGauge);

            autoPlayChoice = new ChoiceGroup("Auto-play:", Choice.MULTIPLE);
            autoPlayChoice.append("Enable", null);
            autoPlayChoice.setSelectedIndex(0, autoPlay);
            append(autoPlayChoice);

            loopChoice = new ChoiceGroup("Loop playback:", Choice.MULTIPLE);
            loopChoice.append("Enable", null);
            loopChoice.setSelectedIndex(0, loopPlayback);
            append(loopChoice);

            append(new StringItem("Storage:",
                "Videos: " + videosFolder + "\n" +
                "Audios: " + audiosFolder));

            saveCmd = new Command("Save", Command.OK, 0);
            backCmd = new Command("Back", Command.BACK, 1);
            addCommand(saveCmd);
            addCommand(backCmd);
            setCommandListener(this);
        }

        public void commandAction(Command c, Displayable d) {
            if (c == backCmd) {
                display.setCurrent(mainMenuScreen);
            } else if (c == saveCmd) {
                volume = volumeGauge.getValue();
                autoPlay = autoPlayChoice.isSelected(0);
                loopPlayback = loopChoice.isSelected(0);

                Alert saved = new Alert("Settings", "Saved!",
                    null, AlertType.CONFIRMATION);
                saved.setTimeout(2000);
                display.setCurrent(saved, mainMenuScreen);
            }
        }
    }

    // ============================================
    // BARRE DE PROGRESSION TEMPS REEL (v3.3)
    // ============================================
    class RealTimeProgressScreen extends Canvas {
        private String title = "Initializing...";
        private String details = "";
        private long downloaded = 0;
        private long total = 0;
        private long startTime = 0;
        private boolean animating = true;
        private int animFrame = 0;
        private long lastUpdate = 0;
        private long lastBytes = 0;
        private long currentSpeed = 0;

        RealTimeProgressScreen() {
            startTime = System.currentTimeMillis();
            lastUpdate = startTime;
            lastBytes = 0;

            new Thread(new Runnable() {
                public void run() {
                    while (animating) {
                        try {
                            Thread.sleep(100);
                            animFrame = (animFrame + 1) % 12;
                            repaint();
                        } catch (Exception e) { break; }
                    }
                }
            }).start();
        }

        void updateProgress(String t, String d, long dl, long tot) {
            title = t;
            details = d;
            downloaded = dl;
            total = tot;

            long now = System.currentTimeMillis();
            if (now - lastUpdate >= 1000 && downloaded > lastBytes) {
                currentSpeed = (downloaded - lastBytes) / ((now - lastUpdate) / 1000);
                lastBytes = downloaded;
                lastUpdate = now;
            }
            repaint();
        }

        void stopAnimation() {
            animating = false;
        }

        protected void paint(Graphics g) {
            int w = getWidth();
            int h = getHeight();

            g.setColor(0x1A1A2E);
            g.fillRect(0, 0, w, h);

            g.setColor(0x0F3460);
            g.fillRect(0, 0, w, 40);
            g.setColor(0xFFFFFF);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
            g.drawString("Downloading", w/2, 12, Graphics.HCENTER | Graphics.TOP);

            int centerY = h / 2 - 40;
            g.setColor(0xE94560);
            int offset = (animFrame * 4) % 36;
            for (int i = 0; i < 3; i++) {
                int y = centerY - 30 + offset + (i * 24);
                if (y > centerY - 30 && y < centerY + 30) {
                    g.fillRect(w/2 - 2, y, 4, 20);
                    g.fillTriangle(w/2, y + 20, w/2 - 8, y + 12, w/2 + 8, y + 12);
                }
            }

            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));
            g.setColor(0x00FF88);
            g.drawString(title, w/2, centerY + 50, Graphics.HCENTER | Graphics.TOP);

            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM));
            g.setColor(0xAAAAAA);
            g.drawString(details, w/2, centerY + 75, Graphics.HCENTER | Graphics.TOP);

            if (total > 0) {
                int barW = w - 60;
                int barH = 24;
                int barX = 30;
                int barY = centerY + 110;
                long progress = (downloaded * 100) / total;
                int progressInt = (int)progress;

                g.setColor(0x16213E);
                g.fillRoundRect(barX, barY, barW, barH, 12, 12);

                int fillW = (int)((barW * progress) / 100);
                g.setColor(0xE94560);
                g.fillRoundRect(barX, barY, fillW, barH, 12, 12);

                g.setColor(0xFFFFFF);
                g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
                g.drawString(progressInt + "%", w/2, barY - 25, Graphics.HCENTER | Graphics.TOP);

                g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
                g.setColor(0x00FF88);
                g.drawString(formatBytes(downloaded), barX, barY + barH + 8, Graphics.LEFT | Graphics.TOP);
                g.setColor(0xAAAAAA);
                g.drawString(formatBytes(total), barX + barW - g.getFont().stringWidth(formatBytes(total)), barY + barH + 8, Graphics.RIGHT | Graphics.TOP);

                if (currentSpeed > 0) {
                    g.setColor(0x00CCAA);
                    g.drawString("Speed: " + formatSpeed(currentSpeed), w/2, barY + barH + 28, Graphics.HCENTER | Graphics.TOP);

                    long remaining = (total - downloaded) / currentSpeed;
                    g.setColor(0xFFCC00);
                    g.drawString("ETA: " + formatTime(remaining), w/2, barY + barH + 48, Graphics.HCENTER | Graphics.TOP);
                }
            }
        }
    }

    // ============================================
    // LECTEUR VIDEO VLC
    // ============================================
    class VLCPlayerScreen extends Canvas implements CommandListener {
        private String statusMsg = "Playing";
        private boolean showControls = true;
        private boolean running = true;
        private Command backCmd;
        private int volumeLevel = 75;

        VLCPlayerScreen() {
            setFullScreenMode(false);

            backCmd = new Command("Back", Command.BACK, 0);
            addCommand(backCmd);
            setCommandListener(this);

            new Thread(new Runnable() {
                public void run() {
                    while (running) {
                        try {
                            Thread.sleep(200);
                            if (isShown()) repaint();
                        } catch (Exception e) { break; }
                    }
                }
            }).start();
        }

        protected void paint(Graphics g) {
            int w = getWidth();
            int h = getHeight();

            g.setColor(0x000000);
            g.fillRect(0, 0, w, h);

            if (showControls) {
                int controlY = h - 70;
                g.setColor(0x2C2C2C);
                g.fillRect(0, controlY, w, 70);

                g.setColor(0xFF8800);
                g.fillRect(0, controlY, w, 3);

                g.setColor(0xFFFFFF);
                g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
                String shortName = currentFileName.length() > 25 ?
                    currentFileName.substring(0, 22) + "..." : currentFileName;
                g.drawString(shortName, 10, controlY + 10, Graphics.LEFT | Graphics.TOP);
                g.drawString(statusMsg, 10, controlY + 28, Graphics.LEFT | Graphics.TOP);
                g.drawString("5:Play/Pause  0:Back  7:Hide", 10, controlY + 46, Graphics.LEFT | Graphics.TOP);
                g.drawString("Vol: " + volumeLevel + "%", w - 10, controlY + 46, Graphics.RIGHT | Graphics.TOP);
            }
        }

        protected void keyPressed(int keyCode) {
            if (keyCode == Canvas.KEY_NUM5) {
                togglePlayPause();
            } else if (keyCode == Canvas.KEY_NUM0) {
                stopAndExit();
            } else if (keyCode == Canvas.KEY_NUM7) {
                showControls = !showControls;
                repaint();
            } else if (keyCode == Canvas.KEY_NUM4 && volumeControl != null) {
                volumeLevel = volumeLevel > 10 ? volumeLevel - 10 : 0;
                try { volumeControl.setLevel(volumeLevel); } catch (Exception e) {}
                repaint();
            } else if (keyCode == Canvas.KEY_NUM6 && volumeControl != null) {
                volumeLevel = volumeLevel < 90 ? volumeLevel + 10 : 100;
                try { volumeControl.setLevel(volumeLevel); } catch (Exception e) {}
                repaint();
            }
        }

        private void togglePlayPause() {
            if (currentPlayer == null) return;
            try {
                if (isPlaying) {
                    currentPlayer.stop();
                    isPlaying = false;
                    isPaused = true;
                    statusMsg = "Paused";
                } else {
                    currentPlayer.start();
                    isPlaying = true;
                    isPaused = false;
                    statusMsg = "Playing";
                }
            } catch (Exception e) {}
        }

        private void stopAndExit() {
            running = false;
            stopPlayer();
            display.setCurrent(videoLibraryScreen);
        }

        public void commandAction(Command c, Displayable d) {
            if (c == backCmd) stopAndExit();
        }
    }

    // ============================================
    // LECTEUR AUDIO
    // ============================================
    class AudioPlayerScreen extends Canvas implements CommandListener {
        private String statusMsg = "Playing";
        private boolean running = true;
        private Command backCmd, prevCmd, nextCmd;
        private int volumeLevel = 75;
        private int[] waveform = new int[20];
        private int wavePhase = 0;

        AudioPlayerScreen() {
            backCmd = new Command("Back", Command.BACK, 0);
            addCommand(backCmd);

            if (currentPlaylist.size() > 1) {
                prevCmd = new Command("Prev", Command.ITEM, 1);
                nextCmd = new Command("Next", Command.ITEM, 2);
                addCommand(prevCmd);
                addCommand(nextCmd);
            }

            setCommandListener(this);

            new Thread(new Runnable() {
                public void run() {
                    while (running) {
                        try {
                            Thread.sleep(100);
                            if (isPlaying) {
                                wavePhase = (wavePhase + 1) % 360;
                                for (int i = 0; i < waveform.length; i++) {
                                    double angle = (wavePhase + i * 18) * 3.14159 / 180.0;
                                    waveform[i] = (int)(Math.sin(angle) * 15.0 + 15.0);
                                }
                            }
                            if (isShown()) repaint();
                        } catch (Exception e) { break; }
                    }
                }
            }).start();
        }

        protected void paint(Graphics g) {
            int w = getWidth();
            int h = getHeight();

            for (int i = 0; i < 20; i++) {
                int r = 26 + i;
                int gb = 33 + i * 2;
                int color = (r << 16) | (gb << 8) | gb;
                g.setColor(color);
                g.fillRect(0, i * h / 20, w, h / 20 + 1);
            }

            int artSize = 100;
            int artX = w / 2 - artSize / 2;
            g.setColor(0x0F3460);
            g.fillRoundRect(artX, 30, artSize, artSize, 15, 15);
            g.setColor(0xE94560);
            g.fillArc(artX + 30, 60, 40, 40, 0, 360);

            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
            g.setColor(0xFFFFFF);
            String shortName = currentFileName.length() > 20 ?
                currentFileName.substring(0, 17) + "..." : currentFileName;
            g.drawString(shortName, w/2, 145, Graphics.HCENTER | Graphics.TOP);

            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            g.setColor(0xCCCCCC);
            g.drawString(statusMsg, w/2, 165, Graphics.HCENTER | Graphics.TOP);

            if (currentPlaylist.size() > 0) {
                g.drawString("Track " + (currentTrackIndex + 1) + " of " + currentPlaylist.size(),
                    w/2, 180, Graphics.HCENTER | Graphics.TOP);
            }

            int barW = 8, spacing = 4;
            int totalW = (barW + spacing) * waveform.length - spacing;
            int startX = (w - totalW) / 2;
            for (int i = 0; i < waveform.length; i++) {
                int barH = waveform[i];
                int x = startX + i * (barW + spacing);
                g.setColor(0xE94560);
                g.fillRect(x, 230 - barH, barW, barH);
            }

            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            g.setColor(0xCCCCCC);
            g.drawString("1:Prev  5:Play/Pause  3:Next  0:Back", w/2, h - 20, Graphics.HCENTER | Graphics.TOP);
            g.drawString("Vol: " + volumeLevel + "%", w - 10, h - 20, Graphics.RIGHT | Graphics.TOP);
        }

        protected void keyPressed(int keyCode) {
            if (keyCode == Canvas.KEY_NUM5) {
                togglePlayPause();
            } else if (keyCode == Canvas.KEY_NUM1) {
                playPreviousTrack();
            } else if (keyCode == Canvas.KEY_NUM3) {
                playNextTrack();
            } else if (keyCode == Canvas.KEY_NUM4 && volumeControl != null) {
                volumeLevel = volumeLevel > 10 ? volumeLevel - 10 : 0;
                try { volumeControl.setLevel(volumeLevel); } catch (Exception e) {}
            } else if (keyCode == Canvas.KEY_NUM6 && volumeControl != null) {
                volumeLevel = volumeLevel < 90 ? volumeLevel + 10 : 100;
                try { volumeControl.setLevel(volumeLevel); } catch (Exception e) {}
            } else if (keyCode == Canvas.KEY_NUM0) {
                running = false;
                stopPlayer();
                display.setCurrent(audioLibraryScreen);
            }
        }

        private void togglePlayPause() {
            if (currentPlayer == null) return;
            try {
                if (isPlaying) {
                    currentPlayer.stop();
                    isPlaying = false;
                    isPaused = true;
                    statusMsg = "Paused";
                } else {
                    currentPlayer.start();
                    isPlaying = true;
                    isPaused = false;
                    statusMsg = "Playing";
                }
            } catch (Exception e) {}
        }

        public void commandAction(Command c, Displayable d) {
            if (c == backCmd) {
                running = false;
                stopPlayer();
                display.setCurrent(audioLibraryScreen);
            } else if (c == prevCmd) {
                playPreviousTrack();
            } else if (c == nextCmd) {
                playNextTrack();
            }
        }
    }

    // ============================================
    // METHODES DE LECTURE
    // ============================================
    private void playVideo(final String path, final String name) {
        stopPlayer();
        currentFilePath = path;
        currentFileName = name;
        isVideoMode = true;
        new Thread(new Runnable() {
            public void run() {
                try {
                    String loc = currentFilePath.startsWith("file://") ?
                        currentFilePath : "file:///" + currentFilePath;

                    currentPlayer = Manager.createPlayer(loc);
                    currentPlayer.addPlayerListener(MediaPlayerPlusMIDlet.this);
                    currentPlayer.prefetch();

                    videoControl = (VideoControl) currentPlayer.getControl("VideoControl");
                    volumeControl = (VolumeControl) currentPlayer.getControl("VolumeControl");

                    if (volumeControl != null) volumeControl.setLevel(volume);

                    display.callSerially(new Runnable() {
                        public void run() {
                            if (videoControl != null) {
                                videoPlayerScreen = new VLCPlayerScreen();
                                try {
                                    videoControl.initDisplayMode(VideoControl.USE_DIRECT_VIDEO, videoPlayerScreen);
                                    videoControl.setDisplayLocation(2, 2);
                                    videoControl.setDisplaySize(
                                        videoPlayerScreen.getWidth() - 4,
                                        videoPlayerScreen.getHeight() - 74
                                    );
                                    videoControl.setVisible(true);
                                    display.setCurrent(videoPlayerScreen);
                                    currentPlayer.start();
                                    isPlaying = true;
                                } catch (Exception e) {
                                    showAlert("Error", "Display failed");
                                }
                            } else {
                                showAlert("Audio Only", "Video not supported");
                                try { currentPlayer.start(); } catch (Exception e) {}
                            }
                        }
                    });
                } catch (Exception e) {
                    final String msg = e.getMessage();
                    display.callSerially(new Runnable() {
                        public void run() {
                            showAlert("Error", msg);
                        }
                    });
                }
            }
        }).start();
    }

    private void playAudio(final String path, final String name) {
        stopPlayer();
        currentFilePath = path;
        currentFileName = name;
        isVideoMode = false;
        new Thread(new Runnable() {
            public void run() {
                try {
                    String loc = currentFilePath.startsWith("file://") ?
                        currentFilePath : "file:///" + currentFilePath;

                    currentPlayer = Manager.createPlayer(loc);
                    currentPlayer.addPlayerListener(MediaPlayerPlusMIDlet.this);
                    currentPlayer.realize();
                    currentPlayer.prefetch();

                    volumeControl = (VolumeControl) currentPlayer.getControl("VolumeControl");
                    if (volumeControl != null) volumeControl.setLevel(volume);

                    display.callSerially(new Runnable() {
                        public void run() {
                            audioPlayerScreen = new AudioPlayerScreen();
                            display.setCurrent(audioPlayerScreen);
                            try {
                                currentPlayer.start();
                                isPlaying = true;
                            } catch (Exception e) {
                                showAlert("Error", "Play failed");
                            }
                        }
                    });
                } catch (Exception e) {
                    final String msg = e.getMessage();
                    display.callSerially(new Runnable() {
                        public void run() {
                            showAlert("Error", msg);
                        }
                    });
                }
            }
        }).start();
    }

    private void playCurrentMedia() {
        if (isVideoMode) {
            playVideo(currentFilePath, currentFileName);
        } else {
            playAudio(currentFilePath, currentFileName);
        }
    }

    // ============================================
    // TELECHARGEMENT VIDEO
    // ============================================
    private void startVideoDownload(final String platform, final String videoId, final String quality) {
        progressScreen = new RealTimeProgressScreen();
        display.setCurrent(progressScreen);
        new Thread(new Runnable() {
            public void run() {
                downloadVideoFile(platform, videoId, quality);
            }
        }).start();
    }

    private void downloadVideoFile(String platform, String videoId, String quality) {
        HttpConnection conn = null;
        InputStream is = null;
        FileConnection fc = null;
        OutputStream os = null;
        try {
            progressScreen.updateProgress("Connecting...", platform + " video", 0, 0);

            String downloadUrl = videoId;
            if (platform.equals("YouTube")) {
                downloadUrl = "http://williamsmobile.co.uk/yt.php?url=https%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3D" +
                    videoId + "&format=" + quality;
            }

            conn = (HttpConnection) Connector.open(downloadUrl);
            conn.setRequestMethod("GET");

            int code = conn.getResponseCode();
            if (code != HttpConnection.HTTP_OK) {
                throw new IOException("HTTP Error: " + code);
            }

            long length = conn.getLength();
            progressScreen.updateProgress("Downloading...", "Saving", 0, length);

            long timestamp = System.currentTimeMillis();
            String fileName = platform + "_" + timestamp + ".mp4";
            String filePath = videosFolder + fileName;

            fc = (FileConnection) Connector.open(filePath, Connector.READ_WRITE);
            if (fc.exists()) fc.delete();
            fc.create();

            os = fc.openOutputStream();
            is = conn.openInputStream();

            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalRead = 0;

            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
                progressScreen.updateProgress("Downloading...", "Saving video", totalRead, length);
                if (totalRead % (buffer.length * 10) == 0) {
                    Thread.yield();
                }
            }

            os.flush();
            long fileSize = fc.fileSize();

            progressScreen.stopAnimation();
            progressScreen.updateProgress("Complete!", formatBytes(fileSize), totalRead, length);
            Thread.sleep(2000);

            final String finalPath = filePath;
            final String finalName = fileName;

            display.callSerially(new Runnable() {
                public void run() {
                    if (autoPlay) {
                        playVideo(finalPath, finalName);
                    } else {
                        showAlert("Success", "Saved: " + finalName);
                    }
                }
            });

        } catch (Exception e) {
            progressScreen.stopAnimation();
            final String msg = e.getMessage();
            display.callSerially(new Runnable() {
                public void run() {
                    showAlert("Error", msg);
                }
            });
        } finally {
            try {
                if (os != null) os.close();
                if (is != null) is.close();
                if (fc != null) fc.close();
                if (conn != null) conn.close();
            } catch (Exception e) {}
        }
    }

    // ============================================
    // TELECHARGEMENT AUDIO
    // ============================================
    private void startAudioDownload(final String platform, final String url, final String format) {
        progressScreen = new RealTimeProgressScreen();
        display.setCurrent(progressScreen);
        new Thread(new Runnable() {
            public void run() {
                downloadAudioFile(platform, url, format);
            }
        }).start();
    }

    private void downloadAudioFile(String platform, String url, String format) {
        HttpConnection conn = null;
        InputStream is = null;
        FileConnection fc = null;
        OutputStream os = null;
        try {
            progressScreen.updateProgress("Connecting...", platform + " audio", 0, 0);

            String downloadUrl = url;
            if (platform.equals("YouTube")) {
                String videoId = extractYouTubeId(url);
                downloadUrl = "http://williamsmobile.co.uk/yt.php?url=https%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3D" +
                    videoId + "&format=mp3";
            }

            conn = (HttpConnection) Connector.open(downloadUrl);
            conn.setRequestMethod("GET");

            int code = conn.getResponseCode();
            if (code != HttpConnection.HTTP_OK) {
                throw new IOException("HTTP Error: " + code);
            }

            long length = conn.getLength();
            progressScreen.updateProgress("Downloading...", "Saving", 0, length);

            long timestamp = System.currentTimeMillis();
            String fileName = platform + "_" + timestamp + "." + format;
            String filePath = audiosFolder + fileName;

            fc = (FileConnection) Connector.open(filePath, Connector.READ_WRITE);
            if (fc.exists()) fc.delete();
            fc.create();

            os = fc.openOutputStream();
            is = conn.openInputStream();

            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalRead = 0;

            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
                progressScreen.updateProgress("Downloading...", "Saving audio", totalRead, length);
                if (totalRead % (buffer.length * 10) == 0) {
                    Thread.yield();
                }
            }

            os.flush();
            long fileSize = fc.fileSize();

            progressScreen.stopAnimation();
            progressScreen.updateProgress("Complete!", formatBytes(fileSize), totalRead, length);
            Thread.sleep(2000);

            final String finalPath = filePath;
            final String finalName = fileName;

            display.callSerially(new Runnable() {
                public void run() {
                    if (autoPlay) {
                        currentPlaylist.removeAllElements();
                        currentPlaylist.addElement(finalName);
                        currentTrackIndex = 0;
                        playAudio(finalPath, finalName);
                    } else {
                        showAlert("Success", "Saved: " + finalName);
                    }
                }
            });

        } catch (Exception e) {
            progressScreen.stopAnimation();
            final String msg = e.getMessage();
            display.callSerially(new Runnable() {
                public void run() {
                    showAlert("Error", msg);
                }
            });
        } finally {
            try {
                if (os != null) os.close();
                if (is != null) is.close();
                if (fc != null) fc.close();
                if (conn != null) conn.close();
            } catch (Exception e) {}
        }
    }

    // ============================================
    // UTILITAIRES
    // ============================================
    private void extractAudio(String videoPath, String audioPath) throws Exception {
        FileConnection videoFc = (FileConnection) Connector.open(videoPath);
        FileConnection audioFc = (FileConnection) Connector.open(audioPath);
        if (!videoFc.exists()) {
            videoFc.close();
            throw new Exception("Video not found");
        }

        if (audioFc.exists()) audioFc.delete();
        audioFc.create();

        InputStream is = videoFc.openInputStream();
        OutputStream os = audioFc.openOutputStream();

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }

        os.flush();
        os.close();
        is.close();
        audioFc.close();
        videoFc.close();
    }

    private void confirmDelete(final String fileName, final boolean isVideo) {
        Alert confirm = new Alert("Confirm",
            "Delete " + fileName + "?",
            null, AlertType.CONFIRMATION);
        confirm.setTimeout(Alert.FOREVER);
        Command yesCmd = new Command("Yes", Command.OK, 0);
        Command noCmd = new Command("No", Command.CANCEL, 1);
        confirm.addCommand(yesCmd);
        confirm.addCommand(noCmd);

        confirm.setCommandListener(new CommandListener() {
            public void commandAction(Command c, Displayable d) {
                if (c.getCommandType() == Command.OK) {
                    try {
                        String folder = isVideo ? videosFolder : audiosFolder;
                        FileConnection fc = (FileConnection) Connector.open(folder + fileName);
                        if (fc.exists()) fc.delete();
                        fc.close();
                        showAlert("Success", "Deleted");
                    } catch (Exception e) {
                        showAlert("Error", "Cannot delete");
                    }
                }

                if (isVideo) {
                    videoLibraryScreen = new VideoLibraryScreen();
                    display.setCurrent(videoLibraryScreen);
                } else {
                    audioLibraryScreen = new AudioLibraryScreen();
                    display.setCurrent(audioLibraryScreen);
                }
            }
        });

        display.setCurrent(confirm);
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(title, msg, null, AlertType.INFO);
        alert.setTimeout(Alert.FOREVER);
        display.setCurrent(alert, mainMenuScreen);
    }

    private String extractYouTubeId(String url) {
        int vPos = url.indexOf("v=");
        if (vPos != -1) {
            vPos += 2;
            int ampPos = url.indexOf("&", vPos);
            return ampPos != -1 ? url.substring(vPos, ampPos) : url.substring(vPos);
        }
        int slashPos = url.indexOf("://");
        if (slashPos != -1) {
            int pathStart = url.indexOf("/", slashPos + 3);
            if (pathStart != -1) {
                String path = url.substring(pathStart + 1);
                int qPos = path.indexOf("?");
                return qPos != -1 ? path.substring(0, qPos) : path;
            }
        }

        return null;
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        return (bytes / (1024 * 1024)) + " MB";
    }

    private String formatSpeed(long bytesPerSecond) {
        if (bytesPerSecond < 1024) {
            return bytesPerSecond + " B/s";
        } else if (bytesPerSecond < 1024 * 1024) {
            return (bytesPerSecond / 1024) + " KB/s";
        } else {
            return (bytesPerSecond / (1024 * 1024)) + " MB/s";
        }
    }

    private String formatTime(long seconds) {
        if (seconds < 0) return "...";
        long mins = seconds / 60;
        long secs = seconds % 60;
        String minStr = mins < 10 ? "0" + mins : String.valueOf(mins);
        String secStr = secs < 10 ? "0" + secs : String.valueOf(secs);
        return minStr + ":" + secStr;
    }
}