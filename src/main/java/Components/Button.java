package Components;

import screens.Screen;

import java.awt.event.KeyEvent;

public class Button implements Component{

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
    public Screen handleKeyDown(KeyEvent key) {
        if (key.getKeyCode() == KeyEvent.VK_ENTER || key.getKeyCode() == KeyEvent.VK_SPACE) {
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
