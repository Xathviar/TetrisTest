package logic.pieces;

import com.googlecode.lanterna.TextColor;
import logic.Grid;
import logic.TetrisField;

import java.awt.*;
import java.util.Arrays;

public class OPiece extends Tetromino<OPiece> {

    public OPiece(TetrisField field) {
        super("O-Piece", TextColor.ANSI.YELLOW, field);
    }

    @Override
    public void initGrid() {
        Boolean[][] position = new Boolean[][]{{false, true, true, false}, {false, true, true, false}, {false, false, false, false}};
        grid[0] = new Grid(position, color, field, this.x, this.y);
        grid[1] = new Grid(position, color, field, this.x, this.y);
        grid[2] = new Grid(position, color, field, this.x, this.y);
        grid[3] = new Grid(position, color, field, this.x, this.y);
    }


    @Override
    public void initWallKicks() {
        for (Point[] wallKick : wallKicks) {
            Arrays.fill(wallKick, new Point(0, 0));
        }
    }

    @Override
    public OPiece newPiece(TetrisField field) {
        return new OPiece(field);
    }
}
