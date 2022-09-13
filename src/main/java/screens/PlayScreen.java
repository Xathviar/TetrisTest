package screens;

import asciiPanel.AsciiPanel;
import logic.TetrisField;

import java.awt.event.KeyEvent;
import java.io.IOException;

public class PlayScreen implements Screen {
    private TetrisField field;
    private final long startTime;

    public PlayScreen() {
        field = new TetrisField(1, this);
        System.out.println("Hello World!");
        startTime = System.currentTimeMillis();
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
            System.out.println("Does it get here?");
            drawBoard(terminal);
            initScreen = false;
        }
        field.printTetrisField(terminal);
    }

    @Override
    public Screen respondToUserInput(KeyEvent key, AsciiPanel terminal) {
        if (loseScreen) {
            return new LoseScreen(field.getLevel(), field.getScore(), System.currentTimeMillis() - startTime);
        }
        switch (key.getKeyCode()) {
            case KeyEvent.VK_LEFT -> field.moveLeft();
            case KeyEvent.VK_RIGHT -> field.moveRight();
            case KeyEvent.VK_UP -> field.rotateClockwise();
            case KeyEvent.VK_DOWN -> field.softDrop();
            case KeyEvent.VK_CONTROL -> field.rotateCClockwise();
            case KeyEvent.VK_SPACE -> field.hardDrop();
            case KeyEvent.VK_SHIFT -> field.swapHold();
        }
        return this;
    }

    private void drawBoard(AsciiPanel terminal) {
        System.out.println("Drawing...");
        int height = 15;
        int width = (terminal.getWidthInCharacters() - 12) / 2;
        System.out.println(width);
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
        String boxFirstLine = leftUp + String.valueOf(straightHorizontally).repeat(width - 2) + rightUp;
        String boxMiddleLines = straightVertically + " ".repeat(width - 2) + straightVertically;
        String boxBottomLine = leftDown + String.valueOf(straightHorizontally).repeat(width - 2) + rightDown;

        terminal.write(boxFirstLine, x, y++);
        for (int i = 0; i < height - 2; i++) {
            terminal.write(boxMiddleLines, x, y++);
        }
        terminal.write(boxBottomLine, x, y);
    }


}
