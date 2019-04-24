# Minesweeper, ver. 0.8
A from-scratch Java implementation of Minesweeper, taking advantage of the java.awt.Graphics library. 
Class code can be found in [../src](../master/src)

**Version 0:** Minesweeper game, with only expert mode available, and no option to reset or create a new game.

**Version 0.1:** Ability to reset and create new game added, with all three difficulties available - beginner, 
intermediate, and advanced.

**Version 0.2:** False flags revealed upon loss. 

**Version 0.3:** Player can now receive a hint when requested. The player will either have a false flag revealed for 
them, if a false flag exists, or will have a flag placed in a spot that is most useful.

**Version 0.4:** Player can now use CTRL as a modifier key to change a left-click into a right-click, allowing them to 
lay flags with CTRL+left-click.

**Version 0.5:** Game timer added.

**Version 0.6:** It is now optional to have all bombs revealed upon a loss. If all bombs are revealed, 
it will no longer be possible to reset the game.
* **0.6.1:** Fixed bug where relocateMine will sometimes relocate a mine back to the 
same spot. 

**Version 0.7:** There is now a time penalty for using a hint. 

**Version 0.8:** Added mouseover dialogue for the hint button - warns the player they will incur a penalty of 30 
seconds if they use a hint.

**Potential future features include:**
* Add help button & screen that explains how to play the game. 
* Scoring system other than timer? 
* Deploy to web. 
* Use cookies? to save/load games. 
* Save high scores using cookies?
* Store daily games in a SQL server? 


**Bugs to fix:**
No current known bugs.