package Components;

import asciiPanel.AsciiPanel;
import screens.Screen;

import java.awt.*;
import java.awt.event.KeyEvent;

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

    public void drawComponent(AsciiPanel terminal, int x, int y, int index) {
        terminal.write(components[index].drawComponent(), x, y, index == selected ? components[index].isSelected() ? Color.GREEN : Color.CYAN : Color.LIGHT_GRAY);
    }

    public void drawAllComponents(AsciiPanel terminal, int x, int y) {
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

    public Screen manageKey(KeyEvent keycode) {
        if (components[selected].isSelected()) {
            return components[selected].handleKeyDown(keycode);

        }
        if (keycode.getKeyCode() == KeyEvent.VK_UP || keycode.getKeyCode() == KeyEvent.VK_W) {
            this.selectAbove();
        } else if (keycode.getKeyCode() == KeyEvent.VK_DOWN || keycode.getKeyCode() == KeyEvent.VK_S) {
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
