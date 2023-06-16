package Helper;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class TerminalHelper {
    Terminal terminal;

    public TerminalHelper(Terminal terminal) {
        this.terminal = terminal;
    }

    public void write(String msg, int x, int y) {
        try {
            terminal.setCursorPosition(x, y);
            terminal.putString(msg);
            flush();
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
            flush();
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
        int x = getWidthInCharacters() / 2 - (msg.length() - 2);
        write(msg, x, y);
    }

    public void clear() {
        try {
            terminal.clearScreen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void flush() {
        try {
            terminal.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
