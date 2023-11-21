package music;
import fileio.input.Stats;
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
        this.timestampStarted = timestamp;
        if (playingType.equals("podcast")) {
            if (user.getAlreadyPlayedPodcasts().containsKey(podcastInput)) {
                playedTime = user.getAlreadyPlayedPodcasts().get(podcastInput);
            }
        }
        playing = true;
        paused = false;
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
        if (repeat == 2) {
            repeat = 0;
        } else {
            repeat++;
        }
        if (playingType.equals("playlist")) {
            if (repeat == 2) {
                selectedToRepeatFromPlaylist = getPlayingSongFromPlaylist(timestamp);
                int duration = 0;
                for (int i = 0; i < playlist.getSongs().size(); i++) {
                    SongInputExtended song = playlist.getSongs().get(playlistIds.get(i));
                    if (song == selectedToRepeatFromPlaylist) {
                        break;
                    }
                    duration += song.getDuration();
                }
                playedTimeFromRepeatedPlaylist =
                        (getCurrentPlayedTime(timestamp) % playlist.getDuration()) - duration;
                timestampStartedRepeat = timestamp - playedTimeFromRepeatedPlaylist;
            }
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
        } else if (playingType.equals("song")) {
            if (repeat == 0) {
                timestampStarted = timestamp
                        - (getCurrentPlayedTime(timestamp) % songInput.getDuration());
            }
        }
    }

    /**
     * Method used to pause the player
     *
     * @param timestamp timestamp of the command
     * @param user      user details
     */
    public void pause(final int timestamp, final UserDetails user) {
        playedTime += timestamp - timestampStarted;
        switch (playingType) {
            case "song":
                if (songInput.getDuration() <= playedTime) {
                    if (repeat == 1) {
                        if (playedTime - songInput.getDuration() >= songInput.getDuration()) {
                            playedTime = songInput.getDuration();
                            setFinished();
                        } else {
                            playedTime -= songInput.getDuration();
                        }
                        repeat = 0;
                    } else if (repeat == 2) {
                        playedTime = playedTime % songInput.getDuration();
                    } else {
                        playedTime = songInput.getDuration();
                        setFinished();
                    }

                }
                break;
            case "playlist":
                if (playlist.getDuration() <= playedTime && playlist.getDuration() != 0) {
                    if (repeat == 1) {
                        playedTime = playedTime % playlist.getDuration();
                    } else if (repeat == 2) {
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
                        setFinished();

                    }
                }
                if (repeat == 2) {
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
                if (repeat == 0) {
                    if (podcastInput.getDuration() <= playedTime) {
                        playedTime = podcastInput.getDuration();
                        setFinished();
                    }
                } else if (repeat == 1) {
                    if (podcastInput.getDuration() <= playedTime) {
                        playedTime -= podcastInput.getDuration();
                        repeat = 0;
                    }
                } else {
                    playedTime = playedTime % podcastInput.getDuration();
                }
                user.getAlreadyPlayedPodcasts().put(podcastInput, playedTime);
                break;
            default:
                break;
        }
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
        pause(timestamp, user);
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
                if (songInput.getDuration() <= currentPlayedTime) {
                    if (repeat == 1) {
                        repeat = 0;
                        if (currentTimestamp - songInput.getDuration() > songInput.getDuration()) {
                            setFinished();
                            return new Stats("", 0, getRepeatMessage(), shuffle, paused);
                        }
                        currentPlayedTime -= songInput.getDuration();
                    } else if (repeat == 2) {
                        currentPlayedTime = currentPlayedTime % songInput.getDuration();
                    } else {
                        setFinished();
                        return new Stats("", 0, getRepeatMessage(), shuffle, paused);

                    }
                }
                return new Stats(songInput.getName(),
                        songInput.getDuration() - currentPlayedTime,
                        getRepeatMessage(),
                        shuffle,
                        paused
                );
            case "playlist":
                if (repeat == 2) {
                    if (isPaused()) {
                        currentPlayedTime = playedTimeFromRepeatedPlaylist;
                    } else {
                        currentPlayedTime = (currentTimestamp - timestampStartedRepeat);
                    }
                    if (currentPlayedTime > selectedToRepeatFromPlaylist.getDuration()) {
                        currentPlayedTime =
                                currentPlayedTime % selectedToRepeatFromPlaylist.getDuration();
                    }
                    return new Stats(
                            selectedToRepeatFromPlaylist.getName(),
                            selectedToRepeatFromPlaylist.getDuration() - currentPlayedTime,
                            getRepeatMessage(),
                            shuffle,
                            paused
                    );
                }
                if (playlist.getDuration() <= currentPlayedTime && playlist.getDuration() != 0) {
                    if (repeat == 1) {
                        currentPlayedTime = currentPlayedTime % playlist.getDuration();
                    } else {
                        setFinished();
                        return new Stats("", 0, getRepeatMessage(), shuffle, paused);
                    }
                }
                int duration = currentPlayedTime;
                for (int i = 0; i < playlist.getSongs().size(); i++) {
                    if (duration >= playlist.getSongs().get(playlistIds.get(i)).getDuration()) {
                        duration -= playlist.getSongs().get(playlistIds.get(i)).getDuration();
                        if (i == playlist.getSongs().size() - 1 && duration == 0) {
                            //setFinished();
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
                if (repeat == 0) {
                    if (podcastInput.getDuration() <= currentPlayedTime) {
                        playedTime = podcastInput.getDuration();
                        setFinished();
                        return new Stats("", 0, getRepeatMessage(), shuffle, paused);
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
        int currentPlayedTime;
        if (isPaused() && !isFinished()) {
            currentPlayedTime = playedTime;
        } else {
            currentPlayedTime = playedTime + (currentTimestamp - timestampStarted);
        }
        return currentPlayedTime;
    }

    /**
     * Verifies if at the specified time the player has finished
     *
     * @param timestamp timestamp
     */
    public void checkIfFinished(final int timestamp) {
        if (repeat == 0) {
            if (this.getCurrentPlayedTime(timestamp) - this.playlist.getDuration() >= 0) {
                setFinished();
            }
        } else if (repeat == 1) {
            if (this.getCurrentPlayedTime(timestamp) - 2 * this.playlist.getDuration() >= 0) {
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
     * Returns the current playing song
     *
     * @param timestamp Current timestamp
     * @return Song input
     */
    public SongInputExtended getPlayingSongFromPlaylist(final int timestamp) {
        int currentPlayedTime = getCurrentPlayedTime(timestamp);
        currentPlayedTime = currentPlayedTime % playlist.getDuration();
        for (int i = 0; i < playlist.getSongs().size(); i++) {
            SongInputExtended song = playlist.getSongs().get(playlistIds.get(i));
            if (currentPlayedTime >= song.getDuration()) {
                currentPlayedTime -= song.getDuration();
                if (i == playlist.getSongs().size() - 1 && currentPlayedTime == 0) {
                    return song;
                }
            } else {
                return song;
            }
        }
        return null;
    }

    /**
     * Method used to shuffle the playlist
     *
     * @param seed      Given seed
     * @param timestamp Current timestamp
     * @return result to this command
     */
    public String shuffle(final int seed, final int timestamp) {
        int currentPlayedTime = getCurrentPlayedTime(timestamp);
        SongInputExtended song = getPlayingSongFromPlaylist(timestamp);
        for (int i = 0; i < playlist.getSongs().size(); i++) {
            if (playlist.getSongs().get(playlistIds.get(i)) == song) {
                break;
            }
            currentPlayedTime -= playlist.getSongs().get(playlistIds.get(i)).getDuration();
        }
        if (shuffle) {
            Collections.sort(playlistIds);
        } else {
            Collections.shuffle(playlistIds, new Random(seed));
        }

        int duration = 0;
        for (int i = 0; i < playlist.getSongs().size(); i++) {
            if (playlist.getSongs().get(playlistIds.get(i)) == song) {
                break;
            }
            duration += playlist.getSongs().get(playlistIds.get(i)).getDuration();
        }

        timestampStarted = timestamp - (duration + currentPlayedTime);

        if (!isPaused()) {
            playedTime = 0;
        } else {
            playedTime = duration + currentPlayedTime;
        }
        String message;
        if (shuffle) {
            message = "Shuffle function deactivated successfully.";
        } else {
            message = "Shuffle function activated successfully.";
        }
        shuffle = !shuffle;
        return message;
    }

    /**
     * Method used to move forward
     *
     * @param timestamp Current timestamp
     * @return String message
     */
    public String forward(final int timestamp) {
        int currentPlayedTime = getCurrentPlayedTime(timestamp);
        currentPlayedTime = currentPlayedTime % podcastInput.getDuration();
        EpisodeInput currentEpisode = null;
        for (EpisodeInput episode : podcastInput.getEpisodes()) {
            if (currentPlayedTime >= episode.getDuration()) {
                currentPlayedTime -= episode.getDuration();
            } else {
                currentEpisode = episode;
                break;
            }
        }
        assert currentEpisode != null;
        this.timestampStarted -= Math.min(currentEpisode.getDuration()
                - currentPlayedTime, SECONDS_TO_SKIP);
        return "Skipped forward successfully.";
    }

    /**
     * Method used to move the player backward
     *
     * @param timestamp Current timestamp
     * @return String response
     */
    public String backward(final int timestamp) {
        int currentPlayedTime = getCurrentPlayedTime(timestamp);
        currentPlayedTime = currentPlayedTime % podcastInput.getDuration();
        for (EpisodeInput episode : podcastInput.getEpisodes()) {
            if (currentPlayedTime >= episode.getDuration()) {
                currentPlayedTime -= episode.getDuration();
            } else {
                break;
            }
        }
        this.timestampStarted += Math.min(currentPlayedTime, SECONDS_TO_SKIP);
        return "Rewound successfully.";
    }

    /**
     * Method used to move to the next song considering the repeat mode
     *
     * @param timestamp Current time
     * @return String message
     */
    public String next(final int timestamp) {
        int currentPlayedTime = getCurrentPlayedTime(timestamp);
        switch (playingType) {
            case "song":
                currentPlayedTime = currentPlayedTime % songInput.getDuration();
                timestampStarted -= songInput.getDuration() - currentPlayedTime;
                if (isPaused()) {
                    playedTime += songInput.getDuration() - currentPlayedTime;
                }
                break;
            case "playlist":
                int currentSongIndex = -1;
                for (Integer playlistId : playlistIds) {
                    SongInputExtended song = playlist.getSongs().get(playlistId);
                    if (currentPlayedTime >= song.getDuration()) {
                        currentPlayedTime -= song.getDuration();
                    } else {
                        currentSongIndex = playlistId;
                        break;
                    }
                }
                int duration = Objects.requireNonNull(getPlayingSongFromPlaylist(timestamp))
                        .getDuration();
                timestampStarted -= duration - currentPlayedTime;
                if (isPaused()) {
                    playedTime += duration - currentPlayedTime;
                }
                if (repeat == 2) {
                    timestampStartedRepeat = timestamp;
                }
                return getSongInputExtendedName(currentSongIndex, timestamp);
            case "podcast":
                currentPlayedTime = currentPlayedTime % podcastInput.getDuration();
                currentPlayedTime = currentPlayedTime % podcastInput.getDuration();
                int episodeDuration = 0;
                for (EpisodeInput episode : podcastInput.getEpisodes()) {
                    if (currentPlayedTime >= episode.getDuration()) {
                        currentPlayedTime -= episode.getDuration();
                    } else {
                        episodeDuration = episode.getDuration();
                        break;
                    }
                }
                timestampStarted -= episodeDuration - currentPlayedTime;
                if (isPaused()) {
                    playedTime += episodeDuration - currentPlayedTime;
                }
                break;
            default:
                break;
        }
        return "Skipped to next track successfully. "
                + "The current track is " + getNextName(timestamp) + ".";
    }

    private String getSongInputExtendedName(final int currentSongIndex, final int timestamp) {

        if (repeat == 2) {
            return "Skipped to next track successfully. The current track is "
                    + selectedToRepeatFromPlaylist.getName() + ".";
        }

        if (playlistIds.indexOf(currentSongIndex) < playlist.getSongs().size() - 1) {
            return "Skipped to next track successfully. The current track is "
                    + Objects.requireNonNull(getPlayingSongFromPlaylist(timestamp)).getName() + ".";
        } else {
            if (repeat == 1) {
                return "Skipped to next track successfully. The current track is "
                        + playlist.getSongs().get(playlistIds.get(0)).getName() + ".";
            } else {
                setFinished();
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
        int currentPlayedTime = getCurrentPlayedTime(currentTimestamp);
        switch (playingType) {
            case "song":
                if (songInput.getDuration() <= currentPlayedTime) {
                    if (repeat == 1) {
                        repeat = 0;
                        if (currentTimestamp - songInput.getDuration()
                                >= songInput.getDuration()) {
                            setFinished();
                            return "";
                        }
                    } else {
                        setFinished();
                        return "";
                    }
                }
                return songInput.getName();
            case "playlist":
                if (repeat == 2) {
                    return selectedToRepeatFromPlaylist.getName();
                }
                if (playlist.getDuration() <= currentPlayedTime && playlist.getDuration() != 0) {
                    if (repeat == 1) {

                        currentPlayedTime = currentPlayedTime % playlist.getDuration();
                    } else {
                        setFinished();
                        return "";

                    }
                }
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
                if (repeat == 0) {
                    if (podcastInput.getDuration() <= currentPlayedTime) {
                        playedTime = podcastInput.getDuration();
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
                break;
        }
        return "";
    }

    /**
     * Method used to move the player to the previous song
     *
     * @param timestamp Current time
     * @return String returned after performing the command
     */
    public String prev(final int timestamp) {
        int currentPlayedTime = getCurrentPlayedTime(timestamp);
        switch (playingType) {
            case "song":
                currentPlayedTime = currentPlayedTime % songInput.getDuration();
                timestampStarted += songInput.getDuration() - currentPlayedTime;
                if (isPaused()) {
                    playedTime -= currentPlayedTime;
                }
                return "Returned to previous track successfully. "
                        + "The current track is " + songInput.getName() + ".";
            case "playlist":
                currentPlayedTime = currentPlayedTime % playlist.getDuration();
                for (int i = 0; i < playlist.getSongs().size(); i++) {
                    SongInputExtended song = playlist.getSongs().get(playlistIds.get(i));
                    if (currentPlayedTime > song.getDuration()) {
                        currentPlayedTime -= song.getDuration();
                    } else {
                        break;
                    }
                }
                if (isPaused()) {
                    timestampStarted = timestamp - (playedTime - currentPlayedTime);
                    playedTime -= currentPlayedTime;

                } else {
                    timestampStarted += currentPlayedTime;
                }

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
                return "Returned to previous track successfully. The current track is "
                        + Objects.requireNonNull(getPlayingSongFromPlaylist(timestamp)).getName()
                        + ".";
            case "podcast":
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
        return "Returned to previous track successfully. The current track is "
                + getNextName(timestamp - currentPlayedTime) + ".";
    }
}
