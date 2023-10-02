package Components;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import screens.Screen;

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
    public Screen handleKeyDown(KeyStroke key) {
        try {
            key.getKeyType();
            key.getCharacter();
        } catch (NullPointerException e) {
            System.out.println((int) key.getCharacter());
        }
        if (!isSelected) {
            try {
                System.out.println(key);
                if (key.getKeyType() == KeyType.Enter || key.getCharacter() == ' ') {
                    isSelected = true;
                }
            } catch (NullPointerException ignored) {

            }
        } else {
            if (key.getKeyType() == KeyType.Enter || key.getCharacter() == ' ') {
                isSelected = false;
            }
            if (key.getKeyType() == KeyType.Backspace) {
                deleteLastLetter();
            }
            char c = key.getCharacter();
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

    public String getLabel() {
        return label;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
