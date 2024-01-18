package logic.pieces;

import logic.Grid;
import logic.TetrisField;

import static java.awt.Color.GREEN;

/**
 * Represents the S-Piece in a Tetris game.
 * Inherits from the Tetromino class.
 */
public class SPiece extends Tetromino<SPiece> {

    /**
     * Represents the S-Piece in a Tetris game.
     * Inherits from the Tetromino class.
     *
     * @param field the TetrisField object to which the S-Piece belongs
     */
    public SPiece(TetrisField field) {
        super("S-Piece", GREEN, field, 4);
    }

    @Override
    public void initGrid() {
        Boolean[][] position = new Boolean[][]{{false, true, true}, {true, true, false}, {false, false, false}};
        grid[0] = new Grid(position, color, field, this.x, this.y);
        position = new Boolean[][]{{false, true, false}, {false, true, true}, {false, false, true}};
        grid[1] = new Grid(position, color, field, this.x, this.y);
        position = new Boolean[][]{{false, false, false}, {false, true, true}, {true, true, false}};
        grid[2] = new Grid(position, color, field, this.x, this.y);
        position = new Boolean[][]{{true, false, false}, {true, true, false}, {false, true, false}};
        grid[3] = new Grid(position, color, field, this.x, this.y);

    }

    @Override
    public void initWallKicks() {
        wallKicks = wallKicksAllButI();
    }

    @Override
    public SPiece newPiece(TetrisField field) {
        return new SPiece(field);
    }
}
