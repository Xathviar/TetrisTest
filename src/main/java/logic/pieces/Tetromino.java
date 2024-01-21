package logic.pieces;

import config.Constants;
import logic.Grid;
import logic.TetrisField;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

/**
 * Abstract class representing a Tetromino in a Tetris game.
 *
 * @param <T> the type of Tetromino being represented
 */
@Getter
@Setter
public abstract class Tetromino<T> {

    /**
     * Which Color the Piece has
     */
    final Color color;

    /**
     * The four Grids of the Tetromino
     */
    final Grid[] grid;

    /**
     * The Tetrisfield on which the Tetromino will be placed
     */
    final TetrisField field;

    /**
     * The Name of the Tetromino
     */
    private final String name;

    /**
     * A 2D Point Array which is used for Wall Kicks in the <a href="https://tetris.fandom.com/wiki/SRS">SRS</a> system.
     *
     */
    Point[][] wallKicks;

    /**
     * The X Coordinate of the Tetromino
     */
    int x;

    /**
     * The Y Coordinate of the Tetromino
     */
    int y;

    /**
     * The current Rotation of the Tetromino
     */
    private int currentRotation;

    /**
     * Indicates whether the Tetromino is ready to be fixed or not.
     */
    private boolean readyToFix;

    /**
     * The ID of the Tetromino which is used for {@link dto.UpdateBoardStateDTO} so that we can relate which integer relates to which Tetromino Piece <br><br>
     *
     * 0: I-Piece <br>
     * 1: J-Piece <br>
     * 2: L-Piece <br>
     * 3: O-Piece <br>
     * 4: S-Piece <br>
     * 5: T-Piece <br>
     * 6: Z-Piece <br>
     */
    private final int pieceId;

    /**
     * Constructs a Tetromino object with the given parameters.
     *
     * @param name     the name of the Tetromino
     * @param color    the color of the Tetromino
     * @param field    the TetrisField object to which the Tetromino belongs
     * @param pieceId  the ID of the Tetromino piece
     */
    Tetromino(String name, Color color, TetrisField field, int pieceId) {
        this.name = name;
        this.color = color;
        this.grid = new Grid[4];
        currentRotation = 0;
        this.field = field;
        x = 3;
        y = 30;
        wallKicks = new Point[4][5];
        initGrid();
        initWallKicks();
        this.pieceId = pieceId;
    }

    /**
     * Create all Wall Kicks except for the I Piece since the I Piece has special Wallkicks
     * @return the initialized 2 Dimensional Wallkick Array {@link Tetromino#wallKicks}
     */
    public static Point[][] wallKicksAllButI() {
        Point[][] wallKicks = new Point[4][5];
        Point test1 = new Point(0, 0);
        Point test2 = new Point(-1, 0);
        Point test3 = new Point(-1, -1);
        Point test4 = new Point(0, 2);
        Point test5 = new Point(-1, 2);
        wallKicks[0][0] = test1;
        wallKicks[0][1] = test2;
        wallKicks[0][2] = test3;
        wallKicks[0][3] = test4;
        wallKicks[0][4] = test5;
        test2 = new Point(1, 0);
        test3 = new Point(1, 1);
        test4 = new Point(0, -2);
        test5 = new Point(1, -2);
        wallKicks[1][0] = test1;
        wallKicks[1][1] = test2;
        wallKicks[1][2] = test3;
        wallKicks[1][3] = test4;
        wallKicks[1][4] = test5;
        test2 = new Point(1, 0);
        test3 = new Point(1, -1);
        test4 = new Point(0, 2);
        test5 = new Point(1, 2);
        wallKicks[2][0] = test1;
        wallKicks[2][1] = test2;
        wallKicks[2][2] = test3;
        wallKicks[2][3] = test4;
        wallKicks[2][4] = test5;
        test2 = new Point(-1, 0);
        test3 = new Point(-1, 1);
        test4 = new Point(0, -2);
        test5 = new Point(-1, -2);
        wallKicks[3][0] = test1;
        wallKicks[3][1] = test2;
        wallKicks[3][2] = test3;
        wallKicks[3][3] = test4;
        wallKicks[3][4] = test5;
        return wallKicks;
    }

    /**
     * Initializes the grid for the Tetromino object.
     * This method is implemented by each subclass of Tetromino.
     */
    public abstract void initGrid();

