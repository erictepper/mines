import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


public class Mines implements MouseListener, ActionListener {
  private JFrame gameFrame;
  private Game gameInstance;
  private JButton newGameButton;
  private JButton instructionsButton;
  private JButton resetButton;
  private JButton beginnerButton;
  private JButton intermediateButton;
  private JButton expertButton;
  private JButton hintButton;
  private Timer gameTimer;
  private JButton revealMinesYesButton;
  private JButton revealMinesNoButton;

  private Mines() {
    gameFrame = new JFrame("Mines");
    gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    gameInstance = new Game();
    gameInstance.setSize(1000, 770);
    gameInstance.setBackground(Color.WHITE);
    gameInstance.addMouseListener(this);

    newGameButton = new JButton("NEW GAME");
    newGameButton.setFont(new Font("Courier New", Font.PLAIN, 16));
    newGameButton.setBounds(360, 120, 130, 40);
    newGameButton.addActionListener(this);
    newGameButton.setActionCommand("new_game_1");

    instructionsButton = new JButton("?");
    instructionsButton.setFont(new Font("Courier New", Font.PLAIN, 16));
    instructionsButton.setBounds(505, 120, 30, 40);
    instructionsButton.addActionListener(this);
    instructionsButton.setActionCommand("instructions_1");

    resetButton = new JButton("RESET");
    resetButton.setFont(new Font("Courier New", Font.PLAIN, 16));
    resetButton.setBounds(550, 120, 130, 40);
    resetButton.addActionListener(this);
    resetButton.setActionCommand("reset");

    hintButton = new JButton("HINT");
    hintButton.setFont(new Font("Courier New", Font.PLAIN, 16));
    hintButton.setBounds(310, 160, 130, 40);
    hintButton.addActionListener(this);
    hintButton.setActionCommand("hint");
    hintButton.addMouseListener(this);

    gameTimer = new Timer(1000, this);
    gameTimer.setActionCommand("timer");

    revealMinesYesButton = new JButton("Yes");
    revealMinesYesButton.setFont(new Font("Courier New", Font.PLAIN, 12));
    revealMinesYesButton.setBounds(585, 207, 50, 30);
    revealMinesYesButton.addActionListener(this);
    revealMinesYesButton.setActionCommand("reveal_yes");

    revealMinesNoButton = new JButton("No");
    revealMinesNoButton.setFont(new Font("Courier New", Font.PLAIN, 12));
    revealMinesNoButton.setBounds(635, 207, 50, 30);
    revealMinesNoButton.addActionListener(this);
    revealMinesNoButton.setActionCommand("reveal_no");

    beginnerButton = new JButton("BEGINNER");
    beginnerButton.setFont(new Font("Courier New", Font.PLAIN, 16));
    beginnerButton.setBounds(185, 340, 130, 40);

    intermediateButton = new JButton("INTERMEDIATE");
    intermediateButton.setFont(new Font("Courier New", Font.PLAIN, 16));
    intermediateButton.setBounds(425, 340, 150, 40);

    expertButton = new JButton("EXPERT");
    expertButton.setFont(new Font("Courier New", Font.PLAIN, 16));
    expertButton.setBounds(685, 340, 130, 40);


    gameFrame.add(newGameButton);
    gameFrame.add(instructionsButton);
    gameFrame.add(resetButton);
    gameFrame.add(hintButton);
    gameFrame.add(revealMinesYesButton);
    gameFrame.add(revealMinesNoButton);
    gameFrame.add(beginnerButton);
    gameFrame.add(intermediateButton);
    gameFrame.add(expertButton);
    gameFrame.add(gameInstance);
    gameFrame.setSize(1000, 770);
    gameFrame.setVisible(true);
    revealMinesYesButton.setVisible(false);
    revealMinesNoButton.setVisible(false);
    beginnerButton.setVisible(false);
    intermediateButton.setVisible(false);
    expertButton.setVisible(false);

    // Starts the game on the new game screen. 
    actionPerformed(new ActionEvent(newGameButton, 0,"new_game_1"));
  }

  public static void main(String[] args) {
    new Mines();
  }

