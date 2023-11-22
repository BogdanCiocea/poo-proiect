package service;

import fileio.extended.PodcastInputExtended;
import fileio.extended.SongInputExtended;
import fileio.input.Stats;
import user.UserDetails;
import fileio.input.CommandInput;
import fileio.input.Filters;
import music.Player;
import music.Playlist;

import java.util.ArrayList;
import java.util.List;

public final class Service {

    private Service() {
    }

    private static final int MAX_RESULTS_LIST = 5;

    /**
     * Method used to search for songs
     *
     * @param filter given filter
     * @param songs  All library songs
     * @return List of returned results
     */
    public static List<SongInputExtended> searchSongs(final Filters filter,
                                                      final List<SongInputExtended> songs) {
        List<SongInputExtended> resultSongs = new ArrayList<>();
        boolean alreadyFiltered = false;
        if (filter.getName() != null) {
            for (SongInputExtended songInput : songs) {
                alreadyFiltered = true;
                if (songInput.getName().startsWith(filter.getName())) {
                    resultSongs.add(songInput);
                }
            }
        }
        if (filter.getAlbum() != null) {
            if (alreadyFiltered) {
                List<SongInputExtended> albumSongs = new ArrayList<>();
                for (SongInputExtended songInput : resultSongs) {
                    if (songInput.getAlbum().equals(filter.getAlbum())) {
                        albumSongs.add(songInput);
                    }
                }
                resultSongs = albumSongs;
            } else {
                alreadyFiltered = true;
                for (SongInputExtended songInput : songs) {
                    if (songInput.getAlbum().equals(filter.getAlbum())) {
                        resultSongs.add(songInput);
                    }
                }
            }
        }
        if (filter.getArtist() != null) {
            if (alreadyFiltered) {
                List<SongInputExtended> albumSongs = new ArrayList<>();
                for (SongInputExtended songInput : resultSongs) {
                    if (songInput.getArtist().equals(filter.getArtist())) {
                        albumSongs.add(songInput);
                    }
                }
                resultSongs = albumSongs;
            } else {
                alreadyFiltered = true;
                for (SongInputExtended songInput : songs) {
                    if (songInput.getArtist().equals(filter.getArtist())) {
                        resultSongs.add(songInput);
                    }
                }
            }
        }
        if (filter.getLyrics() != null) {
            if (alreadyFiltered) {
                List<SongInputExtended> albumSongs = new ArrayList<>();
                for (SongInputExtended songInput : resultSongs) {
                    if (songInput.getLyrics().contains(filter.getLyrics())) {
                        albumSongs.add(songInput);
                    }
                }
                resultSongs = albumSongs;
            } else {
                alreadyFiltered = true;
                for (SongInputExtended songInput : songs) {
                    if (songInput.getLyrics().toLowerCase()
                            .contains(filter.getLyrics().toLowerCase())) {
                        resultSongs.add(songInput);
                    }
                }
            }
        }
        if (filter.getGenre() != null) {
            if (alreadyFiltered) {
                List<SongInputExtended> albumSongs = new ArrayList<>();
                for (SongInputExtended songInput : resultSongs) {
                    if (songInput.getGenre().equalsIgnoreCase(filter.getGenre())) {
                        albumSongs.add(songInput);
                    }
                }
                resultSongs = albumSongs;
            } else {
                alreadyFiltered = true;
                for (SongInputExtended songInput : songs) {
                    if (songInput.getGenre().equalsIgnoreCase(filter.getGenre())) {
                        resultSongs.add(songInput);
                    }
                }
            }
        }

        if (filter.getTags() != null) {
            if (alreadyFiltered) {
                List<SongInputExtended> albumSongs = new ArrayList<>();
                for (SongInputExtended songInput : resultSongs) {
                    if (songInput.getTags().containsAll(filter.getTags())) {
                        albumSongs.add(songInput);
                    }
                }
                resultSongs = albumSongs;
            } else {
                alreadyFiltered = true;
                for (SongInputExtended songInput : songs) {
                    if (songInput.getTags().containsAll(filter.getTags())) {
                        resultSongs.add(songInput);
                    }
                }
            }
        }

        if (filter.getReleaseYear() != null) {
            char compareString = filter.getReleaseYear().charAt(0);
            String yearString = filter.getReleaseYear().substring(1);
            int yearInt = Integer.parseInt(yearString);

            if (alreadyFiltered) {
                List<SongInputExtended> albumSongs = new ArrayList<>();
                for (SongInputExtended songInput : resultSongs) {
                    if (compareString == '<') {
                        if (songInput.getReleaseYear() < yearInt) {
                            albumSongs.add(songInput);
                        }
                    } else {
                        if (songInput.getReleaseYear() > yearInt) {
                            albumSongs.add(songInput);
                        }
                    }
                }
                resultSongs = albumSongs;
            } else {
                for (SongInputExtended songInput : songs) {
                    if (compareString == '<') {
                        if (songInput.getReleaseYear() < yearInt) {
                            resultSongs.add(songInput);
                        }
                    } else {
                        if (songInput.getReleaseYear() > yearInt) {
                            resultSongs.add(songInput);
                        }
                    }
                }
            }
        }

        if (resultSongs.size() > MAX_RESULTS_LIST) {
            return resultSongs.subList(0, MAX_RESULTS_LIST);
        }
        return resultSongs;
    }

