package Helper;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.Terminal;
import nakama.com.google.common.base.Strings;

import java.io.IOException;


public class TerminalHelper {
    Terminal terminal;

    private static final String[] tetrisLogo = (
            " _________  ________  _________  _______     _____   ______  \n" +
                    "|  _   _  ||_   __  ||  _   _  ||_   __ \\   |_   _|.' ____ \\ \n" +
                    "|_/ | | \\_|  | |_ \\_||_/ | | \\_|  | |__) |    | |  | (___ \\_|\n" +
                    "    | |      |  _| _     | |      |  __ /     | |   _.____`. \n" +
                    "   _| |_    _| |__/ |   _| |_    _| |  \\ \\_  _| |_ | \\____) |\n" +
                    "  |_____|  |________|  |_____|  |____| |___||_____| \\______.'\n").split("\n");

    public TerminalHelper(Terminal terminal) {
        this.terminal = terminal;
    }

    public void write(String msg, int x, int y) {
        try {
            terminal.setCursorPosition(x, y);
            terminal.putString(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void hideCursor() {
        try {
            terminal.setCursorPosition(this.getWidthInCharacters(), this.getHeightInCharacters());
            terminal.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void write(String msg, int x, int y, TextColor fg) {
        try {
            terminal.setForegroundColor(fg);
            write(msg, x, y);
            terminal.setForegroundColor(TextColor.ANSI.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void write(String msg, int x, int y, TextColor fg, TextColor bg) {
        try {
            terminal.setForegroundColor(fg);
            terminal.setBackgroundColor(bg);
            write(msg, x, y);
            terminal.setForegroundColor(TextColor.ANSI.DEFAULT);
            terminal.setBackgroundColor(TextColor.ANSI.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void write(char msg, int x, int y) {
        try {
            terminal.setCursorPosition(x, y);
            terminal.putCharacter(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void write(char msg, int x, int y, TextColor fg) {
        try {
            terminal.setForegroundColor(fg);
            write(msg, x, y);
            terminal.setForegroundColor(TextColor.ANSI.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void write(char msg, int x, int y, TextColor fg, TextColor bg) {
        try {
            terminal.setForegroundColor(fg);
            terminal.setBackgroundColor(bg);
            write(msg, x, y);
            terminal.setForegroundColor(TextColor.ANSI.DEFAULT);
            terminal.setBackgroundColor(TextColor.ANSI.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getHeightInCharacters() {
        try {
            return terminal.getTerminalSize().getRows();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getWidthInCharacters() {
        try {
            return terminal.getTerminalSize().getColumns();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeCenter(String msg, int y) {
        int x = getWidthInCharacters() / 2 - (msg.length() / 2);
        write(msg, x, y);
    }

    public void writeCenter(String msg, int y, TextColor.ANSI color) {
        int x = getWidthInCharacters() / 2 - (msg.length() / 2);
        write(msg, x, y, color);
    }

    public void clear() {
        try {
            terminal.clearScreen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeTetrisLogo() {
        for (int i = 0; i < tetrisLogo.length; i++) {
            this.writeCenter(tetrisLogo[i], i + 1);
        }
    }


    public void flush() {
        try {
            terminal.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeBoxAt(TerminalHelper terminal, int x, int y, int width, int height) {
        char box = '#';
        String horizontalLines = Strings.repeat(String.valueOf(box), width);
        String boxMiddleLines = box + Strings.repeat(" ", width - 2) + box;

        terminal.write(horizontalLines, x, y++);
        for (int i = 0; i < height - 2; i++) {
            terminal.write(boxMiddleLines, x, y++);
        }
        terminal.write(horizontalLines, x, y);
    }


}
