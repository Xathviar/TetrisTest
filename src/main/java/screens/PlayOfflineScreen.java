package screens;

import Helper.TerminalHelper;
import com.googlecode.lanterna.input.KeyStroke;
import logic.TetrisField;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class PlayOfflineScreen implements Screen {

    private final TetrisField field;
    private final long startTime;
    public boolean initScreen = true;
    public boolean loseScreen = false;

    public PlayOfflineScreen(TerminalHelper terminal) {
        field = new TetrisField(1, this, (terminal.getWidthInCharacters() - 12) / 2, 16, false);
        startTime = System.currentTimeMillis();
    }


    @Override
    public void displayOutput(TerminalHelper terminal) {
        terminal.clear();
        terminal.writeTetrisLogo();
        initScreen = false;
        field.printTetrisField(terminal);
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
