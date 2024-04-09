package client.ui;

/**
 * This class contains constants and functions relating to ANSI Escape Sequences that are useful in the Client display
 */
public class EscapeSequences {

    private static final String UNICODE_ESCAPE = "\u001b";
    public static final String THIN_SPACE = "\u2009";
    public static final String TWO_THIN_SPACES = "\u2009\u2009";

    private static final String ANSI_ESCAPE = "\033";

    public static final String ERASE_SCREEN = UNICODE_ESCAPE + "[H" + UNICODE_ESCAPE + "[2J";
    public static final String ERASE_LINE = UNICODE_ESCAPE + "[2K";

    public static final String SET_TEXT_BOLD = UNICODE_ESCAPE + "[1m";
    public static final String SET_TEXT_FAINT = UNICODE_ESCAPE + "[2m";
    public static final String RESET_TEXT_BOLD_FAINT = UNICODE_ESCAPE + "[22m";
    public static final String SET_TEXT_ITALIC = UNICODE_ESCAPE + "[3m";
    public static final String RESET_TEXT_ITALIC = UNICODE_ESCAPE + "[23m";
    public static final String SET_TEXT_UNDERLINE = UNICODE_ESCAPE + "[4m";
    public static final String RESET_TEXT_UNDERLINE = UNICODE_ESCAPE + "[24m";
    public static final String SET_TEXT_BLINKING = UNICODE_ESCAPE + "[5m";
    public static final String RESET_TEXT_BLINKING = UNICODE_ESCAPE + "[25m";
    public static final String RESET_TEXT = UNICODE_ESCAPE + "[0m";

    private static final String SET_TEXT_COLOR = UNICODE_ESCAPE + "[38;5;";
    private static final String SET_BG_COLOR = UNICODE_ESCAPE + "[48;5;";
    private static final String SET_BG_COLOR_RGB = UNICODE_ESCAPE + "[48;2;";

    public static final String SET_TEXT_COLOR_BLACK = SET_TEXT_COLOR + "0m";
    public static final String SET_TEXT_COLOR_LIGHT_GREY = SET_TEXT_COLOR + "242m";
    public static final String SET_TEXT_COLOR_DARK_GREY = SET_TEXT_COLOR + "235m";
    public static final String SET_TEXT_COLOR_RED = SET_TEXT_COLOR + "160m";
    public static final String SET_TEXT_COLOR_GREEN = SET_TEXT_COLOR + "46m";
    public static final String SET_TEXT_COLOR_YELLOW = SET_TEXT_COLOR + "226m";
    public static final String SET_TEXT_COLOR_BLUE = SET_TEXT_COLOR + "12m";
    public static final String SET_TEXT_COLOR_MAGENTA = SET_TEXT_COLOR + "5m";
    public static final String SET_TEXT_COLOR_WHITE = SET_TEXT_COLOR + "15m";
    public static final String RESET_TEXT_COLOR = SET_TEXT_COLOR + "0m";

    public static final String SET_BG_COLOR_BLACK = SET_BG_COLOR + "0m";
    public static final String SET_BG_COLOR_LIGHT_GREY = SET_BG_COLOR_RGB + "200;200;200m";
    public static final String SET_BG_COLOR_DARK_GREY = SET_BG_COLOR + "235m";
    public static final String SET_BG_COLOR_RED = SET_BG_COLOR + "160m";
    public static final String SET_BG_COLOR_GREEN = SET_BG_COLOR + "46m";
    public static final String SET_BG_COLOR_LIGHT_SQUARE = SET_BG_COLOR_RGB + "240;238;213m";
    public static final String SET_BG_COLOR_DARK_SQUARE = SET_BG_COLOR_RGB + "111;148;80m";
    public static final String SET_BG_COLOR_DARK_SQUARE_HIGHLIGHT = SET_BG_COLOR_RGB + "173;176;84m";
    public static final String SET_BG_COLOR_LIGHT_SQUARE_HIGHLIGHT = SET_BG_COLOR_RGB + "243;247;121m";
    public static final String SET_BG_COLOR_DARK_GREEN = SET_BG_COLOR + "22m";
    public static final String SET_BG_COLOR_YELLOW = SET_BG_COLOR + "226m";
    public static final String SET_BG_COLOR_BLUE = SET_BG_COLOR + "12m";
    public static final String SET_BG_COLOR_LIGHT_BLUE = SET_BG_COLOR + "104m";
    public static final String SET_BG_COLOR_MAGENTA = SET_BG_COLOR + "5m";
    public static final String SET_BG_COLOR_WHITE = SET_BG_COLOR + "15m";
    public static final String RESET_BG_COLOR = SET_BG_COLOR + "0m";
    public static final String WHITE_KING = TWO_THIN_SPACES+"♔"+TWO_THIN_SPACES;
    public static final String WHITE_QUEEN = TWO_THIN_SPACES+"♕"+TWO_THIN_SPACES;
    public static final String WHITE_BISHOP = TWO_THIN_SPACES+"♗"+TWO_THIN_SPACES;
    public static final String WHITE_KNIGHT = TWO_THIN_SPACES+"♘"+TWO_THIN_SPACES;
    public static final String WHITE_ROOK = TWO_THIN_SPACES+"♖"+TWO_THIN_SPACES;
    public static final String WHITE_PAWN = TWO_THIN_SPACES+"♙"+TWO_THIN_SPACES;
    public static final String BLACK_KING = TWO_THIN_SPACES+"♚"+TWO_THIN_SPACES;
    public static final String BLACK_QUEEN = TWO_THIN_SPACES+"♛"+TWO_THIN_SPACES;
    public static final String BLACK_BISHOP = TWO_THIN_SPACES+"♝"+TWO_THIN_SPACES;
    public static final String BLACK_KNIGHT = TWO_THIN_SPACES+"♞"+TWO_THIN_SPACES;
    public static final String BLACK_ROOK = TWO_THIN_SPACES+"♜"+TWO_THIN_SPACES;
    public static final String BLACK_PAWN = TWO_THIN_SPACES+"♟"+TWO_THIN_SPACES;
    public static final String EMPTY = TWO_THIN_SPACES+"\u2003"+TWO_THIN_SPACES;
    public static final String SPACE = "\u2003";
    public static String moveCursorToLocation(int x, int y) { return UNICODE_ESCAPE + "[" + y + ";" + x + "H"; }
}