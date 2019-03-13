import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


public class Minesweeper implements MouseListener {
    private GameView VIEW;

    private Minesweeper(String size) {
        GameGrid gameGrid = new GameGrid(size, 50, 200, 30);

        JFrame gameFrame = new JFrame("Minesweeper");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        VIEW = new GameView(50, 200, 30, gameGrid);
        VIEW.setSize(1000, 730);
        VIEW.setBackground(Color.WHITE);
        VIEW.addMouseListener(this);
        gameFrame.add(VIEW);
        gameFrame.setSize(1000, 730);
        gameFrame.setVisible(true);
    }

    public static void main(String[] args) {
        new Minesweeper("large");
    }

    public void mouseClicked(MouseEvent e) { }

    public void mousePressed(MouseEvent e) {
        VIEW.mousePressed(e.getX(), e.getY(), e.getButton());
        if (VIEW.getGameStatus()) {
            VIEW.removeMouseListener(this);
        }
    }

    public void mouseReleased(MouseEvent e) { }

    public void mouseEntered(MouseEvent e) { }

    public void mouseExited(MouseEvent e) { }
}
