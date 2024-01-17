package components;

import screens.Screen;

import java.awt.event.KeyEvent;

public class Button implements Component {

    private final String label;
    private final Screen currentScreen;
    private final Screen newScreen;

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
    public Screen handleKeyDown(KeyEvent key) {
        if (key.getKeyCode() == KeyEvent.VK_ENTER || key.getKeyChar() == ' ') {
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

    @Override
    public String getLabel() {
        return label;
    }
}
