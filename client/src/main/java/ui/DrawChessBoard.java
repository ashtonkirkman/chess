package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
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
        drawChessBoard(game.getBoard(), "black");
    }

    public static void drawChessBoard(ChessBoard board, String perspective) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        if (perspective.equalsIgnoreCase("white")) {
            drawWhiteHeaders(out);
            drawBoard(board, out, "white");
            drawWhiteHeaders(out);
        } else {
            drawBlackHeaders(out);
            drawBoard(board, out, "black");
            drawBlackHeaders(out);
        }

        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT);
    }

    public static void updateChessBoard(ChessBoard board, String perspective) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        for (int row = 1; row <= BOARD_SIZE_IN_SQUARES; row++) {
            for (int col = 1; col <= BOARD_SIZE_IN_SQUARES; col++) {
                ChessPiece piece;
                if (perspective.equalsIgnoreCase("white")) {
                    piece = board.getPiece(new ChessPosition(9-row, col));
                }
                else {
                    piece = board.getPiece(new ChessPosition(row, 9-col));
                }
                String pieceToPrint;
                if (piece == null) {
                    pieceToPrint = EMPTY;
                }
                else {
                    pieceToPrint = convertPieceToString(piece);
                }
                if (row % 2 ==0) {
                    if (col % 2 == 0) {
                        printLightSquare(out, pieceToPrint);
                    }
                    else {
                        printDarkSquare(out, pieceToPrint);
                    }
                }
                else {
                    if (col % 2 == 0) {
                        printDarkSquare(out, pieceToPrint);
                    }
                    else {
                        printLightSquare(out, pieceToPrint);
                    }
                }
            }
            setBlack(out);
            System.out.println();
        }
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

    private static void drawBoard(ChessBoard board, PrintStream out, String perspective) {

        for (int boardRow = 1; boardRow <= BOARD_SIZE_IN_SQUARES; ++boardRow) {

            drawRowOfSquares(out, board, boardRow, perspective.equalsIgnoreCase("black") ? "black" : "white");

            if (boardRow <= BOARD_SIZE_IN_SQUARES - 1) {
                setBlack(out);
            }
        }
    }

    private static void drawRowOfSquares(PrintStream out, ChessBoard board, int boardRow, String perspective) {

        drawHeader(out, THIN_SPACE+(perspective.equalsIgnoreCase("white") ? 9-boardRow : boardRow)+THIN_SPACE);
        boolean isWhitePerspective = perspective.equalsIgnoreCase("white");
        for (int boardCol = 1; boardCol <= BOARD_SIZE_IN_SQUARES; ++boardCol) {
            ChessPiece piece;
            if (isWhitePerspective) {
                piece = board.getPiece(new ChessPosition(9-boardRow, boardCol));
            }
            else {
                piece = board.getPiece(new ChessPosition(boardRow, 9-boardCol));
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
                    printLightSquare(out, pieceOutput);
                }
                else {
                    printDarkSquare(out, pieceOutput);
                }
            }
            else {
                if (boardCol % 2 == 0) {
                    printDarkSquare(out, pieceOutput);
                }
                else {
                    printLightSquare(out, pieceOutput);
                }
            }

            setBlack(out);
        }
        drawHeader(out, THIN_SPACE+(perspective.equalsIgnoreCase("white") ? 9-boardRow : boardRow)+THIN_SPACE);
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