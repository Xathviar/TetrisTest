package components;

import lombok.Getter;
import screens.Screen;

import java.awt.event.KeyEvent;

/**
 * This is a Part of a Component which is used to create a Checkbox
 */
@Getter
public class Checkbox implements Component {

    /**
     * This stores the Name of the Button
     */
    private final String label;

    /**
     * This stores the state of the Checkbox
     */
    private boolean state;

    /**
     * This stores the current Screen
     */
    private final Screen currentscreen;

    /**
     * This is the Constructor for the Checkbox Class
     * @param label {@link Checkbox#label}
     * @param currentscreen {@link Checkbox#currentscreen}
     */
    public Checkbox(String label, Screen currentscreen) {
        this.label = label;
        this.state = false;
        this.currentscreen = currentscreen;
    }

    @Override
    public String drawComponent() {
        return state ? "[X] " + label : "[ ] " + label;
    }

    @Override
    public Screen handleKeyDown(KeyEvent key) {
        if (key.getKeyCode() == KeyEvent.VK_ENTER || key.getKeyChar() == ' ') {
            state = !state;
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
