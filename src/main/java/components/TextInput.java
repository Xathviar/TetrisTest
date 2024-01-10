package components;

import com.googlecode.lanterna.input.KeyType;
import screens.Screen;

import java.awt.event.KeyEvent;

public class TextInput implements Component {
    private final String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_1234567890@.";
    private final String password = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_1234567890!<>,.:;()[]{}@#$%^&*-=+_\\|\"'/?~";
    private String label;
    private String input;
    private boolean isSelected;
    private boolean onlyDigits;
    private boolean isPassword;


    public TextInput(String label, boolean onlyDigits, boolean isPassword) {
        this.label = label;
        this.input = "";
        this.onlyDigits = onlyDigits;
        this.isSelected = false;
        this.isPassword = isPassword;
    }

    public TextInput setDefaultInput(String input) {
        this.input = input;
        return this;
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
            if (key.getKeyCode() == KeyEvent.VK_ENTER || key.getKeyChar() == ' ') {
                isSelected = true;
            }
        } else {
            if (key.getKeyCode() == KeyEvent.VK_ENTER || key.getKeyChar() == ' ') {
                isSelected = false;
            }
            if (key.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                deleteLastLetter();
            }
            if (key.getKeyCode() != KeyEvent.CHAR_UNDEFINED) {
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
        }
        return null;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }
    
    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    @Override
    public String getLabel() {
        return label;
    }


}
