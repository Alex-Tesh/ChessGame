package Main;

import Pieces.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void getPiece_shouldReturnCorrectPiece() {
        Piece rook = board.getPiece(0, 0);
        assertNotNull(rook);
        assertEquals("Rook", rook.name);
        assertFalse(rook.isWhite);

        Piece whitePawn = board.getPiece(3, 6);
        assertNotNull(whitePawn);
        assertEquals("Pawn", whitePawn.name);
        assertTrue(whitePawn.isWhite);

        assertNull(board.getPiece(3, 3));
    }

    @Test
    void makeMove_shouldMoveCorrectly() {
        Piece pawn = board.getPiece(0, 6);
        Move move = new Move(board, pawn, 0, 4);
        board.makeMove(move);

        assertNull(board.getPiece(0, 6));
        assertNotNull(board.getPiece(0, 4));
        assertEquals(pawn, board.getPiece(0, 4));
    }

    @Test
    void makeMove_shouldHandleCastling() {
        // Clear pieces between king and rook
        board.capture(board.getPiece(5, 7));
        board.capture(board.getPiece(6, 7));

        Piece king = board.getPiece(4, 7);
        Move castlingMove = new Move(board, king, 6, 7);
        board.makeMove(castlingMove);

        // Check king position
        assertEquals(6, board.getPiece(6, 7).col);
        assertEquals("King", board.getPiece(6, 7).name);

        // Check rook position
        assertEquals(5, board.getPiece(5, 7).col);
        assertEquals("Rook", board.getPiece(5, 7).name);
    }

    @Test
    void capture_shouldRemovePiece() {
        Piece pieceToCapture = board.getPiece(0, 1);
        board.capture(pieceToCapture);
        assertNull(board.getPiece(0, 1));
        assertFalse(board.pieceList.contains(pieceToCapture));
    }

    @Test
    void sameColor_shouldCheckColorCorrectly() {
        Piece whitePawn = board.getPiece(0, 6);
        Piece whiteRook = board.getPiece(0, 7);
        Piece blackPawn = board.getPiece(0, 1);

        assertTrue(board.sameColor(whitePawn, whiteRook));
        assertFalse(board.sameColor(whitePawn, blackPawn));
        assertFalse(board.sameColor(whitePawn, null));
        assertFalse(board.sameColor(null, blackPawn));
    }

    @Test
    void findKing_shouldFindCorrectKing() {
        Piece whiteKing = board.findKing(true);
        assertNotNull(whiteKing);
        assertEquals("King", whiteKing.name);
        assertTrue(whiteKing.isWhite);
        assertEquals(4, whiteKing.col);
        assertEquals(7, whiteKing.row);

        Piece blackKing = board.findKing(false);
        assertNotNull(blackKing);
        assertEquals("King", blackKing.name);
        assertFalse(blackKing.isWhite);
        assertEquals(4, blackKing.col);
        assertEquals(0, blackKing.row);
    }

    @Test
    void getTileNum_shouldCalculateCorrectly() {
        assertEquals(0, board.getTileNum(0, 0));
        assertEquals(7, board.getTileNum(7, 0));
        assertEquals(8, board.getTileNum(0, 1));
        assertEquals(63, board.getTileNum(7, 7));
    }

    @Test
    void addPieces_shouldSetupBoardCorrectly() {
        // Test total number of pieces
        assertEquals(32, board.pieceList.size());

        int whitePawns = 0;
        int blackPawns = 0;
        for (Piece piece : board.pieceList) {
            if (piece.name.equals("Pawn")) {
                if (piece.isWhite) whitePawns++;
                else blackPawns++;
            }
        }
        assertEquals(8, whitePawns);
        assertEquals(8, blackPawns);

        assertTrue(board.getPiece(4, 0) instanceof King);
        assertTrue(board.getPiece(3, 0) instanceof Queen);
        assertTrue(board.getPiece(0, 0) instanceof Rook);
    }

    @Test
    void promotePawn_shouldPromoteToQueen() {
        Piece pawn = board.getPiece(0, 6);
        board.makeMove(new Move(board, pawn, 0, 0));

        Piece promotedPiece = board.getPiece(0, 0);
        assertNotNull(promotedPiece);
        assertEquals("Queen", promotedPiece.name);
        assertTrue(promotedPiece.isWhite);
    }
}