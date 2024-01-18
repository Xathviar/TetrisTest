package logic;

import config.Constants;

import logic.pieces.Tetromino;
import lombok.Getter;
import screens.AsciiPanel;

import java.util.Arrays;

import static config.Constants.BLOCK;
import static config.Constants.backgroundColor;
import static helper.TerminalHelper.writeBoxAt;

/**
 * OpponentTetrisField represents the game field for the opponent in a Tetris game.
 * It stores and manages the tetromino pieces and their positions on the field.
 */
@Getter
public class OpponentTetrisField {

    /**
     * The Height of the Tetris Field
     */
    private final int SCREEN_HEIGHT = 20;

    /**
     * The Width of the Tetris Field
     */
    private final int SCREEN_WIDTH = 10;

    /**
     * This 2D {@link Point} array stores the Tetris Field
     */
    private final Point[][] points = new Point[50][10];

    /**
     * On which X Coordinate to begin printing the Tetris Field
     */
    private final int startX;

    /**
     * On which Y Coordinate to begin printing the Tetris Field
     */
    private final int startY;

    /**
     * Represents the opponent's Tetris field.
     * <p>
     * This class provides a representation of the opponent's Tetris field in a multiplayer game.
     * Each field is a grid of points that can be filled with Tetrominos.
     *
     * @param startX On which X Coordinate to begin printing the Tetris Field.
     * @param startY On which Y Coordinate to begin printing the Tetris Field.
     */
    public OpponentTetrisField(int startX, int startY) {
        for (Point[] point : points) {
            Arrays.fill(point, new Point(true, backgroundColor));
        }

        this.startX = startX;
        this.startY = startY;
    }

    /**
     * Adds a given Tetromino piece to the opponent's Tetris field grid.
     * <p>
     * This method adds the given Tetromino piece to the opponent's Tetris field grid
     * by calling the addGrid method with the Tetromino's returned grid representation.
     * It then checks for any cleared lines in the grid by invoking the checkForClearedLines method.
     *
     * @param tetromino The Tetromino piece to be added to
     */
    public void addPiece(Tetromino tetromino) {
        addGrid(tetromino.returnPiece());
        checkForClearedLines();
    }

    /**
     * Adds a given Grid to the points grid of the opponent's Tetris field.
     * <p>
     * The method iterates over the given grid's setPoints array and adds the corresponding points to the points grid
     * of the opponent's Tetris field. Each set point is represented by a new Point object with isFree set to false
     * and the color of the grid. After adding the grid, the method checks for any cleared lines in the points grid
     * by invoking the checkForClearedLines method.
     *
     * @param grid The Grid to be added to the points grid of the opponent's Tetris field
     */
    private void addGrid(Grid grid) {
        Boolean[][] gridPoints = grid.getSetPoints();
        for (int y = 0; y < gridPoints.length; y++) {
            for (int x = 0; x < gridPoints[y].length; x++) {
                if (gridPoints[y][x]) {
                    points[y + grid.y][x + grid.x] = new Point(false, grid.getColor());
                }
            }
        }
        checkForClearedLines();
    }


    /**
     * Checks for cleared lines in the Tetris field grid.
     * This method iterates through each row of the grid and checks if all points in that row
     * are occupied by Tetrominos. If a row is completely occupied, it clears the line by
     * resetting all points in that row. It then moves the rest of the grid down by one row.
     * This method is invoked
     */
    private void checkForClearedLines() {
        a:
        for (int y = 0; y < points.length; y++) {
            for (int x = 0; x < points[y].length; x++) {
                if (points[y][x].isFree()) {
                    continue a;
                }
                if (x + 1 == points[y].length) {
                    clearLine(y);
                    moveRestDown(y);
                }
            }
        }
    }

    /**
     * Clear a line of the Opponent Tetris Field
     *
     * @param y the y-position of the line that should be cleared
     */
    private void clearLine(int y) {
        for (Point point : points[y]) {
            point.resetPoint();
        }
    }

    /**
     * Moves the rest of the grid down by one row, starting from the specified row position.
     *
     * @param y The row position from which to start moving the rest of the grid down.
     */
    private void moveRestDown(int y) {
        for (int i = y; i > 0; i--) {
            System.arraycopy(points[i - 1], 0, points[i], 0, points[y].length);
        }
    }

    /**
     * Prints the Tetris field to the given AsciiPanel.
     *
     * @param terminal The AsciiPanel instance where the Tetris field will be printed.
     */
    public void printTetrisField(AsciiPanel terminal) {
        drawBoard(terminal);
        for (int i = 0; i < SCREEN_HEIGHT; i++) {
            for (int j = 0; j < SCREEN_WIDTH; j++) {
                if (points[i + 30][j].isFree()) {
                    terminal.write(' ', startX + j, startY + i, backgroundColor, backgroundColor);
                } else {
                    terminal.write(BLOCK, startX + j, startY + i, points[i + 30][j].getColor());
                }
            }
        }
    }

    /**
     * Draws the Tetris board on the given AsciiPanel instance.
     *
     * @param terminal The AsciiPanel instance where the Tetris board will be drawn.
     */
    private void drawBoard(AsciiPanel terminal) {
        int height = startY - 1;
        int width = startX - 1;
        writeBoxAt(terminal, width, height, 12, 22);
    }

    /**
     * Adds garbage lines to the opponent's Tetris field grid.
     * <p>
     * This method adds a specified number of garbage lines to the opponent's Tetris field grid.
     * The lines parameter determines the number of lines to add, and the garbageGap parameter
     * determines the gap position in each line where the garbage blocks will be placed.
     *
     * @param lines      The number of garbage lines to add.
     * @param garbageGap The position of the gap in each line where the garbage blocks will be placed.
     */
    public void addGarbage(int lines, int garbageGap) {
        for (int y = lines; y < 50; y++) {
            System.arraycopy(points[y], 0, points[y - lines], 0, 10);
        }
        for (int y = 0; y < lines; y++) {
            for (int x = 0; x < 10; x++) {
                if (x == garbageGap) {
                    points[y + 50 - lines][x] = new Point(true, backgroundColor);
                } else {
                    points[y + 50 - lines][x] = new Point(false, Constants.disabledColor);
                }
            }
        }

    }

}
