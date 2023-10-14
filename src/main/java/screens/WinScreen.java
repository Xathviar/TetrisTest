package screens;

import Helper.TerminalHelper;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

public class WinScreen implements Screen {
    public WinScreen() {

    }

    @Override
    public void displayOutput(TerminalHelper terminal) {
        terminal.clear();
        terminal.writeTetrisLogo();
        terminal.write("You won... Good for you", 5, 8);
        terminal.writeCenter("-- Press [Enter] to return to the lobby screen.", terminal.getHeightInCharacters());
    }

    @Override
    public Screen respondToUserInput(KeyStroke key, TerminalHelper terminal) {
        if (key.getKeyType() == KeyType.Enter) {
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