    /**
     * Method used to search podcasts
     *
     * @return Podcast list
     */
    public static List<PodcastInputExtended>
    searchPodcasts(final Filters filter, final List<PodcastInputExtended> podcasts) {

        List<PodcastInputExtended> resultPodcasts = new ArrayList<>();

        boolean alreadyFiltered = false;

        if (filter.getName() != null) {
            for (PodcastInputExtended podcastInput : podcasts) {
                alreadyFiltered = true;
                if (podcastInput.getName().startsWith(filter.getName())) {
                    resultPodcasts.add(podcastInput);
                }
            }
        }
        if (filter.getOwner() != null) {
            if (alreadyFiltered) {
                List<PodcastInputExtended> ownerPodcasts = new ArrayList<>();
                for (PodcastInputExtended podcastInput : resultPodcasts) {
                    if (podcastInput.getOwner().equals(filter.getOwner())) {
                        ownerPodcasts.add(podcastInput);
                    }
                }
                resultPodcasts = ownerPodcasts;
            } else {
                for (PodcastInputExtended podcastInput : podcasts) {
                    if (podcastInput.getOwner().equals(filter.getOwner())) {
                        resultPodcasts.add(podcastInput);
                    }
                }
            }
        }

        if (resultPodcasts.size() > MAX_RESULTS_LIST) {
            return resultPodcasts.subList(0, MAX_RESULTS_LIST);
        }
        return resultPodcasts;
    }


    /**
     * Method used to search playlists
     *
     * @return Playlist list
     */
    public static List<Playlist> searchPlaylists(final String username,
                                                 final Filters filter,
                                                 final List<Playlist> playlists) {

        List<Playlist> resultPlaylists = new ArrayList<>();

        boolean alreadyFiltered = false;

        if (filter.getName() != null) {
            for (Playlist playlist : playlists) {
                if (playlist.getOwner().equals(username) || !playlist.isPrivatePlaylist()) {
                    alreadyFiltered = true;
                    if (playlist.getName().startsWith(filter.getName())) {
                        resultPlaylists.add(playlist);
                    }
                }
            }
        }
        if (filter.getOwner() != null) {
            if (alreadyFiltered) {
                List<Playlist> ownerPlaylists = new ArrayList<>();
                for (Playlist playlist : resultPlaylists) {
                    if (playlist.getOwner().equals(filter.getOwner())
                            && (playlist.getOwner().equals(username)
                            || !playlist.isPrivatePlaylist())) {
                        ownerPlaylists.add(playlist);
                    }
                }
                resultPlaylists = ownerPlaylists;
            } else {
                for (Playlist playlist : playlists) {
                    if (playlist.getOwner().equals(filter.getOwner())
                            && (playlist.getOwner().equals(username)
                            || !playlist.isPrivatePlaylist())) {
                        resultPlaylists.add(playlist);
                    }
                }
            }
        }
        if (resultPlaylists.size() > MAX_RESULTS_LIST) {
            return resultPlaylists.subList(0, MAX_RESULTS_LIST);
        }
        return resultPlaylists;
    }

