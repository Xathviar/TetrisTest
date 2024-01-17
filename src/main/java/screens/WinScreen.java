package screens;

import Helper.TerminalHelper;

import java.awt.event.KeyEvent;

public class WinScreen implements Screen {
    public WinScreen() {

    }

    @Override
    public void displayOutput(AsciiPanel terminal) {
//        terminal.clear();
        TerminalHelper.writeTetrisLogo(terminal);
        terminal.write("You won... Good for you", 5, 8);
        terminal.writeCenter("-- Press [Enter] to return to the lobby screen.", terminal.getHeightInCharacters());
    }

    @Override
    public Screen respondToUserInput(KeyEvent key, AsciiPanel terminal) {
        if (key.getKeyCode() == KeyEvent.VK_ENTER) {
            LobbyScreen screen = new LobbyScreen();
            screen.displayOutput(terminal);
            return screen;
        }
        return this;
    }

    @Override
    public boolean finishInput() {
        return false;
    }
}
