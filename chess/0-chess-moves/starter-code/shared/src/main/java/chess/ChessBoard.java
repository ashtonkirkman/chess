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
        /**
         * Create Starting Positions
         */
        ChessPosition a1 = new ChessPosition(1, 1);
        ChessPosition b1 = new ChessPosition(1, 2);
        ChessPosition c1 = new ChessPosition(1, 3);
        ChessPosition d1 = new ChessPosition(1, 4);
        ChessPosition e1 = new ChessPosition(1, 5);
        ChessPosition f1 = new ChessPosition(1, 6);
        ChessPosition g1 = new ChessPosition(1, 7);
        ChessPosition h1 = new ChessPosition(1, 8);
        ChessPosition a2 = new ChessPosition(2, 1);
        ChessPosition b2 = new ChessPosition(2, 2);
        ChessPosition c2 = new ChessPosition(2, 3);
        ChessPosition d2 = new ChessPosition(2, 4);
        ChessPosition e2 = new ChessPosition(2, 5);
        ChessPosition f2 = new ChessPosition(2, 6);
        ChessPosition g2 = new ChessPosition(2, 7);
        ChessPosition h2 = new ChessPosition(2, 8);
        ChessPosition a7 = new ChessPosition(7, 1);
        ChessPosition b7 = new ChessPosition(7, 2);
        ChessPosition c7 = new ChessPosition(7, 3);
        ChessPosition d7 = new ChessPosition(7, 4);
        ChessPosition e7 = new ChessPosition(7, 5);
        ChessPosition f7 = new ChessPosition(7, 6);
        ChessPosition g7 = new ChessPosition(7, 7);
        ChessPosition h7 = new ChessPosition(7, 8);
        ChessPosition a8 = new ChessPosition(8, 1);
        ChessPosition b8 = new ChessPosition(8, 2);
        ChessPosition c8 = new ChessPosition(8, 3);
        ChessPosition d8 = new ChessPosition(8, 4);
        ChessPosition e8 = new ChessPosition(8, 5);
        ChessPosition f8 = new ChessPosition(8, 6);
        ChessPosition g8 = new ChessPosition(8, 7);
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


        /**
         * Create White Chess Piece objects
         */
        ChessPiece whiteARook = new ChessPiece(white, rook);
        ChessPiece whiteBKnight = new ChessPiece(white, knight);
        ChessPiece whiteCBishop = new ChessPiece(white, bishop);
        ChessPiece whiteQueen = new ChessPiece(white, queen);
        ChessPiece whiteKing = new ChessPiece(white, king);
        ChessPiece whiteFBishop = new ChessPiece(white, bishop);
        ChessPiece whiteGKnight = new ChessPiece(white, knight);
        ChessPiece whiteHRook = new ChessPiece(white, rook);
        ChessPiece whiteAPawn = new ChessPiece(white, pawn);
        ChessPiece whiteBPawn = new ChessPiece(white, pawn);
        ChessPiece whiteCPawn = new ChessPiece(white, pawn);
        ChessPiece whiteDPawn = new ChessPiece(white, pawn);
        ChessPiece whiteEPawn = new ChessPiece(white, pawn);
        ChessPiece whiteFPawn = new ChessPiece(white, pawn);
        ChessPiece whiteGPawn = new ChessPiece(white, pawn);
        ChessPiece whiteHPawn = new ChessPiece(white, pawn);

        /**
         * Create Black Piece Objects
         */
        ChessPiece blackARook = new ChessPiece(black, rook);
        ChessPiece blackBKnight = new ChessPiece(black, knight);
        ChessPiece blackCBishop = new ChessPiece(black, bishop);
        ChessPiece blackQueen = new ChessPiece(black, queen);
        ChessPiece blackKing = new ChessPiece(black, king);
        ChessPiece blackFBishop = new ChessPiece(black, bishop);
        ChessPiece blackGKnight = new ChessPiece(black, knight);
        ChessPiece blackHRook = new ChessPiece(black, rook);
        ChessPiece blackAPawn = new ChessPiece(black, pawn);
        ChessPiece blackBPawn = new ChessPiece(black, pawn);
        ChessPiece blackCPawn = new ChessPiece(black, pawn);
        ChessPiece blackDPawn = new ChessPiece(black, pawn);
        ChessPiece blackEPawn = new ChessPiece(black, pawn);
        ChessPiece blackFPawn = new ChessPiece(black, pawn);
        ChessPiece blackGPawn = new ChessPiece(black, pawn);
        ChessPiece blackHPawn = new ChessPiece(black, pawn);

        addPiece(a1, whiteARook);
        addPiece(b1, whiteBKnight);
        addPiece(c1, whiteCBishop);
        addPiece(d1, whiteQueen);
        addPiece(e1, whiteKing);
        addPiece(f1, whiteFBishop);
        addPiece(g1, whiteGKnight);
        addPiece(h1, whiteHRook);
        addPiece(a2, whiteAPawn);
        addPiece(b2, whiteBPawn);
        addPiece(c2, whiteCPawn);
        addPiece(d2, whiteDPawn);
        addPiece(e2, whiteEPawn);
        addPiece(f2, whiteFPawn);
        addPiece(g2, whiteGPawn);
        addPiece(h2, whiteHPawn);

    }
}
