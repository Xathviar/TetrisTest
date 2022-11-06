package Components;

import screens.Screen;

import java.awt.event.KeyEvent;

public class TextInput implements Component {
    private String label;
    private String input;
    private boolean isSelected;
    private boolean onlyDigits;
    private boolean isPassword;
    private final String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_1234567890@.";
    private final String password = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_1234567890!<>,.:;()[]{}@#$%^&*-=+_\\|\"'/?~";


    public TextInput(String label, boolean onlyDigits, boolean isPassword) {
        this.label = label;
        this.input = "";
        this.onlyDigits = onlyDigits;
        this.isSelected = false;
        this.isPassword = isPassword;
    }

    public void deleteLastLetter() {
        if (input.length() > 0) {
            input = input.substring(0, input.length() - 1);
        }
    }

    @Override
    public String drawComponent() {
        return String.format("%s: %s", label, isPassword ? input.replaceAll(".", "X") : input);
    }

    @Override
    public Screen handleKeyDown(KeyEvent key) {
        if (!isSelected) {
            if (key.getKeyCode() == KeyEvent.VK_ENTER || key.getKeyCode() == KeyEvent.VK_SPACE) {
                isSelected = true;
            }
        } else {
            if (key.getKeyCode() == KeyEvent.VK_ENTER || key.getKeyCode() == KeyEvent.VK_SPACE) {
                isSelected = false;
            }
            if (key.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                deleteLastLetter();
            }
            char c = key.getKeyChar();
            if (isPassword) {
                if (password.indexOf(c) > -1) {
                    input += c;
                }
            } else {
                if (letters.indexOf(c) > -1) {
                    input += c;
                }
            }
        }
        return null;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getLabel() {
        return label;
    }

    public String getInput() {
        return input;
    }
}
