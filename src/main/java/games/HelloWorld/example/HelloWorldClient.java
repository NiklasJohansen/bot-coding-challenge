package games.HelloWorld.example;

import core.client.GameClient;
import games.HelloWorld.HelloWorld.ClientResponse;
import games.HelloWorld.HelloWorld.GameState;
import games.HelloWorld.HelloWorld;

/**
 * This example class shows how the {@link GameClient} is used make a basic bot
 * for the {@link HelloWorld} example server. The code utilises the GameState and ClientResponse
 * classes defined in the game code itself, and requires no further configuration. This bot
 * simply prints the data from the server to the console and answers with a response object.
 *
 * @author Niklas Johansen
 */
public class HelloWorldClient
{
    public static void main(String[] args)
    {
        GameClient client = new GameClient("127.0.0.1",55500);
        client.setOnServerUpdate(GameState.class, gameState ->
        {
            System.out.println("[" + gameState.serverName + "]: " + gameState.timeStamp);

            ClientResponse response = new ClientResponse();
            response.clientName = "Java_Client";
            response.message = "Hello " + gameState.serverName + "!";
            client.send(response);
        });
    }
}
