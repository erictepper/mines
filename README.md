# Minesweeper
A from-scratch Java implementation of Minesweeper, taking advantage of the java.awt.Graphics library. 
Class code can be found in [../src](../master/src)

**Potential future features include:**
* Press a button to receive a hint if player becomes stuck. 
* Change new game / reset options to dropdown menu to clean up interface. 
* Save/load game. 
* Scoring system
* Daily games

**Version 1.0:** Minesweeper game, with only expert mode available, and no option to reset or create a new game.

**Version 1.1:** Ability to reset and create new game added, with all three difficulties available - beginner, 
intermediate, and advanced.

**Version 1.2:** False flags revealed upon loss. 

**Version 1.3:** Player can now receive a hint when requested. The player will either have a false flag revealed for 
them, if a false flag exists, or will have a flag placed in a spot that is most useful.