package components;

import config.Constants;

import screens.AsciiPanel;
import screens.Screen;

import java.awt.event.KeyEvent;

public class SelectionHelper {
    private final Component[] components;
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
        terminal.write(components[index].drawComponent(), x, y, index == selected ? components[index].isSelected() ? Constants.selectedColor : Constants.inputColor : Constants.characterColor);
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

    public Screen manageKey(KeyEvent keyEvent) {
        if (components[selected].isSelected()) {
            return components[selected].handleKeyDown(keyEvent);
        }
        if (keyEvent.getKeyCode() == KeyEvent.VK_UP || (keyEvent.getKeyChar() != KeyEvent.CHAR_UNDEFINED && Character.toLowerCase(keyEvent.getKeyChar()) == 'w')) {
            this.selectAbove();
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN || (keyEvent.getKeyChar() != KeyEvent.CHAR_UNDEFINED && Character.toLowerCase(keyEvent.getKeyChar()) == 's')) {
            this.selectBelow();
        } else {
            return components[selected].handleKeyDown(keyEvent);
        }
        return null;
    }


    public String getTextInput(String name) {
        for (Component component : components) {
            if (component instanceof TextInput) {
                if (component.getLabel().equals(name)) {
                    return ((TextInput) component).getInput();
                }
            }
        }
        return null;
    }

    public boolean isTextFieldSelected() {
        return components[selected].isSelected();
    }

    public Component getComponentByLabel(String label) {
        for (Component component : components) {
            if (label.equals(component.getLabel())) {
                return component;
            }
        }
        return null;
    }
}
