package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import chess.ChessBoard;
import chess.*;

import static ui.EscapeSequences.*;

public class DrawChessBoard {

    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_CHARS = 1;
    private static final int LINE_WIDTH_IN_CHARS = 1;

    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        ChessPosition highlightedPiece = new ChessPosition(2, 1);
        drawChessBoard(game, "black", highlightedPiece);
    }

    public static void drawChessBoard(ChessGame game, String perspective, ChessPosition highlightedPiece) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        if (perspective.equalsIgnoreCase("white")) {
            drawWhiteHeaders(out);
            drawBoard(game, out, "white", highlightedPiece);
            drawWhiteHeaders(out);
        } else {
            drawBlackHeaders(out);
            drawBoard(game, out, "black", highlightedPiece);
            drawBlackHeaders(out);
        }

        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT);
    }

    private static void drawWhiteHeaders(PrintStream out) {

        setBlack(out);

        drawHeader(out, SPACE);
        String[] headers = { "a", "b", "c", "d", "e", "f", "g", "h"};
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, THIN_SPACE+headers[boardCol]+THIN_SPACE);
        }
        drawHeader(out, SPACE);

        out.println();
    }

    private static void drawBlackHeaders(PrintStream out) {

        setBlack(out);

        drawHeader(out, SPACE);
        String[] headers = { "h", "g", "f", "e", "d", "c", "b", "a"};
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, THIN_SPACE+headers[boardCol]+THIN_SPACE);
        }
        drawHeader(out, SPACE);

        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(TWO_THIN_SPACES + headerText + TWO_THIN_SPACES);

        setBlack(out);
    }

    private static void drawBoard(ChessGame game, PrintStream out, String perspective, ChessPosition highlightedPiece) {

        for (int boardRow = 1; boardRow <= BOARD_SIZE_IN_SQUARES; ++boardRow) {

            drawRowOfSquares(out, game, boardRow, perspective.equalsIgnoreCase("black") ? "black" : "white", highlightedPiece);

            if (boardRow <= BOARD_SIZE_IN_SQUARES - 1) {
                setBlack(out);
            }
        }
    }

    private static void drawRowOfSquares(PrintStream out, ChessGame game, int boardRow, String perspective, ChessPosition highlightedPiece) {

        Collection<ChessMove> validMoves;
        Collection<ChessPosition> squaresToHighlight = new HashSet<>();
        boolean shouldBeHighlighted = false;

        if (game.getBoard().getPiece(highlightedPiece) != null) {
            validMoves = game.validMoves(highlightedPiece);
            for (var move : validMoves) {
                squaresToHighlight.add(move.getEndPosition());
            }
            squaresToHighlight.add(highlightedPiece);
        }
        drawHeader(out, THIN_SPACE+(perspective.equalsIgnoreCase("white") ? 9-boardRow : boardRow)+THIN_SPACE);
        boolean isWhitePerspective = perspective.equalsIgnoreCase("white");
        for (int boardCol = 1; boardCol <= BOARD_SIZE_IN_SQUARES; ++boardCol) {
            ChessPiece piece;
            ChessPosition currentPosition = isWhitePerspective ? new ChessPosition(9 - boardRow, boardCol) : new ChessPosition(boardRow, 9-boardCol);
            for(var square : squaresToHighlight) {
                if (currentPosition.equals(square)) {
                    shouldBeHighlighted = true;
                    break;
                }
                else {
                    shouldBeHighlighted = false;
                }
            }
            if (isWhitePerspective) {
                piece = game.getBoard().getPiece(new ChessPosition(9-boardRow, boardCol));
            }
            else {
                piece = game.getBoard().getPiece(new ChessPosition(boardRow, 9-boardCol));
            }
            String pieceOutput;
            if (piece == null) {
                pieceOutput = EMPTY;
            }
            else {
                pieceOutput = convertPieceToString(piece);
            }
            if (boardRow % 2 ==0) {
                if (boardCol % 2 == 0) {
                    printLightSquare(out, pieceOutput, shouldBeHighlighted);
                }
                else {
                    printDarkSquare(out, pieceOutput, shouldBeHighlighted);
                }
            }
            else {
                if (boardCol % 2 == 0) {
                    printDarkSquare(out, pieceOutput, shouldBeHighlighted);
                }
                else {
                    printLightSquare(out, pieceOutput, shouldBeHighlighted);
                }
            }

            setBlack(out);
        }
        drawHeader(out, THIN_SPACE+(perspective.equalsIgnoreCase("white") ? 9-boardRow : boardRow)+THIN_SPACE);
        out.println();
    }

    private static String convertPieceToString(ChessPiece piece) {
        var pieceType = piece.getPieceType();
        return switch (pieceType) {
            case ChessPiece.PieceType.ROOK -> piece.getTeamColor() == ChessGame.TeamColor.BLACK ? BLACK_ROOK : WHITE_ROOK;
            case ChessPiece.PieceType.KNIGHT -> piece.getTeamColor() == ChessGame.TeamColor.BLACK ? BLACK_KNIGHT : WHITE_KNIGHT;
            case ChessPiece.PieceType.BISHOP -> piece.getTeamColor() == ChessGame.TeamColor.BLACK ? BLACK_BISHOP : WHITE_BISHOP;
            case ChessPiece.PieceType.QUEEN -> piece.getTeamColor() == ChessGame.TeamColor.BLACK ? BLACK_QUEEN : WHITE_QUEEN;
            case ChessPiece.PieceType.KING -> piece.getTeamColor() == ChessGame.TeamColor.BLACK ? BLACK_KING : WHITE_KING;
            case ChessPiece.PieceType.PAWN -> piece.getTeamColor() == ChessGame.TeamColor.BLACK ? BLACK_PAWN : WHITE_PAWN;
        };
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void printLightSquare(PrintStream out, String piece, boolean shouldBeHighlighted) {
        if (shouldBeHighlighted) {
            out.print(SET_BG_COLOR_LIGHT_SQUARE_HIGHLIGHT);
        } else {
            out.print(SET_BG_COLOR_LIGHT_SQUARE);
        }
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(piece);

        setWhite(out);
    }

    private static void printDarkSquare(PrintStream out, String piece, boolean shouldBeHighlighted) {
        if (shouldBeHighlighted) {
            out.print(SET_BG_COLOR_DARK_SQUARE_HIGHLIGHT);
        } else {
            out.print(SET_BG_COLOR_DARK_SQUARE);
        }
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(piece);

        setBlack(out);
    }
}