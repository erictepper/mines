# Minesweeper, ver. 0.4
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

**Potential future features include:**
* Make the reveal of all bombs upon loss of the game optional, and disable ability to reset the game if bombs have 
  been revealed.
* Save/load game. 
* Game timer
* Scoring system
* Daily games


**Bugs to fix:**
No current bugs. 