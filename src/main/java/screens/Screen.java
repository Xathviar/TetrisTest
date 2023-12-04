package screens;

import Helper.TerminalHelper;
import com.googlecode.lanterna.input.KeyStroke;

public interface Screen {

    void displayOutput(TerminalHelper terminal);

    Screen respondToUserInput(KeyStroke key, TerminalHelper terminal);

    boolean finishInput();

    default boolean isInsideInputField() {
        return false;
    }

}
