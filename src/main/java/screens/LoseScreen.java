package screens;

import Helper.TerminalHelper;
import asciiPanel.AsciiPanel;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class LoseScreen implements Screen {
    private final int level;
    private final long score;
    private final long timePassed;
    private final String time;
    private boolean initialScreen;
    private String name;

    public LoseScreen(int level, long score, long timePassed) {
        this.level = level;
        this.score = score;
        this.timePassed = timePassed;
        this.initialScreen = true;
        time = String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(timePassed), (TimeUnit.MILLISECONDS.toSeconds(timePassed) % 60));
        name = "";
    }

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

    @Override
    public Screen respondToUserInput(KeyStroke key, AsciiPanel terminal) {
        if (key.getKeyType() == KeyType.Enter && name.length() > 0) {
            saveHighScore();
            return new StartScreen();
        } else if (key.getKeyType() == KeyType.Backspace) {
            if (name.length() > 0) {
                name = name.substring(0, name.length() - 1);
            }
        }
        if (key.getKeyType() == KeyType.Character) {
            char c = key.getCharacter();
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

    @Override
    public boolean finishInput() {
        return false;
    }

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
