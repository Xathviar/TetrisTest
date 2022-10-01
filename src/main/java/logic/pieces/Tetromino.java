package logic.pieces;

import logic.Grid;
import logic.TetrisField;

import java.awt.*;

public abstract class Tetromino <T> {
    private final String name;
    final Color color;
    final Grid[] grid;

    private int currentRotation;

    final TetrisField field;

    Point[][] wallKicks;

    int x;
    int y;

    private boolean readyToFix;

    Tetromino(String name, Color color, TetrisField field) {
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
    }

    public abstract void initGrid();

    /**
     * Implement SRS here
     */
    public abstract void initWallKicks();


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

    public Grid returnPiece() {
        return grid[currentRotation];
    }

    public void movePieceLeft() {
        readyToFix = false;
        grid[currentRotation].moveLeft();
        this.x = grid[currentRotation].x;
        updateGrids();
    }


    public void movePieceRight() {
        readyToFix = false;
        grid[currentRotation].moveRight();
        this.x = grid[currentRotation].x;
        updateGrids();
    }

    public int hardDrop() {
        int counter = 0;
        while (true) {
            boolean moved = grid[currentRotation].moveDown();
            if (moved) {
                counter ++;
            } else {
                break;
            }
        }
        return counter;
    }

    public void softDrop()  {
        grid[currentRotation].moveDown();
        this.y = grid[currentRotation].y;
        updateGrids();
    }

    public boolean gameTick() {
        if (!grid[currentRotation].moveDown()) {
            System.out.println("HELLO");
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

    void updateGrids() {
        for (Grid grid1 : grid) {
            grid1.x = this.x;
            grid1.y = this.y;
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public void resetPosition() {
        this.x = 3;
        this.y = 30;
        this.currentRotation = 0;
        updateGrids();
    }

    public Grid[] getGrid() {
        return grid;
    }

    public Tetromino clonePiece() {
        Tetromino ret = (Tetromino) this.newPiece(field);
        ret.x = this.x;
        ret.y = this.y;
        ret.setCurrentRotation(this.getCurrentRotation());
        ret.updateGrids();
        return ret;
    }

    public void changeColorGrey() {
        grid[0].setColor(Color.GRAY);
    }

    public void returnNormalColor() {
        grid[0].setColor(color);
    }

    public int getCurrentRotation() {
        return currentRotation;
    }

    public void setCurrentRotation(int currentRotation) {
        this.currentRotation = currentRotation;
    }

    public abstract T newPiece(TetrisField field);

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

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
