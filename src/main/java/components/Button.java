package components;

import screens.Screen;

import java.awt.event.KeyEvent;

/**
 * This is a Part of a Component which is used to create a Button <br>
 * Once the Button is pressed, the Screen will be updated to {@link Button#newScreen}
 */
public class Button implements Component {

    /**
     * This stores the Name of the Button
     */
    private final String label;

    /**
     * This stores the current Screen
     */
    private final Screen currentScreen;

    /**
     * This stores the new Screen which is called once the button is pressed
     */
    private final Screen newScreen;

    /**
     * This is the Constructor for the Button Class
     * @param label {@link Button#label}
     * @param currentScreen {@link Button#currentScreen}
     * @param newScreen {@link Button#newScreen}
     */
    public Button(String label, Screen currentScreen, Screen newScreen) {
        this.label = label;
        this.currentScreen = currentScreen;
        this.newScreen = newScreen;
    }

    @Override
    public String drawComponent() {
        return label;
    }

    @Override
    public Screen handleKeyDown(KeyEvent key) {
        if (key.getKeyCode() == KeyEvent.VK_ENTER || key.getKeyChar() == ' ') {
            if (currentScreen.finishInput()) {
                return newScreen;
            }
        }
        return null;
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
