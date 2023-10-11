package components;

import com.googlecode.lanterna.input.KeyStroke;
import screens.Screen;

public interface Component {

    public String drawComponent();

    public Screen handleKeyDown(KeyStroke key);

    public boolean isSelected();

}
