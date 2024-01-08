package screens;

import Helper.TerminalHelper;
import com.googlecode.lanterna.input.KeyStroke;

public class ConfigScreen implements Screen{
    @Override
    public void displayOutput(TerminalHelper terminal) {

    }

    @Override
    public Screen respondToUserInput(KeyStroke key, TerminalHelper terminal) {
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
