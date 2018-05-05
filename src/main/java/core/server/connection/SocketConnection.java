package core.server.connection;

import core.server.Event;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * An implementation of the {@link Connection} interface using a standard Java socket.
 * The class starts a new thread to enable non-blocking listening. When new messages are
 * received the event specified by the setOnMessage method is triggered.
 *
 * @author Niklas Johansen
 */
public class SocketConnection implements Connection
{
    private Socket socket;
    private String address;
    private PrintWriter printWriter;
    private Event disconnectEvent;

    /**
     * @param socket an open socket connection
     */
    public SocketConnection(Socket socket)
    {
        try
        {
            this.socket = socket;
            this.address = socket.getInetAddress().getHostAddress();
            this.printWriter = new PrintWriter(socket.getOutputStream(), true);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOnMessage(Consumer<String> msgEvent)
    {
        new Thread(() ->
        {
            try(BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())))
            {
                String line;
                while(!socket.isClosed() && (line = in.readLine()) != null)
                    msgEvent.accept(line);
            }
            catch (IOException e) {}

            if(disconnectEvent != null)
                disconnectEvent.call();

        }).start();
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
     * {@inheritDoc}
     */
    @Override
    public void send(String msg)
    {
        printWriter.println(msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close()
    {
        try
        {
            if(disconnectEvent != null)
                disconnectEvent.call();

            printWriter.close();
            socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAddress()
    {
        return address;
    }
}
