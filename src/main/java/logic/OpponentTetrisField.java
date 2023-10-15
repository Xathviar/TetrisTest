package logic;

import Helper.TerminalHelper;
import com.googlecode.lanterna.TextColor;
import logic.pieces.Tetromino;
import lombok.Getter;

import java.util.Arrays;

import static Helper.TerminalHelper.writeBoxAt;

@Getter
public class OpponentTetrisField {

    private final int SCREEN_HEIGHT = 20;

    private final int SCREEN_WIDTH = 10;

    private final char BLOCK = '#';

    private final String BLOCKCHAIN = "" + BLOCK + BLOCK + BLOCK + BLOCK;


    private final Point[][] points = new Point[50][10];

    @Getter
    private int startX;
    @Getter
    private int startY;

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public OpponentTetrisField(int startX, int startY) {
        for (Point[] point : points) {
            Arrays.fill(point, new Point(true, TextColor.ANSI.BLACK));
        }

        this.startX = startX;
        this.startY = startY;
    }
    public void addPiece(Tetromino tetromino) {
        addGrid(tetromino.returnPiece());
        checkForClearedLines();
    }

    private void addGrid(Grid grid) {
        Boolean[][] gridPoints = grid.getSetPoints();
        for (int y = 0; y < gridPoints.length; y++) {
            for (int x = 0; x < gridPoints[y].length; x++) {
                if (gridPoints[y][x]) {
                    points[y + grid.y][x + grid.x] = new Point(false, grid.getColor());
                }
            }
        }
        checkForClearedLines();
    }


    private void checkForClearedLines() {
        a:
        for (int y = 0; y < points.length; y++) {
            for (int x = 0; x < points[y].length; x++) {
                if (points[y][x].isFree()) {
                    continue a;
                }
                if (x + 1 == points[y].length) {
                    clearLine(y);
                    moveRestDown(y);
                }
            }
        }
    }

    private void clearLine(int y) {
        for (Point point : points[y]) {
            point.resetPoint();
        }
    }

    private void moveRestDown(int y) {
        for (int i = y; i > 0; i--) {
            System.arraycopy(points[i - 1], 0, points[i], 0, points[y].length);
        }
    }

    public void printTetrisField(TerminalHelper terminal) {
        drawBoard(terminal);
        for (int i = 0; i < SCREEN_HEIGHT; i++) {
            for (int j = 0; j < SCREEN_WIDTH; j++) {
                if (points[i + 30][j].isFree()) {
                    terminal.write(' ', startX + j, startY + i, TextColor.ANSI.BLACK, TextColor.ANSI.BLACK);
                } else {
                    terminal.write(BLOCK, startX + j, startY + i, points[i + 30][j].getColor());
                }
            }
        }
    }
    private void drawBoard(TerminalHelper terminal) {
        int height = startY - 1;
        int width = startX - 1;
        writeBoxAt(terminal, width, height, 12, 22);
    }

    public void addGarbage(int lines, int garbageGap) {
        for (int y = lines; y < 50; y++) {
            System.arraycopy(points[y], 0, points[y - lines], 0, 10);
        }
        for (int y = 0; y < lines; y++) {
            for (int x = 0; x < 10; x++) {
                if (x == garbageGap) {
                    points[y + 50 - lines][x] = new Point(true, TextColor.ANSI.BLACK);
                } else {
                    points[y + 50 - lines][x] = new Point(false, TextColor.ANSI.BLACK_BRIGHT);
                }
            }
        }

    }

}
