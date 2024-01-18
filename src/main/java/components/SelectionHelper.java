package components;

import config.Constants;

import screens.AsciiPanel;
import screens.Screen;

import java.awt.event.KeyEvent;

/**
 * This Class is used for easier UI Creation <br>
 * While {@link Component} are the Components, SelectionHelper instead stores all Components
 */
public class SelectionHelper {

    /**
     * This Array stores all Components that are part of the SelectionHelper
     */
    private final Component[] components;

    /**
     * This Variable stores which Component is currently selected
     */
    private int selected;


    /**
     * This Constructor is used for the Creation of a SelectionHelper
     * @param components an Ellipse of all the Components that the SelectionHelper should store
     */
    public SelectionHelper(Component... components) {
        this.components = components;
    }


    /**
     * This Method is used to change {@link SelectionHelper#selected} to the one being above / roll over if selected is 0
     */
    public void selectAbove() {
        if (selected == 0) {
            selected = components.length - 1;
        } else {
            selected--;
        }
    }

    /**
     * This Method is used to change {@link SelectionHelper#selected} to the one being below / roll over if selected is the last {@link Component}
     */
    public void selectBelow() {
        if (selected == components.length - 1) {
            selected = 0;
        } else {
            selected++;
        }
    }

    /**
     * This Method is used to draw a single Component, if it is selected, it will have a different color
     * @param terminal {@link AsciiPanel} - this is used for displaying the output
     * @param x on which X - Coordinate this should be drawn
     * @param y on which Y - Coordinate this should be drawn
     * @param index which component should be drawn currently
     */
    public void drawComponent(AsciiPanel terminal, int x, int y, int index) {
        terminal.write(components[index].drawComponent(), x, y, index == selected ? components[index].isSelected() ? Constants.selectedColor : Constants.inputColor : Constants.characterColor);
    }

    /**
     * This Method draws all Components and calls {@link SelectionHelper#drawComponent(AsciiPanel, int, int, int)}
     * @param terminal  - this is used for displaying the output
     * @param x on which X - Coordinate this should be drawn
     * @param y on which Y - Coordinate this should be drawn
     */
    public void drawAllComponents(AsciiPanel terminal, int x, int y) {
        for (int i = 0; i < components.length; i++) {
            drawComponent(terminal, x, y + i, i);
        }
    }

    /**
     * This Method is used for managing the Keys <br>
     * @param keyEvent A {@link KeyEvent} which is used to decide what to do
     * @return Depending on the Component this method is able to forward to the next Screen
     */
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


    /**
     * This returns the input of a {@link TextInput}
     * @param name The label of the Component
     * @return a String which contains the inputted text of the {@link TextInput}
     */
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

    /**
     * This method checks if the current active Component is selected
     * @return boolean
     */
    public boolean isSelected() {
        return components[selected].isSelected();
    }

    /**
     * This Method returns a {@link Component} which has the same label as the input
     * @param label The label that the Component has
     * @return The {@link Component} which has the label
     */
    public Component getComponentByLabel(String label) {
        for (Component component : components) {
            if (label.equals(component.getLabel())) {
                return component;
            }
        }
        return null;
    }
}
