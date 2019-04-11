# Minesweeper, ver. 1.3
A from-scratch Java implementation of Minesweeper, taking advantage of the java.awt.Graphics library. 
Class code can be found in [../src](../master/src)

**Version 1.0:** Minesweeper game, with only expert mode available, and no option to reset or create a new game.

**Version 1.1:** Ability to reset and create new game added, with all three difficulties available - beginner, 
intermediate, and advanced.

**Version 1.2:** False flags revealed upon loss. 

**Version 1.3:** Player can now receive a hint when requested. The player will either have a false flag revealed for 
them, if a false flag exists, or will have a flag placed in a spot that is most useful.

**Potential future features include:**
* Treat control+left-click as a right-click in order to accommodate single-button mouse laptops. 
* Change new game / reset options to dropdown menu to clean up interface. 
* Save/load game. 
* Game timer
* Scoring system
* Daily games


**Bugs to fix:**
* Resets are treated as a new game - the first move upon a reset will move mines. This should not happen.