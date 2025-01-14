package Interfaces;
import Main.Move;
import Pieces.Piece;

import java.awt.*;

public interface IBoard {
    Piece getPiece(int col, int row);
    void makeMove(Move move);
    boolean isValidMove(Move move);
    void capture(Piece piece);
    boolean sameColor(Piece p1, Piece p2);
//    void addPieces();

}
