package screens;

import Helper.TerminalHelper;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;
import com.heroiclabs.nakama.api.Group;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainClass implements Runnable, KeyListener {
    private Terminal terminal;

    private TerminalHelper terminalHelper;

    public Screen screen;

    public static MainClass aClass;

    public Session session;

    public Client client;

    public SocketClient socket;
    public List<Group> groups = new ArrayList<>();
    public Match match;
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
    public static void main(String[] args) {
        aClass = new MainClass();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Group group : MainClass.aClass.groups) {
                MainClass.aClass.client.deleteGroup(MainClass.aClass.session, group.getId());
            }
        }, "Shutdown-thread"));
    }

    @Override
    public void run() {
        KeyStroke key;
        try {
            while (running) {
                key = terminal.readInput();
                if (key == null)
                    return;
                System.out.println(key);
                screen = screen.respondToUserInput(key, terminalHelper);
                System.out.println("help");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {

    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }
}