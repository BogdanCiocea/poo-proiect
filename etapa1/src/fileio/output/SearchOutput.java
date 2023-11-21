package fileio.output;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public final class SearchOutput extends MessageOutput {

    private List<String> results;

    public SearchOutput(final String command,
                        final String user,
                        final int timestamp,
                        final List<String> results) {
        super(command, user, timestamp, "Search returned " + results.size() + " results");
        this.results = results;
    }

    @Override
    public ObjectNode toObjectNode() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = super.toObjectNode();

        ArrayNode arrayNode = mapper.createArrayNode();
        for (String result : results) {
            arrayNode.add(result);
        }
        node.putArray("results").addAll(arrayNode);
        return node;
    }
}
