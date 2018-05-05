package core.server.connection;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Opens a listener to handle new client connections.
 * This class enables connection through the standard Java socket, as
 * well as through web sockets.
 *
 * When clients connect a new {@link Connection} object is created and the
 * newConnectionEvent is triggered with the connection as the parameter.
 *
 * @author Niklas Johansen
 */
public class ConnectionHandler
{
    private int port;
    private ServerSocket serverSocket;
    private WebSocketServer webSocketServer;
    private Consumer<Connection> newConnectionEvent;

    /**
     * @param port the port used to listen for new connections.
     */
    public ConnectionHandler(int port)
    {
        this.port = port;
    }

    /**
     * Starts the connection listener threads.
     * The defined port will be used for the Java socket.
     * The port number incremented bye one will be used by the web socket.
     *
     * @throws IllegalStateException if the event to be triggered for new connections is not set
     */
    public void start()
    {
        if(newConnectionEvent == null)
            throw new IllegalStateException("No connection listener added!");

        new Thread(() ->
        {
            try
            {
                serverSocket = new ServerSocket(port);
                while(!serverSocket.isClosed())
                    newConnectionEvent.accept(new SocketConnection(serverSocket.accept()));
            }
            catch (IOException e) {e.printStackTrace();}
            System.out.println("Connection listener was shut down!");
        }).start();

        webSocketServer = new WebSocketServerImpl(port + 1);
        webSocketServer.start();

        System.out.println("Listening for new connections on port: " + port + " and " + (port + 1) + " (web socket)");
    }

    /**
     * Shuts down the connection listener and the web socket server.
     */
    public void close()
    {
        try
        {
            serverSocket.close();
            webSocketServer.stop();
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Sets the event to be triggered when a new client connects.
     * The new connection is passed as a parrameter to the event.
     *
     * @param newConnectionEvent the event to be triggered
     */
    public void setOnNewConnection(Consumer<Connection> newConnectionEvent)
    {
        this.newConnectionEvent = newConnectionEvent;
    }

    /**
     * A basic implementation of the WebSocketServer.
     * New web socket connections is stored in a local HashMap and the
     * newConnectionEvent is triggered with the {@link Connection} as parameter.
     * Events form a web socket is passed on to its associated connection.
     */
    private class WebSocketServerImpl extends WebSocketServer
    {
        private Map<WebSocket, WebSocketConnection> connections;

        private WebSocketServerImpl(int port)
        {
            super(new InetSocketAddress(port));
            connections = new HashMap<>();
        }

        @Override
        public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake)
        {
            WebSocketConnection conn = new WebSocketConnection(webSocket);
            connections.put(webSocket, conn);
            newConnectionEvent.accept(conn);
        }

        @Override
        public void onClose(WebSocket webSocket, int i, String s, boolean b)
        {
            connections.get(webSocket).close();
            connections.remove(webSocket);
        }

        @Override
        public void onMessage(WebSocket webSocket, String s)
        {
            connections.get(webSocket).getMessageEvent().accept(s);
        }

        @Override
        public void onError(WebSocket webSocket, Exception e) { }

        @Override
        public void onStart() {}
    }
}