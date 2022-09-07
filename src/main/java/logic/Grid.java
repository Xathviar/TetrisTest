package logic;

import java.awt.*;
import java.util.Arrays;

public class Grid {
    private final Boolean[][] setPoints;
    /**
     * The Horizontal Coordinate
     */
    public int x;
    public int y;
    private Color color;

    private TetrisField field;

    public Grid(Boolean[][] setPoints, Color color, TetrisField field, int x, int y) {
        this.setPoints = setPoints;
        this.color = color;
        this.x = x;
        this.y = y;
        this.field = field;
    }


    public void updateY(int newHeight) {
        //TODO Update Logic here I think
    }

    public void updateX(int newWidth) {

        //TODO Update Logic here I think
    }

    public void moveLeft() {
        if (!isValidPosition(x - 1, y)) {
            return;
        }
        this.x--;
    }

    public void moveRight() {
        if (!isValidPosition(x + 1, y)) {
            return;
        }
        this.x++;
    }

    public boolean moveDown() {
        Integer[] downMostPoints = new Integer[setPoints.length];
        Arrays.fill(downMostPoints, -1);
        for (int y = setPoints.length - 1; y > 0; y--) {
            for (int x = 0; x < setPoints[y].length; x++) {
                if (setPoints[y][x] && downMostPoints[x] == -1) {
                    downMostPoints[x] = y;
                }
            }
        }
        for (int x = 0; x < downMostPoints.length; x++) {
            if (downMostPoints[x] != -1) {
                if (downMostPoints[x] + 1 + this.y >= 20) {
                    return false;
                }
                if (!field.isFreePixel(x + this.x, downMostPoints[x] + 1 + this.y)) {
                    return false;
                }
            }
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

    public void setX(int x) {
        this.x = x;
    }
}
