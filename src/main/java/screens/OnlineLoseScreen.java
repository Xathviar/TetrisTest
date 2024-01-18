package screens;

import helper.TerminalHelper;

import java.awt.event.KeyEvent;

/**
 * The OnlineLoseScreen class represents a screen that is displayed when the player loses a game in an online multiplayer context.
 * It implements the Screen interface and provides methods for displaying the screen, responding to user input, and determining if input is finished.
 * */
public class OnlineLoseScreen implements Screen {

    /**
     * Display the output of the OnlineLoseScreen on the given AsciiPanel terminal.
     *
     * @param terminal the AsciiPanel object representing the terminal
     */
    @Override
    public void displayOutput(AsciiPanel terminal) {
//        terminal.clear();
        TerminalHelper.writeTetrisLogo(terminal);
        terminal.write("You lost... Better luck next time...", 5, 8);
        terminal.writeCenter("-- Press [Enter] to return to the lobby screen.", terminal.getHeightInCharacters());
    }

    /**
     * The respondToUserInput method is used to respond to the user's input by performing various actions depending on the key pressed, and returns the corresponding screen to be
     * displayed.
     *
     * @param key      the KeyEvent object representing the key pressed by the user
     * @param terminal the AsciiPanel object representing the terminal
     * @return the Screen object to be displayed
     */
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
     * It is typically used in conjunction with the handleKeyDown method in the Screen interface.
     *
     * @return true if the input is finished, false otherwise
     */
    @Override
    public boolean finishInput() {
        return false;
    }
}
