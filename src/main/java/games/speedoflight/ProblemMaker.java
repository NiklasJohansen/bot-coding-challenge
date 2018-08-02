package games.speedoflight;

import core.client.GameClient;

public class ProblemMaker
{
    public static void main(String[] args) throws InterruptedException {

        while(true) {
            GameClient[] clientList = new GameClient[4];
            for (int i = 0; i < clientList.length; i++) {
                clientList[i] = new GameClient("127.0.0.1", 55500);
                clientList[i].setOnServerUpdate(GameState.class, gameState -> {
                    System.out.println("Connected!");
                });
                Thread.sleep(100);
            }

            Thread.sleep(200);


            for (int j = 0; j < 10; j++) {

                for (int i = 0; i < clientList.length; i++) {

                    LightPlayer.ClientResponse response = new LightPlayer.ClientResponse();
                    response.fire = true;
                    response.up = true;
                    response.rotLeft = true;
                    response.left = true;
                    clientList[i].send(response);
                    Thread.sleep(10);
                }
            }

            Thread.sleep(50);

            Thread.sleep(1000);
            for (int i = 0; i < clientList.length; i++) {
                clientList[i].disconnect();
                Thread.sleep(50);
            }


        }


    }


}
