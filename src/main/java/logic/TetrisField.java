package logic;

import Helper.TerminalHelper;
import com.googlecode.lanterna.TextColor;
import communication.MatchSendHelper;
import logic.pieces.Tetromino;
import lombok.extern.slf4j.Slf4j;
import screens.PlayOfflineScreen;
import screens.PlayOnlineScreen;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static Helper.TerminalHelper.writeBoxAt;

@Slf4j
public class TetrisField {
    public static final int SCREEN_HEIGHT = 20;
    public static final int SCREEN_WIDTH = 10;
    public static final char BLOCK = '#';

    private int startX;

    private int startY;

    public static final String BLOCKCHAIN = "" + BLOCK + BLOCK + BLOCK + BLOCK;
    private static final int LINE_THRESHOLD = 10;
    private final Point[][] points = new Point[50][10];
    private final RandomGenerator generator;
    private PlayOfflineScreen offlineScreen;
    private PlayOnlineScreen onlineScreen;

    private Tetromino activePiece;
    private Tetromino holdPiece;
    private Tetromino helperPiece;
    private long score;
    private int level;
    private int numberofLinesToClear;
    private int currentMillis = 1000;
    private boolean allowSwap;
    private int combo = -1;
    private boolean storeB2B;
    private boolean isTspin;
    private ScheduledExecutorService exec;
    private java.util.List<Tetromino> nextPieces;
    public static GarbagePieceHandler garbagePieceHandler;

    private final boolean isOnline;

    public TetrisField(int level, PlayOfflineScreen screen, int startX, int startY, boolean isOnline) {
        this.startX = startX;
        this.startY = startY;
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
        if (!isOnline)
            exec.scheduleAtFixedRate(this::gameTick, 0, currentMillis, TimeUnit.MILLISECONDS);
        numberofLinesToClear = LINE_THRESHOLD;
        this.offlineScreen = screen;
        garbagePieceHandler = new GarbagePieceHandler();
        this.isOnline = isOnline;
    }

    public TetrisField(int level, PlayOnlineScreen screen, int startX, int startY, boolean isOnline) {
        this.startX = startX;
        this.startY = startY;
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
        if (!isOnline)
            exec.scheduleAtFixedRate(this::gameTick, 0, currentMillis, TimeUnit.MILLISECONDS);
        numberofLinesToClear = LINE_THRESHOLD;
        this.onlineScreen = screen;
        garbagePieceHandler = new GarbagePieceHandler();
        this.isOnline = isOnline;
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
                System.arraycopy(points[y], 0, points[y - line], 0, 10);
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
            log.info("Lines Sent: " + rawSendCapacity);
            score += 10 * Math.pow(rawSendCapacity, 2) * Math.pow(level, 2);

            numberofLinesToClear -= numberOfClearedLines;
            if (numberofLinesToClear < 1) {
                level++;
                currentMillis -= currentMillis / 10;
                if (!isOnline)
                    rescheduleScheduler();
                numberofLinesToClear = LINE_THRESHOLD;
            }
            int garbageToSend = garbagePieceHandler.removeGarbageLines(sentLinesTotal(numberOfClearedLines));
            if (garbageToSend > 0 && isOnline) {
                MatchSendHelper.SENDGARBAGE.sendUpdate(garbageToSend);
            }
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
        printCurrentPiece(terminal);
        if (holdPiece != null) {
            printHold(terminal);
        }
        printQueue(terminal);
        printScoreAndStuff(terminal);
    }

    private void drawBoard(TerminalHelper terminal) {
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
        height = 15;
        // This is the Box which displays the Hold Piece
        writeBoxAt(terminal, width - 9, height, 9, 7);

        // This is the Box which displays the scoreboard
        writeBoxAt(terminal, width - 9, height + 15, 9, 7);

        // These are the boxes drawn which show th enext four pieces
        writeBoxAt(terminal, width + 10, height, 8, 6);
        writeBoxAt(terminal, width + 10, height + 5, 8, 6);
        writeBoxAt(terminal, width + 10, height + 10, 8, 6);
        writeBoxAt(terminal, width + 10, height + 15, 8, 6);
        terminal.write(" ", width + 11, height + 21);
    }

