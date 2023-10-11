package logic;

import Helper.TerminalHelper;
import com.googlecode.lanterna.TextColor;
import logic.pieces.Tetromino;
import lombok.Getter;

import java.util.Arrays;

import static Helper.TerminalHelper.writeBoxAt;

@Getter
public class OpponentTetrisField {

    private static final int SCREEN_HEIGHT = 20;

    private static final int SCREEN_WIDTH = 10;

    private static final char BLOCK = '#';

    private static final String BLOCKCHAIN = "" + BLOCK + BLOCK + BLOCK + BLOCK;


    private static final Point[][] points = new Point[50][10];

    @Getter
    private static int startX;
    @Getter
    private static int startY;

    public static void setStartX(int startX) {
        OpponentTetrisField.startX = startX;
    }

    public static void setStartY(int startY) {
        OpponentTetrisField.startY = startY;
    }

    public static void initOpponentTetrisField(int startX, int startY) {
        for (Point[] point : points) {
            Arrays.fill(point, new Point(true, TextColor.ANSI.BLACK));
        }
        OpponentTetrisField.startX = startX;
        OpponentTetrisField.startY = startY;
    }

    public static void addPiece(Tetromino tetromino) {
        addGrid(tetromino.returnPiece());
        checkForClearedLines();
    }

    private static void addGrid(Grid grid) {
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


    private static void checkForClearedLines() {
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

    private static void clearLine(int y) {
        for (Point point : points[y]) {
            point.resetPoint();
        }
    }

    private static void moveRestDown(int y) {
        for (int i = y; i > 0; i--) {
            System.arraycopy(points[i - 1], 0, points[i], 0, points[y].length);
        }
    }

    public static void printTetrisField(TerminalHelper terminal) {
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
    private static void drawBoard(TerminalHelper terminal) {
        int height = startY - 1;
        int width = startX;
        char leftDown = '#';
        char leftUp = '#';
        char rightUp = '#';
        char rightDown = '#';
        char straightHorizontally = '#';
        char straightVertically = '#';
        String firstline = leftUp +
                String.valueOf(straightHorizontally).repeat(10) +
                rightUp;
        StringBuilder middleLines = new StringBuilder();
        middleLines.append(straightVertically);
        middleLines.append("          ");
        middleLines.append(straightVertically);
        String bottomLine = leftDown +
                String.valueOf(straightHorizontally).repeat(10) +
                rightDown;

        terminal.write(firstline, width, height++);
        for (int i = 0; i < 20; i++) {
            terminal.write(middleLines.toString(), width, height++);
        }
        terminal.write(bottomLine, width, height);
    }



}
