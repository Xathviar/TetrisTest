package components;

import screens.Screen;

import java.awt.event.KeyEvent;

public interface Component {

    public String drawComponent();

    public Screen handleKeyDown(KeyEvent key);

    public boolean isSelected();

    public String getLabel();

}
