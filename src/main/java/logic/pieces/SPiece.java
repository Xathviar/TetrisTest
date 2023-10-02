package logic.pieces;

import com.googlecode.lanterna.TextColor;
import logic.Grid;
import logic.TetrisField;

public class SPiece extends Tetromino<SPiece> {

    public SPiece(TetrisField field) {
        super("S-Piece", TextColor.ANSI.GREEN, field);
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
