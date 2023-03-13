# Azul

## Description
This project's main goal was to create a fully functional implementation of a board game "[Azul]([https://www.youtube.com/watch?v=7ygt6qM5WdY](https://boardgamegeek.com/boardgame/230802/azul))".
Made as an university project.

Made for 2-4 players, playable both locally and over LAN.\
Games are started via specific request sent from user(s) to the server running on a port specified in properties file.\
Players can register/login to their account and start the game. After connecting to the server a lobby can be created. Other users can join it by entering a unique 5-digit code generated for the lobby, seen by the game's creator.\
A game can be saved and resumed later by the user that created it. 

Although full game-logic has already been implemented and the whole project is functional, it *might* be updated in the future - we'll most likely focus on GUI.

## Technologies
Written in Java, with network features using java.net API\
GUI made in JavaFX\
Javadoc

## Screenshots
Main menu

![start](https://user-images.githubusercontent.com/92323233/224170476-79a72fb0-7c16-4efb-acf2-a182c5bf8fb4.png "Main menu")

Connecting

![connect](https://user-images.githubusercontent.com/92323233/224170715-25a125c4-f74d-41ff-b0b6-63826b3ce644.png "Connecting")

Login Screen

![login](https://user-images.githubusercontent.com/92323233/224170798-c93beac1-a1d3-4c21-97a0-4665a30ea687.png "Login Screen")

Game Connection Screen

![host](https://user-images.githubusercontent.com/92323233/224170876-aaf329cf-5db3-4981-ac2b-5fcc2712efe7.png "Game Connection Screen")

Main Board View

![game](https://user-images.githubusercontent.com/92323233/224171059-1aed527f-4064-4b01-ab74-7a7f19d146d6.png "Board View")

Board View After Few Rounds

![game1](https://user-images.githubusercontent.com/92323233/224171167-e6761f92-9abc-4d34-b4c1-6958e2512ef2.png "Board View")




## How to start
* Clone the repository
* Run the server localy by running Server.java (default port 4000 can be changed in server.properties)
* Run game app by runnig Azul.java
* Connect to the server

## Credits
* Miłosz Kutyła ([GitHub](https://github.com/mkutyla))
* Jakub Ossowski ([GitHub](https://github.com/bilevcik))
* Aleksandra Michalska
* Patryk Ogonowski
