package components;

import lombok.Getter;
import screens.Screen;

import java.awt.event.KeyEvent;

@Getter
public class Checkbox implements Component {
    private final String label;
    private boolean state;

    private final Screen currentscreen;

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
