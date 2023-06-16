package Components;

import Helper.TerminalHelper;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import screens.Screen;

public class SelectionHelper {
    private Component[] components;
    private int selected;

    public SelectionHelper(Component... components) {
        this.components = components;
    }

    public void selectAbove() {
        if (selected == 0) {
            selected = components.length - 1;
        } else {
            selected--;
        }
    }

    public void selectBelow() {
        if (selected == components.length - 1) {
            selected = 0;
        } else {
            selected++;
        }
    }

    public Component getSelectedActor() {
        return components[selected];
    }

    public void drawComponent(TerminalHelper terminal, int x, int y, int index) {
        terminal.write(components[index].drawComponent(), x, y, index == selected ? components[index].isSelected() ? TextColor.ANSI.GREEN : TextColor.ANSI.CYAN : TextColor.ANSI.BLACK_BRIGHT);
    }

    public void drawAllComponents(TerminalHelper terminal, int x, int y) {
        for (int i = 0; i < components.length; i++) {
            drawComponent(terminal, x, y + i, i);
        }
    }

    public boolean isSelected(int current) {
        return current == selected;
    }

    public String getOptionAt(int index) {
        return components[index].drawComponent();
    }

    public int getSelected() {
        return selected;
    }

    public Screen manageKey(KeyStroke keycode) {
        if (components[selected].isSelected()) {
            return components[selected].handleKeyDown(keycode);

        }
        if (keycode.getKeyType() == KeyType.ArrowUp || Character.toLowerCase(keycode.getCharacter()) == 'w') {
            this.selectAbove();
        } else if (keycode.getKeyType() == KeyType.ArrowDown || Character.toLowerCase(keycode.getCharacter()) == 's') {
            this.selectBelow();
        } else {
            return components[selected].handleKeyDown(keycode);
        }
        return null;
    }

    public String getTextInput(String name) {
        for (Component component : components) {
            if (component instanceof TextInput) {
                if (((TextInput) component).getLabel().equals(name)) {
                    return ((TextInput) component).getInput();
                }
            }
        }
        return null;
    }
}