    /**
     * Implement SRS here
     */
    public abstract void initWallKicks();

    /**
     * Rotates the Tetromino counterclockwise. <br><br>
     *
     * If the current rotation is 0, the next rotation will be 3.
     * Otherwise, the next rotation will be the current rotation minus 1. <br><br>
     *
     * The method then iterates through the wallKicks array at the next rotation index.
     * For each Point in the array, it calculates a temporary x and y position based
     * on the current position, the inverse of the current Point, and the next rotation. <br><br>
     *
     * If the grid at the next rotation has a valid position for the temporary x and y,
     * the Tetromino's x and y positions are updated, the grids are updated,
     * the current rotation is set to the next rotation, readyToFix is set to false,
     * and the method returns. <br><br>
     *
     * @see Tetromino#currentRotation
     * @see Tetromino
     * */
    public void rotateCClockwise() {
        int tempRotation;
        if (currentRotation == 0)
            tempRotation = 3;
        else
            tempRotation = currentRotation - 1;
        for (Point point : wallKicks[tempRotation]) {
            int temp_x = (point.x * -1) + x;
            int temp_y = (point.y * -1) + y;
            if (grid[tempRotation].isValidPosition(temp_x, temp_y)) {
                this.x = temp_x;
                this.y = temp_y;
                updateGrids();
                currentRotation = tempRotation;
                readyToFix = false;
                return;
            }
        }
    }

    /**
     * Rotates the Tetromino clockwise. <br><br>
     *
     * If the current rotation is 3, the next rotation will be 0.
     * Otherwise, the next rotation will be the current rotation plus 1. <br><br>
     *
     * The method then iterates through the wallKicks array at the next rotation index.
     * For each Point in the array, it calculates a temporary x and y position based
     * on the current position, the inverse of the current Point, and the next rotation. <br><br>
     *
     * If the grid at the next rotation has a valid position for the temporary x and y,
     * the Tetromino's x and y positions are updated, the grids are updated,
     * the current rotation is set to the next rotation, readyToFix is set to false,
     * and the method returns. <br><br>
     *
     * @see Tetromino#currentRotation
     * @see Tetromino
     * */
    public void rotateClockwise() {
        int tempRotation;
        if (currentRotation == 3)
            tempRotation = 0;
        else
            tempRotation = currentRotation + 1;
        for (Point point : wallKicks[currentRotation]) {
            int temp_x = point.x + x;
            int temp_y = point.y + y;
            if (grid[tempRotation].isValidPosition(temp_x, temp_y)) {
                this.x = temp_x;
                this.y = temp_y;
                updateGrids();
                currentRotation = tempRotation;
                readyToFix = false;
                return;
            }
        }
    }

    /**
     * Returns the Grid of the current Tetromino at its current rotation
     * @return {@link Grid}
     */
    public Grid returnPiece() {
        return grid[currentRotation];
    }

    /**
     * Moves the Tetromino piece to the left.
     * If the Tetromino can move left, the x position of the Tetromino grid is decreased by 1.
     * Otherwise, the Tetromino's readyToFix flag is set to false.
     * After moving, the Tetromino's x position is updated with the x value of the current rotation grid.
     * Finally, the grids are updated accordingly.
     */
    public void movePieceLeft() {
        if (grid[currentRotation].moveLeft()) {
            readyToFix = false;
        }
        this.x = grid[currentRotation].x;
        updateGrids();
    }

    /**
     * Moves the Tetromino piece to the right.
     * If the Tetromino can move to the right, the x position of the Tetromino grid is increased by 1.
     * Otherwise, the Tetromino's readyToFix flag is set to false.
     * After moving, the Tetromino's x position is updated with the x value of the current rotation grid.
     * Finally, the grids are updated accordingly.
     */
    public void movePieceRight() {
        if (grid[currentRotation].moveRight()) {
            readyToFix = false;
        }
        this.x = grid[currentRotation].x;
        updateGrids();
    }

    /**
     * Drops the Tetromino piece as far down as possible.
     *
     * @return the number of rows the Tetromino has dropped
     */
    public int hardDrop() {
        int counter = 0;
        while (true) {
            boolean moved = grid[currentRotation].moveDown();
            if (moved) {
                counter++;
            } else {
                break;
            }
        }
        return counter;
    }

