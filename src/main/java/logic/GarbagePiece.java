package logic;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a piece of garbage in a Tetris game. <br>
 */
@Getter
@Setter
public class GarbagePiece {

    /**
     * How many lines of garbage are sent
     */
    private int lines;

    /**
     * This timestamp represents when the Garbage was created, if it is older than 5 Seconds it will be sent to the tetris field
     */
    private final long timestamp;

    /**
     * Represents a piece of garbage in a Tetris game.
     * Garbage pieces are generated and added to a garbage collector.
     * If a garbage piece is older than 5 seconds, it is considered ready and can be processed or removed.
     */
    public GarbagePiece(int lines) {
        this.lines = lines;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Checks if the GarbagePiece is ready to be processed or removed.
     *
     * @return true if the GarbagePiece is ready, false otherwise
     */
    public boolean checkIsReady() {
        return System.currentTimeMillis() - timestamp > 5000;
    }

    /**
     * Removes the specified number of lines from the total number of lines in the GarbagePiece.
     *
     * @param lines the number of lines to remove
     */
    public void removeLines(int lines) {
        this.lines -= lines;
    }

    @Override
    public String toString() {
        return "GarbagePiece{" +
                "lines=" + lines +
                ", timestamp=" + timestamp +
                '}';
    }
}
