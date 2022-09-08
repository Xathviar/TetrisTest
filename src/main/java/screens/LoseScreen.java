package screens;

import asciiPanel.AsciiPanel;

import java.awt.event.KeyEvent;

public class LoseScreen implements Screen {
    public LoseScreen(int level, long score, long timePassed) {
//TODO
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {
        terminal.write("asdjklf;h", 1, 1);
    }

    @Override
    public Screen respondToUserInput(KeyEvent key, AsciiPanel terminal) {
        return this;
    }
}
