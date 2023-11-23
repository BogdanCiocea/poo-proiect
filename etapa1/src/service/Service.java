package service;

import fileio.extended.PodcastInputExtended;
import fileio.extended.SongInputExtended;
import fileio.Stats;
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
    public static List<SongInputExtended>
    searchSongs(final Filters filter, final List<SongInputExtended> songs) {
        List<SongInputExtended> resultSongs = new ArrayList<>();
        // A flag to check if any filtering has already been done.
        boolean alreadyFiltered = false;
        // If a name filter is provided, filter songs by name.
        if (filter.getName() != null) {
            for (SongInputExtended songInput : songs) {
                alreadyFiltered = true;
                // Check if song name starts with the specified filter name.
                if (songInput.getName().startsWith(filter.getName())) {
                    resultSongs.add(songInput);
                }
            }
        }
        // If an album filter is provided, filter songs by album.
        if (filter.getAlbum() != null) {
            if (alreadyFiltered) {
                // Filter within the already filtered results.
                List<SongInputExtended> albumSongs = new ArrayList<>();
                for (SongInputExtended songInput : resultSongs) {
                    if (songInput.getAlbum().equals(filter.getAlbum())) {
                        albumSongs.add(songInput);
                    }
                }
                resultSongs = albumSongs;
            } else {
                // Filter from the original song list.
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
                // Filter within the already filtered results.
                List<SongInputExtended> albumSongs = new ArrayList<>();
                for (SongInputExtended songInput : resultSongs) {
                    if (songInput.getArtist().equals(filter.getArtist())) {
                        albumSongs.add(songInput);
                    }
                }
                resultSongs = albumSongs;
            } else {
                alreadyFiltered = true;
                // Filter from the original song list.
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
        // Limit the results to a maximum number (MAX_RESULTS_LIST).
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

        // Initialize a list to store podcasts that match the filter criteria.
        List<PodcastInputExtended> resultPodcasts = new ArrayList<>();

        // A flag to check if any filtering has already been done.
        boolean alreadyFiltered = false;

        // If a name filter is provided, filter podcasts by name.
        if (filter.getName() != null) {
            for (PodcastInputExtended podcastInput : podcasts) {
                // Mark that filtering has been applied.
                alreadyFiltered = true;
                // Check if the podcast name starts with the specified filter name.
                if (podcastInput.getName().startsWith(filter.getName())) {
                    resultPodcasts.add(podcastInput);
                }
            }
        }

        // If an owner filter is provided, filter podcasts by owner.
        if (filter.getOwner() != null) {
            if (alreadyFiltered) {
                // Filter within the already filtered results.
                List<PodcastInputExtended> ownerPodcasts = new ArrayList<>();
                for (PodcastInputExtended podcastInput : resultPodcasts) {
                    if (podcastInput.getOwner().equals(filter.getOwner())) {
                        ownerPodcasts.add(podcastInput);
                    }
                }
                // Update the resultPodcasts with the newly filtered list.
                resultPodcasts = ownerPodcasts;
            } else {
                // Filter from the original podcast list.
                for (PodcastInputExtended podcastInput : podcasts) {
                    if (podcastInput.getOwner().equals(filter.getOwner())) {
                        resultPodcasts.add(podcastInput);
                    }
                }
            }
        }

        // Limit the number of results to a maximum defined by MAX_RESULTS_LIST.
        if (resultPodcasts.size() > MAX_RESULTS_LIST) {
            // Return only the first MAX_RESULTS_LIST elements.
            return resultPodcasts.subList(0, MAX_RESULTS_LIST);
        }

        // Return the final list of filtered podcasts.
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

        // Initialize a list to store the playlists that match the filter criteria.
        List<Playlist> resultPlaylists = new ArrayList<>();

        // A flag to check if any filtering has already been done.
        boolean alreadyFiltered = false;

        // If a name filter is provided, filter playlists by name.
        if (filter.getName() != null) {
            for (Playlist playlist : playlists) {
                // Check if the playlist is owned by the user or is a public playlist.
                if (playlist.getOwner().equals(username) || !playlist.isPrivatePlaylist()) {
                    // Mark that filtering has been applied.
                    alreadyFiltered = true;
                    // Check if the playlist name starts with the specified filter name.
                    if (playlist.getName().startsWith(filter.getName())) {
                        resultPlaylists.add(playlist);
                    }
                }
            }
        }

        // If an owner filter is provided, further filter playlists by owner.
        if (filter.getOwner() != null) {
            if (alreadyFiltered) {
                // Filter within the already filtered results.
                List<Playlist> ownerPlaylists = new ArrayList<>();
                for (Playlist playlist : resultPlaylists) {
                    if (playlist.getOwner().equals(filter.getOwner())
                            && (playlist.getOwner().equals(username)
                            || !playlist.isPrivatePlaylist())) {
                        ownerPlaylists.add(playlist);
                    }
                }
                // Update the resultPlaylists with the newly filtered list.
                resultPlaylists = ownerPlaylists;
            } else {
                // Filter from the original playlist list.
                for (Playlist playlist : playlists) {
                    if (playlist.getOwner().equals(filter.getOwner())
                            && (playlist.getOwner().equals(username)
                            || !playlist.isPrivatePlaylist())) {
                        resultPlaylists.add(playlist);
                    }
                }
            }
        }

        // Limit the number of results to a maximum defined by MAX_RESULTS_LIST.
        if (resultPlaylists.size() > MAX_RESULTS_LIST) {
            // Return only the first MAX_RESULTS_LIST elements.
            return resultPlaylists.subList(0, MAX_RESULTS_LIST);
        }

        // Return the final list of filtered playlists.
        return resultPlaylists;
    }

    /**
     * Method used to select a song/playlist/podcast based on what the user search before
     *
     * @return String result message
     */
    public static String selectCommand(final CommandInput commandInput, final UserDetails user) {

        // Calculate the index of the selected item by
        // adjusting the user-provided number to 0-based indexing.
        int index = commandInput.getItemNumber() - 1;

        // Declare a player variable to manage the playback of the selected item.
        Player player;

        // Check if the user has not conducted a search, has already made a
        // selection, or if the player is already loaded.
        if ((user.getSearchSongResults() == null
                && user.getSearchPodcastsResults() == null
                && user.getSearchPlaylistsResults() == null)
                || user.isSelected()
                || (user.getPlayer() != null && user.getPlayer().isLoaded())
        ) {
            // If any of the above conditions are true, reset the
            // selection state and prompt the user to perform a search first.
            user.setSelected(false);
            return "Please conduct a search before making a selection.";
        }

        // Check if the last search type was 'podcast'.
        if (user.getTypeSearched().equals("podcast")) {
            // Ensure that the podcast search results are not null.
            assert user.getSearchPodcastsResults() != null;

            // Validate the selected index against the size of the podcast search results.
            if (index >= user.getSearchPodcastsResults().size()) {
                // If the index is out of range, reset the selection
                // state and return an error message.
                user.setSelected(false);
                return "The selected ID is too high.";
            }

            // Mark the selection as made.
            user.setSelected(true);
            // Initialize the player for the podcast type.
            player = new Player("podcast");
            // Set the selected podcast in the player.
            player.setPodcastInputExtended(user.getSearchPodcastsResults().get(index));
            // Update the user's player setting with the newly created player.
            user.setPlayer(player);

            // Return a success message with the name of the selected podcast.
            return "Successfully selected "
                    + user.getSearchPodcastsResults().get(index).getName() + ".";
        } else if (user.getTypeSearched().equals("song")) {
            // Similar handling as podcasts but for songs.
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
            // Handling for playlists.
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
    public static String load(final int timestamp, final UserDetails user) {

        // Check if a player has been initialized and a source has been selected.
        if (user.getPlayer() == null || !user.isSelected()) {
            // If no source has been selected or the player is not initialized,
            // return an error message.
            return "Please select a source before attempting to load.";
        }

        // Reset the user's selection state. This may be to prevent the same selection
        // from being played multiple times.
        user.setSelected(false);

        // Use the user's player to play the selected source starting from the given timestamp.
        user.getPlayer().play(timestamp, user);

        // Return a success message indicating that the playback has been successfully loaded.
        return "Playback loaded successfully.";
    }

    /**
     * Method used to play/pause the player
     *
     * @return String message
     */
    public static String playPause(final int timestamp, final UserDetails user) {

        // Check if the player is initialized and is in a state where it can
        // pause or resume playback.
        if (user.getPlayer() == null || !user.getPlayer().isLoaded()
                || user.getPlayer().isFinished()) {
            // If the player hasn't loaded a source or playback has finished,
            // return an error message.
            return "Please load a source before attempting to pause or resume playback.";
        }

        // Retrieve the player instance from the user details.
        Player player = user.getPlayer();

        // Check if the player is currently playing.
        if (player.isPlaying()) {
            // If the player is playing, pause it at the current timestamp.
            player.pause(timestamp, user);
            // Return a success message indicating that the playback has been paused.
            return "Playback paused successfully.";
        } else {
            // If the player is not playing (i.e., it's paused),
            // resume playback from the given timestamp.
            player.play(timestamp, user);
            // Return a success message indicating that the playback has been resumed.
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
    public static Stats status(final int currentTimestamp, final UserDetails userDetails) {

        // Check if the user has an initialized player.
        if (userDetails.getPlayer() == null) {
            // If the player is not initialized, return a default Stats object.
            return new Stats("", 0, "No Repeat", false, false);
        }

        // If the player is initialized, get the current status of the player.
        return userDetails.getPlayer().getStatus(currentTimestamp);
    }

    /**
     * Method used to create a playlist for a user
     *
     * @param playlists the list of the user's playlists
     * @param command the command which we get the name of the playlist from
     * @return String message
     */
    public static String
    createPlaylist(final List<Playlist> playlists, final CommandInput command) {

        // Iterate through the existing playlists.
        for (Playlist playlist : playlists) {
            // Check if there is already a playlist with the same name by the same user.
            if (playlist.getName().equals(command.getPlaylistName())
                    && playlist.getOwner().equals(command.getUsername())) {
                // If such a playlist exists, return an error message.
                return "A playlist with the same name already exists.";
            }
        }

        // If no existing playlist matches the criteria, create a new playlist.
        // The new playlist is initialized with the name and owner provided in the command.
        playlists.add(new Playlist(command.getPlaylistName(), command.getUsername()));

        // Return a success message indicating that the playlist has been created.
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
    public static String
    addRemove(final int index, final UserDetails user, final List<Playlist> playlists) {
        // Retrieve the player from the user details.
        Player player = user.getPlayer();

        // Check if a player is loaded and the last command was not a search.
        if (player == null || !player.isLoaded() || user.getLastCommand().equals("search")) {
            // Return an error message if no source is loaded or the last command was a search.
            return "Please load a source before adding to or removing from the playlist.";
        }

        // Check if the loaded source is a song.
        if (!player.getPlayingType().equals("song")) {
            // Return an error message if the loaded source is not a song.
            return "The loaded source is not a song.";
        }

        // Retrieve the user's playlists.
        List<Playlist> userPlaylists = retrieveUserPlaylists(user.getUsername(), playlists);

        // Check if the specified index corresponds to an existing playlist.
        if (userPlaylists.size() < index) {
            // Return an error message if the playlist at the specified index does not exist.
            return "The specified playlist does not exist.";
        }

        // Retrieve the playlist at the given index (adjusted for 0-based indexing).
        Playlist playlist = userPlaylists.get(index - 1);

        // Check if the song is already in the playlist.
        if (playlist.containsSong(player.getSongInputExtended().getName())) {
            // If the song is in the playlist, remove it.
            playlist.removeSong(player.getSongInputExtended());
            // Return a success message indicating the song was removed.
            return "Successfully removed from playlist.";
        } else {
            // If the song is not in the playlist, add it.
            playlist.addSong(player.getSongInputExtended());
            // Return a success message indicating the song was added.
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
    public static List<Playlist>
    retrieveUserPlaylists(final String username, final List<Playlist> playlists) {
        // Create a new list to hold playlists that belong to the specified user.
        List<Playlist> userPlayLists = new ArrayList<>();

        // Iterate through the list of all playlists.
        for (Playlist playlist : playlists) {
            // Check if the current playlist is owned by the user.
            if (playlist.getOwner().equals(username)) {
                // If the playlist is owned by the user, add it to the user's playlist list.
                userPlayLists.add(playlist);
            }
        }

        // Return the list of playlists that belong to the user.
        return userPlayLists;
    }

    /**
     * Method used to handle the repeat
     *
     * @param user the user
     * @param timestamp timestamp when the command was given
     * @return String message
     */
    public static String repeat(final UserDetails user, final int timestamp) {
        // Check if a player is initialized, loaded with a source, and has not finished playback.
        if (user.getPlayer() == null
                || !user.getPlayer().isLoaded()
                || user.getPlayer().isFinished()) {
            // If any of these conditions are true, return an error message.
            return "Please load a source before setting the repeat status.";
        }

        // Set the repeat status of the player.
        user.getPlayer().setRepeat(timestamp);

        // Return a message indicating the current repeat state.
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
    public static String shuffle(final UserDetails user, final int seed, final int timestamp) {
        // Check if a player is initialized, loaded with a source, and has not finished playback.
        if (user.getPlayer() == null || !user.getPlayer().isLoaded()
                || user.getPlayer().isFinished()) {
            // Return an error message if no source is loaded or if playback has finished.
            return "Please load a source before using the shuffle function.";
        }

        // Check if the loaded source is a playlist.
        if (!user.getPlayer().getPlayingType().equals("playlist")) {
            // Return an error message if the loaded source is not a playlist.
            return "The loaded source is not a playlist.";
        }

        // Check if the playlist has finished playing.
        user.getPlayer().checkIfFinished(timestamp);
        if (user.getPlayer().isFinished()) {
            // If the playlist has finished, return an error message.
            return "Please load a source before using the shuffle function.";
        }

        // Shuffle the playlist using the provided seed and return the result.
        return user.getPlayer().shuffle(seed, timestamp);
    }


    /**
     * Method used to move forward
     *
     * @param user the user
     * @param timestamp timestamp when the command was given
     * @return String message
     */
    public static String forward(final UserDetails user, final int timestamp) {
        // Check if a player is initialized, loaded with a source, and has not finished playback.
        if (user.getPlayer() == null || !user.getPlayer().isLoaded()
                || user.getPlayer().isFinished()) {
            // If the player isn't ready, return an error message.
            return "Please load a source before attempting to forward.";
        }

        // Check if the loaded source is a podcast.
        if (!user.getPlayer().getPlayingType().equals("podcast")) {
            // If the source is not a podcast, return an error message.
            return "The loaded source is not a podcast.";
        }

        // Execute the forward command on the player.
        return user.getPlayer().forward(timestamp);
    }

    /**
     * Method used to move backward
     *
     * @param user the user
     * @param timestamp timestamp when the command was given
     * @return String message
     */
    public static String backward(final UserDetails user, final int timestamp) {
        // Check if a player is initialized, loaded with a source, and has not finished playback.
        if (user.getPlayer() == null || !user.getPlayer().isLoaded()
                || user.getPlayer().isFinished()) {
            // If the player isn't ready, return an error message.
            return "Please load a source before attempting to backward.";
        }

        // Check if the loaded source is a podcast.
        if (!user.getPlayer().getPlayingType().equals("podcast")) {
            // If the source is not a podcast, return an error message.
            return "The loaded source is not a podcast.";
        }

        // Execute the backward command on the player.
        return user.getPlayer().backward(timestamp);
    }


    /**
     * Method used handle the moving to the next song considering the repeat mode
     *
     * @param timestamp current time
     * @return String message
     */
    public static String next(final UserDetails user, final int timestamp) {
        // Check if the player is initialized, loaded, and not finished.
        if (user.getPlayer() == null || !user.getPlayer().isLoaded()
                || user.getPlayer().isFinished()) {
            // If the player isn't ready, return an error message.
            return "Please load a source before skipping to the next track.";
        }

        // Move to the next track and get the response message.
        String message = user.getPlayer().next(timestamp);

        // If the player is paused after moving to the next track, resume playback.
        if (user.getPlayer().isPaused()) {
            user.getPlayer().play(timestamp, user);
        }

        // Return the message received from the player.
        return message;
    }

    /**
     *Method used handle the moving to the previous song considering the repeat mode
     *
     * @param timestamp current time
     * @return String message
     */
    public static String prev(final UserDetails user, final int timestamp) {
        // Check if the player is initialized, loaded, and not finished.
        if (user.getPlayer() == null || !user.getPlayer().isLoaded()
                || user.getPlayer().isFinished()) {
            // If the player isn't ready, return an error message.
            return "Please load a source before returning to the previous track.";
        }

        // Move to the previous track and get the response message.
        String message = user.getPlayer().prev(timestamp);

        // If the player is paused after moving to the previous track, resume playback.
        if (user.getPlayer().isPaused()) {
            user.getPlayer().play(timestamp, user);
        }

        // Return the message received from the player.
        return message;
    }

    /**
     * Method used to like/unlike a song that is currently playing
     *
     * @param user the user that likes/unlikes
     * @param timestamp timestamp when the command was given
     * @return String message
     */
    public static String like(final UserDetails user, final int timestamp) {
        // Retrieve the player from the user details.
        Player player = user.getPlayer();

        // Check if a player is initialized, loaded with a source, and has not finished playback.
        if (player == null || !player.isLoaded() || player.isFinished()) {
            // If the player isn't ready, return an error message.
            return "Please load a source before liking or unliking.";
        }

        // Check if the loaded source is a podcast.
        if (player.getPlayingType().equals("podcast")) {
            // If the source is not a song, return an error message.
            return "Loaded source is not a song.";
        }

        // Initialize a variable to hold the currently playing song.
        SongInputExtended song;

        // Determine the currently playing song based on the type of content.
        if (player.getPlayingType().equals("song")) {
            // If the player is playing a song, get the song details.
            song = player.getSongInputExtended();
        } else {
            // If the player is playing from a playlist, get the current song from the playlist.
            song = player.getPlayingSongFromPlaylist(timestamp);
        }

        // Check if the player is in repeat mode.
        if (user.getPlayer().getRepeat() == 2) {
            // If the player is set to repeat a specific song, get that song.
            song = user.getPlayer().getSelectedToRepeatFromPlaylist();
        }

        // Check if the user has already liked the song.
        if (user.getLikedSongs().contains(song)) {
            // If the song is already liked, remove it from the liked songs list.
            user.removeLikedSong(song);
            assert song != null;
            // Decrement the like count of the song.
            song.decrementLike(timestamp);
            // Return a message indicating the song has been unliked.
            return "Unlike registered successfully.";
        } else {
            // If the song is not yet liked, add it to the liked songs list.
            user.addLikedSong(song);
            assert song != null;
            // Increment the like count of the song.
            song.incrementLike(timestamp);
            // Return a message indicating the song has been liked.
            return "Like registered successfully.";
        }
    }

    /**
     * Method to get the liked songs from a user
     *
     * @param userDetails the user
     * @return The list of names of the songs
     */
    public static ArrayList<String> showPreferredSongs(final UserDetails userDetails) {
        // Create a new ArrayList to store the names of the liked songs.
        ArrayList<String> songs = new ArrayList<>();

        // Iterate through the liked songs of the user.
        for (SongInputExtended songInputExtended : userDetails.getLikedSongs()) {
            // Add the name of each liked song to the list.
            songs.add(songInputExtended.getName());
        }

        // Return the list of liked song names.
        return songs;
    }


    /**
     * Method that sorts the likes list and retrieve the first maximum 5 elements
     * @return List results
     */
    public static List<String> getTop5S(final List<SongInputExtended> likes) {
        List<SongInputExtended> copy = new ArrayList<>(likes);

        // Sort the copy based on the number of likes each song has received.
        copy.sort((o1, o2) -> {
            // In case of a tie in likes, sort by the original order in the library.
            if (o2.getLikes().compareTo(o1.getLikes()) == 0) {
                return Integer.compare(likes.indexOf(o1), likes.indexOf(o2));
            } else {
                return o2.getLikes().compareTo(o1.getLikes());
            }
        });

        // Prepare a list to store the top five song names.
        List<String> results = new ArrayList<>();

        // Iterate over the sorted list and add up to five song names to the results.
        for (int i = 0; i < MAX_RESULTS_LIST && i < copy.size(); i++) {
            results.add(copy.get(i).getName());
        }

        // Return the list of top five songs.
        return results;
    }

    /**
     * Method that sorts the followers list and retrieve the first maximum 5 elements
     * @return List results
     */
    public static List<String> getTop5P(final List<Playlist> playlists) {
        // Sort the list of playlists based on the number of followers each playlist has.
        playlists.sort((o1, o2) -> o2.getFollowers().compareTo(o1.getFollowers()));

        // Prepare a list to store the top five playlist names.
        List<String> results = new ArrayList<>();

        // Iterate over the sorted list and add up to five playlist names to the results.
        for (int i = 0; i < MAX_RESULTS_LIST && i < playlists.size(); i++) {
            results.add(playlists.get(i).getName());
        }

        // Return the list of top five playlists.
        return results;
    }


    /**
     * Method called when user wants to follow a playlist
     * @return returned string message
     */
    public static String follow(final UserDetails user) {
        // Check if a player is initialized and a user is selected.
        if (user.getPlayer() == null || !user.isSelected()) {
            return "Please select a source before following or unfollowing.";
        }

        // Check if the selected source is a playlist.
        if (!user.getTypeSearched().equals("playlist")) {
            return "The selected source is not a playlist.";
        }

        // Retrieve the currently selected playlist.
        Playlist playlist = user.getPlayer().getPlaylist();

        // Check if the user owns the playlist.
        if (playlist.getOwner().equals(user.getUsername())) {
            return "You cannot follow or unfollow your own playlist.";
        }

        // Follow or unfollow the playlist and return the appropriate message.
        return "Playlist " + user.follow(playlist) + " successfully.";
    }

    /**
     * Method used to change the visibility for a playlist
     * @return String
     */
    public static String
    switchVisibility(final int index, final String username, final List<Playlist> playlists) {
        // Retrieve the user's playlists.
        List<Playlist> userPlaylists = retrieveUserPlaylists(username, playlists);

        // Check if the specified playlist index is valid.
        if (userPlaylists.size() < index) {
            return "The specified playlist ID is too high.";
        }

        // Change the visibility of the specified playlist.
        userPlaylists.get(index - 1).changeVisibility();

        // Determine the new visibility status.
        String visibility = userPlaylists.get(index - 1).isPrivatePlaylist()
                ? "private" : "public";

        // Return a message indicating the updated visibility status.
        return "Visibility status updated successfully to " + visibility + ".";
    }

}
