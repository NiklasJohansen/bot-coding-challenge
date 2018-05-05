# Hello World!

This project shows the small amount of code you have to write in order to get 
communication going between the server and its clients.

 ## Server
 Setting up a simple server requires few lines of code. Firstly a GameServer object 
 will have to be instantiated and given a port number. Then a response class 
 is defined. This class will contain the data fields expected to be sent from the clients.
 Lastly the server can be started.
 
``` java
 GameServer server = new GameServer(55500);
 server.setClientResponseClass(ClientResponse.class);
 server.start();
```
 
The ClientResponse class for this example looks like this:
 
``` java
public class ClientResponse 
{
    public String clientName;
    public String message;
}
```
 
The server will now accept new connections and receive responses from its connected 
clients. But we have to define some behaviour in order for any communication to be going back 
and forth between the connected players and the server.

``` java
server.setGameLoop(1, () ->
{
    GameState gameState = new GameState();
    gameState.serverName = "GameServer";
    gameState.timestamp = new Date().toString();
    server.broadcast(gameState);
    ...
});
```

The GameServer has built in game loop functionality. The above code loops every second and 
broadcasts a GameState object to all players. What data the GameState object should contain will
depend on each game and what is relevant for the players to know. In this example the 
servers name and a timestamp is used. In a real game this could be a map, information about other
players, game entities, etc. NOTE that starting the server will start the game loop to, so this 
should be set before calling the servers start method.

``` java
public class GameState
{
    public String serverName;
    public String timestamp;
}
```

When a player has received the game state and responded with the defined ClientResponse object,
that objects data can be accessed like shown below. A players data will be
null if no response has been received.

``` java
for(Player player : server.getPlayers())
{
    ClientResponse response = (ClientResponse) player.getPlayerData();
    if(response != null)
        System.out.println("[" + response.clientName + "]: " + response.message);
}
```
 
 A game may want to keep track of when players connect or disconnect. This behaviour can be
 defined as follows.
 
``` java
server.setOnNewPlayerConnection(player -> 
{
    // ...
});

server.setOnPlayerDisconnect(player -> 
{
    // ...
});
```

 ## Client

The client code can be written in many languages and is not required to use the any of the 
functionality available in the core library. As an example, a simple client in JavaScript can be
coded like shown bellow. NOTE that connecting to the server with a web socket is different from 
regular sockets, and the port number is by rule one higher than defined in the server code.

``` js
var connection = new WebSocket('ws://127.0.0.1:55501');

connection.onmessage = function(event) 
{
    var gameState = JSON.parse(event.data);
    
    console.log("[" + gameState.serverName + "]: " + gameState.timestamp);
    
    connection.send(JSON.stringify(
    {
        clientName: "JS_Client",
        message: "Hello " + gameState.serverName + "!"
    }));
}
```

If you have cloned down the games source code, writing a bot in Java requires very few steps. 
By using the games defined GameState and ClientResponse classes directly, setting up a connection
can be done like this:

``` java
import core.client.GameClient;
import games.HelloWorld.HelloWorldServer.ClientResponse;
import games.HelloWorld.HelloWorldServer.GameState;

...

GameClient client = new GameClient("127.0.0.1", 55500);
client.setOnServerUpdate(GameState.class, gameState ->
{
    System.out.println("[" + gameState.serverName + "]: " + gameState.timestamp);

    ClientResponse response = new ClientResponse();
    response.clientName = "Java_Client";
    response.message = "Hello " + gameState.serverName + "!";
    client.send(response);
});
```

A client can of course be written in Java without using parts of the source code. 
The HelloWorldClientStandalone class in the *example* folder shows how this can be done. 




