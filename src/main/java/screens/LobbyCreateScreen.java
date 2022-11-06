package screens;

import asciiPanel.AsciiPanel;

import java.awt.event.KeyEvent;

import static screens.PlayScreen.tetrisLogo;

public class LobbyCreateScreen implements Screen{
    private int selected = 0;
    private String groupName;
    @Override
    public void displayOutput(AsciiPanel terminal) {
        terminal.clear();
        for (int i = 0; i < tetrisLogo.length; i++) {
            terminal.write(tetrisLogo[i], 5, i + 1);
        }
        int y = 10;
        terminal.write("LobbyName: " + groupName, 5, y);
    }

    @Override
    public Screen respondToUserInput(KeyEvent key, AsciiPanel terminal) {
        if (key.getKeyCode() == KeyEvent.VK_DOWN) {
            selected++;
            if (selected == 4) {
                selected = 0;
            }
            return this;
        }
        if (key.getKeyCode() == KeyEvent.VK_UP) {
            selected--;
            if (selected < 0) {
                selected = 3;
            }
            return this;
        }
        String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_1234567890";
        char c = key.getKeyChar();
        if (selected == 0) {
            if (letters.indexOf(c) > -1) {
                groupName += c;
            }
        }
        return this;
    }

    @Override
    public boolean finishInput() {
        return false;
    }
}
