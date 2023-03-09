package communication;

import java.io.Serializable;
import java.util.HashMap;

/**
 * This class is represents a protocol that is being used to communicate by Clients and Server.
 */
public class Protocol implements Serializable {

    /**
     * This protocol's header.
     */
    public Header header;

    /**
     * This protocol's data.
     */
    public HashMap<String, Object> data;

    /**
     * Constructor for this class.
     *
     * @param header this protocol's header
     */
    public Protocol(Header header) {
        this.header = header;
        data = new HashMap<>();
    }

    /**
     * This method adds passed (key, value) pair to this protocol's data.
     *
     * @param key   value's name
     * @param value value to add
     */
    public void put(String key, Object value) {
        data.put(key, value);
    }

    @Override
    public String toString() {
        return "Protocol{" +
                "header=" + header +
                ", data=(" + data.keySet() +
                ";" + data.values() + ")}";
    }
}