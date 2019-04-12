import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


public class Minesweeper implements MouseListener, ActionListener {
    private JFrame GAME_FRAME;
    private Game GAME_INSTANCE;
    private JButton NEW_GAME_BUTTON;
    private JButton BEGINNER_BUTTON;
    private JButton INTERMEDIATE_BUTTON;
    private JButton EXPERT_BUTTON;
    private JButton HINT;
    private Timer GAME_TIMER;

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

        JButton reset = new JButton("RESET");
        reset.setFont(new Font("Courier New", Font.PLAIN, 16));
        reset.setBounds(550, 120, 130, 40);
        reset.addActionListener(this);
        reset.setActionCommand("reset");

        HINT = new JButton("HINT");
        HINT.setFont(new Font("Courier New", Font.PLAIN, 16));
        HINT.setBounds(310, 160, 130, 40);
        HINT.addActionListener(this);
        HINT.setActionCommand("hint");

        GAME_TIMER = new Timer(1000, this);
        GAME_TIMER.setActionCommand("timer");

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
        GAME_FRAME.add(reset);
        GAME_FRAME.add(HINT);
        GAME_FRAME.add(BEGINNER_BUTTON);
        GAME_FRAME.add(INTERMEDIATE_BUTTON);
        GAME_FRAME.add(EXPERT_BUTTON);
        GAME_FRAME.add(GAME_INSTANCE);
        GAME_FRAME.setSize(1000, 770);
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
        if (!GAME_INSTANCE.getGameStartedStatus()) { GAME_TIMER.start(); }
        int button = (e.isControlDown() && e.getButton() == 1) ? 3 : e.getButton();
        GAME_INSTANCE.mousePressed(e.getX(), e.getY(), button);
        if (GAME_INSTANCE.getGameStatus()) {
            GAME_INSTANCE.removeMouseListener(this);
            GAME_TIMER.stop();
        }
        GAME_FRAME.repaint();
    }

    public void mouseReleased(MouseEvent e) { }

    public void mouseEntered(MouseEvent e) { }

    public void mouseExited(MouseEvent e) { }

    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "timer":
                GAME_INSTANCE.timerTick();
                GAME_FRAME.repaint();
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

                GAME_FRAME.repaint();
                break;
            case "hint":
                GAME_INSTANCE.giveHint();
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
                GAME_INSTANCE.addMouseListener(this);
                if (GAME_INSTANCE.getGameStartedStatus()) { GAME_TIMER.start(); }

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

                GAME_FRAME.repaint();
                break;
        }
    }
}
