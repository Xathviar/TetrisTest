package logic;

public class HighScore implements Comparable<HighScore> {
    private String name;
    private int level;
    private long score;
    private String time;

    public HighScore(String name, int level, long score, String time) {
        this.name = name;
        this.level = level;
        this.score = score;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
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
