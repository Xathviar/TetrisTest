package logic.pieces;

import logic.Grid;
import logic.TetrisField;

import java.awt.*;

/**
 * Represents the L-Piece in a Tetris game.
 * Inherits from the Tetromino class.
 */
public class LPiece extends Tetromino<LPiece> {

    /**
     * Represents the L-Piece in a Tetris game.
     * Inherits from the Tetromino class.
     *
     * @param field the TetrisField object to which the L-Piece belongs
     */
    public LPiece(TetrisField field) {
        super("L-Piece", Color.ORANGE, field, 2);
    }

    @Override
    public void initGrid() {
        Boolean[][] position = new Boolean[][]{{false, false, true}, {true, true, true}, {false, false, false}};
        grid[0] = new Grid(position, color, field, this.x, this.y);
        position = new Boolean[][]{{false, true, false}, {false, true, false}, {false, true, true}};
        grid[1] = new Grid(position, color, field, this.x, this.y);
        position = new Boolean[][]{{false, false, false}, {true, true, true}, {true, false, false}};
        grid[2] = new Grid(position, color, field, this.x, this.y);
        position = new Boolean[][]{{true, true, false}, {false, true, false}, {false, true, false}};
        grid[3] = new Grid(position, color, field, this.x, this.y);
    }

    @Override
    public void initWallKicks() {
        wallKicks = wallKicksAllButI();
    }

    @Override
    public LPiece newPiece(TetrisField field) {
        return new LPiece(field);
    }
}
