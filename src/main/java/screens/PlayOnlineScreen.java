package screens;

import Helper.TerminalHelper;
import com.googlecode.lanterna.input.KeyStroke;
import com.heroiclabs.nakama.*;
import com.heroiclabs.nakama.Error;
import com.heroiclabs.nakama.api.ChannelMessage;
import com.heroiclabs.nakama.api.NotificationList;
import communication.MatchSendHelper;
import logic.OpponentTetrisField;
import logic.TetrisField;

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

    public PlayOnlineScreen(TerminalHelper terminal, boolean isHost) {
        field = new TetrisField(1, this, (terminal.getWidthInCharacters() - 12) / 2, 16, true);
        startTime = System.currentTimeMillis();
        opponentTetrisField = new OpponentTetrisField((terminal.getWidthInCharacters()) / 2 + 30, 16);
        exec = Executors.newSingleThreadScheduledExecutor();
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

    @Override
    public void displayOutput(TerminalHelper terminal) {
        terminal.clear();
        terminal.writeTetrisLogo();
        field.printTetrisField(terminal);
        opponentTetrisField.printTetrisField(terminal);
    }

    // TODO Maybe find a way to make this a more smooth experience, idk...
    @Override
    public Screen respondToUserInput(KeyStroke key, TerminalHelper terminal) {
        if (loseScreen) {
            MainClass.aClass.screen = new LoseScreen(field.getLevel(), field.getScore(), System.currentTimeMillis() - startTime);
            field.shutdownThread();
            MainClass.aClass.repaint();
            return MainClass.aClass.screen;
        }
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
