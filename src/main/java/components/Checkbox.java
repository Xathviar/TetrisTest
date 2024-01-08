package components;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import lombok.Getter;
import screens.Screen;

@Getter
public class Checkbox implements Component {
    private String label;
    private boolean state;

    private Screen currentscreen;

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
    public Screen handleKeyDown(KeyStroke key) {
        if (key.getKeyType() == KeyType.Enter || key.getCharacter() == ' ') {
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
