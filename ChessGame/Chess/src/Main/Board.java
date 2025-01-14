package Main;

import Interfaces.IBoard;
import Pieces.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Board extends JPanel implements IBoard{

    public String fenStartingPosition="rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public int tileSize = 85;

    int cols = 8;
    int rows = 8;

    ArrayList<Piece> pieceList = new ArrayList<>();

    public Piece selectedPiece;

    public int enPassantTile=-1;

    private boolean isWhiteMove=true;
    private boolean isGameOver=false;

    public CheckPiece checkPiece=new CheckPiece(this);

    Input input=new Input(this);


    public Board() {
        this.setPreferredSize(new Dimension(cols * tileSize, rows * tileSize));
        this.addMouseListener(input);
        this.addMouseMotionListener(input);

        loadPositionFromFEN(fenStartingPosition);
//        addPieces();
    }

    public void loadPositionFromFEN(String fenString) {
        pieceList.clear();
        String[] parts=fenString.split(" ");

        String position=parts[0];
        int row=0;
        int col=0;
        for(int i=0;i<position.length();i++){
            char ch=position.charAt(i);
            if(ch=='/'){
                row++;
                col=0;
            }else if(Character.isDigit(ch)){
                col+=Character.getNumericValue(ch);
            }else{
                boolean isWhite=Character.isUpperCase(ch);
                char pieceChar=Character.toLowerCase(ch);

                switch(pieceChar){
                    case 'r':
                        pieceList.add(new Rook(this, col,row,isWhite));
                        break;
                    case 'n':
                        pieceList.add(new Knight(this, col,row,isWhite));
                        break;
                    case 'b':
                        pieceList.add(new Bishop(this, col,row,isWhite));
                        break;
                    case 'q':
                        pieceList.add(new Queen(this, col,row,isWhite));
                        break;
                    case 'k':
                        pieceList.add(new King(this, col,row,isWhite));
                        break;
                    case 'p':
                        pieceList.add(new Pawn(this, col,row,isWhite));
                        break;

                }
                col++;
            }
        }
        isWhiteMove=parts[1].equals("w");

        Piece bqr=getPiece(0, 0);
        if(bqr instanceof Rook){
            bqr.isFirstMove=parts[2].contains("q");
        }
        Piece bkr=getPiece(7, 0);
        if(bkr instanceof Rook){
            bkr.isFirstMove=parts[2].contains("k");
        }
        Piece wqr=getPiece(0, 7);
        if(wqr instanceof Rook){
            wqr.isFirstMove=parts[2].contains("Q");
        }
        Piece wkr=getPiece(7, 7);
        if(wkr instanceof Rook){
            wkr.isFirstMove=parts[2].contains("K");
        }
        if(parts[3].equals("-")){
            enPassantTile=-1;
        }else{
            enPassantTile=(7-(parts[3].charAt(1)-'1'))*8+ (parts[3].charAt(0)-'a');
        }
    }

    public Piece getPiece(int col, int row) {
        for(Piece piece : pieceList) {
            if(piece.col == col && piece.row == row) {
                return piece;
            }
        }
        return null;
    }

    public void makeMove(Move move) {

        if (move.piece.name.equals("Pawn")) {
            movePawn(move);
        } else if (move.piece.name.equals("King")) {
            moveKing(move);
        }
        move.piece.col = move.newCol;
        move.piece.row = move.newRow;

        move.piece.xPos = move.newCol * tileSize;
        move.piece.yPos = move.newRow * tileSize;

        move.piece.isFirstMove = false;

        capture(move.capture);

        isWhiteMove=!isWhiteMove;

        updateGameState();
    }

    private void moveKing(Move move) {

        if(Math.abs(move.piece.col-move.newCol)==2){
            Piece rook;
            if(move.piece.col<move.newCol){
                rook=getPiece(7, move.piece.row);
                rook.col=5;
            }else{
                rook=getPiece(0, move.piece.row);
                rook.col=3;
            }
            rook.xPos=rook.col*tileSize;
        }
    }

    private void movePawn(Move move){

        int colorIndex=move.piece.isWhite? 1:-1;

        if(getTileNum(move.newCol, move.newRow)==enPassantTile){
            move.capture=getPiece(move.newCol, move.newRow + colorIndex);
        }
        if(Math.abs(move.piece.row-move.newRow) == 2){
            enPassantTile=getTileNum(move.newCol, move.newRow+ colorIndex);
        }else{
            enPassantTile=1;
        }

        //promotion
        colorIndex=move.piece.isWhite? 0:7;
        if(move.newRow== colorIndex){
            promotePawn(move);
        }

    }

    private void promotePawn(Move move){
        pieceList.add(new Queen( this, move.newCol, move.newRow, move.piece.isWhite));
        capture(move.piece);
    }

    public void capture(Piece piece) {
        pieceList.remove(piece);
    }

    public boolean isValidMove(Move move){

        if(move.piece.isWhite != isWhiteMove){
            return false;
        }

        if(isGameOver){
            return false;
        }

        if(sameColor(move.piece, move.capture)){
            return false;
        }

        if(!move.piece.isValidMovement(move.newCol, move.newRow)){
            return false;
        }

        if(move.piece.moveCollidesWithPiece(move.newCol, move.newRow)){
            return false;
        }

        if(checkPiece.isKingChecked((move))){
            return false;
        }

        return true;
    }


    public boolean sameColor(Piece p1, Piece p2) {
        if(p1==null||p2==null){
            return false;
        }
        return p1.isWhite==p2.isWhite;
    }

    Piece findKing(boolean isWhite) {
        for(Piece piece : pieceList) {
            if(piece.isWhite==isWhite&& piece.name.equals("King")) {
                return piece;
            }
        }
        return null;
    }

    public int getTileNum(int col, int row) {
        return row*rows+col;
    }


//    public void addPieces() {
//        pieceList.add(new Rook(this, 0,0,false));
//        pieceList.add(new Knight(this, 1, 0, false));
//        pieceList.add(new Bishop(this, 2,0,false));
//        pieceList.add(new Queen(this, 3,0,false));
//        pieceList.add(new King(this, 4,0,false));
//        pieceList.add(new Bishop(this, 5,0,false));
//        pieceList.add(new Knight(this, 6, 0, false));
//        pieceList.add(new Rook(this, 7,0,false));
//
//
//        for (int col = 0; col <= 7; col++) {
//            pieceList.add(new Pawn(this, col, 1, false));
//        }
//
//        for (int col = 0; col <= 7; col++) {
//            pieceList.add(new Pawn(this, col, 6, true));
//        }
//
//        pieceList.add(new Rook(this, 0,7,true));
//        pieceList.add(new Knight(this, 1, 7, true));
//        pieceList.add(new Bishop(this, 2,7,true));
//        pieceList.add(new Queen(this, 3,7,true));
//        pieceList.add(new King(this, 4,7,true));
//        pieceList.add(new Bishop(this, 5,7,true));
//        pieceList.add(new Knight(this, 6, 7, true));
//        pieceList.add(new Rook(this, 7,7,true));
//    }

    private void updateGameState(){
        Piece king= findKing(isWhiteMove);
        if(checkPiece.isGameOver(king)){
            if(checkPiece.isKingChecked(new Move(this, king, king.col, king.row))){
                System.out.println(isWhiteMove ? "Black wins!" : "White wins!");
            }else{
                System.out.print("Stalemate");
            }
            isGameOver=true;
        }else if(noPiecesLeft(true) && noPiecesLeft((false))){
            System.out.println("Insufficient Material!");
            isGameOver=true;
        }
    }

    private boolean noPiecesLeft(boolean isWhite){
        ArrayList<String> names= pieceList.stream()
                .filter(p->p.isWhite==isWhite)
                .map(p->p.name)
                .collect(Collectors.toCollection(ArrayList::new));

        if(names.contains("Queen") ||names.contains("Rook")||names.contains("Pawn")){
            return false;
        }
        return names.size()<3;
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) {
                g2d.setColor((i + j) % 2 == 0 ? new Color(223, 214, 204) : new Color(78, 122, 33));
                g2d.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);
            }

        if(selectedPiece!=null)
            for (int i = 0; i < rows; i++)
                for (int j = 0; j < cols; j++) {
                    if (isValidMove(new Move(this, selectedPiece, j, i))) {
                        g2d.setColor(new Color(102, 236, 225));
                        g2d.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);
                    }
                }

        for (Piece piece : pieceList) {
            piece.paint(g2d);
        }

    }
}
