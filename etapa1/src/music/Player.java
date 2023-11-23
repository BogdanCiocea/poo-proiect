package music;
import fileio.Stats;
import fileio.extended.PodcastInputExtended;
import fileio.extended.SongInputExtended;
import fileio.input.EpisodeInput;
import user.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Random;
import java.util.Objects;

/**
 * Player class used to play, pause and work with every user player
 */
public final class Player {

    private static final int SECONDS_TO_SKIP = 90;

    private final String playingType;

    private SongInputExtended songInput;
    private Playlist playlist;

    private List<Integer> playlistIds;
    private PodcastInputExtended podcastInput;

    private SongInputExtended selectedToRepeatFromPlaylist;
    private int playedTimeFromRepeatedPlaylist;

    private int timestampStartedRepeat;

    private int playedTime = 0;
    private boolean playing;

    private boolean paused;

    private boolean finished = false;

    private boolean loaded;
    private int repeat = 0;
    private boolean shuffle;

    private int timestampStarted = -1;

    public Player(final String playingType) {
        this.playingType = playingType;
    }

    public String getPlayingType() {
        return playingType;
    }

    public SongInputExtended getSongInputExtended() {
        return songInput;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public PodcastInputExtended getPodcastInputExtended() {
        return podcastInput;
    }

    public SongInputExtended getSelectedToRepeatFromPlaylist() {
        return selectedToRepeatFromPlaylist;
    }

    public void setSongInputExtended(final SongInputExtended songInput) {
        this.songInput = songInput;
    }

    /**
     * This method sets the playlists selected by the user into player
     *
     * @param playlist playlist selected
     */
    public void setPlaylist(final Playlist playlist) {
        this.playlist = playlist;
        playlistIds = new ArrayList<>();
        for (int i = 0; i < playlist.getSongs().size(); i++) {
            playlistIds.add(i);
        }
    }

    /**
     * This method sets the podcast selected by the user into player
     *
     * @param podcastInput podcast
     */
    public void setPodcastInputExtended(final PodcastInputExtended podcastInput) {
        this.podcastInput = podcastInput;
    }

    public boolean isPlaying() {
        return playing;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Method used to start/restart the player
     *
     * @param timestamp timestamp when the command was given
     * @param user      user that plays
     */
    public void play(final int timestamp, final UserDetails user) {
        // Set the timestamp at which the playback started or resumed.
        this.timestampStarted = timestamp;

        // Check if the type of media currently loaded in the player is a podcast.
        if (playingType.equals("podcast")) {
            // If a podcast is playing, check if the user has previously played this podcast.
            if (user.getAlreadyPlayedPodcasts().containsKey(podcastInput)) {
                // If the podcast was previously played, retrieve the played time.
                playedTime = user.getAlreadyPlayedPodcasts().get(podcastInput);
            }
        }

        // Set the player's status to playing.
        playing = true;

        // Set the player's paused status to false,
        // indicating that the player is actively playing.
        paused = false;

        // Set the player's loaded status to true,
        // indicating that the player has content loaded and ready to play.
        loaded = true;
    }

    /**
     * Sets the player as finished state
     */
    public void setFinished() {
        switch (playingType) {
            case "song":
                playedTime = songInput.getDuration();
                break;

            case "playlist":
                playedTime = playlist.getDuration();
                break;

            case "podcast":
                playedTime = podcastInput.getDuration();
                break;
            default:
                break;
        }
        paused = true;
        playing = false;
        finished = true;
        shuffle = false;
    }

    public int getPlayedTime() {
        return playedTime;
    }

    public boolean isFinished() {
        return finished;
    }

    public int getRepeat() {
        return repeat;
    }

    /**
     * Method used to change the repeat code
     *
     * @param timestamp timestamp of the command
     */
    public void setRepeat(final int timestamp) {
        // Cycle through repeat modes. If the repeat mode is 2, reset it to 0, otherwise increment.
        if (repeat == 2) {
            repeat = 0;
        } else {
            repeat++;
        }

        // If the playing type is a playlist, handle the repeat functionality for playlists.
        if (playingType.equals("playlist")) {
            // If repeat mode is set to repeat the current song (2),
            // set up for repeating the current song.
            if (repeat == 2) {
                // Identify the current song to repeat in the playlist.
                selectedToRepeatFromPlaylist = getPlayingSongFromPlaylist(timestamp);

                // Calculate the total duration of songs played in
                // the playlist up to the current song.
                int duration = 0;
                for (int i = 0; i < playlist.getSongs().size(); i++) {
                    SongInputExtended song = playlist.getSongs().get(playlistIds.get(i));
                    if (song == selectedToRepeatFromPlaylist) {
                        break;
                    }
                    duration += song.getDuration();
                }

                // Determine the played time for the repeated song in the playlist.
                playedTimeFromRepeatedPlaylist =
                        (getCurrentPlayedTime(timestamp) % playlist.getDuration()) - duration;
                timestampStartedRepeat = timestamp - playedTimeFromRepeatedPlaylist;
            }

            // If repeat mode is reset to no repeat (0),
            // adjust the timestamp started for the playlist.
            if (repeat == 0) {
                int duration = 0;
                for (int i = 0; i < playlist.getSongs().size(); i++) {
                    SongInputExtended song = playlist.getSongs().get(playlistIds.get(i));
                    if (song == selectedToRepeatFromPlaylist) {
                        break;
                    }
                    duration += song.getDuration();
                }
                timestampStarted = timestamp - (duration
                        + (timestamp - timestampStartedRepeat)
                        % selectedToRepeatFromPlaylist.getDuration());
            }
            // If the playing type is an individual song,
            // handle the repeat functionality for songs.
        } else if (playingType.equals("song")) {
            // If repeat mode is reset to no repeat, adjust the timestamp started for the song.
            if (repeat == 0) {
                timestampStarted = timestamp
                        - (getCurrentPlayedTime(timestamp) % songInput.getDuration());
            }
        }
    }

    /**
     * Returns the current playing song
     *
     * @param timestamp Current timestamp
     * @return Song input
     */
    public SongInputExtended getPlayingSongFromPlaylist(final int timestamp) {
        // Calculate the total played time up to the current timestamp.
        int currentPlayedTime = getCurrentPlayedTime(timestamp);

        // Adjust the played time to fit within the total duration of the playlist.
        currentPlayedTime = currentPlayedTime % playlist.getDuration();

        // Iterate through the songs in the playlist.
        for (int i = 0; i < playlist.getSongs().size(); i++) {
            // Retrieve the current song based on its position in the playlist.
            SongInputExtended song = playlist.getSongs().get(playlistIds.get(i));

            // Check if the played time exceeds the duration of the current song.
            if (currentPlayedTime >= song.getDuration()) {
                // Deduct the duration of the current song from the played time.
                currentPlayedTime -= song.getDuration();

                // If at the end of the playlist and no time remains, return the last song.
                if (i == playlist.getSongs().size() - 1 && currentPlayedTime == 0) {
                    return song;
                }
            } else {
                // If the played time doesn't exceed the duration, return the current song.
                return song;
            }
        }

        // Return null if no song matches the criteria (should not happen in normal operation).
        return null;
    }

    /**
     * Method used to pause the player
     *
     * @param timestamp timestamp of the command
     * @param user      user details
     */
    public void pause(final int timestamp, final UserDetails user) {
        // Update the total played time.
        playedTime += timestamp - timestampStarted;

        // Handle the pause functionality based on the type of content being played.
        switch (playingType) {
            case "song":
                // For songs, check if the song has finished playing.
                if (songInput.getDuration() <= playedTime) {
                    if (repeat == 1) {
                        // For repeat once, reset the played time if the song has played twice.
                        if (playedTime - songInput.getDuration() >= songInput.getDuration()) {
                            playedTime = songInput.getDuration();
                            setFinished();
                        } else {
                            playedTime -= songInput.getDuration();
                        }
                        repeat = 0;
                    } else if (repeat == 2) {
                        // For infinite repeat, loop the song.
                        playedTime = playedTime % songInput.getDuration();
                    } else {
                        // If no repeat, set the song as finished.
                        playedTime = songInput.getDuration();
                        setFinished();
                    }
                }
                break;
            case "playlist":
                // For playlists, check if the playlist has finished playing.
                if (playlist.getDuration() <= playedTime && playlist.getDuration() != 0) {
                    if (repeat == 1) {
                        // For repeat all, loop the playlist.
                        playedTime = playedTime % playlist.getDuration();
                    } else if (repeat == 2) {
                        // For repeat current song, calculate the played time from repeated song.
                        playedTimeFromRepeatedPlaylist = timestamp - timestampStartedRepeat;
                        if (!playlist.getSongs().isEmpty()) {
                            if (playedTimeFromRepeatedPlaylist
                                    >= selectedToRepeatFromPlaylist.getDuration()) {
                                playedTimeFromRepeatedPlaylist =
                                        playedTimeFromRepeatedPlaylist
                                                % selectedToRepeatFromPlaylist.getDuration();
                            }
                        }
                    } else {
                        // If no repeat, set the playlist as finished.
                        setFinished();
                    }
                }
                if (repeat == 2) {
                    // Update played time for repeating current song.
                    playedTimeFromRepeatedPlaylist = timestamp - timestampStartedRepeat;
                    if (playedTimeFromRepeatedPlaylist
                            >= selectedToRepeatFromPlaylist.getDuration()) {
                        playedTimeFromRepeatedPlaylist =
                                playedTimeFromRepeatedPlaylist
                                        % selectedToRepeatFromPlaylist.getDuration();
                    }
                }
                break;
            case "podcast":
                // For podcasts, handle based on the repeat mode.
                if (repeat == 0) {
                    // If no repeat and podcast has finished, set as finished.
                    if (podcastInput.getDuration() <= playedTime) {
                        playedTime = podcastInput.getDuration();
                        setFinished();
                    }
                } else if (repeat == 1) {
                    // For repeat once, reset the played time if the podcast has played twice.
                    if (podcastInput.getDuration() <= playedTime) {
                        playedTime -= podcastInput.getDuration();
                        repeat = 0;
                    }
                } else {
                    // For infinite repeat, loop the podcast.
                    playedTime = playedTime % podcastInput.getDuration();
                }
                // Update the played time in the user's history for the podcast.
                user.getAlreadyPlayedPodcasts().put(podcastInput, playedTime);
                break;
            default:
                break;
        }

        // Set the player's state to paused and not playing.
        paused = true;
        playing = false;
    }

    /**
     * When user performs a search command  is called this method to stop the player
     *
     * @param timestamp timestamp
     * @param user      user
     */
    public void resetLoader(final int timestamp, final UserDetails user) {
        // First, pause the current playback.
        pause(timestamp, user);

        // Set the 'loaded' flag to false, indicating that the player no longer
        // has content loaded and ready for playback.
        loaded = false;
    }

    /**
     * This method returns the status of the player
     *
     * @param currentTimestamp the time of the status
     * @return Stats object that contains the details about the status
     */
    public Stats getStatus(final int currentTimestamp) {
        int currentPlayedTime = getCurrentPlayedTime(currentTimestamp);
        if (!isLoaded() && !isFinished()) {
            return new Stats("", 0, "No Repeat", false, true);
        }
        switch (playingType) {
            case "song":
                // Check if the current played time exceeds the duration of the song.
                if (songInput.getDuration() <= currentPlayedTime) {
                    // If repeat mode is 'repeat once' (1).
                    if (repeat == 1) {
                        repeat = 0;
                        // Check if the song has played more than twice its duration.
                        if (currentTimestamp - songInput.getDuration() > songInput.getDuration()) {
                            // If so, mark the song as finished and return default Stats.
                            setFinished();
                            return new Stats("", 0, getRepeatMessage(), shuffle, paused);
                        }
                        // Otherwise, adjust the played time for another round.
                        currentPlayedTime -= songInput.getDuration();
                    } else if (repeat == 2) {
                        // Calculate the current played time within the song's duration loop.
                        currentPlayedTime = currentPlayedTime % songInput.getDuration();
                    } else {
                        // Mark the song as finished and return default Stats.
                        setFinished();
                        return new Stats("", 0, getRepeatMessage(), shuffle, paused);
                    }
                }
                return new Stats(
                        songInput.getName(),
                        songInput.getDuration() - currentPlayedTime,
                        getRepeatMessage(),
                        shuffle,
                        paused
                );

            case "playlist":
                // Handle the scenario when the repeat mode is set to repeat the current song (2).
                if (repeat == 2) {
                    // Determine the current played time based on whether the player is paused.
                    if (isPaused()) {
                        currentPlayedTime = playedTimeFromRepeatedPlaylist;
                    } else {
                        currentPlayedTime = (currentTimestamp - timestampStartedRepeat);
                    }
                    if (currentPlayedTime > selectedToRepeatFromPlaylist.getDuration()) {
                        currentPlayedTime = currentPlayedTime
                                % selectedToRepeatFromPlaylist.getDuration();
                    }
                    // Return the status of the currently repeating song.
                    return new Stats(
                            selectedToRepeatFromPlaylist.getName(),
                            selectedToRepeatFromPlaylist.getDuration() - currentPlayedTime,
                            getRepeatMessage(),
                            shuffle,
                            paused
                    );
                }
                // Check if the playlist has finished playing
                // (considering the playlist's total duration).
                if (playlist.getDuration() <= currentPlayedTime && playlist.getDuration() != 0) {
                    if (repeat == 1) {
                        // For repeat all mode, loop back to the start of the playlist.
                        currentPlayedTime = currentPlayedTime % playlist.getDuration();
                    } else {
                        // If no repeat mode, mark the playlist as finished.
                        setFinished();
                        return new Stats("", 0, getRepeatMessage(), shuffle, paused);
                    }
                }

                // Calculate the position within the playlist.
                int duration = currentPlayedTime;
                for (int i = 0; i < playlist.getSongs().size(); i++) {
                    // Deduct the duration of each song until finding the currently playing song.
                    if (duration >= playlist.getSongs().get(playlistIds.get(i)).getDuration()) {
                        duration -= playlist.getSongs().get(playlistIds.get(i)).getDuration();
                        // If at the end of the playlist and no duration left,
                        // return finished status.
                        if (i == playlist.getSongs().size() - 1 && duration == 0) {
                            return new Stats("", 0, getRepeatMessage(), shuffle, true);
                        }
                    } else {
                        return new Stats(
                                playlist.getSongs().get(playlistIds.get(i)).getName(),
                                playlist.getSongs().get(playlistIds.get(i)).getDuration()
                                        - duration,
                                getRepeatMessage(),
                                shuffle,
                                paused
                        );
                    }
                }
                break;

            case "podcast":
                // If repeat mode is 'no repeat' (0).
                if (repeat == 0) {
                    // Check if the podcast has finished playing.
                    if (podcastInput.getDuration() <= currentPlayedTime) {
                        playedTime = podcastInput.getDuration();
                        setFinished();
                        // Return a Stats object indicating the podcast has finished.
                        return new Stats("", 0, getRepeatMessage(), shuffle, paused);
                    }
                }

                // If repeat mode is 'repeat once' (1).
                if (repeat == 1) {
                    // Check if the podcast has been played more than its duration.
                    if (currentPlayedTime >= podcastInput.getDuration()) {
                        // Reset repeat mode to 'no repeat' and adjust the played time.
                        repeat = 0;
                        currentPlayedTime = currentPlayedTime % podcastInput.getDuration();
                    }
                }

                // If repeat mode is 'repeat infinite' (2).
                if (repeat == 2) {
                    // Continuously loop the podcast by calculating
                    // the current played time within its duration.
                    currentPlayedTime = currentTimestamp % podcastInput.getDuration();
                }

                // Initialize a variable to hold the adjusted duration.
                int duration1 = currentPlayedTime;

                // Iterate through each episode in the podcast.
                for (EpisodeInput episode : podcastInput.getEpisodes()) {
                    // Deduct the duration of each episode to find the currently playing episode.
                    if (duration1 >= episode.getDuration()) {
                        duration1 -= episode.getDuration();
                    } else {
                        return new Stats(
                                episode.getName(),
                                episode.getDuration() - duration1,
                                getRepeatMessage(),
                                shuffle,
                                paused
                        );
                    }
                }
                break;

            default:
                break;
        }
        return new Stats("", 0, getRepeatMessage(), shuffle, paused);
    }

    private int getCurrentPlayedTime(final int currentTimestamp) {
        // Initialize a variable to hold the current played time.
        int currentPlayedTime;

        // Check if the player is paused and not finished.
        if (isPaused() && !isFinished()) {
            // If paused, the current played time is equal to the stored played time.
            currentPlayedTime = playedTime;
        } else {
            // If not paused, calculate the current played time by adding the time elapsed
            // since the last timestamp when the player started playing.
            currentPlayedTime = playedTime + (currentTimestamp - timestampStarted);
        }

        // Return the calculated current played time.
        return currentPlayedTime;
    }

    /**
     * Verifies if at the specified time the player has finished
     *
     * @param timestamp timestamp
     */
    public void checkIfFinished(final int timestamp) {
        if (repeat == 0) {
            // Calculate the played time and check if it's equal to
            // or exceeds the playlist's duration.
            if (this.getCurrentPlayedTime(timestamp) - this.playlist.getDuration() >= 0) {
                    // If it does, mark the playback as finished.
                setFinished();
            }
        } else if (repeat == 1) {
            // Calculate the played time for twice the duration (to account for the repeat once).
            if (this.getCurrentPlayedTime(timestamp) - 2 * this.playlist.getDuration() >= 0) {
                // If the played time exceeds twice the duration, mark the playback as finished.
                setFinished();
            }
        }
    }

    /**
     * Returns the message based on the repeat command
     *
     * @return String repeat
     */
    public String getRepeatMessage() {
        String repeatMessage;
        if (repeat == 1) {
            if (playingType.equals("playlist")) {
                repeatMessage = "Repeat All";
            } else {
                repeatMessage = "Repeat Once";
            }
        } else if (repeat == 2) {
            if (playingType.equals("playlist")) {
                repeatMessage = "Repeat Current Song";
            } else {
                repeatMessage = "Repeat Infinite";
            }
        } else {
            repeatMessage = "No Repeat";
        }
        return repeatMessage;
    }

    /**
     * Method used to shuffle the playlist
     *
     * @param seed      Given seed
     * @param timestamp Current timestamp
     * @return result to this command
     */
    public String shuffle(final int seed, final int timestamp) {
        // Calculate the current played time up to this point.
        int currentPlayedTime = getCurrentPlayedTime(timestamp);

        // Get the currently playing song from the playlist.
        SongInputExtended song = getPlayingSongFromPlaylist(timestamp);

        // Deduct the duration of each song played before the current song.
        for (int i = 0; i < playlist.getSongs().size(); i++) {
            if (playlist.getSongs().get(playlistIds.get(i)) == song) {
                break;
            }
            currentPlayedTime -= playlist.getSongs().get(playlistIds.get(i)).getDuration();
        }

        // If shuffle is already activated, sort the playlist back to its original order.
        // Otherwise, shuffle the playlist using the provided seed.
        if (shuffle) {
            Collections.sort(playlistIds);
        } else {
            Collections.shuffle(playlistIds, new Random(seed));
        }

        // Calculate the total duration of songs before the current song after shuffling.
        int duration = 0;
        for (int i = 0; i < playlist.getSongs().size(); i++) {
            if (playlist.getSongs().get(playlistIds.get(i)) == song) {
                break;
            }
            duration += playlist.getSongs().get(playlistIds.get(i)).getDuration();
        }

        // Adjust the timestamp when the current song started playing.
        timestampStarted = timestamp - (duration + currentPlayedTime);

        // Reset the played time if not paused; otherwise, update it.
        if (!isPaused()) {
            playedTime = 0;
        } else {
            playedTime = duration + currentPlayedTime;
        }

        // Prepare a message to indicate the status of the shuffle function.
        String message;
        if (shuffle) {
            message = "Shuffle function deactivated successfully.";
        } else {
            message = "Shuffle function activated successfully.";
        }

        // Toggle the shuffle status.
        shuffle = !shuffle;

        // Return the status message.
        return message;
    }

    /**
     * Method used to move forward
     *
     * @param timestamp Current timestamp
     * @return String message
     */
    public String forward(final int timestamp) {
        // Calculate the current played time in the podcast.
        int currentPlayedTime = getCurrentPlayedTime(timestamp);
        currentPlayedTime = currentPlayedTime % podcastInput.getDuration();

        // Find the current episode in the podcast based on the played time.
        EpisodeInput currentEpisode = null;
        for (EpisodeInput episode : podcastInput.getEpisodes()) {
            if (currentPlayedTime >= episode.getDuration()) {
                currentPlayedTime -= episode.getDuration();
            } else {
                currentEpisode = episode;
                break;
            }
        }

        // Ensure the current episode is not null.
        assert currentEpisode != null;

        // Adjust the timestamp at which the current episode started, skipping forward.
        this.timestampStarted -= Math.min(currentEpisode.getDuration()
                - currentPlayedTime, SECONDS_TO_SKIP);

        // Return a message indicating the successful forward skip.
        return "Skipped forward successfully.";
    }

    /**
     * Method used to move the player backward
     *
     * @param timestamp Current timestamp
     * @return String response
     */
    public String backward(final int timestamp) {
        // Calculate the current played time in the podcast.
        int currentPlayedTime = getCurrentPlayedTime(timestamp);
        currentPlayedTime = currentPlayedTime % podcastInput.getDuration();

        // Find the position in the podcast to move backward from.
        for (EpisodeInput episode : podcastInput.getEpisodes()) {
            if (currentPlayedTime >= episode.getDuration()) {
                currentPlayedTime -= episode.getDuration();
            } else {
                break;
            }
        }

        // Adjust the timestamp at which the current episode started, rewinding the playback.
        this.timestampStarted += Math.min(currentPlayedTime, SECONDS_TO_SKIP);

        // Return a message indicating successful rewinding.
        return "Rewound successfully.";
    }

    /**
     * Method used to move to the next song considering the repeat mode
     *
     * @param timestamp Current time
     * @return String message
     */
    public String next(final int timestamp) {
        // Calculate the current played time for the media.
        int currentPlayedTime = getCurrentPlayedTime(timestamp);

        // Switch based on the type of media being played: song, playlist, or podcast.
        switch (playingType) {
            case "song":
                // For a song, adjust the current played time within the song's duration.
                currentPlayedTime = currentPlayedTime % songInput.getDuration();
                // Update the timestamp when the song started.
                timestampStarted -= songInput.getDuration() - currentPlayedTime;
                // If the player is paused, update the played time accordingly.
                if (isPaused()) {
                    playedTime += songInput.getDuration() - currentPlayedTime;
                }
                break;

            case "playlist":
                // Handle the next action for a playlist.
                int currentSongIndex = -1;
                // Loop through the playlist to find the current song's index.
                for (Integer playlistId : playlistIds) {
                    SongInputExtended song = playlist.getSongs().get(playlistId);
                    // Deduct the duration of each song from the played time.
                    if (currentPlayedTime >= song.getDuration()) {
                        currentPlayedTime -= song.getDuration();
                    } else {
                        // Once the current song is found, store its index and break the loop.
                        currentSongIndex = playlistId;
                        break;
                    }
                }
                // Get the duration of the current song in the playlist.
                int duration = Objects.requireNonNull(getPlayingSongFromPlaylist(timestamp))
                        .getDuration();
                // Update the timestamp for when the song started.
                timestampStarted -= duration - currentPlayedTime;
                // If the player is paused, update the played time.
                if (isPaused()) {
                    playedTime += duration - currentPlayedTime;
                }
                // In repeat mode 2 (repeat current song), reset the repeat timestamp.
                if (repeat == 2) {
                    timestampStartedRepeat = timestamp;
                }
                // Return the name of the next song.
                return getSongInputExtendedName(currentSongIndex, timestamp);

            case "podcast":
                // For a podcast, adjust the current played time within the podcast's duration.
                currentPlayedTime = currentPlayedTime % podcastInput.getDuration();
                int episodeDuration = 0;
                // Loop through the episodes in the podcast.
                for (EpisodeInput episode : podcastInput.getEpisodes()) {
                    // Deduct the duration of each episode from the played time.
                    if (currentPlayedTime >= episode.getDuration()) {
                        currentPlayedTime -= episode.getDuration();
                    } else {
                        // Once the current episode is found, store its duration and break the loop.
                        episodeDuration = episode.getDuration();
                        break;
                    }
                }
                // Update the timestamp for when the episode started.
                timestampStarted -= episodeDuration - currentPlayedTime;
                // If the player is paused, update the played time.
                if (isPaused()) {
                    playedTime += episodeDuration - currentPlayedTime;
                }
                break;

            default:
                // Handle any other unspecified media types.
                break;
        }
        // Return a message indicating successful skip to the next track and its name.
        return "Skipped to next track successfully. The current track is "
                + getNextName(timestamp) + ".";
    }

    private String getSongInputExtendedName(final int currentSongIndex, final int timestamp) {
        // Check if the repeat mode is set to repeat the current song (2).
        if (repeat == 2) {
            // Return a message indicating the current track that will be repeated.
            return "Skipped to next track successfully. The current track is "
                    + selectedToRepeatFromPlaylist.getName() + ".";
        }

        // Check if the current song index is within the bounds of the playlist.
        if (playlistIds.indexOf(currentSongIndex) < playlist.getSongs().size() - 1) {
            // Return a message with the name of the next song in the playlist.
            return "Skipped to next track successfully. The current track is "
                    + Objects.requireNonNull(getPlayingSongFromPlaylist(timestamp)).getName() + ".";
        } else {
            // If the playlist has reached its end and repeat mode is 'repeat all' (1).
            if (repeat == 1) {
                // Return a message indicating the first track in the playlist is now playing.
                return "Skipped to next track successfully. The current track is "
                        + playlist.getSongs().get(playlistIds.get(0)).getName() + ".";
            } else {
                // If the playlist has reached its end and there is no repeat, mark as finished.
                setFinished();
                // Inform the user to load a source before skipping to the next track.
                return "Please load a source before skipping to the next track.";
            }
        }
    }

    /**
     * When next command is given this method returns the name of the next song that is starting
     *
     * @param currentTimestamp Current time
     * @return The song name that is starting
     */
    public String getNextName(final int currentTimestamp) {
        // Calculate the current played time.
        int currentPlayedTime = getCurrentPlayedTime(currentTimestamp);

        // Switch based on the type of media being played.
        switch (playingType) {
            case "song":
                // For songs, check if the song has finished playing.
                if (songInput.getDuration() <= currentPlayedTime) {
                    // Handle repeat once mode.
                    if (repeat == 1) {
                        repeat = 0;
                        if (currentTimestamp - songInput.getDuration() >= songInput.getDuration()) {
                            setFinished();
                            return "";
                        }
                    } else {
                        // Mark as finished if the song has played its duration.
                        setFinished();
                        return "";
                    }
                }
                // Return the name of the current song.
                return songInput.getName();

            case "playlist":
                // For playlists, check if the repeat mode is set to repeat the current song.
                if (repeat == 2) {
                    return selectedToRepeatFromPlaylist.getName();
                }
                // Check if the playlist has finished playing.
                if (playlist.getDuration() <= currentPlayedTime && playlist.getDuration() != 0) {
                    // Handle repeat all mode.
                    if (repeat == 1) {
                        currentPlayedTime = currentPlayedTime % playlist.getDuration();
                    } else {
                        setFinished();
                        return "";
                    }
                }
                // Iterate through the playlist to find the next song.
                int duration = currentPlayedTime;
                for (int i = 0; i < playlist.getSongs().size(); i++) {
                    if (duration > playlist.getSongs().get(playlistIds.get(i)).getDuration()) {
                        duration -= playlist.getSongs().get(playlistIds.get(i)).getDuration();
                    } else {
                        return playlist.getSongs().get(playlistIds.get(i)).getName();
                    }
                }
                break;

            case "podcast":
                // For podcasts, handle each repeat mode and identify the next episode.
                if (repeat == 0) {
                    if (podcastInput.getDuration() <= currentPlayedTime) {
                        setFinished();
                        return "";
                    }
                }
                if (repeat == 1) {
                    if (currentPlayedTime >= podcastInput.getDuration()) {
                        repeat = 0;
                        currentPlayedTime = currentPlayedTime % podcastInput.getDuration();
                    }
                }
                if (repeat == 2) {
                    currentPlayedTime = currentTimestamp % podcastInput.getDuration();
                }
                int duration1 = currentPlayedTime;
                for (EpisodeInput episode : podcastInput.getEpisodes()) {
                    if (duration1 >= episode.getDuration()) {
                        duration1 -= episode.getDuration();
                    } else {
                        return episode.getName();
                    }
                }
                break;

            default:
                // Default case for any other media type.
                break;
        }
        // Return an empty string if no track name is identified.
        return "";
    }

    /**
     * Method used to move the player to the previous song
     *
     * @param timestamp Current time
     * @return String returned after performing the command
     */
    public String prev(final int timestamp) {
        // Calculate the current played time.
        int currentPlayedTime = getCurrentPlayedTime(timestamp);

        // Switch based on the type of media being played.
        switch (playingType) {
            case "song":
                // For songs, calculate the previous position and adjust the timestamp started.
                currentPlayedTime = currentPlayedTime % songInput.getDuration();
                timestampStarted += songInput.getDuration() - currentPlayedTime;
                // If the player is paused, adjust the played time accordingly.
                if (isPaused()) {
                    playedTime -= currentPlayedTime;
                }
                // Return a message with the name of the current song.
                return "Returned to previous track successfully. The current track is "
                        + songInput.getName() + ".";

            case "playlist":
                // For playlists, find the index of the previous song to play.
                currentPlayedTime = currentPlayedTime % playlist.getDuration();
                for (int i = 0; i < playlist.getSongs().size(); i++) {
                    SongInputExtended song = playlist.getSongs().get(playlistIds.get(i));
                    if (currentPlayedTime > song.getDuration()) {
                        currentPlayedTime -= song.getDuration();
                    } else {
                        break;
                    }
                }
                // Adjust the timestamp and played time if paused.
                if (isPaused()) {
                    timestampStarted = timestamp - (playedTime - currentPlayedTime);
                    playedTime -= currentPlayedTime;
                } else {
                    timestampStarted += currentPlayedTime;
                }
                // Handle repeat mode for playlists.
                if (repeat == 2) {
                    int duration = 0;
                    for (int i = 0; i < playlist.getSongs().size(); i++) {
                        SongInputExtended song = playlist.getSongs().get(playlistIds.get(i));
                        if (selectedToRepeatFromPlaylist == song) {
                            break;
                        } else {
                            duration += song.getDuration();
                        }
                    }
                    timestampStarted = timestamp - duration;
                    timestampStartedRepeat = timestamp;
                    if (isPaused()) {
                        playedTimeFromRepeatedPlaylist = 0;
                    }
                }
                // Return a message with the name of the next song in the playlist.
                return "Returned to previous track successfully. The current track is "
                        + Objects.requireNonNull(getPlayingSongFromPlaylist(timestamp)).getName()
                        + ".";

            case "podcast":
                // For podcasts, adjust the played time for the previous episode.
                currentPlayedTime = currentPlayedTime % podcastInput.getDuration();
                for (EpisodeInput episode : podcastInput.getEpisodes()) {
                    if (currentPlayedTime > episode.getDuration()) {
                        currentPlayedTime -= episode.getDuration();
                    } else {
                        break;
                    }
                }
                timestampStarted -= currentPlayedTime;
                if (isPaused()) {
                    playedTime -= currentPlayedTime;
                }
                break;

            default:
                break;
        }
        // Return a message with the name of the previous track, episode,
        // or an empty string if none.
        return "Returned to previous track successfully. The current track is "
                + getNextName(timestamp - currentPlayedTime) + ".";
    }
}
