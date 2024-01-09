package screens;

import Helper.ClasspathResourceLoader;
import Helper.OsUtil;
import asciiPanel.AsciiFont;
import com.heroiclabs.nakama.*;
import communication.MatchSendHelper;
import config.keys.KeyMenuConfig;
import config.keys.KeyPlay;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.IOUtil;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
public class MainClass extends JFrame implements KeyListener {
    public static MainClass aClass;

    public Screen screen;
    public Session session;
    public Client client;
    public SocketClient socket;
    public String group_id = "";
    public String user_id = "";
    public Match match;
    public boolean createdGroup;
    private AsciiPanel terminal;



    public MainClass() {
        super();
        terminal = new AsciiPanel(70, 50, new AsciiFont("custom_cp437_20x20.png", 20, 20));
        screen = new PlayOfflineScreen(terminal);
        add(terminal);
        pack();
        repaint();
        addKeyListener(this);
        ScheduledExecutorService repaint = Executors.newSingleThreadScheduledExecutor();
        repaint.scheduleAtFixedRate(this::repaint, 0, 10, TimeUnit.MILLISECONDS);
        terminal.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                terminal.resize(terminal.getWidth(), terminal.getHeight());
            }
        });
    }

    public static void main(String[] args) {
        MainClass mainClass = new MainClass();
        mainClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainClass.setVisible(true);
        createConfig();
        KeyMenuConfig.initializeKeymap();
        KeyPlay.initializeKeymap();
        aClass = mainClass;
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


    public void repaint() {
        SwingUtilities.invokeLater(() -> {
            screen.displayOutput(terminal);
            super.repaint();
        });
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

    @Override
    public void keyPressed(KeyEvent key) {
        if (!(screen instanceof PlayOfflineScreen || screen instanceof PlayOnlineScreen)) {
            if (screen.isInsideInputField()) {
                screen = screen.respondToUserInput(key, terminal);
            } else {
                screen = KeyMenuConfig.execute(key, screen, terminal);
            }
        } else if (screen instanceof PlayOfflineScreen){
            System.out.println(key);
            ((PlayOfflineScreen) screen).addKey(key);
        } else {
            ((PlayOnlineScreen) screen).addKey(key);
        }
    }

    @Override
    public void keyReleased(KeyEvent key) {
        if (screen instanceof PlayOfflineScreen ) {
            ((PlayOfflineScreen) screen).removeKey(key);
        } else if (screen instanceof PlayOnlineScreen) {
            ((PlayOnlineScreen) screen).removeKey(key);
        }
    }

    @Override
    public void keyTyped(KeyEvent key) {

    }

}