    /**
     * Method used to select a song/playlist/podcast based on what the user search before
     *
     * @return String result message
     */
    public static String selectCommand(final CommandInput commandInput,
                                       final UserDetails user) {

        int index = commandInput.getItemNumber() - 1;
        Player player;
        if ((user.getSearchSongResults() == null
                && user.getSearchPodcastsResults() == null
                && user.getSearchPlaylistsResults() == null)
                || user.isSelected() || (user.getPlayer() != null && user.getPlayer().isLoaded())
        ) {
            user.setSelected(false);
            return "Please conduct a search before making a selection.";
        }
        if (user.getTypeSearched().equals("podcast")) {
            assert user.getSearchPodcastsResults() != null;
            if (index >= user.getSearchPodcastsResults().size()) {
                user.setSelected(false);
                return "The selected ID is too high.";
            }
            user.setSelected(true);
            player = new Player("podcast");
            player.setPodcastInputExtended(user.getSearchPodcastsResults().get(index));
            user.setPlayer(player);

            return "Successfully selected "
                    + user.getSearchPodcastsResults().get(index).getName() + ".";
        } else if (user.getTypeSearched().equals("song")) {
            assert user.getSearchSongResults() != null;
            if (index >= user.getSearchSongResults().size()) {
                user.setSelected(false);
                return "The selected ID is too high.";
            }
            user.setSelected(true);
            player = new Player("song");
            player.setSongInputExtended(user.getSearchSongResults().get(index));
            user.setPlayer(player);
            return "Successfully selected "
                    + user.getSearchSongResults().get(index).getName() + ".";
        } else {
            if (index >= user.getSearchPlaylistsResults().size()) {
                user.setSelected(false);
                return "The selected ID is too high.";
            }
            user.setSelected(true);
            player = new Player("playlist");
            player.setPlaylist(user.getSearchPlaylistsResults().get(index));
            user.setPlayer(player);
            return "Successfully selected "
                    + user.getSearchPlaylistsResults().get(index).getName() + ".";
        }
    }

    /**
     * This method is used to load a selected source to be able to play it
     *
     * @return Result message
     */
    public static String load(final int timestamp,
                              final UserDetails user) {

        if (user.getPlayer() == null || !user.isSelected()) {
            return "Please select a source before attempting to load.";
        }
        user.setSelected(false);
        user.getPlayer().play(timestamp, user);

        return "Playback loaded successfully.";
    }

    /**
     * Method used to play/pause the player
     *
     * @return String message
     */
    public static String playPause(final int timestamp,
                                   final UserDetails user) {

        if (user.getPlayer() == null
                || !user.getPlayer().isLoaded() || user.getPlayer().isFinished()) {
            return "Please load a source before attempting to pause or resume playback.";
        }
        Player player = user.getPlayer();
        if (player.isPlaying()) {
            player.pause(timestamp, user);
            return "Playback paused successfully.";
        } else {
            user.getPlayer().play(timestamp, user);
            return "Playback resumed successfully.";
        }
    }

    /**
     * Method used to get the current user's player
     *
     * @param currentTimestamp the time of the status
     * @param userDetails the player's details
     * @return Stats object that contains the details about the status
     */
    public static Stats status(final int currentTimestamp,
                               final UserDetails userDetails) {
        if (userDetails.getPlayer() == null) {
            return new Stats("", 0, "No Repeat", false, false);
        }
        return userDetails.getPlayer().getStatus(currentTimestamp);
    }

    /**
     * Method used to create a playlist for a user
     *
     * @param playlists the list of the user's playlists
     * @param command the command which we get the name of the playlist from
     * @return String message
     */
    public static String createPlaylist(final List<Playlist> playlists,
                                        final CommandInput command) {
        for (Playlist playlist : playlists) {
            if (playlist.getName().equals(command.getPlaylistName())
                    && playlist.getOwner().equals(command.getUsername())) {
                return "A playlist with the same name already exists.";
            }
        }
        playlists.add(new Playlist(command.getPlaylistName(), command.getUsername()));
        return "Playlist created successfully.";
    }

