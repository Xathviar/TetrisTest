package screens;


import java.awt.event.KeyEvent;

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