  public void mouseClicked(MouseEvent e) { }

  // Handles the interaction of the mouse with the interface.
  public void mousePressed(MouseEvent e) {
    if (e.getSource().equals(hintButton)) { return; }
    int x = e.getX();
    int y = e.getY();
    if (!gameInstance.getGameStartedStatus() && gameInstance.inBounds(x, y)) { gameTimer.start(); }  // TODO: potentially move to Game.java
    int button = (e.isControlDown() && e.getButton() == 1) ? 3 : e.getButton();
    gameInstance.mousePressed(x, y, button);
    if (gameInstance.getGameStatus()) {
      gameInstance.removeMouseListener(this);
      gameTimer.stop();
      if (gameInstance.getGameLostStatus()) {
        gameInstance.showRevealDialogue();
        revealMinesYesButton.setVisible(true);
        revealMinesNoButton.setVisible(true);
        }
    }
    gameFrame.repaint();
  }

  public void mouseReleased(MouseEvent e) { }

  public void mouseEntered(MouseEvent e) {
    if (e.getSource().equals(hintButton)) {
      gameInstance.showHintPenaltyDialogue();
      // we also repaint the gameFrame, otherwise the buttons disappear.
      gameFrame.repaint();
    }
  }

    public void mouseExited(MouseEvent e) {
    if (e.getSource().equals(hintButton)) {
      gameInstance.hideHintPenaltyDialogue();
      // we also repaint the gameFrame, otherwise the buttons disappear.
      gameFrame.repaint();
      }
  }

