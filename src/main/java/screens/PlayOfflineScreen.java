package screens;

import Helper.TerminalHelper;
import com.googlecode.lanterna.input.KeyStroke;
import logic.TetrisField;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class PlayOfflineScreen implements Screen {

    private final TetrisField field;
    private final long startTime;
    private final ScheduledExecutorService exec;
    public boolean initScreen = true;

    //    private void handleInput() {
//        synchronized (MainClass.pressedKeys) {
//            for (Key pressedKey : MainClass.pressedKeys) {
//                if (pressedKey != null)
//                    pressedKey.handleKeyInput(field);
//                else
//                    MainClass.pressedKeys.remove(null);
//            }
//        }
//    }
    public boolean loseScreen = false;

    public PlayOfflineScreen(TerminalHelper terminal) {
        field = new TetrisField(1, this, (terminal.getWidthInCharacters() - 12) / 2, 16);
        startTime = System.currentTimeMillis();
        exec = Executors.newSingleThreadScheduledExecutor();
//        exec.scheduleAtFixedRate(this::handleInput, 0, 10, TimeUnit.MILLISECONDS);
    }

    public void shutdownThread() {
        exec.shutdownNow();
    }

    // TODO Streamline this Displayoutput so that the Tetrisfield handles the complete TetrisField
    // TODO think about how to implement the enemy Screen as well
    @Override
    public void displayOutput(TerminalHelper terminal) {
        if (initScreen) {
            terminal.clear();
            terminal.writeTetrisLogo();
            initScreen = false;
        }
        field.printTetrisField(terminal);
    }

    // TODO Maybe find a way to make this a more smooth experience, idk...
    @Override
    public Screen respondToUserInput(KeyStroke key, TerminalHelper terminal) {
        if (loseScreen) {
            MainClass.aClass.screen = new LoseScreen(field.getLevel(), field.getScore(), System.currentTimeMillis() - startTime);
            exec.shutdownNow();
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
