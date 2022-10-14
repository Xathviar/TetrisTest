package logic;

import java.awt.*;

public class Grid {
    private final Boolean[][] setPoints;
    /**
     * The Horizontal Coordinate
     */
    public int x;
    public int y;
    private Color color;

    private final TetrisField field;

    public Grid(Boolean[][] setPoints, Color color, TetrisField field, int x, int y) {
        this.setPoints = setPoints;
        this.color = color;
        this.x = x;
        this.y = y;
        this.field = field;
    }

    public boolean moveLeft() {
        if (!isValidPosition(x - 1, y)) {
            return false;
        }
        this.x--;
        return true;
    }

    public boolean moveRight() {
        if (!isValidPosition(x + 1, y)) {
            return false;
        }
        this.x++;
        return true;
    }

    public boolean moveDown() {
        if (!isValidPosition(x, y + 1)) {
            return false;
        }
        this.y++;
        return true;
    }

    public boolean isValidPosition(int x, int y) {
        for (int i = 0; i < setPoints.length; i++) {
            for (int j = 0; j < setPoints[i].length; j++) {
                if (setPoints[i][j]) {
                    if (!field.isFreePixel(j + x, i + y)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    public Boolean[][] getSetPoints() {
        return setPoints;
    }


    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}
