import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


public class Minesweeper implements MouseListener, ActionListener {
    private JFrame GAME_FRAME;
    private Game GAME_INSTANCE;
    private JButton NEW_GAME_BUTTON;
    private JButton INSTRUCTIONS_BUTTON;
    private JButton RESET_BUTTON;
    private JButton BEGINNER_BUTTON;
    private JButton INTERMEDIATE_BUTTON;
    private JButton EXPERT_BUTTON;
    private JButton HINT;
    private Timer GAME_TIMER;
    private JButton REVEAL_MINES_YES;
    private JButton REVEAL_MINES_NO;

    private Minesweeper() {
        GAME_FRAME = new JFrame("Minesweeper");
        GAME_FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GAME_INSTANCE = new Game();
        GAME_INSTANCE.setSize(1000, 770);
        GAME_INSTANCE.setBackground(Color.WHITE);
        GAME_INSTANCE.addMouseListener(this);

        NEW_GAME_BUTTON = new JButton("NEW GAME");
        NEW_GAME_BUTTON.setFont(new Font("Courier New", Font.PLAIN, 16));
        NEW_GAME_BUTTON.setBounds(360, 120, 130, 40);
        NEW_GAME_BUTTON.addActionListener(this);
        NEW_GAME_BUTTON.setActionCommand("new_game_1");

        INSTRUCTIONS_BUTTON = new JButton("?");
        INSTRUCTIONS_BUTTON.setFont(new Font("Courier New", Font.PLAIN, 16));
        INSTRUCTIONS_BUTTON.setBounds(505, 120, 30, 40);
        INSTRUCTIONS_BUTTON.addActionListener(this);
        INSTRUCTIONS_BUTTON.setActionCommand("instructions_1");

        RESET_BUTTON = new JButton("RESET");
        RESET_BUTTON.setFont(new Font("Courier New", Font.PLAIN, 16));
        RESET_BUTTON.setBounds(550, 120, 130, 40);
        RESET_BUTTON.addActionListener(this);
        RESET_BUTTON.setActionCommand("reset");

        HINT = new JButton("HINT");
        HINT.setFont(new Font("Courier New", Font.PLAIN, 16));
        HINT.setBounds(310, 160, 130, 40);
        HINT.addActionListener(this);
        HINT.setActionCommand("hint");
        HINT.addMouseListener(this);

        GAME_TIMER = new Timer(1000, this);
        GAME_TIMER.setActionCommand("timer");

        REVEAL_MINES_YES = new JButton("Yes");
        REVEAL_MINES_YES.setFont(new Font("Courier New", Font.PLAIN, 12));
        REVEAL_MINES_YES.setBounds(585, 207, 50, 30);
        REVEAL_MINES_YES.addActionListener(this);
        REVEAL_MINES_YES.setActionCommand("reveal_yes");

        REVEAL_MINES_NO = new JButton("No");
        REVEAL_MINES_NO.setFont(new Font("Courier New", Font.PLAIN, 12));
        REVEAL_MINES_NO.setBounds(635, 207, 50, 30);
        REVEAL_MINES_NO.addActionListener(this);
        REVEAL_MINES_NO.setActionCommand("reveal_no");

        BEGINNER_BUTTON = new JButton("BEGINNER");
        BEGINNER_BUTTON.setFont(new Font("Courier New", Font.PLAIN, 16));
        BEGINNER_BUTTON.setBounds(185, 340, 130, 40);

        INTERMEDIATE_BUTTON = new JButton("INTERMEDIATE");
        INTERMEDIATE_BUTTON.setFont(new Font("Courier New", Font.PLAIN, 16));
        INTERMEDIATE_BUTTON.setBounds(425, 340, 150, 40);

        EXPERT_BUTTON = new JButton("EXPERT");
        EXPERT_BUTTON.setFont(new Font("Courier New", Font.PLAIN, 16));
        EXPERT_BUTTON.setBounds(685, 340, 130, 40);


        GAME_FRAME.add(NEW_GAME_BUTTON);
        GAME_FRAME.add(INSTRUCTIONS_BUTTON);
        GAME_FRAME.add(RESET_BUTTON);
        GAME_FRAME.add(HINT);
        GAME_FRAME.add(REVEAL_MINES_YES);
        GAME_FRAME.add(REVEAL_MINES_NO);
        GAME_FRAME.add(BEGINNER_BUTTON);
        GAME_FRAME.add(INTERMEDIATE_BUTTON);
        GAME_FRAME.add(EXPERT_BUTTON);
        GAME_FRAME.add(GAME_INSTANCE);
        GAME_FRAME.setSize(1000, 770);
        GAME_FRAME.setVisible(true);
        REVEAL_MINES_YES.setVisible(false);
        REVEAL_MINES_NO.setVisible(false);
        BEGINNER_BUTTON.setVisible(false);
        INTERMEDIATE_BUTTON.setVisible(false);
        EXPERT_BUTTON.setVisible(false);
    }

