package core.client;


import core.utils.DataParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * A client for easy interaction with {@link core.server.GameServer}.
 * Provides methods for sending and receiving JSON-formatted data to and from the server.
 * The client opens a TCP connection with the given IP and port. When new updates are received,
 * a callback will be triggered to notify the bot.
 *
 * @author Niklas Johansen
 */
public class GameClient
{
    private final int port;
    private final String address;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter printWriter;
    private Thread clientThread;

    public GameClient(String address, int port)
    {
        this.address = address;
        this.port = port;
    }

    /**
     * Opens a connection to the server and triggers the callback when new messages arrives.
     *
     * @param className the class to parse the JSON message form the server into
     * @param updateCallback the callback to be triggered on new incoming updates
     * @param <T> the class type for the received JSON object
     */
    public <T> void setOnServerUpdate(Class<T> className, Consumer<T> updateCallback)
    {
        clientThread = new Thread(() ->
        {
            try
            {
                this.socket = new Socket(address, port);
                this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.printWriter = new PrintWriter(socket.getOutputStream(), true);

                String input;
                while((input = reader.readLine()) != null && !socket.isClosed())
                {
                    if(input.startsWith("{"))
                    {
                        T gameState = DataParser.parseFromJSON(input, className);
                        updateCallback.accept(gameState);
                    }
                    else System.out.println("[Server]: " + input);
                }
            }
            catch (IOException e)
            {
                System.err.println("Could not connect to " + address + ":" + port);
            }
        });

        clientThread.start();
    }

    /**
     * Disconnects the client gracefully.
     */
    public void disconnect()
    {
        if(clientThread != null && clientThread.isAlive())
        {
            try
            {
                printWriter.close();
                reader.close();
                socket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Parses an object to a JSON-formatted string and sends it to the client.

     * @param obj a POJO.
     */
    public void send(Object obj)
    {
        printWriter.println(DataParser.parseToJSON(obj));
    }

    /**
     * Sends a message to the server.
     *
     * @param msg a message string
     */
    public void send(String msg)
    {
        printWriter.println(msg);
    }
}
