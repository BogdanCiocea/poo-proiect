package fileio.output;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *Class that defines a base output containing only the command name, user and timestamp
 */
public class BaseOutput {
    private final String command;
    private final String user;
    private final int timestamp;

    public BaseOutput(final String command,
                      final String user,
                      final int timestamp) {
        this.command = command;
        this.user = user;
        this.timestamp = timestamp;
    }

    /**
     * Returns an object node that is saved to the output file
     * @return Object node
     */
    public ObjectNode toObjectNode() {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode node = mapper.createObjectNode();
        node.put("command", command);
        node.put("user", user);
        node.put("timestamp", timestamp);
        return node;
    }
}
