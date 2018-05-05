package core.client;


import core.utils.DataParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

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

    public <T> void setOnServerUpdate(Class<T> className, Consumer<T> serverUpdateEvent)
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
                        serverUpdateEvent.accept(gameState);
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

    public <T> void send(T data)
    {
        printWriter.println(DataParser.parseToJSON(data));
    }

    public void send(String data)
    {
        printWriter.println(data);
    }
}
