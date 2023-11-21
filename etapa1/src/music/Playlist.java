package music;

import fileio.extended.SongInputExtended;

import java.util.ArrayList;
import java.util.List;

public final class Playlist {

    private String name;
    private String owner;
    private List<SongInputExtended> songs;
    private int followers;
    private boolean privatePlaylist;

    public Playlist(final String name, final String owner) {
        this.name = name;
        this.owner = owner;
        this.privatePlaylist = false;
        songs = new ArrayList<>();
        this.followers = 0;
    }
    public boolean isPrivatePlaylist() {
        return privatePlaylist;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public List<SongInputExtended> getSongs() {
        return songs;
    }

    public void setSongs(final List<SongInputExtended> songs) {
        this.songs = songs;
    }

    /**
     * Method that changes the visibility from private to public and vice-versa
     */
    public void changeVisibility() {
        privatePlaylist = !privatePlaylist;
    }

    /**
     * Calculates the entire playlist duration
     * @return int total duration
     */
    public int getDuration() {
        int duration = 0;
        for (SongInputExtended song : songs) {
            duration += song.getDuration();
        }

        return duration;
    }

    /**
     * Checks if the current playlist contains or not the given song
     * @param songName Given name song
     * @return true or false
     */
    public boolean containsSong(final String songName) {
        for (SongInputExtended song : songs) {
            if (song.getName().equals(songName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove a song from the current playlist
     * @param song Given song
     */
    public void removeSong(final SongInputExtended song) {
        songs.remove(song);
    }

    /**
     * Adds a new song to playlist
     * @param song Given song
     */
    public void addSong(final SongInputExtended song) {
        songs.add(song);
    }

    /**
     * Add a new follower
     */
    public void addFollower() {
        followers++;
    }

    /**
     * Remove a follower
     */
    public void removeFollower() {
        followers--;
    }

    public Integer getFollowers() {
        return followers;
    }
}
