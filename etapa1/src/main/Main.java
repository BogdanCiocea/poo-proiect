package main;

import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.extended.PodcastInputExtended;
import fileio.input.LibraryInput;
import fileio.output.*;

import user.UserDetails;
import music.Playlist;
import fileio.Stats;
import fileio.input.CommandInput;
import fileio.extended.SongInputExtended;
import fileio.input.SongInput;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import static service.Service.*;

/**
 * The entry point to this homework. It runs the checker that tests your implementation.
 */
public final class Main {
    static final String LIBRARY_PATH = CheckerConstants.TESTS_PATH + "library/library.json";

    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.getName().startsWith("library")) {
                continue;
            }

            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getPath(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePathInput for input file
     * @param filePathOutput for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePathInput,
                              final String filePathOutput) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        LibraryInput library = objectMapper.readValue(new File(LIBRARY_PATH), LibraryInput.class);
        ArrayNode outputs = objectMapper.createArrayNode();
        CommandInput[] commands =
                objectMapper.readValue(
                        new File(filePathInput),
                        CommandInput[].class);
        Map<String, UserDetails> usersDetails = new HashMap<>();
        List<Playlist> playlists = new ArrayList<>();
        for (CommandInput command : commands) {
            String username = command.getUsername();
            UserDetails currentUser =
                    usersDetails.containsKey(username)
                            ? usersDetails.get(username) : new UserDetails(command.getUsername());
            if (!usersDetails.containsKey(username)) {
                usersDetails.put(username, currentUser);
            }
            String message;
            String comm = command.getCommand();
            int tmp = command.getTimestamp();
            switch (comm) {
                case "search":
                    currentUser.resetPlayer(tmp);
                    currentUser.setTypeSearched(command.getType());
                    currentUser.setSelected(false);
                    List<String> results = new ArrayList<>();
                    switch (command.getType()) {
                        case "song" -> {
                            List<SongInputExtended> songRes =
                                    searchSongs(command.getFilters(), library.getSongs());
                            currentUser.setSearchSongResults(songRes);
                            for (SongInput songRe : songRes) {
                                results.add(songRe.getName());
                            }
                        }
                        case "playlist" -> {
                            List<Playlist> playlistsRes =
                                    searchPlaylists(username, command.getFilters(), playlists);
                            currentUser.setSearchPlaylistsResults(playlistsRes);
                            for (Playlist playlistsRe : playlistsRes) {
                                results.add(playlistsRe.getName());
                            }
                        }
                        case "podcast" -> {
                            List<PodcastInputExtended> podcastRes =
                                    searchPodcasts(command.getFilters(), library.getPodcasts());
                            currentUser.setSearchPodcastsResults(podcastRes);
                            for (PodcastInputExtended podcastRe : podcastRes) {
                                results.add(podcastRe.getName());
                            }
                        }
                        default -> {
                        }
                    }
                    outputs.add(new SearchOutput(comm, command.getUsername(),
                            tmp, results).toObjectNode());
                    break;
                case "select":
                    message = selectCommand(command, usersDetails.get(username));
                    outputs.add(new MessageOutput(comm, username, tmp, message
                    ).toObjectNode());
                    break;
                case "createPlaylist":
                    message = createPlaylist(playlists, command);
                    outputs.add(new MessageOutput(comm, username, tmp, message
                    ).toObjectNode());
                    break;
                case "load":
                    message = load(tmp, currentUser);
                    outputs.add(new MessageOutput(comm, username, tmp, message
                    ).toObjectNode());
                    break;
                case "status":
                    Stats stats = status(tmp, currentUser);
                    outputs.add(new StatusOutput(comm, username, tmp, stats
                    ).toObjectNode());
                    break;
                case "playPause":
                    message = playPause(tmp, currentUser);
                    outputs.add(new MessageOutput(comm, username, tmp, message
                    ).toObjectNode());
                    break;
                case "repeat":
                    message = repeat(currentUser, tmp);
                    outputs.add(new MessageOutput(comm, username, tmp, message
                    ).toObjectNode());
                    break;
                case "shuffle":
                    message = shuffle(currentUser, command.getSeed(), tmp);
                    outputs.add(new MessageOutput(comm, username, tmp, message
                    ).toObjectNode());
                    break;
                case "backward":
                    message = backward(currentUser, tmp);
                    outputs.add(new MessageOutput(comm, username, tmp, message).toObjectNode());
                    break;
                case "forward":
                    message = forward(currentUser, tmp);
                    outputs.add(new MessageOutput(comm, username, tmp, message).toObjectNode());
                    break;
                case "next":
                    message = next(currentUser, tmp);
                    outputs.add(new MessageOutput(comm, username, tmp, message).toObjectNode());
                    break;
                case "prev":
                    message = prev(currentUser, tmp);
                    outputs.add(new MessageOutput(comm, username, tmp, message).toObjectNode());
                    break;
                case "addRemoveInPlaylist":
                    message = addRemove(command.getPlaylistId(), currentUser, playlists);
                    outputs.add(new MessageOutput(comm, username, tmp, message).toObjectNode());
                    break;
                case "like":
                    message = like(currentUser, tmp);
                    outputs.add(new MessageOutput(comm, username, tmp, message).toObjectNode());
                    break;
                case "showPreferredSongs":
                    List<String> results1 = showPreferredSongs(currentUser);
                    outputs.add(new PreferredOutput(comm, command.getUsername(),
                            tmp, results1).toObjectNode());
                    break;
                case "showPlaylists":
                    outputs.add(new PlaylistsOutput(comm, command.getUsername(), tmp,
                            retrieveUserPlaylists(username, playlists)).toObjectNode());
                    break;
                case "getTop5Songs":
                    outputs.add(new Top5Output(comm, tmp,
                            getTop5S(library.getSongs())).toObjectNode());
                    break;
                case "getTop5Playlists":
                    outputs.add(new Top5Output(comm, tmp, getTop5P(playlists)).toObjectNode());
                    break;
                case "follow":
                    message = follow(currentUser);
                    outputs.add(new MessageOutput(comm, username, tmp, message).toObjectNode());
                    break;
                case "switchVisibility":
                    message = switchVisibility(command.getPlaylistId(), username, playlists);
                    outputs.add(new MessageOutput(comm, username, tmp, message).toObjectNode());
                    break;
                default:
                    break;
            }
            currentUser.setLastCommand(comm);
        }
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePathOutput), outputs);
    }
}
