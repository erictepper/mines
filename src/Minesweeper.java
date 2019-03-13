import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


public class Minesweeper implements MouseListener {
    private MinesweeperDisplay DISPLAY;

    private Minesweeper(String size) {
        GameGrid gameGrid = new GameGrid(size, 50, 200, 30);

        JFrame gameFrame = new JFrame("Minesweeper");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DISPLAY = new MinesweeperDisplay(50, 200, 30, gameGrid);
        DISPLAY.setSize(1000, 730);
        DISPLAY.setBackground(Color.WHITE);
        DISPLAY.addMouseListener(this);
        gameFrame.add(DISPLAY);
        gameFrame.setSize(1000, 730);
        gameFrame.setVisible(true);
    }

    public static void main(String[] args) {
        new Minesweeper("large");
    }

    public void mouseClicked(MouseEvent e) { }

    public void mousePressed(MouseEvent e) {
        DISPLAY.mouseClicked(e.getX(), e.getY(), e.getButton());
        if (DISPLAY.getGameStatus()) {
            DISPLAY.removeMouseListener(this);
        }
    }

    public void mouseReleased(MouseEvent e) { }

    public void mouseEntered(MouseEvent e) { }

    public void mouseExited(MouseEvent e) { }
}
