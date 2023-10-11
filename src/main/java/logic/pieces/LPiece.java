package logic.pieces;

import com.googlecode.lanterna.TextColor;
import logic.Grid;
import logic.TetrisField;

public class LPiece extends Tetromino<LPiece> {

    public LPiece(TetrisField field) {
        super("L-Piece", TextColor.ANSI.RED_BRIGHT, field, 2);
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
