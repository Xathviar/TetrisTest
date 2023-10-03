package screens;

import Helper.TerminalHelper;
import com.googlecode.lanterna.input.KeyStroke;
import logic.TetrisField;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class PlayScreen implements Screen {
    public static final String[] tetrisLogo = (
            " _________  ________  _________  _______     _____   ______  \n" +
                    "|  _   _  ||_   __  ||  _   _  ||_   __ \\   |_   _|.' ____ \\ \n" +
                    "|_/ | | \\_|  | |_ \\_||_/ | | \\_|  | |__) |    | |  | (___ \\_|\n" +
                    "    | |      |  _| _     | |      |  __ /     | |   _.____`. \n" +
                    "   _| |_    _| |__/ |   _| |_    _| |  \\ \\_  _| |_ | \\____) |\n" +
                    "  |_____|  |________|  |_____|  |____| |___||_____| \\______.'\n").split("\n");
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

    public PlayScreen() {
        field = new TetrisField(1, this);
        startTime = System.currentTimeMillis();
        exec = Executors.newSingleThreadScheduledExecutor();
//        exec.scheduleAtFixedRate(this::handleInput, 0, 10, TimeUnit.MILLISECONDS);
    }

    public static void writeBoxAt(TerminalHelper terminal, int x, int y, int width, int height) {
        char leftDown = '#';
        char leftUp = '#';
        char rightUp = '#';
        char rightDown = '#';
        char straightHorizontally = '#';
        char straightVertically = '#';
        String horizontalLines = String.valueOf(straightHorizontally).repeat(width - 2);
        String boxFirstLine = leftUp + horizontalLines + rightUp;
        String boxMiddleLines = straightVertically + " ".repeat(width - 2) + straightVertically;
        String boxBottomLine = leftDown + horizontalLines + rightDown;

        terminal.write(boxFirstLine, x, y++);
        for (int i = 0; i < height - 2; i++) {
            terminal.write(boxMiddleLines, x, y++);
        }
        terminal.write(boxBottomLine, x, y);
    }

    public void shutdownThread() {
        exec.shutdownNow();
    }

    @Override
    public void displayOutput(TerminalHelper terminal) {
        if (initScreen) {
            terminal.clear();
            for (int i = 0; i < tetrisLogo.length; i++) {
                terminal.write(tetrisLogo[i], 5, i + 1);
            }
            initScreen = false;
        }
        drawBoard(terminal);
        field.printTetrisField(terminal, (terminal.getWidthInCharacters() - 12) / 2, 16);
    }

    @Override
    public Screen respondToUserInput(KeyStroke key, TerminalHelper terminal) {
        if (loseScreen) {
            MainClass.aClass.screen = new LoseScreen(field.getLevel(), field.getScore(), System.currentTimeMillis() - startTime);
            exec.shutdownNow();
            field.shutdownThread();
            MainClass.aClass.repaint();
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

    private void drawBoard(TerminalHelper terminal) {
        int height = 15;
        int width = (terminal.getWidthInCharacters() - 12) / 2;
        char leftDown = '#';
        char leftUp = '#';
        char rightUp = '#';
        char rightDown = '#';
        char straightHorizontally = '#';
        char straightVertically = '#';
        String firstline = leftUp +
                String.valueOf(straightHorizontally).repeat(10) +
                rightUp;
        StringBuilder middleLines = new StringBuilder();
        middleLines.append(straightVertically);
        middleLines.append("          ");
        middleLines.append(straightVertically);
        String bottomLine = leftDown +
                String.valueOf(straightHorizontally).repeat(10) +
                rightDown;

        terminal.write(firstline, width, height++);
        for (int i = 0; i < 20; i++) {
            terminal.write(middleLines.toString(), width, height++);
        }
        terminal.write(bottomLine, width, height);
        height = 15;
        // This is the Box which displays the Hold Piece
        writeBoxAt(terminal, width - 9, height, 9, 7);

        // This is the Box which displays the scoreboard
        writeBoxAt(terminal, width - 9, height + 15, 9, 7);

        // These are the boxes drawn which show th enext four pieces
        writeBoxAt(terminal, width + 10, height, 8, 6);
        writeBoxAt(terminal, width + 10, height + 5, 8, 6);
        writeBoxAt(terminal, width + 10, height + 10, 8, 6);
        writeBoxAt(terminal, width + 10, height + 15, 8, 6);
        terminal.write(" ", width + 11, height + 21);
    }


}
