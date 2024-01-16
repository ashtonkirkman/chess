package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece boardPieces[][] = new ChessPiece[8][8];  // dataType[][] arrayName = new dataType[rows][columns];

    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow();
        int col = position.getColumn();

        boardPieces[row][col] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();

        return boardPieces[row][col];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        ChessPosition a1 = new ChessPosition(1, 1);
        ChessPosition a2 = new ChessPosition(1, 2);
        ChessPosition a3 = new ChessPosition(1, 3);
        ChessPosition a4 = new ChessPosition(1, 4);
        ChessPosition a5 = new ChessPosition(1, 5);
        ChessPosition a6 = new ChessPosition(1, 6);
        ChessPosition a7 = new ChessPosition(1, 7);
        ChessPosition a8 = new ChessPosition(1, 8);
        ChessPosition b1 = new ChessPosition(2, 1);
        ChessPosition b2 = new ChessPosition(2, 2);
        ChessPosition b3 = new ChessPosition(2, 3);
        ChessPosition b4 = new ChessPosition(2, 4);
        ChessPosition b5 = new ChessPosition(2, 5);
        ChessPosition b6 = new ChessPosition(2, 6);
        ChessPosition b7 = new ChessPosition(2, 7);
        ChessPosition b8 = new ChessPosition(2, 8);
        ChessPosition g1 = new ChessPosition(7, 1);
        ChessPosition g2 = new ChessPosition(7, 2);
        ChessPosition g3 = new ChessPosition(7, 3);
        ChessPosition g4 = new ChessPosition(7, 4);
        ChessPosition g5 = new ChessPosition(7, 5);
        ChessPosition g6 = new ChessPosition(7, 6);
        ChessPosition g7 = new ChessPosition(7, 7);
        ChessPosition g8 = new ChessPosition(7, 8);
        ChessPosition h1 = new ChessPosition(8, 1);
        ChessPosition h2 = new ChessPosition(8, 2);
        ChessPosition h3 = new ChessPosition(8, 3);
        ChessPosition h4 = new ChessPosition(8, 4);
        ChessPosition h5 = new ChessPosition(8, 5);
        ChessPosition h6 = new ChessPosition(8, 6);
        ChessPosition h7 = new ChessPosition(8, 7);
        ChessPosition h8 = new ChessPosition(8, 8);

        for(var i = 0; i < boardPieces.length; i++) {
            for (var j = 0; j < boardPieces[0].length; j++) {
                boardPieces[i][j] = null;
            }
        }

        ChessGame.TeamColor white = ChessGame.TeamColor.WHITE;
        ChessGame.TeamColor black = ChessGame.TeamColor.BLACK;

        ChessPiece.PieceType rook = ChessPiece.PieceType.ROOK;
        ChessPiece.PieceType pawn = ChessPiece.PieceType.PAWN;
        ChessPiece.PieceType knight = ChessPiece.PieceType.KNIGHT;
        ChessPiece.PieceType bishop = ChessPiece.PieceType.BISHOP;
        ChessPiece.PieceType queen = ChessPiece.PieceType.QUEEN;
        ChessPiece.PieceType king = ChessPiece.PieceType.KING;

        ChessPiece whiteARook = new ChessPiece(white, rook);
        ChessPiece whiteBKnight = new ChessPiece(white, knight);
        ChessPiece whiteCBishop = new ChessPiece(white, bishop);
        ChessPiece whiteQueen = new ChessPiece(white, queen);
        ChessPiece whiteKing = new ChessPiece(white, king);
        ChessPiece whiteFBishop = new ChessPiece(white, bishop);
        ChessPiece whiteGKnight = new ChessPiece(white, knight);
        ChessPiece whiteHRook = new ChessPiece(white, rook);

        addPiece(a1, whiteARook);
    }
}
