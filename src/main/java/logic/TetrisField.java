package logic;

import Helper.TerminalHelper;
import com.googlecode.lanterna.TextColor;
import logic.pieces.Tetromino;
import screens.PlayScreen;

import java.awt.*;
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

    private final Point[][] points = new Point[50][10];

    private final RandomGenerator generator;

    private Tetromino activePiece;

    private Tetromino holdPiece;

    private Tetromino helperPiece;

    private long score;

    private int level;

    private int numberofLinesToClear;

    private static final int LINE_THRESHOLD = 10;

    private int currentMillis = 1000;

    private boolean allowSwap;

    private int combo = -1;

    private boolean storeB2B;

    private boolean isTspin;


    private ScheduledExecutorService exec;

    private java.util.List<Tetromino> nextPieces;

    private final PlayScreen screen;

    private GarbagePieceHandler garbagePieceHandler;

    public TetrisField(int level, PlayScreen screen) {
        for (Point[] point : points) {
            Arrays.fill(point, new Point(true, TextColor.ANSI.BLACK));
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
        garbagePieceHandler = new GarbagePieceHandler();
        garbagePieceHandler.addGarbage(1);
        garbagePieceHandler.addGarbage(1);
        garbagePieceHandler.addGarbage(1);
    }


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

    private void receiveGarbage() {
        List<GarbagePiece> garbages = garbagePieceHandler.getReadyGarbage();
        for (GarbagePiece garbage : garbages) {
            int garbagePosition = (int) (Math.random() * 9);
            int line = garbage.getLines();
            for (int y = line; y < 50; y++) {
                for (int x = 0; x < 10; x++) {
                    points[y - line][x] = points[y][x];
                }
            }
            for (int y = 0; y < line; y++) {
                for (int x = 0; x < 10; x++) {
                    if (x == garbagePosition) {
                        points[y + 50 - line][x] = new Point(true, TextColor.ANSI.BLACK);
                    } else {
                        points[y + 50 - line][x] = new Point(false, TextColor.ANSI.BLACK_BRIGHT);
                    }
                }
            }
        }
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
                    clearLine(y);
                    numberOfClearedLines++;
                    moveRestDown(y);
                }
            }
        }

        if (numberOfClearedLines != 0) {
            combo++;
            int rawSendCapacity = sentLinesTotal(numberOfClearedLines);
            System.out.println("Lines Sent: " + rawSendCapacity);
            score += 10 * Math.pow(rawSendCapacity, 2) * Math.pow(level, 2);

            numberofLinesToClear -= numberOfClearedLines;
            if (numberofLinesToClear < 1) {
                level++;
                currentMillis -= currentMillis / 10;
                rescheduleScheduler();
                numberofLinesToClear = LINE_THRESHOLD;
            }
            int garbageToSend = garbagePieceHandler.removeGarbageLines(sentLinesTotal(numberOfClearedLines));
            //TODO Schick Gameserver wv. Garbage gesendet wird...
        } else {
            combo = -1;
            receiveGarbage();
        }

    }

    private int sentLinesTotal(int linesCleared) {
        //TODO Tspin Mini maybe? aber kein bock eig.

        int comboLines = sentLinesByCombo();
        int allClearLines = isAllClear();

        if (linesCleared == 4) {
            int backToBackBonus = evaluateBackToBack();
            storeB2B = true;
//            System.out.println("Tetris + Combo " + combo);
            return 4 + comboLines + backToBackBonus + allClearLines;
        }
        if (isTspin) {
            int backToBackBonus = evaluateBackToBack();
            storeB2B = true;
//            System.out.println("T-Spin + Combo " + combo);
            return linesCleared * 2 + comboLines + backToBackBonus + allClearLines;
        }
        storeB2B = false;
//        System.out.println("Combo " + combo);
        return linesCleared - 1 + comboLines + allClearLines;
    }

    private int sentLinesByCombo() {
        switch (combo) {
            case -1:
            case 0:
                return 0;
            case 1:
            case 2:
                return 1;
            case 3:
            case 4:
                return 2;
            case 5:
            case 6:
                return 3;
            case 7:
            case 8:
            case 9:
                return 4;
        }
        return 5;
    }

    private int evaluateBackToBack() {
        if (storeB2B) {
            return 1;
        }
        return 0;
    }

    private void isTspin() {
        Grid current = activePiece.getGrid()[activePiece.getCurrentRotation()];
        isTspin = getActivePiece().toString().equals("T-Piece") && !current.isValidPosition(current.x, current.y - 1);
    }

    private void moveRestDown(int y) {
        for (int i = y; i > 0; i--) {
            System.arraycopy(points[i - 1], 0, points[i], 0, points[y].length);
        }
    }

    private void clearLine(int y) {
        for (Point point : points[y]) {
            point.resetPoint();
        }
    }

    private int isAllClear() {
        for (Point point : points[points.length - 1]) {
            if (point.getColor() != Color.BLACK) {
                return 0;
            }
        }
        return 10;
    }


    public void printTetrisField(TerminalHelper terminal) {
        for (int i = 0; i < SCREEN_HEIGHT; i++) {
            for (int j = 0; j < SCREEN_WIDTH; j++) {
                if (points[i + 30][j].isFree()) {
                    terminal.write(BACKGROUND, 30 + j, 16 + i, TextColor.ANSI.BLACK_BRIGHT, TextColor.ANSI.BLACK);
                } else {
                    terminal.write(BLOCK, 30 + j, 16 + i, points[i + 30][j].getColor());
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

    private void printScoreAndStuff(TerminalHelper terminal) {
        int y = 23;
        terminal.write("LEVEL", 22, y++, TextColor.ANSI.YELLOW);
        terminal.write(String.format("  %03d", level), 21, y++, TextColor.ANSI.WHITE);
        terminal.write(Character.toString(196).repeat(7), 21, y++, TextColor.ANSI.BLACK_BRIGHT);
        terminal.write("SCORE", 21, y++, TextColor.ANSI.YELLOW);
        terminal.write(String.format("%07d", score), 21, y, TextColor.ANSI.WHITE);
    }

    private void printCurrentPiece(TerminalHelper terminal) {
        Grid activePieceGrid = activePiece.returnPiece();
        Boolean[][] gridPoints = activePieceGrid.getSetPoints();
        Grid helperPieceGrid = helperPiece.returnPiece();

        for (int y = 0; y < gridPoints.length; y++) {
            for (int x = 0; x < gridPoints[y].length; x++) {
                if (gridPoints[y][x]) {
                    terminal.write(BLOCK, 30 + x + helperPieceGrid.x, 16 + y + helperPieceGrid.y - 30, TextColor.ANSI.BLACK_BRIGHT, TextColor.ANSI.BLACK);
                    terminal.write(BLOCK, 30 + x + activePieceGrid.x, 16 + y + activePieceGrid.y - 30, activePieceGrid.getColor());
                }
            }
        }
    }

    private synchronized void printHold(TerminalHelper terminal) {
        synchronized (holdPiece) {
            Grid holdPieceGrid = holdPiece.getGrid()[0];
            Boolean[][] gridPoints = holdPieceGrid.getSetPoints();
            terminal.write(BLOCKCHAIN, 23, 17, TextColor.ANSI.BLACK);
            terminal.write(BLOCKCHAIN, 23, 18, TextColor.ANSI.BLACK);
            terminal.write(BLOCKCHAIN, 23, 19, TextColor.ANSI.BLACK);
            for (int y = 0; y < gridPoints.length; y++) {
                for (int x = 0; x < gridPoints[y].length; x++) {
                    if (gridPoints[y][x]) {
                        terminal.write(BLOCK, 23 + x, 17 + y, holdPieceGrid.getColor());
                    }
                }
            }
        }
    }

    private synchronized void printQueue(TerminalHelper terminal) {
        for (int i = 0; i < nextPieces.size(); i++) {
            Grid holdPieceGrid = nextPieces.get(i).getGrid()[0];
            Boolean[][] gridPoints = holdPieceGrid.getSetPoints();
            terminal.write(BLOCKCHAIN, 43, 17 + i * 5, TextColor.ANSI.BLACK);
            terminal.write(BLOCKCHAIN, 43, 18 + i * 5, TextColor.ANSI.BLACK);
            terminal.write(BLOCKCHAIN, 43, 19 + i * 5, TextColor.ANSI.BLACK);
            for (int y = 0; y < gridPoints.length; y++) {
                for (int x = 0; x < gridPoints[y].length; x++) {
                    if (gridPoints[y][x]) {
                        terminal.write(BLOCK, 43 + x, 17 + y + i * 5, holdPieceGrid.getColor());
                    }
                }
            }
        }
    }

    public boolean isFreePixel(int x, int y) {
        if (x < 0 || y < 0 || x >= 10 || y >= 50) {
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


    public void newActivePiece() {
        addGrid(getActivePiece().returnPiece());
        activePiece = generator.getNext();
        if (!activePiece.getGrid()[0].isValidPosition(3, 30)) {
            if (activePiece.getGrid()[0].isValidPosition(3, 29)) {
                activePiece.setY(29);
            } else {
                exec.shutdownNow();
                screen.shutdownThread();
                screen.loseScreen = true;
            }
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
