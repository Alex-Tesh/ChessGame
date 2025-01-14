package Interfaces;

import Main.Move;
import Pieces.Piece;

public interface ICheckPiece {
    boolean isKingChecked(Move move);
    boolean isGameOver(Piece king);
}
