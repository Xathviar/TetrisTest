package screens;

import asciiPanel.AsciiPanel;

import java.awt.event.KeyEvent;

public class StartScreen implements Screen {
    private boolean initScreen = true;

    @Override
    public void displayOutput(AsciiPanel terminal) {
        if (initScreen) {
            terminal.write("Tetris", 1, 1);
            terminal.writeCenter("-- press [enter] to start --", terminal.getHeightInCharacters() - 1);
            initScreen = false;
        }
    }

    @Override
    public Screen respondToUserInput(KeyEvent key, AsciiPanel terminal) {
        if (key.getKeyCode() == KeyEvent.VK_ENTER) {
            PlayScreen screen = new PlayScreen();
            screen.displayOutput(terminal);
            return screen;
        }
        return this;
    }
}
