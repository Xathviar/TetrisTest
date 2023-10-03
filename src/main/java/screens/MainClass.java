package screens;

import Helper.TerminalHelper;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MainClass implements Runnable {
    public static MainClass aClass;

    public Screen screen;
    public Session session;
    public Client client;
    public SocketClient socket;
    public String group_id = "";
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
        aClass = new MainClass();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (MainClass.aClass.createdGroup) {
                MainClass.aClass.client.deleteGroup(MainClass.aClass.session, aClass.group_id);
            } else {
                MainClass.aClass.client.leaveGroup(MainClass.aClass.session, aClass.group_id);
            }
        }, "Delete all groups"));
    }

    private void setupTerminal() {
        Font font = null;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File("/home/af/TetrisTest/src/main/resources/MxPlus_Rainbow100_re_40.ttf"));
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
        font = font.deriveFont(20f);
        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        defaultTerminalFactory.setTerminalEmulatorTitle("Terminal Tetris");
        defaultTerminalFactory.setInitialTerminalSize(new TerminalSize(80, 25));
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
                System.out.println(key);
                screen = screen.respondToUserInput(key, terminalHelper);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}