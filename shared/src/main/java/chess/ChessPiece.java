package chess;

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

    private ChessGame.TeamColor color;
    private PieceType type;

    public static void main(String[] args) {
        ChessBoard myBoard = new ChessBoard();
        myBoard.addPiece(new ChessPosition(2, 4), new ChessPiece(ChessGame.TeamColor.WHITE, PieceType.ROOK));
        myBoard.addPiece(new ChessPosition(2, 3), new ChessPiece(ChessGame.TeamColor.BLACK, PieceType.PAWN));
        myBoard.addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, PieceType.KING));
        myBoard.addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, PieceType.PAWN));
        ChessPosition piecePosition = new ChessPosition(2, 4);
        ChessPiece testPiece = myBoard.getPiece(piecePosition);
        Collection<ChessMove> availableMoves = testPiece.pieceMoves(myBoard, piecePosition);
        for(ChessMove move: availableMoves) {
            System.out.print(move);
            System.out.println();
        }
        System.out.print(myBoard);
        System.out.println();
   }

    public ChessPiece(ChessGame.TeamColor color, ChessPiece.PieceType type) {
        this.color = color;
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
        if(this.type == null) {
            return null;
        }
        else{
            return this.color;
        }
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

    private boolean isValidMove(ChessPiece destinationPiece, ChessPosition destination) {
        int rank = destination.getRow();
        int file = destination.getColumn();

        if(rank >= 1 && rank <= 8 && file >= 1 && file <= 8) {
            return destinationPiece == null || destinationPiece.getTeamColor() != this.color;
        }

        return false;
    }

    private boolean isInBounds(int rank, int file) {
        return rank >= 1 && rank <= 8 && file >= 1 && file <= 8;
    }

    private void addMove(Collection<ChessMove> validMoves, ChessPosition myPosition, ChessPosition destination) {
        int rank = destination.getRow();

        if(this.type == ChessPiece.PieceType.PAWN) {
            if(rank == 1 || rank == 8) {
                validMoves.add(new ChessMove(myPosition, destination, PieceType.KNIGHT));
                validMoves.add(new ChessMove(myPosition, destination, PieceType.QUEEN));
                validMoves.add(new ChessMove(myPosition, destination, PieceType.BISHOP));
                validMoves.add(new ChessMove(myPosition, destination, PieceType.ROOK));
            }
            else validMoves.add(new ChessMove(myPosition, destination, null));
        }
        else validMoves.add(new ChessMove(myPosition, destination, null));
    }

    private boolean isWhite() {
        return this.color == ChessGame.TeamColor.WHITE;
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

        int currentRank = myPosition.getRow();
        int currentFile = myPosition.getColumn();

        ChessPiece destinationPiece;
        ChessPosition destination;
        ChessPiece currentPiece = board.getPiece(myPosition);
        PieceType currentType = currentPiece.getPieceType();

        switch(currentType) {
            case PAWN:
                // Move pawn forward
                destination = new ChessPosition(isWhite() ? currentRank + 1 : currentRank - 1, currentFile);
                destinationPiece = board.getPiece(destination);
                if(isValidMove(destinationPiece, destination) && destinationPiece == null) {
                    addMove(validMoves, myPosition, destination);
                    if(currentRank == (isWhite() ? 2 : 7)) {
                        destination = new ChessPosition(isWhite() ? currentRank + 2 : currentRank - 2, currentFile);
                        destinationPiece = board.getPiece(destination);
                        if(isValidMove(destinationPiece, destination) && destinationPiece == null) {
                            addMove(validMoves, myPosition, destination);
                        }
                    }
                }

                // Capture with pawn
                for(int i = -1; i <= 1; i = i + 2) {
                    if(isInBounds(isWhite() ? currentRank + 1 : currentRank - 1, currentFile + i)) {
                        destination = new ChessPosition(isWhite() ? currentRank + 1 : currentRank - 1, currentFile + i);
                        destinationPiece = board.getPiece(destination);
                        if(isValidMove(destinationPiece, destination) && destinationPiece != null) {
                            addMove(validMoves, myPosition, destination);
                        }
                    }
                }
                break;

            case KING:
                for(int i = -1; i <= 1; i++) {
                    for(int j = -1; j <= 1; j++) {
                        if(isInBounds(currentRank + i, currentFile + j) && !(i == 0 && j == 0)) {
                            destination = new ChessPosition(currentRank + i, currentFile + j);
                            destinationPiece = board.getPiece(destination);
                            if(isValidMove(destinationPiece, destination)) {
                                addMove(validMoves, myPosition, destination);
                            }
                        }
                    }
                }
                break;

            case QUEEN:
                for(int i = -1; i <= 1; i++) {
                    for(int j = -1; j <= 1; j++) {
                        for(int k = 1; k <= 7; k++) {
                            int newRank = currentRank + k*i;
                            int newFile = currentFile + k*j;

                            if((i == 0 && j == 0) || !isInBounds(newRank, newFile)) break;
                            destination = new ChessPosition(newRank, newFile);
                            destinationPiece = board.getPiece(destination);
                            if(isValidMove(destinationPiece, destination)) {
                                addMove(validMoves, myPosition, destination);
                                if(destinationPiece != null) break;
                            }
                            else break;
                        }
                    }
                }
                break;

            case BISHOP:
                for(int i = -1; i <= 1; i = i + 2) {
                    for(int j = -1; j <= 1; j = j + 2) {
                        for(int k = 1; k <= 7; k++) {
                            int newRank = currentRank + k*i;
                            int newFile = currentFile + k*j;

                            if(!isInBounds(newRank, newFile)) break;
                            destination = new ChessPosition(newRank, newFile);
                            destinationPiece = board.getPiece(destination);
                            if(isValidMove(destinationPiece, destination)) {
                                addMove(validMoves, myPosition, destination);
                                if(destinationPiece != null) break;
                            }
                            else break;
                        }
                    }
                }
                break;

            case ROOK:
                for(int i = -1; i <= 1; i++) {
                    for(int j = -1; j <= 1; j++) {
                        for(int k = 1; k <= 7; k++) {
                            int newRank = currentRank + k*i;
                            int newFile = currentFile + k*j;

                            if((Math.abs(i) == 1 && Math.abs(j) == 1) || (i == 0 && j == 0) || !isInBounds(newRank, newFile)) break;
                            destination = new ChessPosition(newRank, newFile);
                            destinationPiece = board.getPiece(destination);
                            if(isValidMove(destinationPiece, destination)) {
                                addMove(validMoves, myPosition, destination);
                                if(destinationPiece != null) break;
                            }
                            else break;
                        }
                    }
                }
                break;

            case KNIGHT:
                for(int i = -2; i <= 2; i++) {
                    for(int j = -2; j <= 2; j++) {
                        int newRank = currentRank + i;
                        int newFile = currentFile + j;

                        if(((Math.abs(i) == 1 && Math.abs(j) == 2) || (Math.abs(i) == 2 && Math.abs(j) == 1)) && isInBounds(newRank, newFile)) {
                            destination = new ChessPosition(newRank, newFile);
                            destinationPiece = board.getPiece(destination);
                            if (isValidMove(destinationPiece, destination)) {
                                addMove(validMoves, myPosition, destination);
                            }
                        }
                    }
                }
                break;
        }

        return validMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessPiece that)) return false;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        switch(type) {
            case PAWN:
                sb.append(color.equals(ChessGame.TeamColor.WHITE) ? "P" : "p");
                break;

            case KING:
                sb.append(color.equals(ChessGame.TeamColor.WHITE) ? "K" : "k");
                break;

            case QUEEN:
                sb.append(color.equals(ChessGame.TeamColor.WHITE) ? "Q" : "q");
                break;

            case ROOK:
                sb.append(color.equals(ChessGame.TeamColor.WHITE) ? "R" : "r");
                break;

            case BISHOP:
                sb.append(color.equals(ChessGame.TeamColor.WHITE) ? "B" : "b");
                break;

            case KNIGHT:
                sb.append(color.equals(ChessGame.TeamColor.WHITE) ? "N" : "n");
                break;

            default:
                break;
        }

        return sb.toString();
    }
}
