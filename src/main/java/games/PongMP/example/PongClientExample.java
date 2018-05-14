package games.PongMP.example;

import core.client.GameClient;
import games.PongMP.GameState;
import games.PongMP.PongPlayer.ClientResponse;

public class PongClientExample
{
    public static void main(String[] args)
    {
        GameClient gameClient = new GameClient("127.0.0.1", 55500);
        gameClient.setOnServerUpdate(GameState.class, gameState ->
        {
            ClientResponse response = new ClientResponse();
            response.moveRight = Math.random() >= 0.5;
            response.moveLeft = Math.random() >= 0.5;
            gameClient.send(response);
        });
    }
}
