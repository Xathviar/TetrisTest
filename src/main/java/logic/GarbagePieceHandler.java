package logic;

import java.util.ArrayList;
import java.util.List;

public class GarbagePieceHandler {
    public final List<GarbagePiece> tetrisGarbageCollector;

    public GarbagePieceHandler() {
        tetrisGarbageCollector = new ArrayList<>();
    }

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

    public void addGarbage(int lines) {
        tetrisGarbageCollector.add(new GarbagePiece(lines));
    }

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

    public int getWaitingGarbage() {
        return tetrisGarbageCollector.size();
    }

    public boolean shouldBeGarbageIndicator(int n) {
        System.out.println(String.format("%d: %d", tetrisGarbageCollector.size(), n));
        return tetrisGarbageCollector.size() >= n;
    }
}
