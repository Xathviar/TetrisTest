package Helper;

import asciiPanel.AsciiPanel;
import com.googlecode.lanterna.TextColor;
import logic.GarbagePieceHandler;
import nakama.com.google.common.base.Ascii;
import nakama.com.google.common.base.Strings;

import java.awt.*;
import java.io.IOException;


public class TerminalHelper {

    private static final String[] tetrisLogo = (
            " _________  ________  _________  _______     _____   ______  \n" +
                    "|  _   _  ||_   __  ||  _   _  ||_   __ \\   |_   _|.' ____ \\ \n" +
                    "|_/ | | \\_|  | |_ \\_||_/ | | \\_|  | |__) |    | |  | (___ \\_|\n" +
                    "    | |      |  _| _     | |      |  __ /     | |   _.____`. \n" +
                    "   _| |_    _| |__/ |   _| |_    _| |  \\ \\_  _| |_ | \\____) |\n" +
                    "  |_____|  |________|  |_____|  |____| |___||_____| \\______.'\n").split("\n");



    public static void writeTetrisLogo(AsciiPanel terminal) {
        for (int i = 0; i < tetrisLogo.length; i++) {
            terminal.writeCenter(tetrisLogo[i], i + 1);
        }
    }



    public static void writeBoxAt(AsciiPanel terminal, int x, int y, int width, int height) {
        char box = '#';
        String horizontalLines = Strings.repeat(String.valueOf(box), width);
        String boxMiddleLines = box + Strings.repeat(" ", width - 2) + box;

        terminal.write(horizontalLines, x, y++);
        for (int i = 0; i < height - 2; i++) {
            terminal.write(boxMiddleLines, x, y++);
        }
        terminal.write(horizontalLines, x, y);
    }

    public static void writeGarbageLine(AsciiPanel terminal, int x, int y, int height, GarbagePieceHandler garbagePieceHandler) {
        for (int i = height - 2; i >= 0; i--) {
            terminal.write("#", x, y++, garbagePieceHandler.shouldBeGarbageIndicator(i) ? Color.MAGENTA : Color.LIGHT_GRAY);
        }
    }


}
