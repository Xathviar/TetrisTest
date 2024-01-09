package logic.pieces;

import com.googlecode.lanterna.TextColor;
import logic.Grid;
import logic.TetrisField;

import static java.awt.Color.MAGENTA;

public class TPiece extends Tetromino<TPiece> {

    public TPiece(TetrisField field) {
        super("T-Piece", MAGENTA, field, 5);
    }

    @Override
    public void initGrid() {
        Boolean[][] position = new Boolean[][]{{false, true, false}, {true, true, true}, {false, false, false}};
        grid[0] = new Grid(position, color, field, this.x, this.y);
        position = new Boolean[][]{{false, true, false}, {false, true, true}, {false, true, false}};
        grid[1] = new Grid(position, color, field, this.x, this.y);
        position = new Boolean[][]{{false, false, false}, {true, true, true}, {false, true, false}};
        grid[2] = new Grid(position, color, field, this.x, this.y);
        position = new Boolean[][]{{false, true, false}, {true, true, false}, {false, true, false}};
        grid[3] = new Grid(position, color, field, this.x, this.y);
    }

    @Override
    public void initWallKicks() {
        wallKicks = wallKicksAllButI();
    }

    @Override
    public TPiece newPiece(TetrisField field) {
        return new TPiece(field);
    }
}
