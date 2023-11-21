package fileio.output;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.Stats;

public final class StatusOutput extends BaseOutput {

    private final Stats stats;

    public StatusOutput(final String command,
                        final String user,
                        final int timestamp,
                        final Stats stats) {
        super(command, user, timestamp);
        this.stats = stats;
    }

    /**
     * Method that transforms this object to a node object
     * @return ObjectNode
     */
    @Override
    public ObjectNode toObjectNode() {
        ObjectNode objectNode =  super.toObjectNode();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("name", stats.getName());
        node.put("remainedTime", stats.getRemainedTime());
        node.put("repeat", stats.getRepeat());
        node.put("shuffle", stats.isShuffle());
        node.put("paused", stats.isPaused());
        objectNode.put("stats", node);
        return objectNode;
    }
}
