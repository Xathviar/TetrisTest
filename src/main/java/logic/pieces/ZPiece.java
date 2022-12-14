package logic.pieces;

import logic.Grid;
import logic.TetrisField;

import java.awt.*;

public class ZPiece extends Tetromino<ZPiece> {

    public ZPiece(TetrisField field) {
        super("Z-Piece", Color.RED, field);
    }

    @Override
    public void initGrid() {
        Boolean[][] position = new Boolean[][]{{true, true, false}, {false, true, true}, {false, false, false}};
        grid[0] = new Grid(position, color, field, this.x, this.y);
        position = new Boolean[][]{{false, false, true}, {false, true, true}, {false, true, false}};
        grid[1] = new Grid(position, color, field, this.x, this.y);
        position = new Boolean[][]{{false, false, false}, {true, true, false}, {false, true, true}};
        grid[2] = new Grid(position, color, field, this.x, this.y);
        position = new Boolean[][]{{false, true, false}, {true, true, false}, {true, false, false}};
        grid[3] = new Grid(position, color, field, this.x, this.y);

    }

    @Override
    public void initWallKicks() {
        wallKicks = wallKicksAllButI();
    }

    @Override
    public ZPiece newPiece(TetrisField field) {
        return new ZPiece(field);
    }
}