    private void printScoreAndStuff(TerminalHelper terminal) {
        int y = startY + 15;
        terminal.write("LEVEL", startX - 7, y++, TextColor.ANSI.YELLOW);
        terminal.write(String.format("  %03d", level), startX - 8, y++, TextColor.ANSI.WHITE);
        terminal.write(Character.toString('-').repeat(7), startX - 8, y++, TextColor.ANSI.BLACK_BRIGHT);
        terminal.write(" SCORE ", startX - 8, y++, TextColor.ANSI.YELLOW);
        terminal.write(String.format("%07d", score), startX - 8, y, TextColor.ANSI.WHITE);
    }

    private void printCurrentPiece(TerminalHelper terminal) {
        Grid activePieceGrid = activePiece.returnPiece();
        Boolean[][] gridPoints = activePieceGrid.getSetPoints();
        Grid helperPieceGrid = helperPiece.returnPiece();

        for (int y = 0; y < gridPoints.length; y++) {
            for (int x = 0; x < gridPoints[y].length; x++) {
                if (gridPoints[y][x]) {
                    terminal.write(BLOCK, startX + x + helperPieceGrid.x, startY + y + helperPieceGrid.y - 30, TextColor.ANSI.BLACK_BRIGHT, TextColor.ANSI.BLACK);
                    terminal.write(BLOCK, startX + x + activePieceGrid.x, startY + y + activePieceGrid.y - 30, activePieceGrid.getColor());
                }
            }
        }
    }

    private synchronized void printHold(TerminalHelper terminal) {
        synchronized (holdPiece) {
            Grid holdPieceGrid = holdPiece.getGrid()[0];
            Boolean[][] gridPoints = holdPieceGrid.getSetPoints();
            terminal.write(BLOCKCHAIN, startX - 7, startY + 1, TextColor.ANSI.BLACK);
            terminal.write(BLOCKCHAIN, startX - 7, startY + 2, TextColor.ANSI.BLACK);
            terminal.write(BLOCKCHAIN, startX - 7, startY + 3, TextColor.ANSI.BLACK);
            for (int y = 0; y < gridPoints.length; y++) {
                for (int x = 0; x < gridPoints[y].length; x++) {
                    if (gridPoints[y][x]) {
                        terminal.write(BLOCK, startX - 7 + x, startY + 1 + y, holdPieceGrid.getColor());
                    }
                }
            }
        }
    }

    private synchronized void printQueue(TerminalHelper terminal) {
        for (int i = 0; i < nextPieces.size(); i++) {
            Grid holdPieceGrid = nextPieces.get(i).getGrid()[0];
            Boolean[][] gridPoints = holdPieceGrid.getSetPoints();
            terminal.write(BLOCKCHAIN, startX + 12, startY + 1 + i * 5, TextColor.ANSI.BLACK);
            terminal.write(BLOCKCHAIN, startX + 12, startY + 2 + i * 5, TextColor.ANSI.BLACK);
            terminal.write(BLOCKCHAIN, startX + 12, startY + 3 + i * 5, TextColor.ANSI.BLACK);
            for (int y = 0; y < gridPoints.length; y++) {
                for (int x = 0; x < gridPoints[y].length; x++) {
                    if (gridPoints[y][x]) {
                        terminal.write(BLOCK, startX + 12 + x, startY + 1 + y + i * 5, holdPieceGrid.getColor());
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
//        debugTetrisField();
    }

    private void debugTetrisField() {
        for (Point[] point : points) {
            System.out.print("#");
            for (Point point1 : point) {
                System.out.printf("%3s", point1.isFree() ? " " : "x");
            }
            System.out.println("#");
        }
    }


    public void newActivePiece() {
        addGrid(getActivePiece().returnPiece());
        if (isOnline)
            MatchSendHelper.UPDATEBOARD.sendUpdate(activePiece);
        activePiece = generator.getNext();
        if (!activePiece.getGrid()[0].isValidPosition(3, 30)) {
            if (activePiece.getGrid()[0].isValidPosition(3, 29)) {
                activePiece.setY(29);
            } else {
                exec.shutdownNow();
                if (isOnline) {
                    MatchSendHelper.LOOSE.sendUpdate();
                    onlineScreen.loseScreen = true;
                } else {
                    offlineScreen.loseScreen = true;
                }
            }
        }
        nextPieces = generator.peek(4);
        calculateNewHelperPiecePosition();
        if (!allowSwap) {
            holdPiece.returnNormalColor();
        }
        allowSwap = true;
        log.info(String.format("x: %d | y: %d\n", activePiece.getX(), activePiece.getY()));
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
