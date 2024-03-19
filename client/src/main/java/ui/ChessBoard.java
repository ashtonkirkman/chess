package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static ui.EscapeSequences.*;

public class ChessBoard {

    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_CHARS = 1;
    private static final int LINE_WIDTH_IN_CHARS = 1;

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        drawBlackHeaders(out);
        drawBlackChessBoard(out);
        drawBlackHeaders(out);

        out.println();

        drawWhiteHeaders(out);
        drawWhiteChessBoard(out);
        drawWhiteHeaders(out);


        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
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

    private static void drawBlackChessBoard(PrintStream out) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {

            drawRowOfSquares(out, boardRow, "black");

            if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
                setBlack(out);
            }
        }
    }

    private static void drawWhiteChessBoard(PrintStream out) {

        for (int boardRow = BOARD_SIZE_IN_SQUARES - 1; boardRow >= 0; --boardRow) {

            drawRowOfSquares(out, boardRow, "white");

            if (boardRow > 0) {
                setBlack(out);
            }
        }
    }

    private static void drawRowOfSquares(PrintStream out, int boardRow, String perspective) {

        drawHeader(out, THIN_SPACE+(boardRow + 1)+THIN_SPACE);
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {

            String piece = selectPiece(boardRow, boardCol, perspective);
            if (boardRow % 2 == 0) {
                if (boardCol % 2 == 0) {
                    printDarkSquare(out, piece);
                } else {
                    printLightSquare(out, piece);
                }
            } else {
                if (boardCol % 2 == 0) {
                    printLightSquare(out, piece);
                } else {
                    printDarkSquare(out, piece);
                }
            }

            setBlack(out);
        }
        drawHeader(out, THIN_SPACE+(boardRow + 1)+THIN_SPACE);
        out.println();
    }

    private static String selectPiece(int boardRow, int boardCol, String perspective) {

        if (boardRow == 0) {
            return switch (boardCol) {
                case 0, 7 -> WHITE_ROOK;
                case 1, 6 -> WHITE_KNIGHT;
                case 2, 5 -> WHITE_BISHOP;
                case 3 -> perspective.equals("black") ? WHITE_KING : WHITE_QUEEN;
                case 4 -> perspective.equals("black") ? WHITE_QUEEN: WHITE_KING;
                default -> EMPTY;
            };
        } else if (boardRow == 1) {
            return WHITE_PAWN;
        } else if (boardRow == 6) {
            return BLACK_PAWN;
        } else if (boardRow == 7) {
            return switch (boardCol) {
                case 0, 7 -> BLACK_ROOK;
                case 1, 6 -> BLACK_KNIGHT;
                case 2, 5 -> BLACK_BISHOP;
                case 3 -> perspective.equals("black") ? BLACK_KING: BLACK_QUEEN;
                case 4 -> perspective.equals("black") ? BLACK_QUEEN: BLACK_KING;
                default -> EMPTY;
            };
        }
        return EMPTY;
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void printLightSquare(PrintStream out, String piece) {
        out.print(SET_BG_COLOR_LIGHT_SQUARE);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(piece);

        setWhite(out);
    }

    private static void printDarkSquare(PrintStream out, String piece) {
        out.print(SET_BG_COLOR_DARK_SQUARE);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(piece);

        setBlack(out);
    }
}