package components;

import screens.Screen;

import java.awt.event.KeyEvent;

public interface Component {

    String drawComponent();

    Screen handleKeyDown(KeyEvent key);

    boolean isSelected();

    String getLabel();

}
