package logic;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;


/**
 * The Grid class represents a grid of the Tetromino.
 * It contains information about the position, color, and set points of the grid.
 */
@Getter
@Setter
public class Grid {

    /**
     * The setPoints variable represents the set of points that make up a Tetromino grid.
     * It is a 2-dimensional Boolean array where each element represents a pixel of the grid.
     * A value of true indicates that the pixel is set (occupied), and false indicates that it is not set (empty).
     */
    private final Boolean[][] setPoints;

    /**
     * The TetrisField variable represents the field on which the Tetrominoes are placed.
     * It provides methods to check for valid positions and free pixels.
     */
    private final TetrisField field;


    /**
     * The Horizontal Coordinate
     */
    public int x;

    /**
     * The Vertical Coordinate
     */
    public int y;

    /**
     * The Color class represents the color of a Tetromino grid.
     */
    private Color color;

    /**
     * Constructs a Grid object with the given set of points, color, field, x and y coordinates.
     *
     * @param setPoints the set of points that make up the Tetromino grid
     * @param color the color of the Tetromino grid
     * @param field the field on which the Tetrominoes are placed
     * @param x the horizontal coordinate
     * @param y the vertical coordinate
     */
    public Grid(Boolean[][] setPoints, Color color, TetrisField field, int x, int y) {
        this.setPoints = setPoints;
        this.color = color;
        this.x = x;
        this.y = y;
        this.field = field;
    }

    /**
     * Moves the Tetromino grid to the left.
     *
     * @return true if the move is successful, false otherwise
     */
    public boolean moveLeft() {
        if (!isValidPosition(x - 1, y)) {
            return false;
        }
        this.x--;
        return true;
    }

    /**
     * Moves the Tetromino grid to the right.
     *
     * @return true if the move is successful, false otherwise
     */
    public boolean moveRight() {
        if (!isValidPosition(x + 1, y)) {
            return false;
        }
        this.x++;
        return true;
    }

    /**
     * Moves the Tetromino grid down.
     *
     * @return true if the move is successful, false otherwise
     */
    public boolean moveDown() {
        if (!isValidPosition(x, y + 1)) {
            return false;
        }
        this.y++;
        return true;
    }

    /**
     * Determines whether a given position is valid for a Tetromino grid.
     *
     * @param x the x-coordinate of the position
     * @param y the y-coordinate of the position
     * @return true if the position is valid, false otherwise
     */
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


}
