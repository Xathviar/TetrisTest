package screens;

import Helper.TerminalHelper;
import asciiPanel.AsciiPanel;
import com.googlecode.lanterna.input.KeyStroke;
import logic.TetrisField;

public class PlayOfflineScreen implements Screen {

    private final TetrisField field;
    private final long startTime;
    public boolean initScreen = true;
    public boolean loseScreen = false;

    public PlayOfflineScreen(AsciiPanel terminal) {
        field = new TetrisField(1, this, (terminal.getWidthInCharacters() - 12) / 2, 16, false);
        startTime = System.currentTimeMillis();
    }


    @Override
    public void displayOutput(AsciiPanel terminal) {
        terminal.clear();
        TerminalHelper.writeTetrisLogo(terminal);
        initScreen = false;
        field.printTetrisField(terminal);
    }

    @Override
    public Screen respondToUserInput(KeyStroke key, AsciiPanel terminal) {
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
