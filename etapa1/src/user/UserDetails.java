package user;

import fileio.extended.PodcastInputExtended;
import fileio.extended.SongInputExtended;
import music.Player;
import music.Playlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class UserDetails {

    private final String username;
    private List<SongInputExtended> searchSongResults;

    private final List<SongInputExtended> likedSongs;

    private List<PodcastInputExtended> searchPodcastsResults;

    private List<Playlist> searchPlaylistsResults;

    private final List<Playlist> followedPlaylists;

    private final Map<PodcastInputExtended, Integer> alreadyPlayedPodcasts;

    private String typeSearched;

    private Player player;

    private boolean selected;

    private String lastCommand;

    public UserDetails(final String username) {
        likedSongs = new ArrayList<>();
        alreadyPlayedPodcasts = new HashMap<>();
        this.username = username;
        this.followedPlaylists = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    /**
     * Method used when the current user wants to follow/unfollow a playlist
     * @param playlist playlist
     * @return String followed or unfollowed
     */
    public String follow(final Playlist playlist) {
        if (followedPlaylists.contains(playlist)) {
            followedPlaylists.remove(playlist);
            playlist.removeFollower();
            return "unfollowed";
        } else {
            followedPlaylists.add(playlist);
            playlist.addFollower();
            return "followed";
        }
    }

    public List<SongInputExtended> getSearchSongResults() {
        return searchSongResults;
    }

    public void setSearchSongResults(final List<SongInputExtended> searchSongResults) {
        this.searchSongResults = searchSongResults;
    }

    public List<PodcastInputExtended> getSearchPodcastsResults() {
        return searchPodcastsResults;
    }

    public String getLastCommand() {
        return lastCommand;
    }

    public void setLastCommand(final String lastCommand) {
        this.lastCommand = lastCommand;
    }

    public List<Playlist> getSearchPlaylistsResults() {
        return searchPlaylistsResults;
    }

    public void setSearchPlaylistsResults(final List<Playlist> searchPlaylistsResults) {
        this.searchPlaylistsResults = searchPlaylistsResults;
    }

    /**
     * Saves the results when the current user performs a search command for a playlist
     * @param searchPodcastsResults playlists results
     */
    public void setSearchPodcastsResults(final List<PodcastInputExtended> searchPodcastsResults) {
        this.searchPodcastsResults = searchPodcastsResults;
    }

    public String getTypeSearched() {
        return typeSearched;
    }

    public void setTypeSearched(final String typeSearched) {
        this.typeSearched = typeSearched;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(final Player player) {
        this.player = player;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(final boolean selected) {
        this.selected = selected;
    }

    public List<SongInputExtended> getLikedSongs() {
        return likedSongs;
    }

    /**
     * Add a new song to the liked song list
     * @param song Liked song
     */
    public void addLikedSong(final SongInputExtended song) {
        likedSongs.add(song);
    }

    /**
     * remove the song when unliking
     * @param song Unliked song
     */
    public void removeLikedSong(final SongInputExtended song) {
        likedSongs.remove(song);
    }

    /**
     * Method used to reset the player
     * @param timestamp current time
     */
    public void resetPlayer(final int timestamp) {
        if (player != null) {
            player.resetLoader(timestamp, this);
            if (player.getPlayingType().equals("podcast")) {
                alreadyPlayedPodcasts.put(player.getPodcastInputExtended(), player.getPlayedTime());
            }
            player.setFinished();
        }
    }

    public Map<PodcastInputExtended, Integer> getAlreadyPlayedPodcasts() {
        return alreadyPlayedPodcasts;
    }
}
