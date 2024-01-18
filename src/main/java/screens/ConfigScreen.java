package screens;


import java.awt.event.KeyEvent;

/**
 * The Config Screen is used to update the Configuration such as Keybindings, gameplay variables and colors from a GUI
 * This still has to be implemented
 */
public class ConfigScreen implements Screen{

    @Override
    public void displayOutput(AsciiPanel terminal) {

    }

    @Override
    public Screen respondToUserInput(KeyEvent key, AsciiPanel terminal) {
        return null;
    }

    @Override
    public boolean finishInput() {
        return false;
    }

    @Override
    public boolean isInsideInputField() {
        return Screen.super.isInsideInputField();
    }
}
