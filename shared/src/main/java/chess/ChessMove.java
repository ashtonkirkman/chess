package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private ChessPosition startPosition;
    private ChessPosition endPosition;
    private ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return this.startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return this.endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return this.promotionPiece;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessMove chessMove)) return false;
        return Objects.equals(startPosition, chessMove.startPosition) && Objects.equals(endPosition, chessMove.endPosition) && promotionPiece == chessMove.promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        int row = endPosition.getRow();
        int column = endPosition.getColumn();

        switch(column) {
            case 1:
                sb.append("a");
                break;
            case 2:
                sb.append("b");
                break;
            case 3:
                sb.append("c");
                break;
            case 4:
                sb.append("d");
                break;
            case 5:
                sb.append("e");
                break;
            case 6:
                sb.append("f");
                break;
            case 7:
                sb.append("g");
                break;
            case 8:
                sb.append("h");
                break;

        }
        switch(row) {
            case 1:
                sb.append("1");
                break;
            case 2:
                sb.append("2");
                break;
            case 3:
                sb.append("3");
                break;
            case 4:
                sb.append("4");
                break;
            case 5:
                sb.append("5");
                break;
            case 6:
                sb.append("6");
                break;
            case 7:
                sb.append("7");
                break;
            case 8:
                sb.append("8");
                break;
        }

        if(promotionPiece != null) sb.append(" - ").append(promotionPiece);

        return sb.toString();
    }
}
