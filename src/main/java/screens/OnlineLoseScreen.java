package screens;

import Helper.TerminalHelper;
import asciiPanel.AsciiPanel;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

public class OnlineLoseScreen implements Screen {
    public OnlineLoseScreen() {
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {
//        terminal.clear();
        TerminalHelper.writeTetrisLogo(terminal);
        terminal.write("You lost... Better luck next time...", 5, 8);
        terminal.writeCenter("-- Press [Enter] to return to the lobby screen.", terminal.getHeightInCharacters());
    }

    @Override
    public Screen respondToUserInput(KeyStroke key, AsciiPanel terminal) {
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
