package screens;

import config.Constants;
import helper.TerminalHelper;

import communication.MatchSendHelper;
import config.keys.KeyPlay;
import logic.OpponentTetrisField;
import logic.TetrisField;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class PlayOnlineScreen implements Screen, Runnable {

    private static TetrisField field;
    private final long startTime;
    public boolean loseScreen = false;
    public static boolean win = false;

    public static OpponentTetrisField opponentTetrisField;

    private final ScheduledExecutorService exec;

    private final boolean isHost;

    private final Set<KeyPlay> pressedKeys = new HashSet<>();

    public PlayOnlineScreen(AsciiPanel terminal, boolean isHost) {
        field = new TetrisField(1, this, (terminal.getWidthInCharacters() - 12) / 2, 16);
        startTime = System.currentTimeMillis();
        opponentTetrisField = new OpponentTetrisField((terminal.getWidthInCharacters()) / 2 + 30, 16);
        exec = Executors.newSingleThreadScheduledExecutor();
        this.isHost = isHost;
        if (isHost) {
            exec.scheduleAtFixedRate(PlayOnlineScreen::tickMaster, 0, 1, TimeUnit.SECONDS);
        }
        ScheduledExecutorService repaint = Executors.newSingleThreadScheduledExecutor();
        repaint.scheduleAtFixedRate(this, 0, Constants.KEYLISTENERTIMER, TimeUnit.MILLISECONDS);

    }

    public static void gameTick() {
        field.gameTick();
    }

    public static void tickMaster() {
        gameTick();
        MatchSendHelper.GAMETICK.sendUpdate();
    }

    public void exitGroupMatch() {
        try {
            MainClass.aClass.socket.leaveMatch(MainClass.aClass.match.getMatchId()).get();
            if (MainClass.aClass.createdGroup) {
                MainClass.aClass.client.deleteGroup(MainClass.aClass.session, MainClass.aClass.group_id);
            } else {
                MainClass.aClass.client.leaveGroup(MainClass.aClass.session, MainClass.aClass.group_id);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void displayOutput(AsciiPanel terminal) {
        if (win) {
            exitGroupMatch();
            if (isHost)
                exec.shutdownNow();
            MainClass.aClass.screen = new WinScreen();
            MainClass.aClass.repaint();
            return;
        }
        if (loseScreen) {
            exitGroupMatch();
            if (isHost)
                exec.shutdownNow();
            MainClass.aClass.screen = new OnlineLoseScreen();
            MainClass.aClass.repaint();
            return;
        }
        terminal.clear();
        TerminalHelper.writeTetrisLogo(terminal);
        field.printTetrisField(terminal);
        opponentTetrisField.printTetrisField(terminal);
    }

    @Override
    public Screen respondToUserInput(KeyEvent key, AsciiPanel terminal) {

        return this;
    }

    @Override
    public boolean finishInput() {
        return false;
    }

    public void addKey(KeyEvent keyEvent) {
        if (KeyPlay.getKey(keyEvent, false) != null)
            pressedKeys.add(KeyPlay.getKey(keyEvent, false));
    }

    public void removeKey(KeyEvent keyEvent) {
        if (KeyPlay.getKey(keyEvent, false) != null)
            pressedKeys.remove(KeyPlay.getKey(keyEvent, true));
    }

    @Override
    public void run() {
        for (KeyPlay pressedKey : pressedKeys) {
            pressedKey.execute(field);
            pressedKey.incrementCounter();
        }
    }
}
