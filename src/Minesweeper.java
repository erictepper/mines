import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


public class Minesweeper implements MouseListener {
    private GamePanel GAME_PANEL;

    private Minesweeper(String size) {
        GameGrid gameGrid = new GameGrid(size, 50, 200, 30);

        JFrame gameFrame = new JFrame("Minesweeper");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GAME_PANEL = new GamePanel(50, 200, 30, gameGrid);
        GAME_PANEL.setSize(1000, 730);
        GAME_PANEL.setBackground(Color.WHITE);
        GAME_PANEL.addMouseListener(this);
        gameFrame.add(GAME_PANEL);
        gameFrame.setSize(1000, 730);
        gameFrame.setVisible(true);
    }

    public static void main(String[] args) {
        new Minesweeper("large");
    }

    public void mouseClicked(MouseEvent e) { }

    public void mousePressed(MouseEvent e) {
        GAME_PANEL.mouseClicked(e.getX(), e.getY(), e.getButton());
        if (GAME_PANEL.getGameStatus()) {
            GAME_PANEL.removeMouseListener(this);
        }
    }

    public void mouseReleased(MouseEvent e) { }

    public void mouseEntered(MouseEvent e) { }

    public void mouseExited(MouseEvent e) { }
}
