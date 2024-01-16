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

        ChessPosition[][] positions = new ChessPosition[8][8];

        for(var i = 0; i < 8; i++) {
            for(var j = 0; j < 8; j++) {
                positions[i][j] = new ChessPosition(i, j);
            }
        }

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

        for(var i = 0; i < 8; i++) {
            addPiece(positions[0][i], whitePieces[i]);
            addPiece(positions[1][i], whitePieces[i+8]);
            addPiece(positions[7][i], blackPieces[i]);
            addPiece(positions[6][i], blackPieces[i+8]);
        }
    }
}
