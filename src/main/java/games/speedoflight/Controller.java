package games.speedoflight;

import core.server.GameServer;
import games.speedoflight.environmemt.Map;
import games.speedoflight.environmemt.MapEntity;
import games.speedoflight.environmemt.SpawnPoint;
import javafx.animation.AnimationTimer;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Controller
{
    private final int ROUND_BEGIN_COUNTDOWN = 10;

    @FXML private Canvas canvas;
    @FXML private AnchorPane anchorPane;

    private Map map;
    private Camera camera;
    private GameServer<LightPlayer> server;
    private ArrayList<Bullet> bullets;

    private boolean gameStarted;
    private boolean roundOver;
    private float animationCounter;

    private int countDown = 10;
    private long countDownTimer;

    private int fps;
    private int fpsCounter;
    private long fpsTimer;

    public static boolean enable = false;


    @FXML
    public void initialize()
    {
        addEventListeners();
        startGameLoop();
        openServer();

        this.map = new Map();
        this.camera = new Camera(canvas, map.getCenterX(), map.getCenterY());
        this.camera.setTarget(map.getCenterX(), map.getCenterY());
        this.bullets = new ArrayList<>();
        this.countDown = ROUND_BEGIN_COUNTDOWN;
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

    private void openServer()
    {
        this.server = new GameServer<>(LightPlayer.class);
        this.server.setOnNewPlayerConnection(player ->
        {
            SpawnPoint point = map.getNextSpawnPoint();
            player.setX(point.getX());
            player.setY(point.getY());
            player.setXLast(point.getX());
            player.setYLast(point.getY());
        });
        this.server.start(55500);
    }

    private void updateGame()
    {
        updateGameState();

        if(gameStarted)
        {
            server.getPlayers().forEach(player ->
            {
                player.update(bullets);
                resolveCollisions();
            });

            for(int i = 0; i < bullets.size() && bullets.size() > 0; i++)
            {
                Bullet b = bullets.get(i);
                b.update();

                for(MapEntity o : map.getMapEntities())
                    o.resolveBulletCollision(b);

                if(b.isDead())
                {
                    bullets.remove(b);
                    i--;
                }
            }
        }
    }

    private void updateGameState()
    {
        int playersAlive = 0;
        for(LightPlayer player: server.getPlayers())
            if(!player.isDead())
                playersAlive++;

        if(gameStarted)
        {
            if(playersAlive < 2 && server.getPlayers().size() > 1)
                roundOver = true;

            if(roundOver)
            {
                if(countDown == ROUND_BEGIN_COUNTDOWN)
                {
                    Collections.sort(server.getPlayers());
                    countDown--;
                    countDownTimer = System.currentTimeMillis();
                    server.allowNewConnections(true);
                }
                else if(countDown == 0)
                {
                    countDown = ROUND_BEGIN_COUNTDOWN;
                    roundOver = false;
                    server.allowNewConnections(false);
                    respawn();
                    map.reloadMap();
                }
                else if(System.currentTimeMillis() > countDownTimer + 1000)
                {
                    countDown--;
                    countDownTimer = System.currentTimeMillis();
                }
            }
        }
    }

    private void resolveCollisions()
    {
        List<LightPlayer> players = server.getPlayers();
        for (int  i = 0; i < players.size() - 1; i++)
            for (int j = i + 1; j < players.size(); j++)
                players.get(i).resolvePlayerCollision(players.get(j));

        for(MapEntity entity : map.getMapEntities())
            for(LightPlayer player : players)
                entity.resolvePlayerCollision(player);
    }

    private void renderGame()
    {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        camera.startCapture();
        map.draw(camera);

        for(Bullet b : bullets)
            b.draw(camera);

        for(LightPlayer player: server.getPlayers())
            player.draw(camera);

        camera.endCapture();
        camera.update();

        if(gameStarted)
        {
            camera.followAlivePlayers(server.getPlayers());

            if(roundOver)
                renderScoreBoard(gc);
        }
        else
        {
            renderStartScreen(gc);
            camera.setTarget(map.getCenterX(), map.getCenterY());
            camera.setTargetZoom(1.0f);
        }

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(20));
        gc.fillText(String.valueOf(fps), 10, 20);

        fpsCounter++;
        if(System.currentTimeMillis() > fpsTimer + 1000)
        {
            fps = fpsCounter;
            fpsCounter = 0;
            fpsTimer = System.currentTimeMillis();
        }

        animationCounter = (animationCounter + 1) % 256;
    }

    private void renderStartScreen(GraphicsContext gc)
    {
        List<LightPlayer> players = server.getPlayers();

        float xCenter = (float) canvas.getWidth() / 2;

        gc.setFill(Color.color(0f,0f,0f,0.6));
        gc.fillRect(0,0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);

        gc.setFont(Font.font(40));
        gc.fillText("PLAYERS (" + players.size() + ")", xCenter, canvas.getHeight() * 0.2);

        int index = 1;
        for (LightPlayer player : players)
        {
            float fontSize = 30 + 2 * (float)Math.sin((animationCounter / 256) * Math.PI * 2 + index * (Math.PI));
            gc.setFont(Font.font(fontSize));
            gc.fillText(player.getName(), xCenter, canvas.getHeight() * 0.2 + 50 * index);
            index++;
        }

        float fontSize = 30 + 5 * (float)Math.sin((animationCounter / 256) * Math.PI * 2);
        gc.setFont(Font.font(fontSize));
        gc.fillText("PRESS SPACE TO START GAME", xCenter, canvas.getHeight() * 0.9);
    }

    private void renderScoreBoard(GraphicsContext gc)
    {
        gc.setFill(Color.color(0f,0f,0f,0.8));
        gc.fillRect(0,0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font(40));
        gc.fillText("--------- SCOREBOARD ---------", canvas.getWidth() / 2, canvas.getHeight() * 0.2);

        int index = 1;
        for(LightPlayer player: server.getPlayers())
        {
            gc.setFont(Font.font(30));
            gc.fillText(player.getName() + ":    " +
                    player.getKills() + "  /  " + player.getDeaths(),
                    canvas.getWidth() / 2, canvas.getHeight() * 0.2 + 50 * index);
            index++;
        }

        gc.setFont(Font.font(30));
        gc.fillText("NEW ROUND BEGINS IN", canvas.getWidth() / 2, canvas.getHeight() * 0.8);
        gc.setFont(Font.font(60));
        gc.fillText(String.valueOf(countDown), canvas.getWidth() / 2, canvas.getHeight() * 0.9);
    }

    public void respawn()
    {
        Collections.shuffle(server.getPlayers());
        for(LightPlayer player: server.getPlayers())
        {
            player.setAlive();
            SpawnPoint point = map.getNextSpawnPoint();
            player.setX(point.getX());
            player.setY(point.getY());
            player.setXLast(point.getX());
            player.setYLast(point.getY());
        }
    }

    private void addEventListeners()
    {
        Platform.runLater(() ->
        {
            canvas.setOnMouseMoved(event ->
            {

            });

            anchorPane.getScene().setOnKeyTyped(event -> {

            });

            anchorPane.getScene().setOnKeyPressed(event ->
            {
                if(event.getCode() == KeyCode.SPACE)
                {
                    if(!gameStarted && server.getPlayers().size() > 0)
                    {
                        gameStarted = true;
                        server.allowNewConnections(false);
                    }
                }
                else if (event.getCode() == KeyCode.ESCAPE)
                {
                    if(gameStarted)
                    {
                        gameStarted = false;
                        server.allowNewConnections(true);
                    }
                }
                else if(event.getCode() == KeyCode.E)
                    enable = true;
            });

            anchorPane.getScene().setOnKeyReleased(event ->
            {
                if(event.isControlDown() && event.getCode() == KeyCode.E)
                    enable = false;
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
}
