package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

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


        ChessGame myGame = new ChessGame();
        ChessBoard currentBoard = myGame.getBoard();
        System.out.println(myGame);

        myGame.parseAlgebraicMoves("1. Nf3 Nf6 2. c4 g6 3. Nc3 Bg7 4. d4 O-O 5. Bf4 d5 6. Qb3 dxc4 7. Qxc4 c6 8. e4 Nbd7 9. Rd1 Nb6 10. Qc5 Bg4 11. Bg5 Na4 12. Qa3 Nxc3 13. bxc3 Nxe4 14. Bxe7 Qb6 15. Bc4 Nxc3 16. Bc5 Rfe8+ 17. Kf1 Be6 18. Bxb6 Bxc4+ 19. Kg1 Ne2+ 20. Kf1 Nxd4+ 21. Kg1 Ne2+ 22. Kf1 Nc3+ 23. Kg1 axb6 24. Qb4 Ra4 25. Qxb6 Nxd1 26. h3 Rxa2 27. Kh2 Nxf2 28. Re1 Rxe1 29. Qd8+ Bf8 30. Nxe1 Bd5 31. Nf3 Ne4 32. Qb8 b5 33. h4 h5 34. Ne5 Kg7 35. Kg1 Bc5+ 36. Kf1 Ng3+ 37. Ke1 Bb4+ 38. Kd1 Bb3+ 39. Kc1 Ne2+ 40. Kb1 Nc3+ 41. Kc1 Rc2#");
        System.out.println(myGame);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamTurn;
    }

    private void attemptToMakeMove(ChessMove move) {
        try {
            this.makeMove(move);
        } catch (InvalidMoveException ex) {
            System.out.println(ex.getMessage());
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
//        if(king.hasMoved() || rook.hasMoved()) {
//            return false;
//        }
        for(int i = 6; i < 8; i++) {
            if(board.getPiece(new ChessPosition(kingPosition.getRow(), i)) != null) {
                return false;
            }
            testMove(new ChessMove(kingPosition, new ChessPosition(kingPosition.getRow(), i), null));
            if(isInCheck(teamTurn)) {
                return false;
            }
            undoMove(new ChessMove(kingPosition, new ChessPosition(kingPosition.getRow(), i), null), null);
        }
        return true;
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
//        if(king.hasMoved() || rook.hasMoved()) {
//            return false;
//        }
        for(int i = 2; i < 5; i++) {
            if(board.getPiece(new ChessPosition(kingPosition.getRow(), i)) != null) {
                return false;
            }
            testMove(new ChessMove(kingPosition, new ChessPosition(kingPosition.getRow(), i), null));
            if(isInCheck(teamTurn)) {
                return false;
            }
            undoMove(new ChessMove(kingPosition, new ChessPosition(kingPosition.getRow(), i), null), null);
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

    private void checkToAddMove(ChessPiece.PieceType pieceType, ChessPosition endPosition) {

        ChessPosition[][] currentPosition = new ChessPosition[8][8];
        ChessPiece[][] currentPiece = new ChessPiece[8][8];
        ChessPosition startPosition = null;
        boolean moveAdded = false;

        for(int i = 0; i < 8; i++) {
            if(moveAdded) {
                break;
            }
            for(int j = 0; j < 8; j++){
                currentPosition[i][j] = new ChessPosition(i+1, j+1);
                currentPiece[i][j] = board.getPiece(currentPosition[i][j]);
                if(currentPiece[i][j] == null) {
                    continue;
                }
                if(currentPiece[i][j].getPieceType() == pieceType && currentPiece[i][j].getTeamColor() == teamTurn) {
                    startPosition = currentPosition[i][j];
                    if (validMoves(startPosition).contains(new ChessMove(startPosition, endPosition, null))) {
                        attemptToMakeMove(new ChessMove(startPosition, endPosition, null));
                        moveAdded = true;
                        break;
                    }
                }
            }
        }
    }

    private void parseAlgebraicMoves(String moveString) {
        String[] moveTokens = moveString.split("\\s*\\d+\\.\\s*");
        ChessPosition startPosition = new ChessPosition(1, 1);
        ChessPosition endPosition = null;

        ChessPosition[][] currentPosition = new ChessPosition[8][8];
        ChessPiece[][] currentPiece = new ChessPiece[8][8];

        for(String token : moveTokens) {
            String[] moveParts = token.trim().split("\\s+");
            for(String part : moveParts) {

                if(part.matches("[KQRBN]x?[a-h][1-8]\\+?")) {

                    int rank = part.matches("[KQRBN][a-h][1-8]\\+?") ? Character.getNumericValue(part.charAt(2)) : Character.getNumericValue(part.charAt(3));
                    int file = part.matches("[KQRBN][a-h][1-8]\\+?") ? part.charAt(1) - 'a' + 1 : part.charAt(2) - 'a' + 1;

                    char piece = part.charAt(0);

                    endPosition = new ChessPosition(rank, file);

                    switch(piece) {
                        case 'K':
                            checkToAddMove(ChessPiece.PieceType.KING, endPosition);
                            break;
                        case 'Q':
                            checkToAddMove(ChessPiece.PieceType.QUEEN, endPosition);
                            break;
                        case 'R':
                            checkToAddMove(ChessPiece.PieceType.ROOK, endPosition);
                            break;
                        case 'B':
                            checkToAddMove(ChessPiece.PieceType.BISHOP, endPosition);
                            break;
                        case 'N':
                            checkToAddMove(ChessPiece.PieceType.KNIGHT, endPosition);
                            break;
                    }
                }
                else if(part.matches("[KQRBN][a-h][a-h][1-8]\\+?")) {

                    int rank = Character.getNumericValue(part.charAt(3));
                    int file = part.charAt(2) - 'a' + 1;
                    int startFile = part.charAt(1) - 'a' + 1;

                    char piece = part.charAt(0);

                    endPosition = new ChessPosition(rank, file);

                    switch(piece) {
                        case 'K':
                            for(int i = 0; i < 8; i++) {
                                currentPosition[i][startFile-1] = new ChessPosition(i+1, startFile);
                                currentPiece[i][startFile-1] = board.getPiece(currentPosition[i][startFile-1]);
                                if(currentPiece[i][startFile-1] == null) {
                                    continue;
                                }
                                if(currentPiece[i][startFile-1].getPieceType() == ChessPiece.PieceType.KING && currentPiece[i][startFile-1].getTeamColor() == teamTurn) {
                                    startPosition = currentPosition[i][startFile-1];
                                    if (validMoves(startPosition).contains(new ChessMove(startPosition, endPosition, null))) {
                                        attemptToMakeMove(new ChessMove(startPosition, endPosition, null));
                                        break;
                                    }
                                }
                            }
                            break;
                        case 'Q':
                            for(int i = 0; i < 8; i++) {
                                currentPosition[i][startFile-1] = new ChessPosition(i+1, startFile);
                                currentPiece[i][startFile-1] = board.getPiece(currentPosition[i][startFile-1]);
                                if(currentPiece[i][startFile-1] == null) {
                                    continue;
                                }
                                if(currentPiece[i][startFile-1].getPieceType() == ChessPiece.PieceType.QUEEN && currentPiece[i][startFile-1].getTeamColor() == teamTurn) {
                                    startPosition = currentPosition[i][startFile-1];
                                    if (validMoves(startPosition).contains(new ChessMove(startPosition, endPosition, null))) {
                                        attemptToMakeMove(new ChessMove(startPosition, endPosition, null));
                                        break;
                                    }
                                }
                            }
                            break;
                        case 'R':
                            for(int i = 0; i < 8; i++) {
                                currentPosition[i][startFile-1] = new ChessPosition(i+1, startFile);
                                currentPiece[i][startFile-1] = board.getPiece(currentPosition[i][startFile-1]);
                                if(currentPiece[i][startFile-1] == null) {
                                    continue;
                                }
                                if(currentPiece[i][startFile-1].getPieceType() == ChessPiece.PieceType.ROOK && currentPiece[i][startFile-1].getTeamColor() == teamTurn) {
                                    startPosition = currentPosition[i][startFile-1];
                                    if (validMoves(startPosition).contains(new ChessMove(startPosition, endPosition, null))) {
                                        attemptToMakeMove(new ChessMove(startPosition, endPosition, null));
                                        break;
                                    }
                                }
                            }
                            break;
                        case 'B':
                            for(int i = 0; i < 8; i++) {
                                currentPosition[i][startFile-1] = new ChessPosition(i+1, startFile);
                                currentPiece[i][startFile-1] = board.getPiece(currentPosition[i][startFile-1]);
                                if(currentPiece[i][startFile-1] == null) {
                                    continue;
                                }
                                if(currentPiece[i][startFile-1].getPieceType() == ChessPiece.PieceType.BISHOP && currentPiece[i][startFile-1].getTeamColor() == teamTurn) {
                                    startPosition = currentPosition[i][startFile-1];
                                    if (validMoves(startPosition).contains(new ChessMove(startPosition, endPosition, null))) {
                                        attemptToMakeMove(new ChessMove(startPosition, endPosition, null));
                                        break;
                                    }
                                }
                            }
                            break;
                        case 'N':
                            for(int i = 0; i < 8; i++) {
                                currentPosition[i][startFile-1] = new ChessPosition(i+1, startFile);
                                currentPiece[i][startFile-1] = board.getPiece(currentPosition[i][startFile-1]);
                                if(currentPiece[i][startFile-1] == null) {
                                    continue;
                                }
                                if(currentPiece[i][startFile-1].getPieceType() == ChessPiece.PieceType.KNIGHT && currentPiece[i][startFile-1].getTeamColor() == teamTurn) {
                                    startPosition = currentPosition[i][startFile-1];
                                    if (validMoves(startPosition).contains(new ChessMove(startPosition, endPosition, null))) {
                                        attemptToMakeMove(new ChessMove(startPosition, endPosition, null));
                                        break;
                                    }
                                }
                            }
                            break;
                    }
                }
                else if(part.matches("[KQRBN][1-8][a-h][1-8]\\+?")) {
                    int rank = Character.getNumericValue(part.charAt(3));
                    int file = part.charAt(2) - 'a' + 1;
                    int startRank = Character.getNumericValue(part.charAt(1));

                    char piece = part.charAt(0);

                    endPosition = new ChessPosition(rank, file);

                    switch(piece) {
                        case 'K':
                            for (int i = 0; i < 8; i++) {
                                currentPosition[startRank - 1][i] = new ChessPosition(startRank, i + 1);
                                currentPiece[startRank - 1][i] = board.getPiece(currentPosition[startRank - 1][i]);
                                if (currentPiece[startRank - 1][i] == null) {
                                    continue;
                                }
                                if (currentPiece[startRank-1][i].getPieceType() == ChessPiece.PieceType.KING && currentPiece[startRank-1][i].getTeamColor() == teamTurn) {
                                    startPosition = currentPosition[startRank-1][i];
                                    if (validMoves(startPosition).contains(new ChessMove(startPosition, endPosition, null))) {
                                        attemptToMakeMove(new ChessMove(startPosition, endPosition, null));
                                        break;
                                    }
                                }
                            }
                            break;
                        case 'Q':
                            for (int i = 0; i < 8; i++) {
                                currentPosition[startRank - 1][i] = new ChessPosition(startRank, i + 1);
                                currentPiece[startRank - 1][i] = board.getPiece(currentPosition[startRank - 1][i]);
                                if (currentPiece[startRank - 1][i] == null) {
                                    continue;
                                }
                                if (currentPiece[startRank-1][i].getPieceType() == ChessPiece.PieceType.QUEEN && currentPiece[startRank-1][i].getTeamColor() == teamTurn) {
                                    startPosition = currentPosition[startRank-1][i];
                                    if (validMoves(startPosition).contains(new ChessMove(startPosition, endPosition, null))) {
                                        attemptToMakeMove(new ChessMove(startPosition, endPosition, null));
                                        break;
                                    }
                                }
                            }
                            break;
                        case 'R':
                            for (int i = 0; i < 8; i++) {
                                currentPosition[startRank - 1][i] = new ChessPosition(startRank, i + 1);
                                currentPiece[startRank - 1][i] = board.getPiece(currentPosition[startRank - 1][i]);
                                if (currentPiece[startRank - 1][i] == null) {
                                    continue;
                                }
                                if (currentPiece[startRank-1][i].getPieceType() == ChessPiece.PieceType.ROOK && currentPiece[startRank-1][i].getTeamColor() == teamTurn) {
                                    startPosition = currentPosition[startRank-1][i];
                                    if (validMoves(startPosition).contains(new ChessMove(startPosition, endPosition, null))) {
                                        attemptToMakeMove(new ChessMove(startPosition, endPosition, null));
                                        break;
                                    }
                                }
                            }
                            break;
                        case 'B':
                            for (int i = 0; i < 8; i++) {
                                currentPosition[startRank - 1][i] = new ChessPosition(startRank, i + 1);
                                currentPiece[startRank - 1][i] = board.getPiece(currentPosition[startRank - 1][i]);
                                if (currentPiece[startRank - 1][i] == null) {
                                    continue;
                                }
                                if (currentPiece[startRank-1][i].getPieceType() == ChessPiece.PieceType.BISHOP && currentPiece[startRank-1][i].getTeamColor() == teamTurn) {
                                    startPosition = currentPosition[startRank-1][i];
                                    if (validMoves(startPosition).contains(new ChessMove(startPosition, endPosition, null))) {
                                        attemptToMakeMove(new ChessMove(startPosition, endPosition, null));
                                        break;
                                    }
                                }
                            }
                            break;
                        case 'N':
                            for (int i = 0; i < 8; i++) {
                                currentPosition[startRank - 1][i] = new ChessPosition(startRank, i + 1);
                                currentPiece[startRank - 1][i] = board.getPiece(currentPosition[startRank - 1][i]);
                                if (currentPiece[startRank - 1][i] == null) {
                                    continue;
                                }
                                if (currentPiece[startRank-1][i].getPieceType() == ChessPiece.PieceType.KNIGHT && currentPiece[startRank-1][i].getTeamColor() == teamTurn) {
                                    startPosition = currentPosition[startRank-1][i];
                                    if (validMoves(startPosition).contains(new ChessMove(startPosition, endPosition, null))) {
                                        attemptToMakeMove(new ChessMove(startPosition, endPosition, null));
                                        break;
                                    }
                                }
                            }
                            break;
                    }
                }
                else if(part.matches("[a-h][1-8]\\+?")) {
                    int rank = Character.getNumericValue(part.charAt(1));
                    int file = part.charAt(0) - 'a' + 1;
                    endPosition = new ChessPosition(rank, file);

                    for(int i = 1; i < 7; i++) {
                        currentPosition[i][file-1] = new ChessPosition(i+1, file);
                        currentPiece[i][file-1] = board.getPiece(currentPosition[i][file-1]);
                        if(currentPiece[i][file-1] == null) {
                            continue;
                        }
                        if(currentPiece[i][file-1].getPieceType() == ChessPiece.PieceType.PAWN && currentPiece[i][file-1].getTeamColor() == teamTurn) {
                            startPosition = currentPosition[i][file-1];
                            if (validMoves(startPosition).contains(new ChessMove(startPosition, endPosition, null))) {
                                attemptToMakeMove(new ChessMove(startPosition, endPosition, null));
                                break;
                            }
                        }
                    }
                }
                else if(part.matches("[a-h]x[a-h][1-8]\\+?")) {
                    int rank = Character.getNumericValue(part.charAt(3));
                    int file = part.charAt(2) - 'a' + 1;
                    endPosition = new ChessPosition(rank, file);

                    int startFile = part.charAt(0) - 'a' + 1;

                    for(int i = 1; i < 7; i++) {
                        currentPosition[i][startFile-1] = new ChessPosition(i+1, startFile);
                        currentPiece[i][startFile-1] = board.getPiece(currentPosition[i][startFile-1]);
                        if(currentPiece[i][startFile-1] == null) {
                            continue;
                        }
                        if(currentPiece[i][startFile-1].getPieceType() == ChessPiece.PieceType.PAWN && currentPiece[i][startFile-1].getTeamColor() == teamTurn) {
                            startPosition = currentPosition[i][startFile-1];
                            if (validMoves(startPosition).contains(new ChessMove(startPosition, endPosition, null))) {
                                attemptToMakeMove(new ChessMove(startPosition, endPosition, null));
                                break;
                            }
                        }
                    }
                }
                else if(part.matches("O-O")) {
                    if(teamTurn == TeamColor.WHITE) {
                        attemptToMakeMove(new ChessMove(e[1], g[1], null));
                    }
                    else {
                        attemptToMakeMove(new ChessMove(e[8], g[8], null));
                    }
                }
                else if(part.matches("O-O-O")) {
                    if(teamTurn == TeamColor.WHITE) {
                        attemptToMakeMove(new ChessMove(e[1], c[1], null));
                    }
                    else {
                        attemptToMakeMove(new ChessMove(e[8], c[8], null));
                    }
                }
                else {
                    System.out.println("Invalid move: " + part);
                }
                System.out.println(this);
            }
        }
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

