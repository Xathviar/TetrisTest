package logic.pieces;

import logic.Grid;
import logic.TetrisField;

import java.awt.*;

public class IPiece extends Tetromino <IPiece> {

    public IPiece(TetrisField field) {
        super("I-Piece", Color.CYAN, field);
    }

    @Override
    public void initGrid() {
        Boolean[][] position = new Boolean[][]{{false, false, false, false}, {true, true, true, true}, {false, false, false, false}, {false, false, false, false}};
        grid[0] = new Grid(position, color, field, this.x, this.y);
        position = new Boolean[][]{{false, false, true, false}, {false, false, true, false}, {false, false, true, false}, {false, false, true, false}};
        grid[1] = new Grid(position, color, field, this.x, this.y);
        position = new Boolean[][]{{false, false, false, false}, {false, false, false, false}, {true, true, true, true}, {false, false, false, false}};
        grid[2] = new Grid(position, color, field, this.x, this.y);
        position = new Boolean[][]{{false, true, false, false}, {false, true, false, false}, {false, true, false, false}, {false, true, false, false}};
        grid[3] = new Grid(position, color, field, this.x, this.y);
    }

    @Override
    public void initWallKicks() {
        Point test1 = new Point(0, 0);
        Point test2 = new Point(-2, 0);
        Point test3 = new Point(1, 0);
        Point test4 = new Point(-2, -1);
        Point test5 = new Point(1, 2);
        wallKicks[0][0] = test1;
        wallKicks[0][1] = test2;
        wallKicks[0][2] = test3;
        wallKicks[0][3] = test4;
        wallKicks[0][4] = test5;
        test2 = new Point(-1, 0);
        test3 = new Point(2, 0);
        test4 = new Point(-1, 2);
        test5 = new Point(2, -1);
        wallKicks[1][0] = test1;
        wallKicks[1][1] = test2;
        wallKicks[1][2] = test3;
        wallKicks[1][3] = test4;
        wallKicks[1][4] = test5;
        test2 = new Point(2, 0);
        test3 = new Point(-1, 0);
        test4 = new Point(2, 1);
        test5 = new Point(-1, -2);
        wallKicks[2][0] = test1;
        wallKicks[2][1] = test2;
        wallKicks[2][2] = test3;
        wallKicks[2][3] = test4;
        wallKicks[2][4] = test5;
        test2 = new Point(-1, 0);
        test3 = new Point(-2, 0);
        test4 = new Point(1, -2);
        test5 = new Point(-2, 1);
        wallKicks[3][0] = test1;
        wallKicks[3][1] = test2;
        wallKicks[3][2] = test3;
        wallKicks[3][3] = test4;
        wallKicks[3][4] = test5;
    }

    @Override
    public IPiece newPiece(TetrisField field) {
        return new IPiece(field);
    }
}
