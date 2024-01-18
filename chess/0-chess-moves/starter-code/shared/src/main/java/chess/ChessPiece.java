package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceColor;
    private PieceType type;

    public static void main(String[] args) {
        ChessBoard myBoard = new ChessBoard();
        myBoard.addPiece(new ChessPosition(2, 4), new ChessPiece(ChessGame.TeamColor.WHITE, PieceType.PAWN));
        myBoard.addPiece(new ChessPosition(7, 4), new ChessPiece(ChessGame.TeamColor.BLACK, PieceType.PAWN));
        myBoard.addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, PieceType.ROOK));
        myBoard.addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, PieceType.PAWN));
        ChessPosition piecePosition = new ChessPosition(7, 4);
        ChessPiece testPiece = myBoard.getPiece(piecePosition);
        Collection<ChessMove> availableMoves = testPiece.pieceMoves(myBoard, piecePosition);
        for(ChessMove move: availableMoves) {
            System.out.print(move);
            System.out.println();
        }
        System.out.print(myBoard);
        System.out.println();
   }

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        if(this.type == null) {
            return null;
        }
        else{
            return this.type;
        }
    }

    private boolean isValidMove(ChessPosition myPosition, ChessBoard board) {
        // Rules: there cannot be a piece of the same color in the position I want to move
        // The piece cannot move off the board
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        ChessPiece targetPiece = board.getPiece(myPosition);

        if (row >= 1 && row <= 8 && column >= 1 && column <= 8) {
            return targetPiece == null || targetPiece.pieceColor != this.pieceColor;
        }

        return false;
    }

    private void movePawn(Collection<ChessMove> validMoves, ChessGame.TeamColor color, ChessPosition myPosition, ChessPosition endPosition) {
        int rank = myPosition.getRow();

        if(rank == (isWhite(color) ? 7 : 2)) {
            validMoves.add(new ChessMove(myPosition, endPosition, PieceType.QUEEN));
            validMoves.add(new ChessMove(myPosition, endPosition, PieceType.KNIGHT));
            validMoves.add(new ChessMove(myPosition, endPosition, PieceType.BISHOP));
            validMoves.add(new ChessMove(myPosition, endPosition, PieceType.ROOK));
        }
        else {
            validMoves.add(new ChessMove(myPosition, endPosition, null));
        }
    }

    private boolean isWhite(ChessGame.TeamColor color) {
        return color == ChessGame.TeamColor.WHITE;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new HashSet<>();

        int currentRow = myPosition.getRow();
        int currentColumn = myPosition.getColumn();

        ChessPiece.PieceType currentPiece = getPieceType();
        ChessGame.TeamColor currentColor = getTeamColor();

        // Implementation for a pawn
        switch (currentPiece) {
            case PAWN:
                // Move pawn forward
                ChessPosition oneForward = (isWhite(currentColor) ? new ChessPosition(currentRow+1, currentColumn) : new ChessPosition(currentRow-1, currentColumn));
                ChessPiece targetPiece = board.getPiece(oneForward);
                if(isValidMove(oneForward, board) && targetPiece == null) {
                    movePawn(validMoves, currentColor, myPosition, oneForward);

                    //Given the pawn can move one forward, check if it can move two forward.
                    if(isWhite(currentColor) ? currentRow == 2 : currentRow == 7) {
                        ChessPosition twoForward = (isWhite(currentColor) ? new ChessPosition(currentRow + 2, currentColumn) : new ChessPosition(currentRow - 2, currentColumn));
                        targetPiece = board.getPiece(twoForward);
                        if(isValidMove(twoForward, board) && targetPiece == null) {
                            movePawn(validMoves, currentColor, myPosition, twoForward);
                        }
                    }
                }
                // Capture diagonally right
                ChessPosition diagonalRight = (isWhite(currentColor) ? new ChessPosition(currentRow+1, currentColumn+1) : new ChessPosition(currentRow-1, currentColumn-1));
                targetPiece = board.getPiece(diagonalRight);
                if(isValidMove(diagonalRight, board) && targetPiece != null) {
                    movePawn(validMoves, currentColor, myPosition, diagonalRight);
                }
                // Capture diagonally left
                ChessPosition diagonalLeft = (isWhite(currentColor) ? new ChessPosition(currentRow+1, currentColumn-1) : new ChessPosition(currentRow - 1, currentColumn + 1));
                targetPiece = board.getPiece(diagonalLeft);
                if(isValidMove(diagonalLeft, board) && targetPiece != null){
                    movePawn(validMoves, currentColor, myPosition, diagonalLeft);
                }
                break;

            case KNIGHT:
                break;

            case BISHOP:
                break;

            case KING:
                break;

            case QUEEN:
                break;

            case ROOK:
                break;

            default:
                break;
        }

        return validMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessPiece that)) return false;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        switch(type) {
            case PAWN:
                sb.append(pieceColor.equals(ChessGame.TeamColor.WHITE) ? "P" : "p");
                break;

            case KING:
                sb.append(pieceColor.equals(ChessGame.TeamColor.WHITE) ? "K" : "k");
                break;

            case QUEEN:
                sb.append(pieceColor.equals(ChessGame.TeamColor.WHITE) ? "Q" : "q");
                break;

            case ROOK:
                sb.append(pieceColor.equals(ChessGame.TeamColor.WHITE) ? "R" : "r");
                break;

            case BISHOP:
                sb.append(pieceColor.equals(ChessGame.TeamColor.WHITE) ? "B" : "b");
                break;

            case KNIGHT:
                sb.append(pieceColor.equals(ChessGame.TeamColor.WHITE) ? "N" : "n");
                break;

            default:
                break;
        }

        return sb.toString();
    }
}
