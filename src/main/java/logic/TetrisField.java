package logic;

import asciiPanel.AsciiPanel;
import logic.pieces.Tetromino;

import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TetrisField {
    public static final int SCREEN_HEIGHT = 20;
    public static final int SCREEN_WIDTH = 10;
    public static final char BLOCK = 219;

    public static final String BLOCKCHAIN = "" + BLOCK + BLOCK + BLOCK + BLOCK;

    public static final char BACKGROUND = 1;

    private static Color PREVIEWCOLOR = new Color(255,255,255, 128);
    private Point[][] points = new Point[20][10];

    private RandomGenerator generator;

    private Tetromino activePiece;

    private Tetromino holdPiece;

    private Tetromino nextPiece;

    private Tetromino helperPiece;


    private ScheduledExecutorService exec;

    public TetrisField() {
        for (Point[] point : points) {
            Arrays.fill(point, new Point(true, Color.BLACK));
        }
        generator = new RandomGenerator(this);
        activePiece = generator.getNext();
        calculateNewHelperPiecePosition();
        nextPiece = generator.getNext();
        holdPiece = null;
        exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(this::gameTick, 0, 1, TimeUnit.SECONDS);
    }


    /**
     * Finished Pieces get here, Since they won't move anymore. Most likey at least
     *
     * @param grid
     */
    public void addGrid(Grid grid) {
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

    public void swapHold() {
        if (holdPiece != null) {
            synchronized (holdPiece) {
                Tetromino temp = holdPiece;
                holdPiece = activePiece;
                activePiece = temp;
            }
        } else {
            synchronized (activePiece) {
                holdPiece = activePiece;
                activePiece = nextPiece;
                nextPiece = generator.getNext();
            }
        }
        synchronized (holdPiece) {
            holdPiece.resetPosition();
        }
        helperPiece = activePiece;
    }

    private void checkForClearedLines() {
        a:
        for (int y = 0; y < points.length; y++) {
            for (int x = 0; x < points[y].length; x++) {
                if (points[y][x].isFree()) {
                    continue a;
                }
                if (x + 1 == points[y].length) {
                    System.out.println("Clearing Line at y=" + y);
                    clearLine(y);
                    moveRestDown(y);
                }
            }
        }
    }

    private void moveRestDown(int y) {
        for (int i = y; i > 0; i--) {
            for (int x = 0; x < points[y].length; x++) {
                points[i][x] = points[i - 1][x];
            }
        }
    }

    private void clearLine(int y) {
        for (Point point : points[y]) {
            point.resetPoint();
        }
    }


    public void printTetrisField(AsciiPanel terminal) {
        for (int i = 0; i < SCREEN_HEIGHT; i++) {
            for (int j = 0; j < SCREEN_WIDTH; j++) {
                if (points[i][j].getColor() == Color.BLACK) {
                    terminal.write(BACKGROUND, 30 + j, 16 + i, Color.GRAY);
                } else {
                    terminal.write(BLOCK, 30 + j, 16 + i, points[i][j].getColor());
                }
            }
        }
        printCurrentPiece(terminal);
        if (holdPiece != null) {
            printHold(terminal);
        }
        printQueue(terminal);
    }

    private void printCurrentPiece(AsciiPanel terminal) {
        Grid activePieceGrid = activePiece.returnPiece();
        Boolean[][] gridPoints = activePieceGrid.getSetPoints();
        Grid helperPieceGrid = helperPiece.returnPiece();

        for (int y = 0; y < gridPoints.length; y++) {
            for (int x = 0; x < gridPoints[y].length; x++) {
                if (gridPoints[y][x]) {
                    terminal.write(BLOCK, 30 + x + helperPieceGrid.x, 16 + y + helperPieceGrid.y, PREVIEWCOLOR);
                    terminal.write(BLOCK, 30 + x + activePieceGrid.x, 16 + y + activePieceGrid.y, activePieceGrid.getColor());
                }
            }
        }
    }

    private synchronized void printHold(AsciiPanel terminal) {
        synchronized (holdPiece) {
            Grid holdPieceGrid = holdPiece.getGrid()[0];
            Boolean[][] gridPoints = holdPieceGrid.getSetPoints();
            terminal.write(BLOCKCHAIN, 23, 17, Color.BLACK);
            terminal.write(BLOCKCHAIN, 23, 18, Color.BLACK);
            terminal.write(BLOCKCHAIN, 23, 19, Color.BLACK);
            for (int y = 0; y < gridPoints.length; y++) {
                for (int x = 0; x < gridPoints[y].length; x++) {
                    if (gridPoints[y][x]) {
                        terminal.write(BLOCK, 23 + x, 17 + y, holdPieceGrid.getColor());
                    }
                }
            }
        }
    }

    private synchronized void printQueue(AsciiPanel terminal) {
        Grid holdPieceGrid = nextPiece.getGrid()[0];
        Boolean[][] gridPoints = holdPieceGrid.getSetPoints();
        terminal.write(BLOCKCHAIN, 43, 17, Color.BLACK);
        terminal.write(BLOCKCHAIN, 43, 18, Color.BLACK);
        terminal.write(BLOCKCHAIN, 43, 19, Color.BLACK);
        for (int y = 0; y < gridPoints.length; y++) {
            for (int x = 0; x < gridPoints[y].length; x++) {
                if (gridPoints[y][x]) {
                    terminal.write(BLOCK, 43 + x, 17 + y, holdPieceGrid.getColor());
                }
            }
        }
    }

    public boolean isFreePixel(int x, int y) {
        if (x < 0 || y < 0 || x >= 10 || y >= 20) {
            return false;
        }
        return points[y][x].isFree();
    }

    public Tetromino getActivePiece() {
        return activePiece;
    }

    public void moveLeft() {
        getActivePiece().movePieceLeft();
        calculateNewHelperPiecePosition();
    }

    public void moveRight() {
        getActivePiece().movePieceRight();
        calculateNewHelperPiecePosition();
    }

    public void rotateClockwise() {
        getActivePiece().rotateClockwise();
        calculateNewHelperPiecePosition();
    }

    public void rotateCClockwise() {
        getActivePiece().rotateCClockwise();
        calculateNewHelperPiecePosition();
    }

    public void gameTick() {
        if (getActivePiece().gameTick()) {
            newActivePiece();
        }
    }

    public void hardDrop() {
        activePiece.hardDrop();
        newActivePiece();
//        printCurrentField();
    }


    private void printCurrentField() {
        for (Point[] point : points) {
            for (Point value : point) {
                System.out.print(value);
            }
            System.out.println();
        }
    }

    public void newActivePiece() {
        addGrid(getActivePiece().returnPiece());
        activePiece = nextPiece;
        nextPiece = generator.getNext();
        calculateNewHelperPiecePosition();
    }

    private void calculateNewHelperPiecePosition() {
        helperPiece = activePiece.clonePiece();
        System.out.printf("%dx:%dy | %dx:%dy\n", activePiece.getX(), activePiece.getY(), helperPiece.getX(), helperPiece.getY());
        helperPiece.hardDrop();
    }


    public void softDrop() {
        gameTick();
    }

    private void rescheduleScheduler(int millis) {
        exec.shutdownNow();
        exec.scheduleAtFixedRate(this::gameTick, 0, millis, TimeUnit.MILLISECONDS);
    }
}
