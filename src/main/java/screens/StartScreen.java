package screens;

import config.Constants;
import Helper.TerminalHelper;

import com.googlecode.lanterna.input.KeyType;
import logic.HighScore;
import lombok.extern.slf4j.Slf4j;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import static Helper.TerminalHelper.writeBoxAt;

@SuppressWarnings("ResultOfMethodCallIgnored")
@Slf4j
public class StartScreen implements Screen {
    public final Set<HighScore> scores;

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
            File file = new File("highscores.txt");
            try {
                file.createNewFile();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {
        terminal.clear();
        TerminalHelper.writeTetrisLogo(terminal);
        terminal.write("Press 'o' to play offline Tetris", 1, 12);
        terminal.writeCenter("-- press [enter] to start --", terminal.getHeightInCharacters() - 1);
        int maximum = terminal.getHeightInCharacters() - 30 > 1 ? 10 : terminal.getHeightInCharacters() - 22;
        if (maximum > -1) {
            writeBoxAt(terminal, terminal.getWidthInCharacters() / 2 - 20, 15, 41, maximum + 5);
            terminal.writeCenter("      Name      | Level | Score | Time ", 16);
            terminal.writeCenter("---------------------------------------", 17);
            int i = 0;
            for (HighScore score : scores) {
                if (i == maximum + 1) {
                    break;
                }
                switch (i) {
                    case 0:
                        terminal.writeCenter(score.toString(), 18 + i, Constants.firstPlayer);
                        break;
                    case 1:
                        terminal.writeCenter(score.toString(), 18 + i, Constants.secondPlayer);
                        break;
                    case 2:
                        terminal.writeCenter(score.toString(), 18 + i, Constants.thirdPlayer);
                        break;
                    default:
                        terminal.writeCenter(score.toString(), 18 + i);
                }
                i++;
            }
        }
    }

    @Override
    public Screen respondToUserInput(KeyEvent key, AsciiPanel terminal) {
        if (key.getKeyCode() == KeyEvent.VK_ENTER) {
            LoginScreen screen = new LoginScreen();
            screen.displayOutput(terminal);
            return screen;
        }
        if (key.getKeyChar() != KeyEvent.CHAR_UNDEFINED && Character.toLowerCase(key.getKeyChar()) == 'o') {
            PlayOfflineScreen screen = new PlayOfflineScreen(terminal);
            screen.displayOutput(terminal);
            return screen;
        }
        if (key.getKeyChar() != KeyEvent.CHAR_UNDEFINED && Character.toLowerCase(key.getKeyChar()) == 'e') {
            PlayOnlineScreen screen = new PlayOnlineScreen(terminal, false);
            screen.displayOutput(terminal);
            return screen;
        }

        return this;
    }

    @Override
    public boolean finishInput() {
        MainClass.aClass.screen = new LoginScreen();
        return true;
    }
}
