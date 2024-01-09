package screens;

import asciiPanel.AsciiPanel;
import com.googlecode.lanterna.input.KeyStroke;

public interface Screen {

    void displayOutput(AsciiPanel terminal);

    Screen respondToUserInput(KeyStroke key, AsciiPanel terminal);

    boolean finishInput();

    default boolean isInsideInputField() {
        return false;
    }

}
