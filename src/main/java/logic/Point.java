package logic;

import config.Constants;

import java.awt.*;

public class Point {
    private boolean isFree;
    private Color color;

    public Point(boolean isFree, Color color) {
        this.isFree = isFree;
        this.color = color;
    }

    public Color getColor() {
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
        color = Constants.backgroundColor;
    }
}
