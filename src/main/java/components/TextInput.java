package components;

import lombok.Getter;
import lombok.Setter;
import screens.Screen;

import java.awt.event.KeyEvent;

@Getter
@Setter
/**
 * This is a Part of a Component which is used to create a TextInput
 */
public class TextInput implements Component {
    /**
     * This stores all characters that are available to be typed in a normal TextInput Field
     */
    private final String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_1234567890@.";
    /**
     * This stores all characters that are available to be typed in a password TextInput Field
     */
    private final String password = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_1234567890!<>,.:;()[]{}@#$%^&*-=+_\\|\"'/?~";
    /**
     * This stores the Name of the TextInput
     */
    private final String label;
    /**
     * This stores the text that was typed inside the text input
     */
    private String input;
    /**
     * This stores if the TextInput is currently selected
     */
    private boolean isSelected;
    /**
     * This stores if the TextInput should only allow Digits
     */
    private final boolean onlyDigits;
    /**
     * This stores if the TextInput is a Password Input Field
     */
    private final boolean isPassword;


    /**
     * This is the Constructor for the TextInput Class
     * @param label {@link TextInput#label}
     * @param onlyDigits If this is set to true, then only Digits are allowed to be entered into this field
     * @param isPassword If this is set to true, then the input will be hidden
     */
    public TextInput(String label, boolean onlyDigits, boolean isPassword) {
        this.label = label;
        this.input = "";
        this.onlyDigits = onlyDigits;
        this.isSelected = false;
        this.isPassword = isPassword;
    }

    /**
     * This can be used to manually set the Input of the TextInput
     * @param input The String which should be used as the new TextInput
     * @return itself
     */
    public TextInput setDefaultInput(String input) {
        this.input = input;
        return this;
    }

    /**
     * This Method is used to delete the last letter of the TextInput
     */
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

    @Override
    public String getLabel() {
        return label;
    }


}