    /**
     * Method used to add/remove a song from a playlist
     *
     * @param index the playlistId
     * @param user the user
     * @param playlists the user's list of playlists
     * @return String message
     */
    public static String addRemove(final int index,
                                   final UserDetails user,
                                   final List<Playlist> playlists) {
        Player player = user.getPlayer();

        if (player == null || !player.isLoaded() || user.getLastCommand().equals("search")) {
            return "Please load a source before adding to or removing from the playlist.";
        }
        if (!player.getPlayingType().equals("song")) {
            return "The loaded source is not a song.";
        }
        List<Playlist> userPlaylists = retrieveUserPlaylists(user.getUsername(), playlists);
        if (userPlaylists.size() < index) {
            return "The specified playlist does not exist.";
        }
        Playlist playlist = userPlaylists.get(index - 1);
        if (playlist.containsSong(player.getSongInputExtended().getName())) {
            playlist.removeSong(player.getSongInputExtended());
            return "Successfully removed from playlist.";
        } else {
            playlist.addSong(player.getSongInputExtended());
            return "Successfully added to playlist.";
        }
    }

    /**
     * Method used to get user's playlists
     *
     * @param username the name of the user
     * @param playlists the playlists of the user
     * @return The list of user's playlists
     */
    public static List<Playlist> retrieveUserPlaylists(final String username,
                                                       final List<Playlist> playlists) {
        List<Playlist> userPlayLists = new ArrayList<>();

        for (Playlist playlist : playlists) {
            if (playlist.getOwner().equals(username)) {
                userPlayLists.add(playlist);
            }
        }
        return userPlayLists;
    }

    /**
     * Method used to handle the repeat
     *
     * @param user the user
     * @param timestamp timestamp when the command was given
     * @return String message
     */
    public static String repeat(final UserDetails user,
                                final int timestamp) {
        if (user.getPlayer() == null
                || !user.getPlayer().isLoaded()
                || user.getPlayer().isFinished()) {
            return "Please load a source before setting the repeat status.";
        }

        user.getPlayer().setRepeat(timestamp);
        return "Repeat mode changed to "
                + user.getPlayer().getRepeatMessage().toLowerCase() + ".";
    }

    /**
     * Method used to handle the shuffle
     *
     * @param user the user
     * @param seed the seed given
     * @param timestamp timestamp when the command was given
     * @return String message
     */
    public static String shuffle(final UserDetails user,
                                 final int seed,
                                 final int timestamp) {
        if (user.getPlayer() == null
                || !user.getPlayer().isLoaded()
                || user.getPlayer().isFinished()) {
            return "Please load a source before using the shuffle function.";
        }
        if (!user.getPlayer().getPlayingType().equals("playlist")) {
            return "The loaded source is not a playlist.";
        }
        user.getPlayer().checkIfFinished(timestamp);
        if (user.getPlayer().isFinished()) {
            return "Please load a source before using the shuffle function.";
        }
        return user.getPlayer().shuffle(seed, timestamp);
    }

    /**
     * Method used to move forward
     *
     * @param user the user
     * @param timestamp timestamp when the command was given
     * @return String message
     */
    public static String forward(final UserDetails user,
                                 final int timestamp) {
        if (user.getPlayer() == null
                || !user.getPlayer().isLoaded()
                || user.getPlayer().isFinished()) {
            return "Please load a source before attempting to forward.";
        }
        if (!user.getPlayer().getPlayingType().equals("podcast")) {
            return "The loaded source is not a podcast.";
        }
        return user.getPlayer().forward(timestamp);
    }

    /**
     * Method used to move backward
     *
     * @param user the user
     * @param timestamp timestamp when the command was given
     * @return String message
     */
    public static String backward(final UserDetails user,
                                  final int timestamp) {
        if (user.getPlayer() == null
                || !user.getPlayer().isLoaded()
                || user.getPlayer().isFinished()) {
            return "Please load a source before attempting to backward.";
        }
        if (!user.getPlayer().getPlayingType().equals("podcast")) {
            return "The loaded source is not a podcast.";
        }
        return user.getPlayer().backward(timestamp);
    }

    /**
     * Method used handle the moving to the next song considering the repeat mode
     *
     * @param timestamp current time
     * @return String message
     */
    public static String next(final UserDetails user,
                              final int timestamp) {
        if (user.getPlayer() == null
                || !user.getPlayer().isLoaded()
                || user.getPlayer().isFinished()) {
            return "Please load a source before skipping to the next track.";
        }
        String message = user.getPlayer().next(timestamp);
        if (user.getPlayer().isPaused()) {
            user.getPlayer().play(timestamp, user);
        }
        return message;
    }

