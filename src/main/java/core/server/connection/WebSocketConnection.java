package core.server.connection;

import core.server.Event;
import org.java_websocket.WebSocket;

import java.util.function.Consumer;

/**
 * An implementation of the {@link Connection} interface using a web socket.
 * New messages received by the web socket server is forwarded through this
 * class by the specified messageEvent.
 *
 * @author Niklas Johansen
 */
public class WebSocketConnection implements Connection
{
    private WebSocket webSocket;
    private Consumer<String> messageEvent;
    private Event disconnectEvent;
    private String address;

    /**
     * @param webSocket the web socket to be used for sending data to the client
     */
    public WebSocketConnection(WebSocket webSocket)
    {
        this.webSocket = webSocket;
        this.address = webSocket.getLocalSocketAddress().getHostName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(String msg)
    {
        if(webSocket.isOpen())
            webSocket.send(msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOnMessage(Consumer<String> msgEvent)
    {
        this.messageEvent = msgEvent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOnDisconnect(Event event)
    {
        this.disconnectEvent = event;
    }

    /**
     * Used by the web socket server to forward new messages.
     *
     * @return the event to be triggered when a new messages is received
     */
    public Consumer<String> getMessageEvent()
    {
        return messageEvent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close()
    {
        disconnectEvent.call();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAddress()
    {
        return address;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnected()
    {
        return webSocket.isOpen();
    }
}
