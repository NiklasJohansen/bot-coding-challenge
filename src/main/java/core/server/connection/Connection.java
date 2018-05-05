package core.server.connection;

import core.server.Event;

import java.util.function.Consumer;

/**
 * A generic connection interface enabling multiple connection implementations.
 *
 * @author Niklas Johansen
 * @see SocketConnection
 * @see WebSocketConnection
 */
public interface Connection
{
    /**
     * Sends a message to the client.
     *
     * @param msg a string of data
     */
    void send(String msg);

    /**
     * Sets the event to be triggered when a new message is received from the server.
     *
     * @param msgEvent the event to be triggered
     */
    void setOnMessage(Consumer<String> msgEvent);

    /**
     * Sets the event to be triggered when the connection is closed by the client.
     *
     * @param disconnectEvent the event to be triggered
     */
    void setOnDisconnect(Event disconnectEvent);

    /**
     * Closes the connection.
     */
    void close();

    /**
     * @return the IP-address of the connected client
     */
    String getAddress();
}
