package core.server;

import com.google.gson.JsonSyntaxException;
import core.server.connection.Connection;
import core.utils.DataParser;

/**
 * Handles the interaction with a connected client.
 * Provides methods for sending and retrieving JSON formatted data.
 *
 * @param <T> the class containing the data fields expected to be sent from the clients
 * @author Niklas Johansen
 */
public abstract class Player<T>
{
    private Connection connection;
    private Class<T> clientResponseClass;
    private T response;

    /**
     * @param clientResponseClass the class in which received JSON-packets should be parsed to
     */
    public Player(Class<T> clientResponseClass)
    {
        this.clientResponseClass = clientResponseClass;
    }

    /**
     * Adds the connection to the player and sets the event to trigger when a new message is received.
     *
     * @param connection an open client connection
     */
    void addConnection(Connection connection)
    {
        this.connection = connection;
        this.connection.setOnMessage(msg ->
        {
            if(msg.startsWith("{"))
            {
                try
                {
                    this.response = DataParser.parseFromJSON(msg, clientResponseClass);
                }
                catch (JsonSyntaxException | NumberFormatException e)
                {
                    connection.send("ERROR: JSON format does not match the defined response class");
                }
            }
            else System.out.println("[" + connection.getAddress() + "]: " + msg);
        });
    }

    /**
     * Parses an object to a JSON-formatted string and sends it to the client.
     *
     * @param obj a POJO
     */
    public void send(Object obj)
    {
        connection.send(DataParser.parseToJSON(obj));
    }

    /**
     * Sends a message to the client.
     *
     * @param msg a message string
     */
    public void send(String msg)
    {
        connection.send(msg);
    }

    /**
     * Returns the response object sent from the client.
     * The object is null if no response has been received.
     *
     * @return an object of the class type specified in the constructor
     */
    public T getResponse()
    {
        return response;
    }

    /**
     * @return the IP address of the player
     */
    public String getIpAddress()
    {
        return connection.getAddress();
    }

    /**
     * @return true if the connection to the player is open
     */
    public boolean isConnected()
    {
        return connection.isConnected();
    }

    /**
     * Closes the connection to the client.
     */
    public void disconnect()
    {
        connection.close();
    }

    /**
     * Sets the event to trigger when the player disconnects.
     *
     * @param disconnectEvent the event to be triggered
     */
    public void setOnDisconnect(Event disconnectEvent)
    {
        connection.setOnDisconnect(disconnectEvent);
    }
}
