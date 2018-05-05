package games.HelloWorld;

import core.server.GameServer;
import core.server.Player;

import java.util.Date;
import java.util.List;

/**
 * This is an example class showing the steps necessary in setting up a {@link GameServer}.
 *
 * These steps are required for the server to run:
 * - Create a new GameServer instance on a given port
 * - Define the ClientResponse class in which JSON-formatted packets from the player will be parsed to
 * - Start the server (should be done last, as starting the server will initiate some of the optional steps)
 *
 * Optional steps:
 * - Define what should happen when a new player connects
 * - Define what should happen when a player disconnects
 * - Set a game loop to repeatedly update the players with the current GameState.
 *
 * This example server is set up to send a GameState object to all connected players every second.
 * The content of the response packets received from the players will be printed to the console.
 *
 * @author Niklas Johansen
 */
public class HelloWorldServer
{
    public static void main(String[] args)
    {
        GameServer server = new GameServer(55500);
        server.setClientResponseClass(ClientResponse.class);
        server.setOnNewPlayerConnection(player -> System.out.println(player.getIpAddress() + " connected!"));
        server.setOnPlayerDisconnect(player -> System.out.println(player.getIpAddress() + " disconnected!"));
        server.setGameLoop(1, () ->
        {
            GameState gameState = new GameState();
            gameState.serverName = "GameServer";
            gameState.timestamp = new Date().toString();
            server.broadcast(gameState);

            System.out.println();
            for(Player player : server.getPlayers())
            {
                ClientResponse response = (ClientResponse) player.getPlayerData();
                if(response != null)
                    System.out.println("[" + response.clientName + "]: " + response.message);
            }
        });
        server.start();
    }

    /**
     * This class contains the data expected to be sent from the players.
     */
    public static class ClientResponse
    {
        public String clientName;
        public String message;
    }

    /**
     * This class should contain game relevant data.
     * The content will be parsed to JSON and broadcast to all connected players.
     */
    public static class GameState
    {
        public String serverName;
        public String timestamp;
    }
}
