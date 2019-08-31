# mines

### General Info
Version 0.10.0

A from-scratch Java clone of Minesweeper, taking advantage of the java.awt.Graphics library. 
Class code can be found in [src/main/java](../master/src/main/java).

This project requires Maven 3.6.1 to build and can be built by running `mvn clean package` from the command line. This 
will build the project in the `target/` directory, and the application can then be run from the `mines-0.10.jar` 
java executable. 

### Versions
**Version 0:** Mines game, with only expert mode available, and no option to reset or create a new game.

**Version 0.1:** Ability to reset and create new game added, with all three difficulties available - beginner, 
intermediate, and advanced.

**Version 0.2:** False flags revealed upon loss. 

**Version 0.3:** Player can now receive a hint when requested. The player will either have a false flag revealed for 
them, if a false flag exists, or will have a flag placed in a spot that is most useful.

**Version 0.4:** Player can now use CTRL as a modifier key to change a left-click into a right-click, allowing them to 
lay flags with CTRL+left-click.

**Version 0.5:** Game timer added.

**Version 0.6:** It is now optional to have all mines revealed upon a loss. If all mines are revealed, 
it will no longer be possible to reset the game.
* **0.6.1:** Fixed bug where relocateMine will sometimes relocate a mine back to the 
same spot. 

**Version 0.7:** There is now a time penalty for using a hint. 

**Version 0.8:** Added mouseover dialogue for the hint button - warns the player they will incur a penalty of 30 
seconds if they use a hint.
* **0.8.1:** Fixed bug where game will begin on a mouse press that is outside of the game grid.
* **0.8.2:** Hint penalty dialogue will no longer display when game is not in play (e.g. before it starts, after it 
ends, or when the board is hidden).

**Version 0.9:** Added help button & screen that explains how to play the game. Fixed formatting to conform to Google's
style guide at [https://google.github.io/styleguide/javaguide.html](https://google.github.io/styleguide/javaguide.html).

**Version 0.10:** Minesweeper will have the option to run from Java Web Start so it can be hosted on the web.  

### To-do
###### Potential future features include: 
* Add "click anywhere to begin" text to game screen before game start.
* Create alternative for when mine and flag images cannot be found.
* Scoring system other than timer? 
* Host downloadable .jar online. 
* Use cookies? to save/load games. 
* Save high scores using cookies?
* Store daily games in a SQL server? 


###### Bugs to fix:
* No current known bugs. 