package fileio.input;

public final class Stats {
    private final String name;
    private final int remainedTime;
    private final String repeat;
    private final boolean shuffle;
    private final boolean paused;

    public Stats(final String name,
                 final int remainedTime,
                 final String repeat,
                 final boolean shuffle,
                 final boolean paused) {
        this.name = name;
        this.remainedTime = remainedTime;
        this.repeat = repeat;
        this.shuffle = shuffle;
        this.paused = paused;
    }

    public String getName() {
        return name;
    }

    public int getRemainedTime() {
        return remainedTime;
    }

    public String getRepeat() {
        return repeat;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public boolean isPaused() {
        return paused;
    }
}