    /**
     *Method used handle the moving to the previous song considering the repeat mode
     *
     * @param timestamp current time
     * @return String message
     */
    public static String prev(final UserDetails user,
                              final int timestamp) {
        if (user.getPlayer() == null
                || !user.getPlayer().isLoaded()
                || user.getPlayer().isFinished()) {
            return "Please load a source before returning to the previous track.";
        }
        String message = user.getPlayer().prev(timestamp);
        if (user.getPlayer().isPaused()) {
            user.getPlayer().play(timestamp, user);
        }
        return message;
    }

    /**
     * Method used to like/unlike a song that is currently playing
     *
     * @param user the user that likes/unlikes
     * @param timestamp timestamp when the command was given
     * @return String message
     */
    public static String like(final UserDetails user,
                              final int timestamp) {
        Player player = user.getPlayer();

        if (player == null || !player.isLoaded() || player.isFinished()) {
            return "Please load a source before liking or unliking.";
        }
        if (player.getPlayingType().equals("podcast")) {
            return "Loaded source is not a song.";
        }
        SongInputExtended song;
        if (player.getPlayingType().equals("song")) {
            song = player.getSongInputExtended();
        } else {
            song = player.getPlayingSongFromPlaylist(timestamp);
        }
        if (user.getPlayer().getRepeat() == 2) {
            song = user.getPlayer().getSelectedToRepeatFromPlaylist();
        }

        if (user.getLikedSongs().contains(song)) {
            user.removeLikedSong(song);
            assert song != null;
            song.decrementLike(timestamp);
            return "Unlike registered successfully.";
        } else {
            user.addLikedSong(song);
            assert song != null;
            song.incrementLike(timestamp);
            return "Like registered successfully.";
        }
    }

    public static ArrayList<String> showPreferredSongs(UserDetails userDetails) {
        ArrayList<String> songs = new ArrayList<>();
        for (SongInputExtended songInputExtended : userDetails.getLikedSongs()) {
            songs.add(songInputExtended.getName());
        }
        return songs;
    }

    /**
     * Method that sorts the likes list and retrieve the first maximum 5 elements
     * @return List results
     */
    public static List<String> getTop5S(final List<SongInputExtended> likes) {
        List<SongInputExtended> copy = new ArrayList<>(likes);
        copy.sort((o1, o2) -> {
            if (o2.getLikes().compareTo(o1.getLikes()) == 0) {
                return Integer.compare(likes.indexOf(o1), likes.indexOf(o2));
            } else {
                return o2.getLikes().compareTo(o1.getLikes());
            }
        });

        List<String> results = new ArrayList<>();

        for (int i = 0; i < MAX_RESULTS_LIST && i < copy.size(); i++) {
            results.add(copy.get(i).getName());
        }
        return results;
    }

    public static List<String> getTop5P(final List<Playlist> playlists) {

        playlists.sort((o1, o2) -> o2.getFollowers().compareTo(o1.getFollowers()));
        List<String> results = new ArrayList<>();
        for (int i = 0; i < MAX_RESULTS_LIST && i < playlists.size(); i++) {
            results.add(playlists.get(i).getName());
        }
        return results;

    }

    /**
     * Method called when user wants to follow a playlist
     * @return returned string message
     */
    public static String follow(final UserDetails user) {
        if (user.getPlayer() == null || !user.isSelected()) {
            return "Please select a source before following or unfollowing.";
        }
        if (!user.getTypeSearched().equals("playlist")) {
            return "The selected source is not a playlist.";
        }
        Playlist playlist = user.getPlayer().getPlaylist();
        if (playlist.getOwner().equals(user.getUsername())) {
            return "You cannot follow or unfollow your own playlist.";
        }
        return "Playlist " + user.follow(playlist) + " successfully.";
    }

    /**
     * Method used to change the visibility for a playlist
     * @return String
     */
    public static String switchVisibility(final int index,
                                          final String username,
                                          final List<Playlist> playlists) {
        List<Playlist> userPlaylists = retrieveUserPlaylists(username, playlists);
        if (userPlaylists.size() < index) {
            return "The specified playlist ID is too high.";
        }
        userPlaylists.get(index - 1).changeVisibility();
        String visibility =
                userPlaylists.get(index - 1).isPrivatePlaylist() ? "private" : "public";
        return "Visibility status updated successfully to " + visibility + ".";
    }
}
