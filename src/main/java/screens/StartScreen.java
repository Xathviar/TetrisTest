package screens;

import asciiPanel.AsciiPanel;
import logic.HighScore;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import static screens.PlayScreen.tetrisLogo;
import static screens.PlayScreen.writeBoxAt;

public class StartScreen implements Screen {
    private boolean initScreen = true;

    public Set<HighScore> scores;

    public StartScreen() {
        scores = new TreeSet<>();
        readHighScores();
    }

    private void readHighScores() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("highscores.txt"));
            for (String line : lines) {
                String[] lineSplit = line.split(";");
                String name = lineSplit[0];
                int level = Integer.parseInt(lineSplit[1]);
                long score = Long.parseLong(lineSplit[2]);
                int timePassed = Integer.parseInt(lineSplit[3]);
                scores.add(new HighScore(name, level, score, String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(timePassed), (TimeUnit.MILLISECONDS.toSeconds(timePassed) % 60))));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(scores);
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {
        if (initScreen) {
            terminal.clear();
            for (int i = 0; i < tetrisLogo.length; i++) {
                terminal.write(tetrisLogo[i], 5, i + 1);
            }
            terminal.writeCenter("-- press [enter] to start --", terminal.getHeightInCharacters() - 1);
            writeBoxAt(terminal, 15, 15, 41, 14);
            terminal.write("      Name      | Level | Score | Time ", 16, 16);
            terminal.write("---------------------------------------", 16, 17);
            int i = 0;
            for (HighScore score : scores) {
                if (i == 10) {
                    break;
                }
                terminal.write(score.toString(), 16, 18 + i);
                i++;
            }
            initScreen = false;
        }
    }

    @Override
    public Screen respondToUserInput(KeyEvent key, AsciiPanel terminal) {
        if (key.getKeyCode() == KeyEvent.VK_ENTER) {
            PlayScreen screen = new PlayScreen();
            screen.displayOutput(terminal);
            return screen;
        }
        return this;
    }
}
