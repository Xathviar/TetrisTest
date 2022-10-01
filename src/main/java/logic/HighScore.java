package logic;

public class HighScore implements Comparable<HighScore> {
    private final String name;
    private final int level;
    private final long score;
    private final String time;

    public HighScore(String name, int level, long score, String time) {
        this.name = name;
        this.level = level;
        this.score = score;
        this.time = time;
    }

    @Override
    public int compareTo(HighScore o) {
        if (score == o.score) {
            if (level == o.level) {
                if (time.equals(o.time)) {
                    return name.compareTo(o.name);
                } else {
                    return time.compareTo(o.time);
                }
            } else {
                return Integer.compare(o.level, level);
            }
        } else {
            return Long.compare(o.score, score);
        }
    }

    @Override
    public String toString() {
        return String.format("%16s|   %2d  |%07d| %s ", StringUtils.center(name, 16, ' '), level, score, time);
    }
}
