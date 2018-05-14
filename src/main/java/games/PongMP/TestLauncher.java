package games.PongMP;

import games.PongMP.example.PongClientExample;

public class TestLauncher
{
    public static void main(String[] args) throws InterruptedException
    {
        new Thread(() -> PongMain.main(null)).start();

        Thread.sleep(500);

        for(int i = 0; i < 9; i++)
            PongClientExample.main(null);
    }
}
