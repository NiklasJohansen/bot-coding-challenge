package games.HelloWorld.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This example code shows how establishing a connection to the server could be done
 * without using any functionality from the core library. The only dependency required for
 * the following code to run is the Gson JSON-parser from Google.
 *
 * The GameData and ClientResponse classes defined inside this class is used to parse the
 * JSON-formatted messaged into easily accessible objects. They should match the classes
 * defined by the server exactly.
 *
 * This bot simply prints the data from the server to the console and answers with a ClientResponse object.
 *
 * @author Niklas Johansen
 */
public class HelloWorldClientStandalone
{
    /**
     * Opens a connection to the server on the defined IP and port.
     * @param args input arguments
     */
    public static void main(String[] args)
    {
        Gson gson = new GsonBuilder().create();
        try (Socket socket = new Socket("127.0.0.1", 55500);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())))
        {
            String line;
            while((line = in.readLine()) != null)
            {
                if(line.startsWith("{"))
                {
                    GameState gameState = gson.fromJson(line, GameState.class);
                    ClientResponse response = handleUpdate(gameState);
                    out.println(gson.toJson(response));
                }
                else System.out.println("[" + socket.getRemoteSocketAddress() + "]: " + line);
            }
        }
        catch (IOException e)
        {
            System.err.println("Connection to server was closed");
        }
    }

    /**
     * Handles the update from the server and creates the response to be returned
     * @param gameState the game state object received from the server
     * @return the response object to send back to the server
     */
    public static ClientResponse handleUpdate(GameState gameState)
    {
        System.out.println("[" + gameState.serverName + "]: " + gameState.timestamp);

        ClientResponse response = new ClientResponse();
        response.clientName = "Java_Client_Standalone";
        response.message = "Hello " + gameState.serverName + "!";

        return response;
    }

    /**
     * This class represents the game relevant data from the server.
     * It should exactly match the class defined by the server.
     */
    private static class GameState
    {
        private String serverName;
        private String timestamp;
    }

    /**
     * This class represents the response to send back to the server.
     * It should exactly match the class defined by the server.
     */
    private static class ClientResponse
    {
        private String clientName;
        private String message;
    }
}
