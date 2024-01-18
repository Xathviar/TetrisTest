package screens;

import helper.TerminalHelper;

import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Represents the screen displayed when the player loses the game.
 */
public class LoseScreen implements Screen {
    /**
     * Represents the level of the game.
     */
    private final int level;
    /**
     * Represents the score achieved by the player in the game.
     * The score is a positive long value.
     */
    private final long score;
    /**
     * Represents the amount of time passed in milliseconds.
     * This variable is used in the LoseScreen class to keep track of the time passed during the game.
     * It is a private final variable, thus its value cannot be changed after initialization.
     */
    private final long timePassed;
    /**
     *
     */
    private final String time;
    /**
     * Represents the initial screen.
     * <p>
     * The initial screen indicates whether it is the first time the screen has been displayed.
     */
    private boolean initialScreen;
    /**
     * Represents the name of a LoseScreen object.
     * <p>
     * The name variable stores the name of the LoseScreen object.
     * <p>
     * This variable is private, so it can only be accessed within the LoseScreen class itself.
     */
    private String name;

    /**
     * Creates a LoseScreen object with the given level, score, and time passed.
     *
     * @param level      the level of the game
     * @param score      the score achieved in the game
     * @param timePassed the time passed in milliseconds
     */
    public LoseScreen(int level, long score, long timePassed) {
        this.level = level;
        this.score = score;
        this.timePassed = timePassed;
        this.initialScreen = true;
        time = String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(timePassed), (TimeUnit.MILLISECONDS.toSeconds(timePassed) % 60));
        name = "";
    }

    /**
     * Displays the output on the AsciiPanel terminal.
     *
     * @param terminal the AsciiPanel terminal to display the output
     */
    @Override
    public void displayOutput(AsciiPanel terminal) {
        if (initialScreen) {
            terminal.clear();
            TerminalHelper.writeTetrisLogo(terminal);
            initialScreen = false;
            terminal.writeCenter("-- press [enter] to start a new Game --", terminal.getHeightInCharacters() - 1);
            terminal.write(String.format("Score: %s", score), 2, 11);
            terminal.write(String.format("Level: %s", level), 2, 12);
            terminal.write(String.format("Played Time: %s", time), 2, 13);
        }
        terminal.write("                              ", 17, 10);
        terminal.write(String.format("Input your Name:%s", name), 2, 10);
    }

    /**
     * Responds to the user's input by performing various actions depending on the key pressed,
     * and returns the corresponding screen to be displayed.
     *
     * @param key      the KeyEvent object representing the key pressed by the user
     * @param terminal the AsciiPanel object representing the terminal
     * @return the Screen object to be displayed
     */
    @Override
    public Screen respondToUserInput(KeyEvent key, AsciiPanel terminal) {
        if (key.getKeyCode() == KeyEvent.VK_ENTER && name.length() > 0) {
            saveHighScore();
            return new StartScreen();
        } else if (key.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (name.length() > 0) {
                name = name.substring(0, name.length() - 1);
            }
        } else {
            char c = key.getKeyChar();
            String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_ ";
            if (c == 127) {
                if (name.length() > 0) {
                    name = name.substring(0, name.length() - 1);
                }
                return this;
            } else if (letters.indexOf(c) > -1 && name.length() <= 16) {
                name += c;
                return this;
            }
        }
        return this;
    }

    /**
     * Signals that the input has finished.
     *
     * @return {@code true} if the input has finished, {@code false}*/
    @Override
    public boolean finishInput() {
        return false;
    }

    /**
     * Saves the high score by writing the name, level, score, and time passed to a file.
     *
     * @throws RuntimeException if an I/O error occurs while writing the high score
     */
    private void saveHighScore() {
        try {
            FileWriter fw = new FileWriter("highscores.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(name + ";" + level + ";" + score + ";" + timePassed);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
