package screens;

import asciiPanel.AsciiPanel;
import logic.TetrisField;

import java.awt.event.KeyEvent;

public class PlayScreen implements Screen {
    private TetrisField field;

    public PlayScreen() {
        field = new TetrisField();
        System.out.println("Hello World!");
    }

    public boolean initScreen = true;
    private static final String[] tetrisLogo = """
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
        field.printHold(terminal);
    }

    @Override
    public Screen respondToUserInput(KeyEvent key, AsciiPanel terminal) {
        switch (key.getKeyCode()) {
            case KeyEvent.VK_LEFT -> field.moveLeft();
            case KeyEvent.VK_RIGHT -> field.moveRight();
            case KeyEvent.VK_UP -> field.rotateClockwise();
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
        StringBuilder firstline = new StringBuilder();
        firstline.append(leftUp);
        firstline.append(String.valueOf(straightHorizontally).repeat(10));
        firstline.append(rightUp);

        StringBuilder middleLines = new StringBuilder();
        middleLines.append(straightVertically);
        middleLines.append("          ");
        middleLines.append(straightVertically);

        StringBuilder bottomLine = new StringBuilder();
        bottomLine.append(leftDown);
        bottomLine.append(String.valueOf(straightHorizontally).repeat(10));
        bottomLine.append(rightDown);

        terminal.write(firstline.toString(), width, height++);
        for (int i = 0; i < 20; i++) {
            terminal.write(middleLines.toString(), width, height++);
        }
        terminal.write(bottomLine.toString(), width, height);
        System.out.println("Finished Drawing...");
    }

}
