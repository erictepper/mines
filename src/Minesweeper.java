import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


public class Minesweeper implements MouseListener, ActionListener {
    private JFrame GAME_FRAME;
    private GameView VIEW;
    private JButton BEGINNER_BUTTON;
    private JButton INTERMEDIATE_BUTTON;
    private JButton EXPERT_BUTTON;

    private Minesweeper() {
        GAME_FRAME = new JFrame("Minesweeper");
        GAME_FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        VIEW = new GameView();
        VIEW.setSize(1000, 730);
        VIEW.setBackground(Color.WHITE);
        VIEW.addMouseListener(this);

        JButton new_game_button = new JButton("NEW GAME");
        new_game_button.setFont(new Font("Courier New", Font.PLAIN, 16));
        new_game_button.setBounds(310, 120, 130, 40);
        new_game_button.addActionListener(this);
        new_game_button.setActionCommand("new_game");

        JButton reset = new JButton("RESET");
        reset.setFont(new Font("Courier New", Font.PLAIN, 16));
        reset.setBounds(450, 120, 130, 40);
        reset.addActionListener(this);
        reset.setActionCommand("reset");

        BEGINNER_BUTTON = new JButton("BEGINNER");
        BEGINNER_BUTTON.setFont(new Font("Courier New", Font.PLAIN, 16));
        BEGINNER_BUTTON.setBounds(185, 300, 130, 40);

        INTERMEDIATE_BUTTON = new JButton("INTERMEDIATE");
        INTERMEDIATE_BUTTON.setFont(new Font("Courier New", Font.PLAIN, 16));
        INTERMEDIATE_BUTTON.setBounds(425, 300, 150, 40);

        EXPERT_BUTTON = new JButton("EXPERT");
        EXPERT_BUTTON.setFont(new Font("Courier New", Font.PLAIN, 16));
        EXPERT_BUTTON.setBounds(685, 300, 130, 40);


        GAME_FRAME.add(new_game_button);
        GAME_FRAME.add(reset);
        GAME_FRAME.add(BEGINNER_BUTTON);
        GAME_FRAME.add(INTERMEDIATE_BUTTON);
        GAME_FRAME.add(EXPERT_BUTTON);
        GAME_FRAME.add(VIEW);
        GAME_FRAME.setSize(1000, 730);
        GAME_FRAME.setVisible(true);
        BEGINNER_BUTTON.setVisible(false);
        INTERMEDIATE_BUTTON.setVisible(false);
        EXPERT_BUTTON.setVisible(false);
    }

    public static void main(String[] args) {
        new Minesweeper();
    }

    public void mouseClicked(MouseEvent e) { }

    public void mousePressed(MouseEvent e) {
        VIEW.mousePressed(e.getX(), e.getY(), e.getButton());
        if (VIEW.getGameStatus()) {
            VIEW.removeMouseListener(this);
        }
        GAME_FRAME.repaint();
    }

    public void mouseReleased(MouseEvent e) { }

    public void mouseEntered(MouseEvent e) { }

    public void mouseExited(MouseEvent e) { }

    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "reset":
                VIEW.reset();
                VIEW.removeMouseListener(this);
                VIEW.addMouseListener(this);

                BEGINNER_BUTTON.setVisible(false);
                BEGINNER_BUTTON.removeActionListener(this);
                INTERMEDIATE_BUTTON.setVisible(false);
                INTERMEDIATE_BUTTON.removeActionListener(this);
                EXPERT_BUTTON.setVisible(false);
                EXPERT_BUTTON.removeActionListener(this);

                GAME_FRAME.repaint();
                break;
            case "new_game":
                VIEW.hideGrid();
                VIEW.removeMouseListener(this);

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
                GAME_FRAME.repaint();
                break;
            case "beginner":
                VIEW.newGame("beginner");
                VIEW.addMouseListener(this);

                BEGINNER_BUTTON.setVisible(false);
                BEGINNER_BUTTON.removeActionListener(this);
                INTERMEDIATE_BUTTON.setVisible(false);
                INTERMEDIATE_BUTTON.removeActionListener(this);
                EXPERT_BUTTON.setVisible(false);
                EXPERT_BUTTON.removeActionListener(this);

                GAME_FRAME.repaint();
                break;
            case "intermediate":
                VIEW.newGame("intermediate");
                VIEW.addMouseListener(this);

                BEGINNER_BUTTON.setVisible(false);
                BEGINNER_BUTTON.removeActionListener(this);
                INTERMEDIATE_BUTTON.setVisible(false);
                INTERMEDIATE_BUTTON.removeActionListener(this);
                EXPERT_BUTTON.setVisible(false);
                EXPERT_BUTTON.removeActionListener(this);

                GAME_FRAME.repaint();
                break;
            case "expert":
                VIEW.newGame("expert");
                VIEW.addMouseListener(this);

                BEGINNER_BUTTON.setVisible(false);
                BEGINNER_BUTTON.removeActionListener(this);
                INTERMEDIATE_BUTTON.setVisible(false);
                INTERMEDIATE_BUTTON.removeActionListener(this);
                EXPERT_BUTTON.setVisible(false);
                EXPERT_BUTTON.removeActionListener(this);

                GAME_FRAME.repaint();
                break;
        }
    }
}
