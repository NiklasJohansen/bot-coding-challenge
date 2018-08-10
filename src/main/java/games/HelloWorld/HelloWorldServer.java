package games.HelloWorld;

import core.server.GameServer;
import core.server.Player;

import java.util.Date;

/**
 * This is an example class showing the steps necessary in setting up a {@link GameServer}.
 *
 * These steps are required for the server to run:
 * - Create a player implementation and specify a ClientResponse class
 * - Create a new GameServer instance
 * - Start the server on a given port (should be done last, as starting the server will initiate some of the optional steps)
 *
 * Optional steps:
 * - Define what should happen when a new player connects
 * - Define what should happen when a player disconnects
 * - Set a game loop to repeatedly update the players with the current GameData.
 *
 * This example server is set up to send a GameData object to all connected players every second.
 * The content of the response packets received from the players will be printed to the console.
 *
 * @author Niklas Johansen
 */
public class HelloWorldServer
{
    public static void main(String[] args)
    {
        GameServer<HelloWorldPlayer> server = new GameServer<>(HelloWorldPlayer.class);

        server.setOnNewPlayerConnection(player ->
                System.out.println(player.getIpAddress() + " connected!"));

        server.setOnPlayerDisconnect(player ->
                System.out.println(player.getIpAddress() + " disconnected!"));

        server.setGameLoop(1, () ->
        {
            GameState gameState = new GameState();
            gameState.serverName = "GameServer";
            gameState.timestamp = new Date().toString();
            server.broadcast(gameState);

            System.out.println();
            for(HelloWorldPlayer player : server.getPlayers())
            {
                ClientResponse response = player.getResponse();
                if(response != null)
                    System.out.println("[" + response.clientName + "]: " + response.message);
            }
        });

        server.start(55500);
    }

    /**
     * This class is an implementation of the Player used by the GameServer.
     */
    public static class HelloWorldPlayer extends Player<ClientResponse>
    {
        // other player data //

        public HelloWorldPlayer()
        {
            super(ClientResponse.class);
        }
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
     * This class contains game relevant data.
     * The content will be parsed to JSON and broadcast to all connected players.
     */
    public static class GameState
    {
        public String serverName;
        public String timestamp;
    }
}
