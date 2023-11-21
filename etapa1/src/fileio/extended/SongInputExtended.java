package fileio.extended;

import fileio.input.SongInput;

public class SongInputExtended extends SongInput {
    private int lastTimestampLike;

    public Integer getLikes() {
        return likes;
    }

    public final void setLikes(final int likes) {
        this.likes = likes;
    }

    private int likes;

    /**
     * Method used to like a song
     *
     * @param timestamp the timestamp
     */
    public final void incrementLike(final int timestamp) {
        likes++;
        lastTimestampLike = timestamp;
    }

    /**
     * Method used to unlike a song
     *
     * @param timestamp the timestamp
     */
    public final void decrementLike(final int timestamp) {
        likes--;
        lastTimestampLike = timestamp;
    }
}
