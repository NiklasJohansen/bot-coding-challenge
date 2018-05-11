# Bot Coding Challenge
Code your own bot and compete among friends in fun and challenging games!

### How it works
Each challenge is based around one game. The game will have controllable players, and these players 
will need some sort of guidance. This is your job! Create a program that provides your player
with instructions of what to do. Smart instructions and wise decisions yield more points, and more 
points is usually a good thing in terms of winning games!

The program you write will interact with the game through a server/client connection. The server
broadcasts game relevant data to all connected players, and the players responds back with instructions. 
The game runs until one player/team wins it all.

### How do I start coding a bot?
You start by navigating to the folder of games (src/main/java/games). Each folder will contain
a README with further information about how to interface with that particular game. All games will
ship with code to a basic example bot, making it easy for everyone to get started. Just clone down 
the repository and continue coding on the supplied example bot. Example code will be given in the 
following languages:

* JavaScript
* Java

You are of course welcome to use whatever programming language you feel comfortable with. The only 
requirement is the support of a TCP socket connection.

### Organize a social coding challenge
To make this fun, gather your coding friends and let the game run on a big screen. Participants are 
presented with a chosen game from the library and given time to code their bot. When the participants 
are done coding and ready for battle, they all connect to the game from their own computer. This 
eliminates the hassle of trying to compile each and everyone's program to run on your poor machine. 
The bots run on the machines they were coded on and everyone is happy!

### Contribute
If you have fun ideas to new or existing games and want to contribute to the project, please feel free to 
fork this repository and create a new pull request! I would gladly review new contributions and merge
them in.

### Game library
Not much to see here yet, but take a look at the HelloWorld example project to see how communication
between a player and the server is working.