    public static void main(String[] args) {
        new Minesweeper();
    }

    public void mouseClicked(MouseEvent e) { }

    // Handles the interaction of the mouse with the interface.
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (!GAME_INSTANCE.getGameStartedStatus() && GAME_INSTANCE.inBounds(x, y)) { GAME_TIMER.start(); }  // TODO: potentially move to Game.java
        int button = (e.isControlDown() && e.getButton() == 1) ? 3 : e.getButton();
        GAME_INSTANCE.mousePressed(x, y, button);
        if (GAME_INSTANCE.getGameStatus()) {
            GAME_INSTANCE.removeMouseListener(this);
            GAME_TIMER.stop();
            if (GAME_INSTANCE.getGameLostStatus()) {
                GAME_INSTANCE.showRevealDialogue();
                REVEAL_MINES_YES.setVisible(true);
                REVEAL_MINES_NO.setVisible(true);
            }
        }
        GAME_FRAME.repaint();
    }

    public void mouseReleased(MouseEvent e) { }

    public void mouseEntered(MouseEvent e) {
        if (e.getSource().equals(HINT)) {
            GAME_INSTANCE.showHintPenaltyDialogue();
            // we also repaint the GAME_FRAME, otherwise the buttons disappear.
            GAME_FRAME.repaint();
        }
    }

    public void mouseExited(MouseEvent e) {
        if (e.getSource().equals(HINT)) {
            GAME_INSTANCE.hideHintPenaltyDialogue();
            // we also repaint the GAME_FRAME, otherwise the buttons disappear.
            GAME_FRAME.repaint();
        }
    }

    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "timer":
                GAME_INSTANCE.timerTick();
                GAME_FRAME.repaint();
                break;
            case "hint":
                if (!GAME_INSTANCE.getGameStatus() && !GAME_INSTANCE.isGridHidden()) {
                    GAME_INSTANCE.giveHint();
                    GAME_FRAME.repaint();
                }
                break;
            case "reveal_yes":
                GAME_INSTANCE.revealAllMines();
                RESET_BUTTON.setEnabled(false);
                GAME_INSTANCE.hideRevealDialogue();
                REVEAL_MINES_YES.setVisible(false);
                REVEAL_MINES_NO.setVisible(false);
                GAME_FRAME.repaint();
                break;
            case "reveal_no":
                GAME_INSTANCE.hideRevealDialogue();
                REVEAL_MINES_YES.setVisible(false);
                REVEAL_MINES_NO.setVisible(false);
                break;
            case "reset":
                GAME_INSTANCE.reset();
                GAME_INSTANCE.removeMouseListener(this);
                GAME_INSTANCE.addMouseListener(this);
                GAME_TIMER.start();

                BEGINNER_BUTTON.setVisible(false);
                BEGINNER_BUTTON.removeActionListener(this);
                INTERMEDIATE_BUTTON.setVisible(false);
                INTERMEDIATE_BUTTON.removeActionListener(this);
                EXPERT_BUTTON.setVisible(false);
                EXPERT_BUTTON.removeActionListener(this);
                HINT.removeActionListener(this);
                HINT.addActionListener(this);
                NEW_GAME_BUTTON.setActionCommand("new_game_1");

                RESET_BUTTON.setEnabled(true);
                GAME_INSTANCE.hideRevealDialogue();
                REVEAL_MINES_YES.setVisible(false);
                REVEAL_MINES_NO.setVisible(false);

                GAME_FRAME.repaint();
                break;
            case "new_game_1":
                GAME_INSTANCE.hideGrid();
                GAME_INSTANCE.removeMouseListener(this);
                GAME_TIMER.stop();

                BEGINNER_BUTTON.removeActionListener(this);
                BEGINNER_BUTTON.addActionListener(this);
                BEGINNER_BUTTON.setActionCommand("beginner");
                BEGINNER_BUTTON.setVisible(true);
                INTERMEDIATE_BUTTON.removeActionListener(this);
                INTERMEDIATE_BUTTON.addActionListener(this);
                INTERMEDIATE_BUTTON.setActionCommand("intermediate");
                INTERMEDIATE_BUTTON.setVisible(true);
                EXPERT_BUTTON.removeActionListener(this);
                EXPERT_BUTTON.addActionListener(this);
                EXPERT_BUTTON.setActionCommand("expert");
                EXPERT_BUTTON.setVisible(true);
                HINT.removeActionListener(this);
                NEW_GAME_BUTTON.setActionCommand("new_game_2");

                GAME_FRAME.repaint();
                break;
            case "new_game_2":
                GAME_INSTANCE.showGrid();
                if (!GAME_INSTANCE.getGameStatus()) {
                    GAME_INSTANCE.addMouseListener(this);
                    if (GAME_INSTANCE.getGameStartedStatus()) { GAME_TIMER.start(); }
                }

                BEGINNER_BUTTON.setVisible(false);
                BEGINNER_BUTTON.removeActionListener(this);
                INTERMEDIATE_BUTTON.setVisible(false);
                INTERMEDIATE_BUTTON.removeActionListener(this);
                EXPERT_BUTTON.setVisible(false);
                EXPERT_BUTTON.removeActionListener(this);
                HINT.addActionListener(this);
                NEW_GAME_BUTTON.setActionCommand("new_game_1");

                GAME_FRAME.repaint();
                break;
            case "beginner":
                GAME_INSTANCE.newGame("beginner");
                GAME_INSTANCE.addMouseListener(this);

                BEGINNER_BUTTON.setVisible(false);
                BEGINNER_BUTTON.removeActionListener(this);
                INTERMEDIATE_BUTTON.setVisible(false);
                INTERMEDIATE_BUTTON.removeActionListener(this);
                EXPERT_BUTTON.setVisible(false);
                EXPERT_BUTTON.removeActionListener(this);
                HINT.addActionListener(this);
                NEW_GAME_BUTTON.setActionCommand("new_game_1");

                RESET_BUTTON.setEnabled(true);
                GAME_INSTANCE.hideRevealDialogue();
                REVEAL_MINES_YES.setVisible(false);
                REVEAL_MINES_NO.setVisible(false);

                GAME_FRAME.repaint();
                break;
            case "intermediate":
                GAME_INSTANCE.newGame("intermediate");
                GAME_INSTANCE.addMouseListener(this);

                BEGINNER_BUTTON.setVisible(false);
                BEGINNER_BUTTON.removeActionListener(this);
                INTERMEDIATE_BUTTON.setVisible(false);
                INTERMEDIATE_BUTTON.removeActionListener(this);
                EXPERT_BUTTON.setVisible(false);
                EXPERT_BUTTON.removeActionListener(this);
                HINT.addActionListener(this);
                NEW_GAME_BUTTON.setActionCommand("new_game_1");

                RESET_BUTTON.setEnabled(true);
                GAME_INSTANCE.hideRevealDialogue();
                REVEAL_MINES_YES.setVisible(false);
                REVEAL_MINES_NO.setVisible(false);

                GAME_FRAME.repaint();
                break;
            case "expert":
                GAME_INSTANCE.newGame("expert");
                GAME_INSTANCE.addMouseListener(this);

                BEGINNER_BUTTON.setVisible(false);
                BEGINNER_BUTTON.removeActionListener(this);
                INTERMEDIATE_BUTTON.setVisible(false);
                INTERMEDIATE_BUTTON.removeActionListener(this);
                EXPERT_BUTTON.setVisible(false);
                EXPERT_BUTTON.removeActionListener(this);
                HINT.addActionListener(this);
                NEW_GAME_BUTTON.setActionCommand("new_game_1");

                RESET_BUTTON.setEnabled(true);
                GAME_INSTANCE.hideRevealDialogue();
                REVEAL_MINES_YES.setVisible(false);
                REVEAL_MINES_NO.setVisible(false);

                GAME_FRAME.repaint();
                break;
        }
    }
}
