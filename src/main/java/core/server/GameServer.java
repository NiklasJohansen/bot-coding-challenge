package core.server;

import core.server.connection.ConnectionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * A server for easy interaction with connected {@link Player players}.
 * Opens a listener on the specified port and adds new player connections
 * to a local list. Events to be triggered when players connect and disconnect
 * can be set through provided methods.
 *
 * This class also provides functionality for timed updates through the
 * setGameLoop method.
 *
 * @author Niklas Johansen
 */
public class GameServer
{
    private Thread gameLoopThread;
    private Class clientResponseClass;
    private ConnectionHandler connectionHandler;

    private Consumer<Player> newPlayerConnectionEvent;
    private Consumer<Player> playerDisconnectEvent;
    private List<Player> players;

    private boolean enableNewConnections;
    private boolean reverseBroadcast;

    /**
     * @param port the port used by the connection listener
     */
    public GameServer(int port)
    {
        this.connectionHandler = new ConnectionHandler(port);
        this.players = Collections.synchronizedList(new ArrayList<>());
        this.enableNewConnections = true;
    }

    /**
     * Starts up the game server by starting the connection listener and
     * the game loop (if set).
     *
     * @throws IllegalStateException if the client response class is not set
     */
    public void start()
    {
        if(clientResponseClass == null)
            throw new IllegalStateException("Client response class not set");

        if(gameLoopThread != null)
            gameLoopThread.start();

        connectionHandler.setOnNewConnection(conn ->
        {
            Player player = new Player(conn, clientResponseClass);

            if(enableNewConnections)
            {
                players.add(player);
                player.setOnDisconnect(() ->
                {
                    players.remove(player);
                    if(playerDisconnectEvent != null)
                        playerDisconnectEvent.accept(player);
                });

                if(newPlayerConnectionEvent != null)
                    newPlayerConnectionEvent.accept(player);
            }
            else
            {
                player.send("Server is closed for new connections!");
                player.disconnect();
            }
        });

        connectionHandler.start();
    }

    /**
     * Disconnects a player from the server.
     *
     * @param player the player to disconnect
     */
    public void kickPlayer(Player player)
    {
        player.disconnect();
    }

    /**
     * Sends an object to all connected players.
     * To prevent early connection advantage, the direction of iteration is
     * reversed on every other broadcast.

     * @param obj the object to send
     */
    public void broadcast(Object obj)
    {
        if(reverseBroadcast)
        {
            for(int i = players.size() - 1; i >= 0; i--)
                players.get(i).send(obj);
        }
        else players.forEach(p -> p.send(obj));

        reverseBroadcast = !reverseBroadcast;
    }

    /**
     * Shuts down the server by disconnecting all players,
     * closing the connection listener and stopping the game loop.
     */
    public void shutdown()
    {
        players.forEach(Player::disconnect);
        connectionHandler.close();
        gameLoopThread = null;
    }

    /**
     * @return the list of all connected players
     */
    public List<Player> getPlayers()
    {
        return players;
    }

    /**
     * Enables new players to connect. Enabled by default.
     *
     * @param state the state indicating whether new players are allowed to connect
     */
    public void enableNewConnections(boolean state)
    {
        this.enableNewConnections = state;
    }

    /**
     * Sets the class type to be used when parsing the JSON-formatted packets from the players.
     *
     * @param clientResponseClass the class type matching the JSON formatted player-response
     */
    public void setClientResponseClass(Class clientResponseClass)
    {
        this.clientResponseClass = clientResponseClass;
    }

    /**
     * Sets the event to be triggered when a new player connects to the server.
     *
     * @param event the event to be triggered, returns the new player.
     */
    public void setOnNewPlayerConnection(Consumer<Player> event)
    {
        this.newPlayerConnectionEvent = event;
    }

    /**
     * Sets the event to be triggered when a player disconnects from the server.
     *
     * @param event the event to be triggered, returns the the disconnected player.
     */
    public void setOnPlayerDisconnect(Consumer<Player> event)
    {
        this.playerDisconnectEvent = event;
    }

    /**
     * Sets an event to be triggered at a specified rate.
     *
     * @param targetUpdateRate the number of updates per second
     * @param gameLoopEvent the event to be triggered
     */
    public void setGameLoop(int targetUpdateRate, Event gameLoopEvent)
    {
        gameLoopThread = new Thread(() ->
        {
            long updateTimeNanoSec = 1000000000 / targetUpdateRate;
            while(gameLoopThread != null)
            {
                long lastTime = System.nanoTime();
                gameLoopEvent.call();
                try
                {
                    Thread.sleep((lastTime - System.nanoTime() + updateTimeNanoSec) / 1000000);
                }
                catch (InterruptedException ignore) {}
            }
        });
    }
}
