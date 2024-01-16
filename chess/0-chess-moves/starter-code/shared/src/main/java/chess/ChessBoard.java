package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private static final int boardSize = 8;
    private ChessPiece boardPieces[][] = new ChessPiece[boardSize][boardSize];  // dataType[][] arrayName = new dataType[rows][columns];

    public ChessBoard() {
        resetBoard();
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

        boardPieces[row-1][col-1] = piece;
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

        return boardPieces[row-1][col-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {

        ChessPosition[][] positions = new ChessPosition[boardSize][boardSize];

        for(var i = 0; i < boardSize; i++) {
            for(var j = 0; j < boardSize; j++) {
                positions[i][j] = new ChessPosition(i+1, j+1);
            }
        }

        for(var i = 0; i < boardSize; i++) {
            for (var j = 0; j < boardSize; j++) {
                addPiece(positions[i][j], null);
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

        ChessPiece[] whitePieces = {
                new ChessPiece(white, rook), new ChessPiece(white, knight),
                new ChessPiece(white, bishop), new ChessPiece(white, queen),
                new ChessPiece(white, king), new ChessPiece(white, bishop),
                new ChessPiece(white, knight), new ChessPiece(white, rook),
                new ChessPiece(white, pawn), new ChessPiece(white, pawn),
                new ChessPiece(white, pawn), new ChessPiece(white, pawn),
                new ChessPiece(white, pawn), new ChessPiece(white, pawn),
                new ChessPiece(white, pawn), new ChessPiece(white, pawn)
        };

        ChessPiece[] blackPieces = {
                new ChessPiece(black, rook), new ChessPiece(black, knight),
                new ChessPiece(black, bishop), new ChessPiece(black, queen),
                new ChessPiece(black, king), new ChessPiece(black, bishop),
                new ChessPiece(black, knight), new ChessPiece(black, rook),
                new ChessPiece(black, pawn), new ChessPiece(black, pawn),
                new ChessPiece(black, pawn), new ChessPiece(black, pawn),
                new ChessPiece(black, pawn), new ChessPiece(black, pawn),
                new ChessPiece(black, pawn), new ChessPiece(black, pawn)
        };

        for(var i = 0; i < boardSize; i++) {
            addPiece(positions[0][i], whitePieces[i]);      // Fill up the 1st rank with white's mains
            addPiece(positions[1][i], whitePieces[i+8]);    // Fill up the 2nd rank with white's pawns
            addPiece(positions[7][i], blackPieces[i]);      // Fill up the 8th rank with black's mains
            addPiece(positions[6][i], blackPieces[i+8]);    // Fill up the 7th rank with black's pawns
        }
    }
}
