package logic;

import com.googlecode.lanterna.TextColor;

public class Point {
    private boolean isFree;
    private TextColor color;

    public Point(boolean isFree, TextColor color) {
        this.isFree = isFree;
        this.color = color;
    }

    public TextColor getColor() {
        return color;
    }

    public boolean isFree() {
        return isFree;
    }

    @Override
    public String toString() {
        return isFree ? "O" : "X";
    }

    public void resetPoint() {
        isFree = true;
        color = TextColor.ANSI.BLACK;
    }
}
