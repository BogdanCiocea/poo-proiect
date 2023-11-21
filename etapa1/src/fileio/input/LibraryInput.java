package fileio.input;

import fileio.extended.PodcastInputExtended;
import fileio.extended.SongInputExtended;

import java.util.ArrayList;

public final class LibraryInput {
    private ArrayList<SongInputExtended> songs;
    private ArrayList<PodcastInputExtended> podcasts;
    private ArrayList<UserInput> users;

    public LibraryInput() {
    }

    public ArrayList<SongInputExtended> getSongs() {
        return songs;
    }

    public void setSongs(final ArrayList<SongInputExtended> songs) {
        this.songs = songs;
    }

    public ArrayList<PodcastInputExtended> getPodcasts() {
        return podcasts;
    }

    public void setPodcasts(final ArrayList<PodcastInputExtended> podcasts) {
        this.podcasts = podcasts;
    }

    public ArrayList<UserInput> getUsers() {
        return users;
    }

    public void setUsers(final ArrayList<UserInput> users) {
        this.users = users;
    }
}
