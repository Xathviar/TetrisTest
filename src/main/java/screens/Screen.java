package screens;


import java.awt.event.KeyEvent;

public interface Screen {

    void displayOutput(AsciiPanel terminal);

    Screen respondToUserInput(KeyEvent key, AsciiPanel terminal);

    boolean finishInput();

    default boolean isInsideInputField() {
        return false;
    }

}
