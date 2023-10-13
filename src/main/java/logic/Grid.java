package logic;

import com.googlecode.lanterna.TextColor;
import lombok.Getter;

@Getter
public class Grid {
    private final Boolean[][] setPoints;
    private final TetrisField field;
    /**
     * The Horizontal Coordinate
     */
    public int x;
    public int y;
    private TextColor color;

    public Grid(Boolean[][] setPoints, TextColor color, TetrisField field, int x, int y) {
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


    public TextColor getColor() {
        return color;
    }

    public void setColor(TextColor color) {
        this.color = color;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

}
