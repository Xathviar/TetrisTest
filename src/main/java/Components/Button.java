package Components;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import screens.Screen;

public class Button implements Component {

    private String label;
    private Screen currentScreen;
    private Screen newScreen;

    public Button(String label, Screen currentScreen, Screen newScreen) {
        this.label = label;
        this.currentScreen = currentScreen;
        this.newScreen = newScreen;
    }

    @Override
    public String drawComponent() {
        return label;
    }

    @Override
    public Screen handleKeyDown(KeyStroke key) {
        if (key.getKeyType() == KeyType.Enter || key.getCharacter() == ' ') {
            if (currentScreen.finishInput()) {
                return newScreen;
            }
        }
        return null;
    }

    @Override
    public boolean isSelected() {
        return false;
    }
}
