package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

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
        myBoard.addPiece(new ChessPosition(4, 4), new ChessPiece(ChessGame.TeamColor.WHITE, PieceType.PAWN));
        myBoard.addPiece(new ChessPosition(5, 5), new ChessPiece(ChessGame.TeamColor.BLACK, PieceType.PAWN));
        myBoard.addPiece(new ChessPosition(5, 3), new ChessPiece(ChessGame.TeamColor.BLACK, PieceType.ROOK));
        ChessPosition piecePosition = new ChessPosition(4, 4);
        ChessPiece testPiece = myBoard.getPiece(piecePosition);
        Collection<ChessMove> availableMoves = testPiece.pieceMoves(myBoard, piecePosition);
        for(ChessMove move: availableMoves) {
            System.out.print(move.getEndPosition().getRow() + ", " + move.getEndPosition().getColumn());
            System.out.println();
        }
        myBoard.displayBoard();
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
                if(currentColor == ChessGame.TeamColor.WHITE) {
                    // Move directly forward
                    ChessPosition oneForward = new ChessPosition(currentRow + 1, currentColumn);
                    ChessPiece targetPiece = board.getPiece(oneForward);
                    if(isValidMove(oneForward, board) && targetPiece == null) {
                        validMoves.add(new ChessMove(myPosition, oneForward, null));
                    }
                    // Capture diagonally right
                    ChessPosition diagonalRight = new ChessPosition(currentRow+1, currentColumn+1);
                    targetPiece = board.getPiece(diagonalRight);
                    if(isValidMove(diagonalRight, board) && targetPiece != null) {
                        validMoves.add(new ChessMove(myPosition, diagonalRight, null));
                    }
                    // Capture diagonally left
                    ChessPosition diagonalLeft = new ChessPosition(currentRow+1, currentColumn-1);
                    targetPiece = board.getPiece(diagonalLeft);
                    if(isValidMove(diagonalLeft, board) && targetPiece != null){
                        validMoves.add(new ChessMove(myPosition, diagonalLeft, null));
                    }
                }
                else {
                    // Move directly forward
                    ChessPosition oneForward = new ChessPosition(currentRow - 1, currentColumn);
                    ChessPiece targetPiece = board.getPiece(oneForward);
                    if (isValidMove(oneForward, board) && targetPiece == null) {
                        validMoves.add(new ChessMove(myPosition, oneForward, null));
                    }
                    // Capture diagonally right
                    ChessPosition diagonalRight = new ChessPosition(currentRow - 1, currentColumn - 1);
                    targetPiece = board.getPiece(diagonalRight);
                    if (isValidMove(diagonalRight, board) && targetPiece != null) {
                        validMoves.add(new ChessMove(myPosition, diagonalRight, null));
                    }
                    // Capture diagonally left
                    ChessPosition diagonalLeft = new ChessPosition(currentRow - 1, currentColumn + 1);
                    targetPiece = board.getPiece(diagonalLeft);
                    if (isValidMove(diagonalLeft, board) && targetPiece != null) {
                        validMoves.add(new ChessMove(myPosition, diagonalLeft, null));
                    }
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
}
