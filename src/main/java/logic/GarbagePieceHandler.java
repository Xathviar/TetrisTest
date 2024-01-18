package logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a handler for garbage pieces in a Tetris game.
 * Garbage pieces are generated and added to a garbage collector.
 * The handler provides methods to manage and retrieve the garbage pieces.
 */
public class GarbagePieceHandler {

    /**
     * This list stores all Garbage that is currently in the Queue
     */
    public final List<GarbagePiece> tetrisGarbageCollector;

    /**
     * Represents a handler for garbage pieces in a Tetris game.
     * Garbage pieces are generated and added to a garbage collector.
     * The handler provides methods to manage and retrieve the garbage pieces.
     */
    public GarbagePieceHandler() {
        tetrisGarbageCollector = new ArrayList<>();
    }

    /**
     * Retrieves ready garbage pieces from the garbage collector.
     *
     * @return a list of GarbagePiece objects that are ready to be processed and then being removed
     */
    public List<GarbagePiece> getReadyGarbage() {
        List<GarbagePiece> ret = new ArrayList<>();
        for (GarbagePiece garbagePiece : tetrisGarbageCollector) {
            if (garbagePiece.checkIsReady()) {
                ret.add(garbagePiece);
            }
        }
        tetrisGarbageCollector.removeAll(ret);
        return ret;
    }

    /**
     * Adds a specified number of lines of garbage to the garbage collector.
     *
     * @param lines the number of lines of garbage to add
     */
    public void addGarbage(int lines) {
        tetrisGarbageCollector.add(new GarbagePiece(lines));
    }

    /**
     * Removes the specified number of lines from the garbage pieces in the garbage collector.
     *
     * @param lines the number of lines to remove from the garbage pieces
     * @return the remaining number of lines to be removed, after subtracting from the garbage pieces
     */
    public int removeGarbageLines(int lines) {
        List<GarbagePiece> toRemove = new ArrayList<>();
        for (GarbagePiece garbagePiece : tetrisGarbageCollector) {
            int diff = garbagePiece.getLines() - lines;
            if (diff > 0) {
                garbagePiece.removeLines(lines);
                return 0;
            } else if (diff == 0) {
                toRemove.add(garbagePiece);
                lines = 0;
                break;
            } else {
                toRemove.add(garbagePiece);
                lines -= garbagePiece.getLines();
            }
        }
        tetrisGarbageCollector.removeAll(toRemove);
        return lines;
    }

    /**
     * Determines whether a given number indicates that there should be garbage in the Tetris game.
     *
     * @param n the number to be checked against the size of the garbage collector
     * @return true if the size of the garbage collector is greater than or equal to the given number, false otherwise
     */
    public boolean shouldBeGarbageIndicator(int n) {
        return tetrisGarbageCollector.size() >= n;
    }
}
