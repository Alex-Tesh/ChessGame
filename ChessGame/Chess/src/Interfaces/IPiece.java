package Interfaces;

import java.awt.Graphics;

public interface IPiece {
    boolean isValidMovement(int col, int row);
    boolean moveCollidesWithPiece(int col, int row);
    void paint(Graphics g);
}
