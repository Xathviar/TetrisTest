package screens;

import asciiPanel.AsciiFont;
import asciiPanel.AsciiPanel;
import logic.Key;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainClass extends JFrame implements KeyListener {
    private final AsciiPanel terminal;

    public Screen screen;

    public static final Set<Key> pressedKeys = new HashSet<>();

    public static MainClass aClass;


    public MainClass() {
        super();
        terminal = new AsciiPanel(70, 50, new AsciiFont("custom_cp437_20x20.png", 20, 20));
        add(terminal);
        pack();
        screen = new StartScreen();
        addKeyListener(this);
        repaint();
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(() -> {
            repaint();
        }, 0, 50, TimeUnit.MILLISECONDS);
    }

    public void repaint() {
        SwingUtilities.invokeLater(() -> {
            screen.displayOutput(terminal);
            super.repaint();
        });
    }

    public void keyPressed(KeyEvent e) {
        screen = screen.respondToUserInput(e, terminal);
        synchronized (MainClass.pressedKeys) {
            if (screen instanceof PlayScreen) {
                Key keyToAdd = Key.getEnumFromKeyCode(e.getKeyCode());
                MainClass.pressedKeys.add(keyToAdd);
            }
        }
    }


    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }


    @Override
    public void keyReleased(KeyEvent keyEvent) {
        synchronized (MainClass.pressedKeys) {
            Key pressedKey = Key.getEnumFromKeyCode(keyEvent.getKeyCode());
            if (pressedKey != null) {
                pressedKey.resetKey();
                MainClass.pressedKeys.remove(pressedKey);
            }
        }
    }


    public static void main(String[] args) {
        MainClass mainClass = new MainClass();
        mainClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainClass.setVisible(true);
        aClass = mainClass;
    }
}