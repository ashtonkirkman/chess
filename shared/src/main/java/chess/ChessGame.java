package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();
    }

    public static void main(String[] args) {
        ChessPosition[] a = new ChessPosition[9];
        ChessPosition[] b = new ChessPosition[9];
        ChessPosition[] c = new ChessPosition[9];
        ChessPosition[] d = new ChessPosition[9];
        ChessPosition[] e = new ChessPosition[9];
        ChessPosition[] f = new ChessPosition[9];
        ChessPosition[] g = new ChessPosition[9];
        ChessPosition[] h = new ChessPosition[9];
        a[0] = null; b[0] = null; c[0] = null; d[0] = null; e[0] = null; f[0] = null; g[0] = null; h[0] = null;
        for(int i = 1; i < 9; i++) {
            a[i] = new ChessPosition(i, 1);
            b[i] = new ChessPosition(i, 2);
            c[i] = new ChessPosition(i, 3);
            d[i] = new ChessPosition(i, 4);
            e[i] = new ChessPosition(i, 5);
            f[i] = new ChessPosition(i, 6);
            g[i] = new ChessPosition(i, 7);
            h[i] = new ChessPosition(i, 8);
        }

        ChessGame myGame = new ChessGame();
        ChessBoard currentBoard = myGame.getBoard();
        currentBoard.clearBoard();
        myGame.setBoard(currentBoard);
        System.out.println(myGame);

        currentBoard.addPiece(b[6], new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        myGame.setBoard(currentBoard);
        System.out.println(myGame);

        currentBoard.addPiece(e[6], new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        myGame.setBoard(currentBoard);
        System.out.println(myGame);

        currentBoard.addPiece(e[7], new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        myGame.setBoard(currentBoard);
        System.out.println(myGame);

        currentBoard.addPiece(f[6], new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        myGame.setBoard(currentBoard);
        System.out.println(myGame);

        currentBoard.addPiece(d[8], new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.KING));
        myGame.setBoard(currentBoard);
        System.out.println(myGame);

        try {
            myGame.makeMove(new ChessMove(e[7], e[8], ChessPiece.PieceType.BISHOP));
        } catch (InvalidMoveException ex) {
            System.out.println(ex.getMessage());
        }

        System.out.println(myGame);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    private ChessPiece testMove(ChessMove move) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition destination = move.getEndPosition();
        ChessPiece currentPiece = board.getPiece(startPosition);
        ChessPiece destinationPiece = board.getPiece(destination);
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();

        if(promotionPiece != null) {
            currentPiece = new ChessPiece(currentPiece.getTeamColor(), promotionPiece);
        }

        board.addPiece(destination, currentPiece);
        board.addPiece(startPosition, null);

        return destinationPiece;
    }

    private void undoMove(ChessMove move, ChessPiece capturedPiece) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition destination = move.getEndPosition();
        ChessPiece currentPiece = board.getPiece(destination);
        board.addPiece(startPosition, currentPiece);
        board.addPiece(destination, capturedPiece);
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece currentPiece = board.getPiece(startPosition);
        if (currentPiece == null) {
            return null;
        }
        TeamColor currentColor = currentPiece.getTeamColor();
        Collection<ChessMove> potentiallyValidMoves = currentPiece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new HashSet<>();
        ChessPiece capturedPiece;
        for (ChessMove move : potentiallyValidMoves) {
            capturedPiece = this.testMove(move);
            if(!this.isInCheck(currentColor)) {
                validMoves.add(move);
            }
            this.undoMove(move, capturedPiece);
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition destination = move.getEndPosition();
        ChessPiece currentPiece = board.getPiece(startPosition);
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();

        if(promotionPiece != null) {
            currentPiece = new ChessPiece(currentPiece.getTeamColor(), promotionPiece);
        }

        Collection<ChessMove> validMoves = this.validMoves(startPosition);
        if (currentPiece == null) {
            throw new InvalidMoveException("Selected Piece is null");
        }
        if (currentPiece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Wrong team's turn");
        }
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move: " + move);
        }
        board.addPiece(destination, currentPiece);
        board.addPiece(startPosition, null);
        if (teamTurn == TeamColor.WHITE) {
            teamTurn = TeamColor.BLACK;
        } else {
            teamTurn = TeamColor.WHITE;
        }
    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition[][] currentPosition = new ChessPosition[8][8];
        ChessPiece[][] currentPiece = new ChessPiece[8][8];
        ChessPosition kingPosition = null;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                currentPosition[i][j] = new ChessPosition(i + 1, j + 1);
                currentPiece[i][j] = board.getPiece(currentPosition[i][j]);
                if(currentPiece[i][j] == null) {
                    continue;
                }
                if (currentPiece[i][j].getPieceType() == ChessPiece.PieceType.KING && currentPiece[i][j].getTeamColor() == teamColor) {
                    kingPosition = currentPosition[i][j];
                }
            }
        }
        Collection<ChessMove> validMoves;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(currentPiece[i][j] == null) {
                    continue;
                }
                validMoves = currentPiece[i][j].pieceMoves(board, currentPosition[i][j]);
                if (validMoves == null) {
                    continue;
                }
                if (validMoves.contains(new ChessMove(currentPosition[i][j], kingPosition, null))) {
                    return true;
                }
                else if (validMoves.contains(new ChessMove(currentPosition[i][j], kingPosition, ChessPiece.PieceType.QUEEN))) {
                    return true;
                }
                else if (validMoves.contains(new ChessMove(currentPosition[i][j], kingPosition, ChessPiece.PieceType.ROOK))) {
                    return true;
                }
                else if (validMoves.contains(new ChessMove(currentPosition[i][j], kingPosition, ChessPiece.PieceType.BISHOP))) {
                    return true;
                }
                else if (validMoves.contains(new ChessMove(currentPosition[i][j], kingPosition, ChessPiece.PieceType.KNIGHT))) {
                    return true;
                }
            }
        }
        return false;
    }



    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(!this.isInCheck(teamColor)) {
            return false;
        }
        ChessPosition[][] currentPosition = new ChessPosition[8][8];
        ChessPiece[][] currentPiece = new ChessPiece[8][8];
        ChessPosition kingPosition = null;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++){
                currentPosition[i][j] = new ChessPosition(i+1, j+1);
                currentPiece[i][j] = board.getPiece(currentPosition[i][j]);
                if(currentPiece[i][j] == null) {
                    continue;
                }
                if(currentPiece[i][j].getPieceType() == ChessPiece.PieceType.KING && currentPiece[i][j].getTeamColor() == teamColor) {
                    kingPosition = currentPosition[i][j];
                    break;
                }
            }
            if(kingPosition != null) {
                break;
            }
        }
        Collection<ChessMove> validMoves = validMoves(kingPosition);
        return validMoves.isEmpty();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)) {
            return false;
        }
        ChessPosition[][] currentPosition = new ChessPosition[8][8];
        ChessPiece[][] currentPiece = new ChessPiece[8][8];

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++){
                currentPosition[i][j] = new ChessPosition(i+1, j+1);
                currentPiece[i][j] = board.getPiece(currentPosition[i][j]);
                if(currentPiece[i][j] == null) {
                    continue;
                }
                if(currentPiece[i][j].getTeamColor() == teamColor) {
                    Collection<ChessMove> validMoves = validMoves(currentPosition[i][j]);
                    if(!validMoves.isEmpty()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessGame chessGame)) return false;
        return teamTurn == chessGame.teamTurn && Objects.deepEquals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }

    @Override
    public String toString() {
//        if(isInCheckmate(TeamColor.WHITE)) {
//            return "Black Wins!\n\n" + board;
//        }
//        else if(isInCheckmate(TeamColor.BLACK)) {
//            return "White Wins!\n\n" + board;
//        }
//        else if(isInStalemate(TeamColor.WHITE) || isInStalemate(TeamColor.BLACK)) {
//            return "Stalemate!\n\n" + board;
//        }

        return teamTurn + " to Play\n\n" + board;
    }
}

