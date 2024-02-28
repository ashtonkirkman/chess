package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;

    private final ChessPosition[] a = new ChessPosition[9];
    private final ChessPosition[] b = new ChessPosition[9];
    private final ChessPosition[] c = new ChessPosition[9];
    private final ChessPosition[] d = new ChessPosition[9];
    private final ChessPosition[] e = new ChessPosition[9];
    private final ChessPosition[] f = new ChessPosition[9];
    private final ChessPosition[] g = new ChessPosition[9];
    private final ChessPosition[] h = new ChessPosition[9];

    public ChessGame() {
        this.initializeChessPositions();
        this.teamTurn = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();
    }

    private void initializeChessPositions() {
        a[0] = null; b[0] = null; c[0] = null; d[0] = null; e[0] = null; f[0] = null; g[0] = null; h[0] = null;
        for (int i = 1; i < 9; i++) {
            a[i] = new ChessPosition(i, 1);
            b[i] = new ChessPosition(i, 2);
            c[i] = new ChessPosition(i, 3);
            d[i] = new ChessPosition(i, 4);
            e[i] = new ChessPosition(i, 5);
            f[i] = new ChessPosition(i, 6);
            g[i] = new ChessPosition(i, 7);
            h[i] = new ChessPosition(i, 8);
        }
    }

    public static void main(String[] args) {

        String filename = "shared/src/main/java/chess/gameMoves.txt";

        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String gameMoves;
            while ((gameMoves = reader.readLine()) != null) {
                stringBuilder.append(gameMoves);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        if (currentPiece.getPieceType() == ChessPiece.PieceType.KING) {
            if(canCastleKingside(currentColor)) {
                validMoves.add(new ChessMove(startPosition, new ChessPosition(startPosition.getRow(), startPosition.getColumn() + 2), null));
            }
            if(canCastleQueenside(currentColor)) {
                validMoves.add(new ChessMove(startPosition, new ChessPosition(startPosition.getRow(), startPosition.getColumn() - 2), null));
            }
        }
        return validMoves;
    }

    private boolean canCastleKingside(TeamColor teamTurn) {
        ChessPosition kingPosition = null;
        ChessPosition rookPosition = null;
        ChessPiece king = null;
        ChessPiece rook = null;
        if(teamTurn == TeamColor.WHITE) {
            kingPosition = e[1];
            rookPosition = h[1];
        }
        else {
            kingPosition = e[8];
            rookPosition = h[8];
        }
        king = board.getPiece(kingPosition);
        rook = board.getPiece(rookPosition);
        if((king == null || rook == null) || (king.getPieceType() != ChessPiece.PieceType.KING || rook.getPieceType() != ChessPiece.PieceType.ROOK)) {
            return false;
        }
        for(int i = 6; i < 8; i++) {
            if (checkRanks(teamTurn, kingPosition, i)) return false;
        }
        return true;
    }

    private boolean checkRanks(TeamColor teamTurn, ChessPosition kingPosition, int i) {
        if(board.getPiece(new ChessPosition(kingPosition.getRow(), i)) != null) {
            return true;
        }
        testMove(new ChessMove(kingPosition, new ChessPosition(kingPosition.getRow(), i), null));
        if(isInCheck(teamTurn)) {
            return true;
        }
        undoMove(new ChessMove(kingPosition, new ChessPosition(kingPosition.getRow(), i), null), null);
        return false;
    }

    private boolean canCastleQueenside(TeamColor teamTurn) {
        ChessPosition kingPosition = null;
        ChessPosition rookPosition = null;
        ChessPiece king = null;
        ChessPiece rook = null;
        if(teamTurn == TeamColor.WHITE) {
            kingPosition = e[1];
            rookPosition = a[1];
        }
        else {
            kingPosition = e[8];
            rookPosition = a[8];
        }
        king = board.getPiece(kingPosition);
        rook = board.getPiece(rookPosition);
        if((king == null || rook == null) || (king.getPieceType() != ChessPiece.PieceType.KING || rook.getPieceType() != ChessPiece.PieceType.ROOK)) {
            return false;
        }
        for(int i = 2; i < 5; i++) {
            if (checkRanks(teamTurn, kingPosition, i)) return false;
        }
        return true;
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
        if(currentPiece.getPieceType() == ChessPiece.PieceType.KING) {
            if(Math.abs(startPosition.getColumn() - destination.getColumn()) == 2) {
                if(destination.getColumn() > startPosition.getColumn()) {
                    board.addPiece(new ChessPosition(startPosition.getRow(), 8), null);
                    board.addPiece(new ChessPosition(startPosition.getRow(), 6), new ChessPiece(currentPiece.getTeamColor(), ChessPiece.PieceType.ROOK));
                }
                else {
                    board.addPiece(new ChessPosition(startPosition.getRow(), 1), null);
                    board.addPiece(new ChessPosition(startPosition.getRow(), 4), new ChessPiece(currentPiece.getTeamColor(), ChessPiece.PieceType.ROOK));
                }
            }
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
        if(isInCheckmate(TeamColor.WHITE)) {
            return "Black Wins!\n\n" + board;
        }
        else if(isInCheckmate(TeamColor.BLACK)) {
            return "White Wins!\n\n" + board;
        }
        else if(isInStalemate(TeamColor.WHITE) || isInStalemate(TeamColor.BLACK)) {
            return "Stalemate!\n\n" + board;
        }

        return teamTurn + " to Play\n\n" + board;
    }
}

