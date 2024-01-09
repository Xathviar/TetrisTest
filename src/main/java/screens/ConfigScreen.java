package screens;

import asciiPanel.AsciiPanel;
import com.googlecode.lanterna.input.KeyStroke;

public class ConfigScreen implements Screen{
    @Override
    public void displayOutput(AsciiPanel terminal) {

    }

    @Override
    public Screen respondToUserInput(KeyStroke key, AsciiPanel terminal) {
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
