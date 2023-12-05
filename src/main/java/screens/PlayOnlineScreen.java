package screens;

import Helper.TerminalHelper;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import communication.MatchSendHelper;
import logic.OpponentTetrisField;
import logic.TetrisField;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class PlayOnlineScreen implements Screen {

    private static TetrisField field;
    private final long startTime;
    public boolean loseScreen = false;
    public static boolean win = false;

    public static OpponentTetrisField opponentTetrisField;

    private ScheduledExecutorService exec;

    private boolean isHost;

    public PlayOnlineScreen(TerminalHelper terminal, boolean isHost) {
        field = new TetrisField(1, this, (terminal.getWidthInCharacters() - 12) / 2, 16, true);
        startTime = System.currentTimeMillis();
        opponentTetrisField = new OpponentTetrisField((terminal.getWidthInCharacters()) / 2 + 30, 16);
        exec = Executors.newSingleThreadScheduledExecutor();
        this.isHost = isHost;
        if (isHost) {
            exec.scheduleAtFixedRate(PlayOnlineScreen::tickMaster, 0, 1, TimeUnit.SECONDS);
        }
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
    public void displayOutput(TerminalHelper terminal) {
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
        terminal.writeTetrisLogo();
        field.printTetrisField(terminal);
        opponentTetrisField.printTetrisField(terminal);
    }

    @Override
    public Screen respondToUserInput(KeyStroke key, TerminalHelper terminal) {
        switch (key.getKeyType()) {
            case ArrowLeft:
                field.moveLeft();
                break;
            case ArrowRight:
                field.moveRight();
                break;
            case ArrowDown:
                field.softDrop();
                break;
            case ArrowUp:
                field.rotateClockwise();
                break;
            case Character:
                switch (Character.toLowerCase(key.getCharacter())) {
                    case ' ':
                        field.hardDrop();
                        break;
                    case 'z':
                    case 'y':
                        field.rotateCClockwise();
                        break;
                    case 'x':
                        field.swapHold();
                        break;
                }
        }

        return this;
    }

    @Override
    public boolean finishInput() {
        return false;
    }

}
