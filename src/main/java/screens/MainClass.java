package screens;

import Helper.ClasspathResourceLoader;
import Helper.OsUtil;
import Helper.TerminalHelper;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.heroiclabs.nakama.*;
import communication.MatchSendHelper;
import config.KeyConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.IOUtil;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


//TODO fix issue that skill issue player has to restart the game
//TODO fix issue that losing Player doesn't display the game correctly
//TODO go back the menu...
//TODO Add Config so that player don't have to input everything all the time
@Slf4j
public class MainClass implements Runnable {
    public static MainClass aClass;

    public Screen screen;
    public Session session;
    public Client client;
    public SocketClient socket;
    public String group_id = "";
    public String user_id = "";
    public Match match;
    public boolean createdGroup;
    private Terminal terminal;
    private TerminalHelper terminalHelper;
    private boolean running;


    public MainClass() {
        setupTerminal();
        screen = new StartScreen();
        repaint();
        ScheduledExecutorService repaint = Executors.newSingleThreadScheduledExecutor();
        repaint.scheduleAtFixedRate(this::repaint, 0, 50, TimeUnit.MILLISECONDS);
        Thread t = new Thread(this);
        running = true;
        t.start();
    }

    public static void main(String[] args) {
        createConfig();
        KeyConfig.initializeKeymap();
        aClass = new MainClass();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (MainClass.aClass.createdGroup) {
                MainClass.aClass.client.deleteGroup(MainClass.aClass.session, aClass.group_id);
            } else {
                MainClass.aClass.client.leaveGroup(MainClass.aClass.session, aClass.group_id);
            }
        }, "Delete all groups"));
    }

    @SneakyThrows
    private static void createConfig() {
        File configFile = OsUtil.getConfigFile("tty-tetris.conf");
        if (configFile.exists())
            return;
        InputStream defaultConfig = ClasspathResourceLoader.of("tty-tetris.conf").getInputStream();
        IOUtil.copy(defaultConfig, Files.newOutputStream(configFile.toPath()));
    }

    private void setupTerminal() {
        Font font;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, Thread.currentThread().getContextClassLoader().getResourceAsStream("MxPlus_Rainbow100_re_40.ttf"));
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
        font = font.deriveFont(20f);
        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        defaultTerminalFactory.setTerminalEmulatorTitle("Terminal Tetris");
//        defaultTerminalFactory.setInitialTerminalSize(new TerminalSize(120, 80));
        defaultTerminalFactory.setPreferTerminalEmulator(true);
        SwingTerminalFontConfiguration config = SwingTerminalFontConfiguration.newInstance(font);

        defaultTerminalFactory.setTerminalEmulatorFontConfiguration(config);
        try {
            terminal = defaultTerminalFactory.createTerminal();
            terminal.setCursorVisible(false);
            terminalHelper = new TerminalHelper(terminal);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void repaint() {
        SwingUtilities.invokeLater(() -> {
            screen.displayOutput(terminalHelper);
            terminalHelper.flush();
        });
    }

    @Override
    public void run() {
        KeyStroke key;
        try {
            while (running) {
                key = terminal.readInput();
                if (key == null)
                    return;
                if (key.getKeyType() == KeyType.EOF)
                    System.exit(0);
                if (screen.isInsideInputField()) {
                    screen = screen.respondToUserInput(key, terminalHelper);
                } else {
                    screen = KeyConfig.execute(key, screen, terminalHelper);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createSocket() {
        try {
            this.socket = this.client.createSocket();
            SocketListener listener = new AbstractSocketListener() {
                @Override
                public void onDisconnect(final Throwable t) {
                    log.debug("Socket disconnected.");
                }
                @Override
                public void onMatchData(MatchData matchData) {
                    if (matchData.getData() != null) {
                        MatchSendHelper.receiveUpdate((int) matchData.getOpCode(), new String(matchData.getData()));
                    } else {
                        MatchSendHelper.receiveUpdate((int) matchData.getOpCode(), null);
                    }

                }
            };
            this.socket.connect(MainClass.aClass.session, listener).get();
            log.debug("Socket connected.");
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

}