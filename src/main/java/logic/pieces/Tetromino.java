package logic.pieces;

import logic.Grid;
import logic.TetrisField;

import java.awt.*;

public abstract class Tetromino <T> {
    private final String name;
    final Color color;
    final Grid[] grid;

    private int currentRotation;

    TetrisField field;

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
        y = 0;
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
        for (Point point : wallKicks[tempRotation]) {
            int temp_x = point.x + x;
            int temp_y = point.y + y;
            if (grid[tempRotation].isValidPosition(temp_x, temp_y)) {
                this.x = temp_x;
                this.y = temp_y;
                updateGrids();
                currentRotation = tempRotation;
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
        this.y = 0;
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getCurrentRotation() {
        return currentRotation;
    }

    public void setCurrentRotation(int currentRotation) {
        this.currentRotation = currentRotation;
    }

    public abstract T newPiece(TetrisField field);
}
