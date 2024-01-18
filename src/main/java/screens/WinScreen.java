package screens;

import helper.TerminalHelper;

import java.awt.event.KeyEvent;

/**
 * The WinScreen class implements the Screen interface and represents a screen displayed when the player wins the game.
 * It displays a victory message and prompts the player to return to the lobby screen.
 */
public class WinScreen implements Screen {

    /**
     * Displays the WinScreen output on the AsciiPanel terminal.
     *
     * @param terminal the AsciiPanel object representing the terminal
     */
    @Override
    public void displayOutput(AsciiPanel terminal) {
//        terminal.clear();
        TerminalHelper.writeTetrisLogo(terminal);
        terminal.write("You won... Good for you", 5, 8);
        terminal.writeCenter("-- Press [Enter] to return to the lobby screen.", terminal.getHeightInCharacters());
    }

    /**
     * Responds to the user's input by performing various actions depending on the key pressed,
     * and returns the corresponding screen to be displayed.
     *
     * @param key      the KeyEvent object representing the key pressed by*/
    @Override
    public Screen respondToUserInput(KeyEvent key, AsciiPanel terminal) {
        if (key.getKeyCode() == KeyEvent.VK_ENTER) {
            LobbyScreen screen = new LobbyScreen();
            screen.displayOutput(terminal);
            return screen;
        }
        return this;
    }

    /**
     * The finishInput method is used to determine if the user has finished providing input.
     *
     * @return true if the input is finished, false otherwise
     */
    @Override
    public boolean finishInput() {
        return false;
    }
}
