package ui;

import java.util.Arrays;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Client {
    private ChessBoard chessBoard;
    private ServerFacade server;
    private final String serverUrl;
    private State state = State.LOGGED_OUT;

    public Client(String serverUrl) {
        this.chessBoard = new ChessBoard();
        this.server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public static void main(String[] args) {
        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        var client = new Client(serverUrl);
        client.run();
    }

    public void run() {
        System.out.println("Welcome to 240 chess. Type Help to get started.");
        Scanner scanner = new Scanner(System.in);
        String line = "";
        while (!line.equals("quit")) {
            printPrompt();
            line = scanner.nextLine();

            try {
                String result = eval(line);
                System.out.println(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        if (state == State.LOGGED_IN) {
            System.out.print("\n" + "[LOGGED_IN] >>> ");
        } else {
            System.out.print("\n" + "[LOGGED_OUT] >>> ");
        }
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            System.out.println("Command: " + cmd + " Params: " + Arrays.toString(params) + " State: " + state);
            if (state == State.LOGGED_OUT) {
                return switch(cmd) {
                    case "quit" -> "Goodbye!";
                    case "login" -> login(params);
                    case "register" -> register(params);
                    case "logout" -> logout();
                    case "help" -> help();
                    default -> "Unknown command: " + cmd;
                };
            }
            else {
                return switch(cmd) {
                    case "quit" -> "Goodbye!";
                    case "create" -> create(params);
//                    case "list" -> list();
                    case "join" -> join(params);
//                    case "observe" -> observe(params);
                    case "logout" -> logout();
                    case "help" -> help();
                    default -> "Unknown command: " + cmd;
                };
            }
        } catch (Throwable e) {
            return e.toString();
        }
    }

    public String create(String... params) {
        if (params.length != 1) {
            return "Usage: create <NAME>";
        }
        var name = params[0];
        return "Created game " + name;
    }

    public String join(String... params) {
        if (params.length < 1) {
            return "Usage: join <ID> [WHITE|BLACK|<empty>]";
        }
        var id = params[0];
        var color = (params.length > 1) ? params[1] : "";
        chessBoard.drawChessBoard();
        return "Joined game " + id + " as " + color;
    }

    public String register(String... params) {
        if (params.length != 3) {
            return "Usage: register <USERNAME> <PASSWORD> <EMAIL>";
        }
        var username = params[0];
        var password = params[1];
        var email = params[2];
        state = State.LOGGED_IN;
        return "Logged in as " + username;
    }

    public String logout() {
        state = State.LOGGED_OUT;
        return "Logged out";
    }

    public String login(String... params) {
        if (params.length != 2) {
            return "Usage: login <USERNAME> <PASSWORD>";
        }
        var username = params[0];
        var password = params[1];
        state = State.LOGGED_IN;
        return "Logged in as " + username;
    }

    public String help() {
        if (state == State.LOGGED_OUT) {
            return SET_TEXT_COLOR_BLUE + "  register <USERNAME> <PASSWORD> <EMAIL>" + SET_TEXT_COLOR_MAGENTA + " - to create an account\n" +
                    SET_TEXT_COLOR_BLUE + "  login <USERNAME> <PASSWORD>" + SET_TEXT_COLOR_MAGENTA + " - to play chess\n" +
                    SET_TEXT_COLOR_BLUE + "  quit" + SET_TEXT_COLOR_MAGENTA + " - playing chess\n" +
                    SET_TEXT_COLOR_BLUE + "  help" + SET_TEXT_COLOR_MAGENTA + " - with possible commands" + RESET_TEXT;

        }
        return SET_TEXT_COLOR_BLUE + "  create <NAME>" + SET_TEXT_COLOR_MAGENTA + " - a game\n" +
                SET_TEXT_COLOR_BLUE + "  list" + SET_TEXT_COLOR_MAGENTA + " - games\n" +
                SET_TEXT_COLOR_BLUE + "  join <ID> [WHITE|BLACK|<empty>]" + SET_TEXT_COLOR_MAGENTA + " - a game\n" +
                SET_TEXT_COLOR_BLUE + "  observe <ID>" + SET_TEXT_COLOR_MAGENTA + " - a game\n" +
                SET_TEXT_COLOR_BLUE + "  logout" + SET_TEXT_COLOR_MAGENTA + " - when you are done\n" +
                SET_TEXT_COLOR_BLUE + "  quit" + SET_TEXT_COLOR_MAGENTA + " - playing chess\n" +
                SET_TEXT_COLOR_BLUE + "  help" + SET_TEXT_COLOR_MAGENTA + " - with possible commands" + RESET_TEXT;
    }
}