    public int instantSDF() {
        int counter = 0;
        while (true) {
            boolean moved = grid[currentRotation].moveDown();
            if (moved) {
                counter++;
            } else {
                break;
            }
        }
        this.x = grid[currentRotation].x;
        this.y = grid[currentRotation].y;
        updateGrids();
        return counter;
    }

    /**
     * Moves the Tetromino piece one row down.
     *
     * <p>
     * This method moves the current Tetromino piece one row down by calling the {@link Grid#moveDown()} method
     * of the current rotation grid. If the Tetromino can move down, the y position of the Tetromino is updated
     * with the new y value of the current rotation grid. The grids are then updated accordingly.
     * </p>
     */
    public void softDrop() {
        grid[currentRotation].moveDown();
        this.y = grid[currentRotation].y;
        updateGrids();
    }

    /**
     * Progresses the game by moving the Tetromino down one row.
     * If the Tetromino cannot move down, it checks if it is ready to fix.
     * If it is ready to fix, it returns true indicating that the current Tetromino should be fixed and a new Tetromino should be created.
     * If it is not ready to fix, it sets the readyToFix flag to true.
     * If the Tetromino can move down, it updates the y position of the Tetromino with the new y value of the current rotation grid.
     * It then calls the updateGrids() method to update the grids accordingly.
     * Finally, it returns false indicating that the game should continue without fixing the current Tetromino.
     *
     * @return true if the current Tetromino should be fixed and a new Tetromino should be created, false otherwise
     */
    public boolean gameTick() {
        if (!grid[currentRotation].moveDown()) {
            if (readyToFix) {
                readyToFix = false;
                return true;
            } else {
                readyToFix = true;
            }
        }
        this.y = grid[currentRotation].y;
        updateGrids();
        return false;
    }

    /**
     * Updates the position of the Tetromino grid. Each grid in the Tetromino is updated with the current x and y coordinates of the Tetromino.
     * This method is called after the x and y coordinates of the Tetromino are modified.
     */
    public void updateGrids() {
        for (Grid grid1 : grid) {
            grid1.x = this.x;
            grid1.y = this.y;
        }
    }

    @Override
    public String toString() {
        return String.format("%s: - %d|%d", name, x, y);
    }

    /**
     * Resets the position of the Tetromino by setting the x coordinate to 3, the y coordinate to 30, and the current rotation to 0.
     * The grids of the Tetromino are then updated with the new coordinates.
     */
    public void resetPosition() {
        this.x = 3;
        this.y = 30;
        this.currentRotation = 0;
        updateGrids();
    }

    /**
     * Creates a clone of the current Tetromino object. <br>
     *
     * This is used for creating the helper piece which displays the tetris Piece at the bottom
     * @return a new Tetromino object that is a copy of the current Tetromino
     */
    public Tetromino clonePiece() {
        Tetromino ret = (Tetromino) this.newPiece(field);
        ret.x = this.x;
        ret.y = this.y;
        ret.setCurrentRotation(this.getCurrentRotation());
        ret.updateGrids();
        return ret;
    }

    /**
     * Changes the color of the element at index 0 in the grid array to grey.
     * The grid array is a field of the Tetromino class.
     */
    public void changeColorGrey() {
        grid[0].setColor(Constants.disabledColor);
    }

    /**
     * Returns the normal color of the Tetromino by setting the color of the first grid element to the Tetromino's color.
     */
    public void returnNormalColor() {
        grid[0].setColor(color);
    }

    /**
     * Creates a new instance of the specified subclass of Tetromino with the given TetrisField.
     *
     * @param field the TetrisField object to which the Tetromino belongs
     * @return a new instance of the specified subclass of Tetromino
     */
    public abstract T newPiece(TetrisField field);

    /**
     * Sets the y coordinate of the Tetromino, and also updates the y coordinate for all the grids
     *
     * @param y the new y coordinate
     */
    public void setY(int y) {
        this.y = y;
        for (Grid grid1 : grid) {
            grid1.setY(y);
        }
    }

    /**
     * Sets the x coordinate of the Tetromino, and also updates the x coordinate for all the grids
     *
     * @param x the new x coordinate
     */
    public void setX(int x) {
        this.x = x;
        for (Grid grid1 : grid) {
            grid1.setX(x);
        }
    }

}
