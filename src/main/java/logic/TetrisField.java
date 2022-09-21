package logic;

import asciiPanel.AsciiPanel;
import logic.pieces.TPiece;
import logic.pieces.Tetromino;
import screens.PlayScreen;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TetrisField {
    public static final int SCREEN_HEIGHT = 20;
    public static final int SCREEN_WIDTH = 10;
    public static final char BLOCK = 219;

    public static final String BLOCKCHAIN = "" + BLOCK + BLOCK + BLOCK + BLOCK;

    public static final char BACKGROUND = 1;

    private static Color PREVIEWCOLOR = new Color(255, 255, 255, 128);
    private Point[][] points = new Point[20][10];

    private RandomGenerator generator;

    private Tetromino activePiece;

    private Tetromino holdPiece;

    private Tetromino helperPiece;

    private long score;

    private int level;

    private int numberofLinesToClear;

    private static final int LINE_THRESHOLD = 10;

    private static final int SPEEDMULTIPLIER = 50;

    private int currentMillis = 1000;

    private boolean allowSwap;

    private int combo = -1;

    /**
     * 0: normal
     * 1: Tetris
     * 2: T-Spin
     */
    private boolean storeB2B;

    private boolean isTspin;


    private ScheduledExecutorService exec;

    private java.util.List<Tetromino> nextPieces;

    private PlayScreen screen;

    public TetrisField(int level, PlayScreen screen) {
        for (Point[] point : points) {
            Arrays.fill(point, new Point(true, Color.BLACK));
        }
        generator = new RandomGenerator(this);
        activePiece = generator.getNext();
        calculateNewHelperPiecePosition();
        nextPieces = generator.peek(4);
        holdPiece = null;
        allowSwap = true;
        score = 0;
        this.level = level;
        for (int i = 0; i < level - 1; i++) {
            currentMillis -= this.currentMillis / 10;
        }
        exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(this::gameTick, 0, currentMillis, TimeUnit.MILLISECONDS);
        numberofLinesToClear = LINE_THRESHOLD;
        this.screen = screen;
    }

    public TetrisField(int level, PlayScreen screen, String levelPath) throws IOException {
        for (Point[] point : points) {
            Arrays.fill(point, new Point(true, Color.BLACK));
        }
        List<String> lines = Files.readAllLines(Paths.get(levelPath));
        for (int i = 0; i < lines.size(); i++) {
            for (int j = 0; j < lines.get(i).length(); j++) {
                if (lines.get(i).charAt(j) == '#') {
                    points[i][j] = new Point(false, Color.GRAY);
                }
            }
        }
        generator = new RandomGenerator(this);
        activePiece = new TPiece(this);
        calculateNewHelperPiecePosition();
        nextPieces = generator.peek(4);
        holdPiece = null;
        score = 0;
        this.level = level;
        for (int i = 0; i < level - 1; i++) {
            currentMillis -= this.currentMillis / 10;
        }
        exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(this::gameTick, 0, currentMillis, TimeUnit.MILLISECONDS);
        numberofLinesToClear = LINE_THRESHOLD;
        this.screen = screen;
    }


    /**
     * Placed Pieces get here, Since they won't move anymore. Most likey at least
     *
     * @param grid
     */
    public void addGrid(Grid grid) {
        isTspin();

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
        if (!allowSwap) {
            return;
        }
        if (holdPiece != null) {
            synchronized (holdPiece) {
                Tetromino temp = holdPiece;
                holdPiece = activePiece;
                activePiece = temp;
            }
        } else {
            synchronized (activePiece) {
                holdPiece = activePiece;
                activePiece = generator.getNext();
                nextPieces = generator.peek(4);
            }
        }
        synchronized (holdPiece) {
            holdPiece.resetPosition();
        }
        helperPiece = activePiece;
        calculateNewHelperPiecePosition();

        allowSwap = false;
        holdPiece.changeColorGrey();
    }

    private void checkForClearedLines() {
        int numberOfClearedLines = 0;
        a:
        for (int y = 0; y < points.length; y++) {
            for (int x = 0; x < points[y].length; x++) {
                if (points[y][x].isFree()) {
                    continue a;
                }
                if (x + 1 == points[y].length) {
                    System.out.println("Clearing Line at y=" + y);
                    clearLine(y);
                    numberOfClearedLines++;
                    moveRestDown(y);
                }
            }
        }

        if (numberOfClearedLines != 0) {
            combo++;
            System.out.println("Lines Sent: " + sentLinesTotal(numberOfClearedLines));
        } else {
            combo = -1;
        }

        score += 10 * Math.pow(numberOfClearedLines, 2) * Math.pow(level, 2);
        numberofLinesToClear -= numberOfClearedLines;
        if (numberofLinesToClear < 1) {
            level++;
            currentMillis -= currentMillis / 10;
            rescheduleScheduler();
            numberofLinesToClear = LINE_THRESHOLD;
        }
    }

    private int sentLinesTotal(int linesCleared) {
        //TODO Tspin Mini maybe? aber kein bock eig.

        int comboLines = sentLinesByCombo();
        int allClearLines = isAllClear();

        if (linesCleared == 4) {
            int backToBackBonus = evaluateBackToBack(1);
            storeB2B = true;
//            System.out.println("Tetris + Combo " + combo);
            return 4 + comboLines + backToBackBonus + allClearLines;
        }
        if (isTspin) {
            int backToBackBonus = evaluateBackToBack(2);
            storeB2B = true;
//            System.out.println("T-Spin + Combo " + combo);
            return linesCleared*2 + comboLines + backToBackBonus + allClearLines;
        }
        storeB2B = false;
//        System.out.println("Combo " + combo);
        return linesCleared-1 + comboLines + allClearLines;
    }

    private int sentLinesByCombo() {
        return switch (combo) {
            case -1, 0 -> 0;
            case 1, 2 -> 1;
            case 3, 4 -> 2;
            case 5, 6 -> 3;
            case 7, 8, 9 -> 4;
            default -> 5;
        };
    }

    private int evaluateBackToBack(int current) {
        if (storeB2B) {
            System.out.print(" Back-To-Back ");
            return 1;
        }
        return 0;
    }

    private void isTspin() {
        Grid current = activePiece.getGrid()[activePiece.getCurrentRotation()];
        isTspin = getActivePiece().toString().equals("T-Piece") && !current.isValidPosition(current.x, current.y-1);
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

    private int isAllClear() {
        for (Point point : points[points.length-1]) {
            if (point.getColor() != Color.BLACK) {
                return 0;
            }
        }
        return 10;
    }


    public void printTetrisField(AsciiPanel terminal) {
        for (int i = 0; i < SCREEN_HEIGHT; i++) {
            for (int j = 0; j < SCREEN_WIDTH; j++) {
                if (points[i][j].isFree()) {
                    terminal.write(BACKGROUND, 30 + j, 16 + i, Color.GRAY, Color.BLACK);
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
        printScoreAndStuff(terminal);
    }

    private void printScoreAndStuff(AsciiPanel terminal) {
        int y = 23;
        terminal.write("LEVEL", 22, y++, Color.YELLOW);
        terminal.write(String.format("  %03d", level), 21, y++, Color.WHITE);
        terminal.write(Character.toString(196).repeat(7), 21, y++, Color.LIGHT_GRAY);
        terminal.write("SCORE", 21, y++, Color.YELLOW);
        terminal.write(String.format("%07d", score), 21, y, Color.WHITE);
    }

    private void printCurrentPiece(AsciiPanel terminal) {
        Grid activePieceGrid = activePiece.returnPiece();
        Boolean[][] gridPoints = activePieceGrid.getSetPoints();
        Grid helperPieceGrid = helperPiece.returnPiece();

        for (int y = 0; y < gridPoints.length; y++) {
            for (int x = 0; x < gridPoints[y].length; x++) {
                if (gridPoints[y][x]) {
                    terminal.write(BLOCK, 30 + x + helperPieceGrid.x, 16 + y + helperPieceGrid.y, Color.LIGHT_GRAY, Color.BLACK);
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
        for (int i = 0; i < nextPieces.size(); i++) {
            Grid holdPieceGrid = nextPieces.get(i).getGrid()[0];
            Boolean[][] gridPoints = holdPieceGrid.getSetPoints();
            terminal.write(BLOCKCHAIN, 43, 17 + i * 5, Color.BLACK);
            terminal.write(BLOCKCHAIN, 43, 18 + i * 5, Color.BLACK);
            terminal.write(BLOCKCHAIN, 43, 19 + i * 5, Color.BLACK);
            for (int y = 0; y < gridPoints.length; y++) {
                for (int x = 0; x < gridPoints[y].length; x++) {
                    if (gridPoints[y][x]) {
                        terminal.write(BLOCK, 43 + x, 17 + y + i * 5, holdPieceGrid.getColor());
                    }
                }
            }
        }
//        Grid holdPieceGrid = nextPieces.get(0).getGrid()[0];
//        Boolean[][] gridPoints = holdPieceGrid.getSetPoints();
//        terminal.write(BLOCKCHAIN, 43, 17, Color.BLACK);
//        terminal.write(BLOCKCHAIN, 43, 18, Color.BLACK);
//        terminal.write(BLOCKCHAIN, 43, 19, Color.BLACK);
//        for (int y = 0; y < gridPoints.length; y++) {
//            for (int x = 0; x < gridPoints[y].length; x++) {
//                if (gridPoints[y][x]) {
//                    terminal.write(BLOCK, 43 + x, 17 + y, holdPieceGrid.getColor());
//                }
//            }
//        }
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
        int counter = activePiece.hardDrop();
        score += (counter) >> 1;
        newActivePiece();
//        printCurrentField();
    }


    public void printCurrentField() {
        for (Point[] point : points) {
            for (Point value : point) {
                System.out.print(value);
            }
            System.out.println();
        }
    }

    public void newActivePiece() {
        addGrid(getActivePiece().returnPiece());
        activePiece = generator.getNext();
        if (!activePiece.getGrid()[0].isValidPosition(3, 0)) {
            exec.shutdownNow();
            screen.loseScreen = true;
        }
        nextPieces = generator.peek(4);
        calculateNewHelperPiecePosition();
        if (!allowSwap) {
            holdPiece.returnNormalColor();
        }
        allowSwap = true;
    }

    private void calculateNewHelperPiecePosition() {
        helperPiece = activePiece.clonePiece();
        helperPiece.hardDrop();
    }


    public void softDrop() {
        getActivePiece().softDrop();
//        gameTick();
    }

    private void rescheduleScheduler() {
        exec.shutdownNow();
        exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(this::gameTick, 0, currentMillis, TimeUnit.MILLISECONDS);
    }

    public long getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public void shutdownThread() {
        exec.shutdownNow();
    }
}
