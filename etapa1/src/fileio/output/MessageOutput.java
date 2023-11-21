package fileio.output;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class MessageOutput extends BaseOutput {

    private final String message;

    public MessageOutput(final String command,
                         final String user,
                         final int timestamp,
                         final String message) {
        super(command, user, timestamp);
        this.message = message;
    }

    /**
     * Method that transforms this object to a node object
     * @return ObjectNode
     */
    @Override
    public ObjectNode toObjectNode() {
        ObjectNode node = super.toObjectNode();
        node.put("message", message);
        return node;
    }
}
