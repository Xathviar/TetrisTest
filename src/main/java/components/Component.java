package components;

import screens.Screen;

import java.awt.event.KeyEvent;

/**
 * This Interface is used for easier UI Creation
 */
public interface Component {

    /**
     * This is used to display the Component
     *
     * @return a String which is then displayed
     */
    String drawComponent();

    /**
     * This is used so that the Components can have different Key Handling depending on what they need
     * @param key A {@link KeyEvent} which is used to decide what to do
     * @return Depending on the Component this method is able to forward to the next Screen
     */
    Screen handleKeyDown(KeyEvent key);

    /**
     * This method checks if the Component is Selected
     * @return true - if the component is selected; false - if the component is not selected
     */
    boolean isSelected();

    /**
     * Getter which returns the Label of the Component
     * @return Label as a String
     */
    String getLabel();

}
