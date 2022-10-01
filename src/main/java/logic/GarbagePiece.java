package logic;

public class GarbagePiece {
    private int lines;
    private long timestamp;

    public GarbagePiece(int lines) {
        this.lines = lines;
        this.timestamp = System.currentTimeMillis();
    }

    public boolean checkIsReady() {
        return System.currentTimeMillis() - timestamp > 300000;
    }

    public int getLines() {
        return lines;
    }

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
