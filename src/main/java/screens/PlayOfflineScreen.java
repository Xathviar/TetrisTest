package screens;

import helper.TerminalHelper;
import config.keys.KeyPlay;
import logic.TetrisField;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayOfflineScreen implements Screen, Runnable {

    private final TetrisField field;
    private final long startTime;
    public boolean initScreen = true;
    public boolean loseScreen = false;

    private final Set<KeyPlay> pressedKeys = new HashSet<>();

    public PlayOfflineScreen(AsciiPanel terminal) {
        field = new TetrisField(1, this, (terminal.getWidthInCharacters() - 12) / 2, 16, false);
        startTime = System.currentTimeMillis();
        ScheduledExecutorService repaint = Executors.newSingleThreadScheduledExecutor();
        repaint.scheduleAtFixedRate(this, 0, 1, TimeUnit.MILLISECONDS);

    }


    @Override
    public void displayOutput(AsciiPanel terminal) {
        terminal.clear();
        TerminalHelper.writeTetrisLogo(terminal);
        initScreen = false;
        field.printTetrisField(terminal);
    }

    @Override
    public Screen respondToUserInput(KeyEvent key, AsciiPanel terminal) {
        if (loseScreen) {
            MainClass.aClass.screen = new LoseScreen(field.getLevel(), field.getScore(), System.currentTimeMillis() - startTime);
            field.shutdownThread();
            MainClass.aClass.repaint();
            return MainClass.aClass.screen;
        }
        return this;
    }

    @Override
    public boolean finishInput() {
        return false;
    }


    public void addKey(KeyEvent keyEvent) {
        if (KeyPlay.getKey(keyEvent) != null)
            pressedKeys.add(KeyPlay.getKey(keyEvent));
    }

    public void removeKey(KeyEvent keyEvent) {
        if (KeyPlay.getKey(keyEvent) != null)
            pressedKeys.remove(KeyPlay.getKey(keyEvent));
    }

    @Override
    public void run() {
        for (KeyPlay pressedKey : pressedKeys) {
            pressedKey.execute(field);
            pressedKey.incrementCounter();
        }
    }
}