  public void actionPerformed(ActionEvent e) {
    switch (e.getActionCommand()) {
      case "timer":
        gameInstance.timerTick();
        gameFrame.repaint();
        break;
      case "hint":
        if (!gameInstance.getGameStatus() && !gameInstance.isGridHidden()) {
          gameInstance.giveHint();
          gameFrame.repaint();
        }
        break;
      case "reveal_yes":
        gameInstance.revealAllMines();
        resetButton.setEnabled(false);
        gameInstance.hideRevealDialogue();
        revealMinesYesButton.setVisible(false);
        revealMinesNoButton.setVisible(false);
        gameFrame.repaint();
        break;
      case "reveal_no":
        gameInstance.hideRevealDialogue();
        revealMinesYesButton.setVisible(false);
        revealMinesNoButton.setVisible(false);
        break;
      case "reset":
        gameInstance.reset();
        gameInstance.removeMouseListener(this);
        gameInstance.addMouseListener(this);
        if (!gameInstance.getGameStatus()) {
          if (gameInstance.getGameStartedStatus()) { gameTimer.start(); }
        }

        beginnerButton.setVisible(false);
        beginnerButton.removeActionListener(this);
        intermediateButton.setVisible(false);
        intermediateButton.removeActionListener(this);
        expertButton.setVisible(false);
        expertButton.removeActionListener(this);
        hintButton.removeActionListener(this);
        hintButton.addActionListener(this);
        newGameButton.setActionCommand("new_game_1");
        instructionsButton.setActionCommand("instructions_1");

        resetButton.setEnabled(true);
        gameInstance.hideRevealDialogue();
        revealMinesYesButton.setVisible(false);
        revealMinesNoButton.setVisible(false);
        gameInstance.hideGameInstructions();

        gameFrame.repaint();
        break;
      case "new_game_1":
        gameInstance.hideGrid();
        gameInstance.removeMouseListener(this);
        gameTimer.stop();

        beginnerButton.removeActionListener(this);
        beginnerButton.addActionListener(this);
        beginnerButton.setActionCommand("beginner");
        beginnerButton.setVisible(true);
        intermediateButton.removeActionListener(this);
        intermediateButton.addActionListener(this);
        intermediateButton.setActionCommand("intermediate");
        intermediateButton.setVisible(true);
        expertButton.removeActionListener(this);
        expertButton.addActionListener(this);
        expertButton.setActionCommand("expert");
        expertButton.setVisible(true);
        hintButton.removeActionListener(this);
        newGameButton.setActionCommand("new_game_2");
        instructionsButton.setActionCommand("instructions_1");
        gameInstance.hideGameInstructions();

        gameFrame.repaint();
        break;
      case "new_game_2":
        gameInstance.showGrid();
        if (!gameInstance.getGameStatus()) {
          gameInstance.addMouseListener(this);
          if (gameInstance.getGameStartedStatus()) { gameTimer.start(); }
        }

        beginnerButton.setVisible(false);
        beginnerButton.removeActionListener(this);
        intermediateButton.setVisible(false);
        intermediateButton.removeActionListener(this);
        expertButton.setVisible(false);
        expertButton.removeActionListener(this);
        hintButton.addActionListener(this);
        newGameButton.setActionCommand("new_game_1");
        gameInstance.hideGameInstructions();

        gameFrame.repaint();
        break;
      case "beginner":
        gameInstance.newGame("beginner");
        gameInstance.addMouseListener(this);

        beginnerButton.setVisible(false);
        beginnerButton.removeActionListener(this);
        intermediateButton.setVisible(false);
        intermediateButton.removeActionListener(this);
        expertButton.setVisible(false);
        expertButton.removeActionListener(this);
        hintButton.addActionListener(this);
        newGameButton.setActionCommand("new_game_1");
        instructionsButton.setActionCommand("instructions_1");

        resetButton.setEnabled(true);
        gameInstance.hideRevealDialogue();
        revealMinesYesButton.setVisible(false);
        revealMinesNoButton.setVisible(false);
        gameInstance.hideGameInstructions();

        gameFrame.repaint();
        break;
      case "intermediate":
        gameInstance.newGame("intermediate");
        gameInstance.addMouseListener(this);

        beginnerButton.setVisible(false);
        beginnerButton.removeActionListener(this);
        intermediateButton.setVisible(false);
        intermediateButton.removeActionListener(this);
        expertButton.setVisible(false);
        expertButton.removeActionListener(this);
        hintButton.addActionListener(this);
        newGameButton.setActionCommand("new_game_1");
        instructionsButton.setActionCommand("instructions_1");

        resetButton.setEnabled(true);
        gameInstance.hideRevealDialogue();
        revealMinesYesButton.setVisible(false);
        revealMinesNoButton.setVisible(false);
        gameInstance.hideGameInstructions();

        gameFrame.repaint();
        break;
      case "expert":
        gameInstance.newGame("expert");
        gameInstance.addMouseListener(this);

        beginnerButton.setVisible(false);
        beginnerButton.removeActionListener(this);
        intermediateButton.setVisible(false);
        intermediateButton.removeActionListener(this);
        expertButton.setVisible(false);
        expertButton.removeActionListener(this);
        hintButton.addActionListener(this);
        newGameButton.setActionCommand("new_game_1");
        instructionsButton.setActionCommand("instructions_1");

        resetButton.setEnabled(true);
        gameInstance.hideRevealDialogue();
        revealMinesYesButton.setVisible(false);
        revealMinesNoButton.setVisible(false);
        gameInstance.hideGameInstructions();

        gameFrame.repaint();
        break;
      case "instructions_1":
        gameInstance.hideGrid();
        gameInstance.showGameInstructions();
        gameInstance.removeMouseListener(this);
        gameTimer.stop();

        beginnerButton.setVisible(false);
        beginnerButton.removeActionListener(this);
        intermediateButton.setVisible(false);
        intermediateButton.removeActionListener(this);
        expertButton.setVisible(false);
        expertButton.removeActionListener(this);
        hintButton.removeActionListener(this);
        newGameButton.setActionCommand("new_game_1");
        instructionsButton.setActionCommand("instructions_2");

        gameFrame.repaint();
        break;
      case "instructions_2":
        gameInstance.showGrid();
        gameInstance.hideGameInstructions();
        if (!gameInstance.getGameStatus()) {
          gameInstance.removeMouseListener(this);
          gameInstance.addMouseListener(this);
          if (gameInstance.getGameStartedStatus()) { gameTimer.start(); }
        }

        hintButton.addActionListener(this);
        instructionsButton.setActionCommand("instructions_1");

        gameFrame.repaint();
    }
  }
}
