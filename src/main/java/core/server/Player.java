package core.server;

import com.google.gson.JsonSyntaxException;
import core.server.connection.Connection;
import core.utils.DataParser;

/**
 *  Handles the interaction with a connected client.
 *  Provides methods for sending and retrieving JSON formatted data.
 *
 * @author Niklas Johansen
 */
public class Player
{
    private Connection connection;
    private Object playerResponse;

    /**
     * @param connection an open client connection
     * @param clientResponseClass the class in which received JSON-packets should be parsed to
     */
    public Player(Connection connection, Class clientResponseClass)
    {
        this.connection = connection;
        this.connection.setOnMessage(msg ->
        {
            if(msg.startsWith("{"))
            {
                try
                {
                    this.playerResponse = DataParser.parseFromJSON(msg, clientResponseClass);
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
     * Returns the players data object if the connected client has sent a JSON-formatted packet
     * matching the specified response class. It will otherwise return null.
     *
     * @return an object of the class type specified in the constructor
     */
    public Object getPlayerData()
    {
        return playerResponse;
    }

    /**
     * @return the IP address of the player
     */
    public String getIpAddress()
    {
        return connection.getAddress();
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
