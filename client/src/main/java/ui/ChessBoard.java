package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static ui.EscapeSequences.*;

public class ChessBoard {

    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_CHARS = 1;
    private static final int LINE_WIDTH_IN_CHARS = 1;
    private static Random rand = new Random();

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawBlackHeaders(out);
        drawChessBoard(out);
        drawBlackHeaders(out);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawWhiteHeaders(PrintStream out) {

        setBlack(out);

        out.print(" ");
        String[] headers = { "A", "B", "C", "D", "E", "F", "G", "H" };
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[boardCol]);

            if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                out.print(SPACE.repeat(LINE_WIDTH_IN_CHARS));
                out.print(" ");
            }
        }

        out.println();
    }

    private static void drawBlackHeaders(PrintStream out) {

        setBlack(out);

        drawHeader(out, " ");
        String[] headers = { "H", "G", "F", "E", "D", "C", "B", "A" };
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[boardCol]);
        }
        drawHeader(out, " ");

        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(" " + headerText + SPACE);

        setBlack(out);
    }

    private static void drawChessBoard(PrintStream out) {

            for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {

                drawRowOfSquares(out, boardRow);

                if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
                    setBlack(out);
                }
            }
    }

    private static void drawRowOfSquares(PrintStream out, int boardRow) {

        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_CHARS; ++squareRow) {
            drawHeader(out, ""+(boardRow + 1));
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {

                if (squareRow == SQUARE_SIZE_IN_CHARS / 2) {
                    int prefixLength = SQUARE_SIZE_IN_CHARS / 2;
                    int suffixLength = SQUARE_SIZE_IN_CHARS - prefixLength - 1;

                    out.print(EMPTY.repeat(prefixLength));
                    String piece = selectPiece(boardRow, boardCol);
                    String pieceColor = SET_TEXT_COLOR_BLACK;
                    if (boardRow % 2 == 0) {
                        if (boardCol % 2 == 0) {
                            printDarkSquare(out, piece, pieceColor);
                        } else {
                            printLightSquare(out, piece, pieceColor);
                        }
                    } else {
                        if (boardCol % 2 == 0) {
                            printLightSquare(out, piece, pieceColor);
                        } else {
                            printDarkSquare(out, piece, pieceColor);
                        }
                    }
                    out.print(EMPTY.repeat(suffixLength));
                }
                else {
                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_CHARS));
                }

                setBlack(out);
            }
            drawHeader(out, ""+(boardRow + 1));
            out.println();
        }
    }

    private static String selectColor(int boardRow) {
        return switch (boardRow) {
            case 0, 1 -> SET_TEXT_COLOR_RED;
            default -> SET_TEXT_COLOR_BLUE;
        };
    }

    private static String selectPiece(int boardRow, int boardCol) {
        if (boardRow == 0) {
            return switch (boardCol) {
                case 0, 7 -> WHITE_ROOK;
                case 1, 6 -> WHITE_KNIGHT;
                case 2, 5 -> WHITE_BISHOP;
                case 3 -> WHITE_KING;
                case 4 -> WHITE_QUEEN;
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
                case 3 -> BLACK_KING;
                case 4 -> BLACK_QUEEN;
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

    private static void printLightSquare(PrintStream out, String piece, String pieceColor) {
        out.print(SET_BG_COLOR_LIGHT_SQUARE);
        out.print(pieceColor);

        out.print(piece);

        setWhite(out);
    }

    private static void printDarkSquare(PrintStream out, String piece, String pieceColor) {
        out.print(SET_BG_COLOR_DARK_SQUARE);
        out.print(pieceColor);

        out.print(piece);

        setBlack(out);
    }
}