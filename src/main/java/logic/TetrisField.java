package logic;

import config.Constants;

import communication.MatchSendHelper;
import logic.pieces.Tetromino;
import lombok.extern.slf4j.Slf4j;
import nakama.com.google.common.base.Strings;
import screens.AsciiPanel;
import screens.PlayOfflineScreen;
import screens.PlayOnlineScreen;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static Helper.TerminalHelper.writeBoxAt;
import static Helper.TerminalHelper.writeGarbageLine;

@Slf4j
public class TetrisField {
    public static final int SCREEN_HEIGHT = 20;
    public static final int SCREEN_WIDTH = 10;

    private int startX;

    private int startY;

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
            Arrays.fill(point, new Point(true, Constants.backgroundColor));
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
            Arrays.fill(point, new Point(true, Constants.backgroundColor));
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
                        points[y + 50 - line][x] = new Point(true, Constants.backgroundColor);
                    } else {
                        points[y + 50 - line][x] = new Point(false, Constants.disabledColor);
                    }
                }
            }
            MatchSendHelper.UPDATEGARBAGE.sendUpdate(line, garbagePosition);
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
            log.debug("Lines Sent: " + rawSendCapacity);
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
            if (point.getColor() != Constants.backgroundColor) {
                return 0;
            }
        }
        return 10;
    }


    public void printTetrisField(AsciiPanel terminal) {
        drawBoard(terminal);
        for (int i = 0; i < SCREEN_HEIGHT; i++) {
            for (int j = 0; j < SCREEN_WIDTH; j++) {
                if (points[i + 30][j].isFree()) {
                    terminal.write(' ', startX + j, startY + i, Constants.backgroundColor, Constants.backgroundColor);
                } else {
                    terminal.write(Constants.BLOCK, startX + j, startY + i, points[i + 30][j].getColor());
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

    private void drawBoard(AsciiPanel terminal) {
        int height = startY - 1;
        writeBoxAt(terminal, startX - 1, height, 12, 22);

        height = 15;
        // This is the Box which displays the Hold Piece
        writeBoxAt(terminal, startX - 9, height, 9, 7);

        // This is the Box which displays the scoreboard
        writeBoxAt(terminal, startX - 9, height + 15, 9, 7);

        // These are the boxes drawn which show the next four pieces
        writeBoxAt(terminal, startX + 10, height, 8, 6);
        writeBoxAt(terminal, startX + 10, height + 5, 8, 6);
        writeBoxAt(terminal, startX + 10, height + 10, 8, 6);
        writeBoxAt(terminal, startX + 10, height + 15, 8, 6);
        terminal.write(" ", startX + 11, height + 21);
        // This displays incoming Garbage
        if (isOnline) {
            writeGarbageLine(terminal, startX - 1, height, 22, garbagePieceHandler);
        }
    }

    private void printScoreAndStuff(AsciiPanel terminal) {
        int y = startY + 15;
        terminal.write("LEVEL", startX - 7, y++, Constants.importantText);
        terminal.write(String.format("  %03d", level), startX - 8, y++, Constants.characterColor);
        terminal.write(Strings.repeat(Character.toString('-'), 7), startX - 8, y++, Constants.wallColor);
        terminal.write(" SCORE ", startX - 8, y++, Constants.importantText);
        terminal.write(String.format("%07d", score), startX - 8, y, Constants.characterColor);
    }

    private void printCurrentPiece(AsciiPanel terminal) {
        Grid activePieceGrid = activePiece.returnPiece();
        Boolean[][] gridPoints = activePieceGrid.getSetPoints();
        Grid helperPieceGrid = helperPiece.returnPiece();

        for (int y = 0; y < gridPoints.length; y++) {
            for (int x = 0; x < gridPoints[y].length; x++) {
                if (gridPoints[y][x]) {
                    terminal.write(Constants.BLOCK, startX + x + helperPieceGrid.x, startY + y + helperPieceGrid.y - 30, Constants.disabledColor, Constants.backgroundColor);
                    terminal.write(Constants.BLOCK, startX + x + activePieceGrid.x, startY + y + activePieceGrid.y - 30, activePieceGrid.getColor());
                }
            }
        }
    }

    private synchronized void printHold(AsciiPanel terminal) {
        synchronized (holdPiece) {
            Grid holdPieceGrid = holdPiece.getGrid()[0];
            Boolean[][] gridPoints = holdPieceGrid.getSetPoints();
            terminal.write(Constants.BLOCKCHAIN, startX - 7, startY + 1, Constants.backgroundColor);
            terminal.write(Constants.BLOCKCHAIN, startX - 7, startY + 2, Constants.backgroundColor);
            terminal.write(Constants.BLOCKCHAIN, startX - 7, startY + 3, Constants.backgroundColor);
            for (int y = 0; y < gridPoints.length; y++) {
                for (int x = 0; x < gridPoints[y].length; x++) {
                    if (gridPoints[y][x]) {
                        terminal.write(Constants.BLOCK, startX - 7 + x, startY + 1 + y, holdPieceGrid.getColor());
                    }
                }
            }
        }
    }

    private synchronized void printQueue(AsciiPanel terminal) {
        for (int i = 0; i < nextPieces.size(); i++) {
            Grid holdPieceGrid = nextPieces.get(i).getGrid()[0];
            Boolean[][] gridPoints = holdPieceGrid.getSetPoints();
            terminal.write(Constants.BLOCKCHAIN, startX + 12, startY + 1 + i * 5, Constants.backgroundColor);
            terminal.write(Constants.BLOCKCHAIN, startX + 12, startY + 2 + i * 5, Constants.backgroundColor);
            terminal.write(Constants.BLOCKCHAIN, startX + 12, startY + 3 + i * 5, Constants.backgroundColor);
            for (int y = 0; y < gridPoints.length; y++) {
                for (int x = 0; x < gridPoints[y].length; x++) {
                    if (gridPoints[y][x]) {
                        terminal.write(Constants.BLOCK, startX + 12 + x, startY + 1 + y + i * 5, holdPieceGrid.getColor());
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
        if (isOnline) {
            activePiece.setY(activePiece.returnPiece().getY());
            MatchSendHelper.UPDATEBOARD.sendUpdate(activePiece);
        }
        addGrid(getActivePiece().returnPiece());
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

    public void dasLeft() {
        boolean locationChanged = true;
        while (locationChanged) {
            int oldX = activePiece.getX();
            moveLeft();
            if (oldX == activePiece.getX()) {
                locationChanged = false;
            }
        }
    }

    public void dasRight() {
        boolean locationChanged = true;
        while (locationChanged) {
            int oldX = activePiece.getX();
            moveRight();
            if (oldX == activePiece.getX()) {
                locationChanged = false;
            }
        }
    }

    public void instantsdf() {
        int counter = activePiece.hardDrop();
        score += (counter) >> 1;
    }
}
