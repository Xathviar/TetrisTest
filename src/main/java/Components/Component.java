package Components;

import screens.Screen;

import java.awt.event.KeyEvent;
import java.util.HashMap;

public interface Component {

    public String drawComponent();

    public Screen handleKeyDown(KeyEvent key);

    public boolean isSelected();

}
