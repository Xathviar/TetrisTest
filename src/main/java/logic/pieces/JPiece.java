package logic.pieces;

import logic.Grid;
import logic.TetrisField;

import java.awt.*;

/**
 * Represents the J-Piece in a Tetris game.
 * Inherits from the Tetromino class.
 */
public class JPiece extends Tetromino<JPiece> {

    /**
     * Represents the J-Piece in a Tetris game.
     * Inherits from the Tetromino class.
     *
     * @param field the TetrisField object to which the J-Piece belongs
     */
    public JPiece(TetrisField field) {
        super("J-Piece", Color.BLUE, field, 1);
    }

    @Override
    public void initGrid() {
        Boolean[][] position = new Boolean[][]{{true, false, false}, {true, true, true}, {false, false, false}};
        grid[0] = new Grid(position, color, field, this.x, this.y);
        position = new Boolean[][]{{false, true, true}, {false, true, false}, {false, true, false}};
        grid[1] = new Grid(position, color, field, this.x, this.y);
        position = new Boolean[][]{{false, false, false}, {true, true, true}, {false, false, true}};
        grid[2] = new Grid(position, color, field, this.x, this.y);
        position = new Boolean[][]{{false, true, false}, {false, true, false}, {true, true, false}};
        grid[3] = new Grid(position, color, field, this.x, this.y);
    }


    @Override
    public void initWallKicks() {
        wallKicks = wallKicksAllButI();
    }


    @Override
    public JPiece newPiece(TetrisField field) {
        return new JPiece(field);
    }
}
