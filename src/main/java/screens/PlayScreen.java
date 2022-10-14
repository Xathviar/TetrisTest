package screens;

import asciiPanel.AsciiPanel;
import logic.Key;
import logic.TetrisField;

import java.awt.event.KeyEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayScreen implements Screen {
    private final TetrisField field;
    private final long startTime;
    private final ScheduledExecutorService exec;


    public PlayScreen() {
        field = new TetrisField(1, this);
        startTime = System.currentTimeMillis();
        exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(this::handleInput, 0, 10, TimeUnit.MILLISECONDS);
    }

    public void shutdownThread() {
        exec.shutdownNow();
    }

    private void handleInput() {
        if (loseScreen) {
            MainClass.aClass.screen = new LoseScreen(field.getLevel(), field.getScore(), System.currentTimeMillis() - startTime);
            exec.shutdownNow();
            field.shutdownThread();
            MainClass.aClass.repaint();
        }
        synchronized (MainClass.pressedKeys) {
            for (Key pressedKey : MainClass.pressedKeys) {
                if (pressedKey != null)
                    pressedKey.handleKeyInput(field);
                else
                    MainClass.pressedKeys.remove(null);
            }
        }
    }

    public boolean initScreen = true;

    public boolean loseScreen = false;
    public static final String[] tetrisLogo = """
             _________  ________  _________  _______     _____   ______  \s
            |  _   _  ||_   __  ||  _   _  ||_   __ \\   |_   _|.' ____ \\ \s
            |_/ | | \\_|  | |_ \\_||_/ | | \\_|  | |__) |    | |  | (___ \\_|\s
                | |      |  _| _     | |      |  __ /     | |   _.____`. \s
               _| |_    _| |__/ |   _| |_    _| |  \\ \\_  _| |_ | \\____) |\s
              |_____|  |________|  |_____|  |____| |___||_____| \\______.'\s
            """.split("\n");

    @Override
    public void displayOutput(AsciiPanel terminal) {
        if (initScreen) {
            terminal.clear();
            for (int i = 0; i < tetrisLogo.length; i++) {
                terminal.write(tetrisLogo[i], 5, i + 1);
            }
            initScreen = false;
        }
        drawBoard(terminal);
        field.printTetrisField(terminal);
    }

    @Override
    public Screen respondToUserInput(KeyEvent key, AsciiPanel terminal) {
        return this;
    }

    private void drawBoard(AsciiPanel terminal) {
        int height = 15;
        int width = (terminal.getWidthInCharacters() - 12) / 2;
        char leftDown = 200;
        char leftUp = 201;
        char rightUp = 187;
        char rightDown = 188;
        char straightHorizontally = 205;
        char straightVertically = 186;
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
        writeBoxAt(terminal, width - 8, height, 8, 6);
        writeBoxAt(terminal, width + 12, height, 8, 6);
        writeBoxAt(terminal, width + 12, height + 5, 8, 6);
        writeBoxAt(terminal, width + 12, height + 10, 8, 6);
        writeBoxAt(terminal, width + 12, height + 15, 8, 6);
        writeBoxAt(terminal, width - 9, height + 7, 9, 15);
        terminal.write((char) 182, width - 1, height + 10);
        terminal.write((char) 199, width - 9, height + 10);
        terminal.write((char) 204, width + 12, height + 5);
        terminal.write((char) 204, width + 12, height + 10);
        terminal.write((char) 204, width + 12, height + 15);
        terminal.write((char) 185, width + 19, height + 5);
        terminal.write((char) 185, width + 19, height + 10);
        terminal.write((char) 185, width + 19, height + 15);

    }

    public static void writeBoxAt(AsciiPanel terminal, int x, int y, int width, int height) {
        char leftDown = 200;
        char leftUp = 201;
        char rightUp = 187;
        char rightDown = 188;
        char straightHorizontally = 205;
        char straightVertically = 186;
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


}
