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

//        drawHeaders(out);

        drawChessBoard(out);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeaders(PrintStream out) {

        setBlack(out);

        String[] headers = { " A ", " B ", " C ", " D ", " E ", " F ", " G ", " H " };
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[boardCol]);

            if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                out.print(EMPTY.repeat(LINE_WIDTH_IN_CHARS));
            }
        }

        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        int prefixLength = SQUARE_SIZE_IN_CHARS / 2;
        int suffixLength = SQUARE_SIZE_IN_CHARS - prefixLength - 1;

        out.print(EMPTY.repeat(prefixLength));
        printHeaderText(out, headerText);
        out.print(EMPTY.repeat(suffixLength));
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_GREEN);

        out.print(player);

        setBlack(out);
    }

    private static void drawChessBoard(PrintStream out) {

            for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {

                drawRowOfSquares(out, boardRow);

                if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
//                    drawVerticalLine(out);
                    setBlack(out);
                }
            }
    }

    private static void drawRowOfSquares(PrintStream out, int boardRow) {

        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_CHARS; ++squareRow) {
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {

                if (squareRow == SQUARE_SIZE_IN_CHARS / 2) {
                    int prefixLength = SQUARE_SIZE_IN_CHARS / 2;
                    int suffixLength = SQUARE_SIZE_IN_CHARS - prefixLength - 1;

                    out.print(EMPTY.repeat(prefixLength));
                    String piece = selectPiece(boardRow, boardCol);
                    String pieceColor = SET_TEXT_COLOR_BLACK;
//                    String pieceColor = selectColor(boardRow);
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
                case 3 -> WHITE_QUEEN;
                case 4 -> WHITE_KING;
                default -> EMPTY;
            };
        }
        else if (boardRow == 1) {
            return WHITE_PAWN;
        }
        else if (boardRow == 6) {
            return BLACK_PAWN;
        }
        else if (boardRow == 7) {
            return switch (boardCol) {
                case 0, 7 -> BLACK_ROOK;
                case 1, 6 -> BLACK_KNIGHT;
                case 2, 5 -> BLACK_BISHOP;
                case 3 -> BLACK_QUEEN;
                case 4 -> BLACK_KING;
                default -> EMPTY;
            };
        }
        return EMPTY;
    }

    private static void drawFromWhitePerspective(PrintStream out) {
        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {

            drawRowOfSquares(out, boardRow);

            if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
                drawVerticalLine(out);
                setBlack(out);
            }
        }
    }

    private static void drawVerticalLine(PrintStream out) {

        int boardSizeInSpaces = BOARD_SIZE_IN_SQUARES * SQUARE_SIZE_IN_CHARS +
                (BOARD_SIZE_IN_SQUARES - 1) * LINE_WIDTH_IN_CHARS;

        for (int lineRow = 0; lineRow < LINE_WIDTH_IN_CHARS; ++lineRow) {
            setRed(out);
            out.print(EMPTY.repeat(boardSizeInSpaces));

            setBlack(out);
            out.println();
        }
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setRed(PrintStream out) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_RED);
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


//package ui;
//
//import java.io.PrintStream;
//import java.nio.charset.StandardCharsets;
//import java.util.Random;
//
//import static ui.EscapeSequencesTic.*;
//
//public class TicTacToe {
//
//    private static final int BOARD_SIZE_IN_SQUARES = 3;
//    private static final int SQUARE_SIZE_IN_CHARS = 3;
//    private static final int LINE_WIDTH_IN_CHARS = 1;
//    private static final String EMPTY = "   ";
//    private static final String X = " X ";
//    private static final String O = " O ";
//    private static Random rand = new Random();
//
//
//    public static void main(String[] args) {
//        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
//
//        out.print(ERASE_SCREEN);
//
//        drawHeaders(out);
//
//        drawTicTacToeBoard(out);
//
//        out.print(SET_BG_COLOR_BLACK);
//        out.print(SET_TEXT_COLOR_WHITE);
//    }
//
//    private static void drawHeaders(PrintStream out) {
//
//        setBlack(out);
//
//        String[] headers = { "TIC", "TAC", "TOE" };
//        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
//            drawHeader(out, headers[boardCol]);
//
//            if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
//                out.print(EMPTY.repeat(LINE_WIDTH_IN_CHARS));
//            }
//        }
//
//        out.println();
//    }
//
//    private static void drawHeader(PrintStream out, String headerText) {
//        int prefixLength = SQUARE_SIZE_IN_CHARS / 2;
//        int suffixLength = SQUARE_SIZE_IN_CHARS - prefixLength - 1;
//
//        out.print(EMPTY.repeat(prefixLength));
//        printHeaderText(out, headerText);
//        out.print(EMPTY.repeat(suffixLength));
//    }
//
//    private static void printHeaderText(PrintStream out, String player) {
//        out.print(SET_BG_COLOR_BLACK);
//        out.print(SET_TEXT_COLOR_GREEN);
//
//        out.print(player);
//
//        setBlack(out);
//    }
//
//    private static void drawTicTacToeBoard(PrintStream out) {
//
//        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
//
//            drawRowOfSquares(out);
//
//            if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
//                drawVerticalLine(out);
//                setBlack(out);
//            }
//        }
//    }
//
//    private static void drawRowOfSquares(PrintStream out) {
//
//        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_CHARS; ++squareRow) {
//            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
//                setWhite(out);
//
//                if (squareRow == SQUARE_SIZE_IN_CHARS / 2) {
//                    int prefixLength = SQUARE_SIZE_IN_CHARS / 2;
//                    int suffixLength = SQUARE_SIZE_IN_CHARS - prefixLength - 1;
//
//                    out.print(EMPTY.repeat(prefixLength));
//                    printPlayer(out, rand.nextBoolean() ? X : O);
//                    out.print(EMPTY.repeat(suffixLength));
//                }
//                else {
//                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_CHARS));
//                }
//
//                if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
//                    // Draw right line
//                    setRed(out);
//                    out.print(EMPTY.repeat(LINE_WIDTH_IN_CHARS));
//                }
//
//                setBlack(out);
//            }
//
//            out.println();
//        }
//    }
//
//    private static void drawVerticalLine(PrintStream out) {
//
//        int boardSizeInSpaces = BOARD_SIZE_IN_SQUARES * SQUARE_SIZE_IN_CHARS +
//                (BOARD_SIZE_IN_SQUARES - 1) * LINE_WIDTH_IN_CHARS;
//
//        for (int lineRow = 0; lineRow < LINE_WIDTH_IN_CHARS; ++lineRow) {
//            setRed(out);
//            out.print(EMPTY.repeat(boardSizeInSpaces));
//
//            setBlack(out);
//            out.println();
//        }
//    }
//
//    private static void setWhite(PrintStream out) {
//        out.print(SET_BG_COLOR_WHITE);
//        out.print(SET_TEXT_COLOR_WHITE);
//    }
//
//    private static void setRed(PrintStream out) {
//        out.print(SET_BG_COLOR_RED);
//        out.print(SET_TEXT_COLOR_RED);
//    }
//
//    private static void setBlack(PrintStream out) {
//        out.print(SET_BG_COLOR_BLACK);
//        out.print(SET_TEXT_COLOR_BLACK);
//    }
//
//    private static void printPlayer(PrintStream out, String player) {
//        out.print(SET_BG_COLOR_WHITE);
//        out.print(SET_TEXT_COLOR_BLACK);
//
//        out.print(player);
//
//        setWhite(out);
//    }
//}