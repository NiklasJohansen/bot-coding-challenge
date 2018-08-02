package games.speedoflight;

import core.server.GameServer;
import javafx.animation.AnimationTimer;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Controller
{
    @FXML private Canvas canvas;
    @FXML private AnchorPane anchorPane;

    private GameState gameState;
    private GameServer<LightPlayer> server;
    private ArrayList<Bullet> bullets;
    private ArrayList<Obstacle> obstacles;

    private int frameCounter = 0;
    private float width = 1920;
    private float height = 1027;

    private int respawnIndex = 0;
    private float[][] respawns = {
            {30,30},
            {width - 30,30},
            {width - 30,height - 30},
            {30,height - 30},
            {width / 2 ,height - 30}
    };

    @FXML
    public void initialize() {
        addEventListeners();
        startGameLoop();
        openServer();
        createMap();
        this.gameState = new GameState();
        this.bullets = new ArrayList<>();
    }

    public void closeRequest()
    {
        Platform.exit();
        System.exit(0);
    }

    private void startGameLoop()
    {
        new AnimationTimer()
        {
            @Override
            public void handle(long now)
            {
                updateGame();
                renderGame();
            }
        }.start();
    }

    private void updateGame()
    {
        server.getPlayers().forEach(player ->
        {
            //player.send(gameState);
            player.update(bullets);
            player.borderConstrain(0, 0, (float)canvas.getWidth(), (float)canvas.getHeight());
            resolveCollisions();
        });

        for(int i = 0; i < bullets.size() && bullets.size() > 0; i++)
        {
            Bullet b = bullets.get(i);
            b.update();
            b.borderConstrain(0, 0, (float)canvas.getWidth(), (float)canvas.getHeight());
            for(Obstacle o : obstacles)
                o.resolveBulletCollision(b);

            if(b.isDead())
            {
                bullets.remove(b);
                i--;
            }
        }
    }

    private void openServer()
    {
        this.server = new GameServer<>(LightPlayer.class);
        this.server.setOnNewPlayerConnection(player -> {
            player.setX(respawns[respawnIndex][0]);
            player.setY(respawns[respawnIndex][1]);
            player.setXLast(respawns[respawnIndex][0]);
            player.setYLast(respawns[respawnIndex][1]);
            respawnIndex = (respawnIndex + 1) % respawns.length;
        });

        this.server.start(55500);
    }

    private void resolveCollisions()
    {
        List<LightPlayer> players = server.getPlayers();
        for (int  i = 0; i < players.size() - 1; i++)
            for (int j = i + 1; j < players.size(); j++)
                players.get(i).resolvePlayerCollision(players.get(j));

        for(Obstacle obstacle : obstacles)
            for(LightPlayer player : players)
                obstacle.resolveCollision(player);
    }

    private void renderGame()
    {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for(LightPlayer player: server.getPlayers())
            player.draw(gc);

        for(Bullet b : bullets)
            b.draw(gc);

        for(Obstacle o : obstacles)
            o.draw(gc);

        int playersAlive = 0;
        for(LightPlayer player: server.getPlayers())
            if(!player.isDead())
                playersAlive++;

        if(playersAlive == 1 && server.getPlayers().size() > 1)
        {
            gc.setFill(Color.color(0f,0f,0f,0.8));
            gc.fillRect(0,0, canvas.getWidth(), canvas.getHeight());

            int index = 0;
            for(LightPlayer player: server.getPlayers())
            {
                gc.setFill(Color.WHITE);
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText(player.getName() + ":    " + (int)player.getKills() + "  /  " + (int)player.getDeaths(), canvas.getWidth() / 2, 200 + 60 * index);
                index++;
            }

            frameCounter++;
            if(frameCounter >= 60 * 10)
            {
                frameCounter = 0;
                respawn();
            }
        }
    }

    public void respawn()
    {
        int index = 0;
        Collections.shuffle(server.getPlayers());
        for(LightPlayer player: server.getPlayers())
        {
            player.setAlive();
            player.setX(respawns[index][0]);
            player.setY(respawns[index][1]);
            player.setXLast(respawns[index][0]);
            player.setYLast(respawns[index][1]);
            index++;
            if (index >= respawns.length)
                index = 0;
        }
    }

    private void addEventListeners()
    {
        Platform.runLater(() ->
        {

            canvas.setOnMouseMoved(event ->
            {

            });

            anchorPane.getScene().setOnKeyPressed(event ->
            {

            });

            anchorPane.getScene().setOnKeyReleased(event ->
            {

            });

            anchorPane.getScene().widthProperty().addListener((o, oldValue, newValue) ->
            {
                canvas.setWidth(newValue.doubleValue());
            });

            anchorPane.getScene().heightProperty().addListener((o, oldValue, newValue) ->
            {
                canvas.setHeight(newValue.doubleValue());
            });
        });
    }

    private void createMap()
    {
        float width = 1920;
        float height = 1027;

        this.obstacles = new ArrayList<>();
        this.obstacles.add(new Obstacle(100,100,280,280));
        this.obstacles.add(new Obstacle(width - 380,100,280,280));
        this.obstacles.add(new Obstacle(width - 380,height - 380,280,280));
        this.obstacles.add(new Obstacle(100,height - 380,280,280));

        this.obstacles.add(new Obstacle(0,height/2 - 30,450,60));
        this.obstacles.add(new Obstacle(width - 450,height/2 - 30,450,60));

        this.obstacles.add(new Obstacle(width / 2 - 150, 150,300,70));
        this.obstacles.add(new Obstacle(width / 2 - 150, height - 150 - 70,300,70));

        this.obstacles.add(new Obstacle(width / 2 - 150, height / 2 - 150,300,300));

        this.obstacles.add(new Obstacle(width / 2 - 35 - 350, 0,70,320));
        this.obstacles.add(new Obstacle(width / 2 - 35 + 350, 0,70,320));

        this.obstacles.add(new Obstacle(width / 2 - 35 - 350, height-320,70,320));
        this.obstacles.add(new Obstacle(width / 2 - 35 + 350, height-320,70,320));
    }

}
