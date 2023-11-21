package fileio.extended;

import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;

public class PodcastInputExtended extends PodcastInput {
    /**
     * Retrieve the podcast entire duration
     * @return duration of current podcast
     */
    public int getDuration() {
        int duration = 0;
        for (EpisodeInput episode : super.getEpisodes()) {
            duration += episode.getDuration();
        }
        return duration;
    }
}
