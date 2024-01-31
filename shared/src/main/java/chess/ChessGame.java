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
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        setBoard(board);
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
        System.out.println(myGame.validMoves(a[2]));
        System.out.println(myGame.isInCheck(myGame.teamTurn));
        System.out.println(currentBoard);
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

    private void testMove(ChessMove move) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition destination = move.getEndPosition();
        ChessPiece currentPiece = board.getPiece(startPosition);
        board.addPiece(destination, currentPiece);
        board.addPiece(startPosition, null);
    }

    private void undoMove(ChessMove move) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition destination = move.getEndPosition();
        ChessPiece currentPiece = board.getPiece(destination);
        board.addPiece(startPosition, currentPiece);
        board.addPiece(destination, null);
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
        Collection<ChessMove> potentiallyValidMoves = currentPiece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new HashSet<>();
        for (ChessMove move : potentiallyValidMoves) {
            this.testMove(move);
            if(!this.isInCheck(this.teamTurn)) {
                validMoves.add(move);
            }
            this.undoMove(move);
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
        if (currentPiece == null) {
            throw new InvalidMoveException("Selected Piece is null");
        }
        if (currentPiece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Wrong team's turn");
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
throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
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
}

