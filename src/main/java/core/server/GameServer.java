package core.server;

import core.server.connection.Connection;
import core.server.connection.ConnectionHandler;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * A server for easy interaction with connected {@link Player players}.
 * Opens a listener on the specified port and adds new player connections
 * to a local list. Events to be triggered when players connect and disconnect
 * can be set through provided methods. The class also provides functionality
 * for timed updates through the setGameLoop method.
 *
 * The server is made generic to enable different player implementation in
 * different games.
 *
 * @param <P> the player implementation class
 * @author Niklas Johansen
 */
public class GameServer<P extends Player>
{
    private Thread gameLoopThread;
    private ConnectionHandler connectionHandler;

    private final List<P> players;
    private final Class<P> playerClass;

    private Consumer<P> newPlayerConnectionEvent;
    private Consumer<P> playerDisconnectEvent;

    private boolean allowNewConnections;
    private boolean reverseBroadcast;
    private boolean removePlayerOnDisconnect;

    /**
     * NOTE: in order for the server to create objects of the player implementation class,
     * only a default constructor without parameters can be used.
     *
     * @param playerClass the player implementation class
     */
    public GameServer(Class<P> playerClass)
    {
        for(Constructor<?> c : playerClass.getConstructors())
            if(c.getParameterCount() > 0)
                throw new IllegalArgumentException("The player class constructor cannot take parameters!");

        this.playerClass = playerClass;
        this.players = Collections.synchronizedList(new ArrayList<>());
        this.allowNewConnections = true;
        this.removePlayerOnDisconnect = true;
    }

    /**
     * Starts up the game server by starting the connection listener and the game loop (if set).
     *
     * @param port the port used by the connection listener
     * @throws IllegalStateException if the client response class is not set
     */
    public void start(int port)
    {
        connectionHandler = new ConnectionHandler(port);
        connectionHandler.setOnNewConnection(this::handleNewConnection);
        connectionHandler.start();

        if(gameLoopThread != null)
            gameLoopThread.start();
    }

    /**
     * Creates a new player object with the supplied connection and adds it to the player list.
     * The connection will be closed immediately if new connection is not allowed.
     *
     * @param connection the connection to be handled
     */
    private void handleNewConnection(Connection connection)
    {
        try
        {
            if(allowNewConnections)
            {
                P player = playerClass.newInstance();
                player.addConnection(connection);
                player.setOnDisconnect(() ->
                {
                    synchronized(players)
                    {
                        if(removePlayerOnDisconnect)
                            players.remove(player);
                    }

                    if(playerDisconnectEvent != null)
                        playerDisconnectEvent.accept(player);
                });

                synchronized(players)
                {
                    players.add(player);
                }

                if(newPlayerConnectionEvent != null)
                    newPlayerConnectionEvent.accept(player);
            }
            else connection.close();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
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
        synchronized(players)
        {
            if(reverseBroadcast)
            {
                for(int i = players.size() - 1; i >= 0; i--)
                    players.get(i).send(obj);
            }
            else players.forEach(p -> p.send(obj));

            reverseBroadcast = !reverseBroadcast;
        }
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
    public List<P> getPlayers()
    {
        return players;
    }

    /**
     * Allows new players to connect.
     * Default value is true.
     *
     * @param state the state indicating whether new players are allowed to connect or not
     */
    public void allowNewConnections(boolean state)
    {
        this.allowNewConnections = state;
    }

    /**
     * Sets whether a player should be removed from the player-list when it disconnects.
     * Default value is true.
     *
     * @param state the state indicating whether players should be removed or not
     */
    public void setRemovePlayerOnDisconnect(boolean state)
    {
        this.removePlayerOnDisconnect = state;
    }

    /**
     * Sets the event to be triggered when a new player connects to the server.
     *
     * @param event the event to be triggered, returns the new player.
     */
    public void setOnNewPlayerConnection(Consumer<P> event)
    {
        this.newPlayerConnectionEvent = event;
    }

    /**
     * Sets the event to be triggered when a player disconnects from the server.
     *
     * @param event the event to be triggered, returns the the disconnected player.
     */
    public void setOnPlayerDisconnect(Consumer<P> event)
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
                synchronized (players)
                {
                    gameLoopEvent.call();
                }
                try
                {
                    Thread.sleep(Math.max(0,(lastTime - System.nanoTime() + updateTimeNanoSec) / 1000000));
                }
                catch (InterruptedException ignore) {}
            }
        });
    }
}