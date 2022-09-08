import asciiPanel.AsciiFont;
import asciiPanel.AsciiPanel;
import screens.Screen;
import screens.StartScreen;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainClass extends JFrame implements KeyListener {
    private final AsciiPanel terminal;

    private Screen screen;

    public MainClass() {
        super();
        terminal = new AsciiPanel(70, 80, new AsciiFont("custom_cp437_20x20.png", 20, 20));
        terminal.write("humble Beginning");
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
    }


    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }


    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }


    public static void main(String[] args) {
        MainClass mainClass = new MainClass();
        mainClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainClass.setVisible(true);
    }
}