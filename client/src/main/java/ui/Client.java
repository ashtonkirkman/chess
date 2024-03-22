package ui;

import exception.ResponseException;
import server.Server;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Client {
    private ChessBoard chessBoard;
    private ServerFacade serverFacade;
    private final String serverUrl;
    private String authToken;
    private State state = State.LOGGED_OUT;

    public Client(String serverUrl) {
        this.chessBoard = new ChessBoard();
        this.serverFacade = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public static void main(String[] args) {
        var serverUrl = "http://localhost:8080";
        String portNumber = "8080";
        if (args.length == 1) {
            serverUrl = args[0];
        } else if (args.length == 2) {
            portNumber = args[1];
            serverUrl = "http://" + args[0] + ":" + portNumber;
        }

        int port = Integer.parseInt(portNumber);
        Server server = new Server();
        server.run(port);

        var client = new Client(serverUrl);
        client.run();
        server.stop();
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
                var msg = e.getMessage();
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
            if (state == State.LOGGED_OUT) {
                return switch(cmd) {
                    case "quit" -> "Goodbye!";
                    case "login" -> login(params);
                    case "register" -> register(params);
                    case "logout" -> logout();
                    case "help" -> help();
                    case "clear" -> clear();
                    default -> "Unknown command: " + cmd;
                };
            }
            else {
                return switch(cmd) {
                    case "quit" -> "Goodbye!";
                    case "create" -> create(params);
                    case "list" -> list();
                    case "join" -> join(params);
                    case "observe" -> observe(params);
                    case "logout" -> logout();
                    case "help" -> help();
                    default -> "Unknown command: " + cmd;
                };
            }
        } catch (Throwable e) {
            return e.getMessage();
        }
    }

    public String create(String... params) throws ResponseException, IOException {
        if (params.length != 1) {
            return "Usage: create <NAME>";
        }
        var name = params[0];
        serverFacade.createGame(authToken, name);
        return "Created game " + name;
    }

    public String clear() throws ResponseException, IOException {
        serverFacade.clear(authToken);
        return "Cleared all games";
    }

    public String list() throws ResponseException, IOException {
        StringBuilder gameInfo = new StringBuilder();
        var games = serverFacade.listGames(authToken);
        int numGames = games.games().size();
        int count = 0;
        for (var game : games.games()) {
            String gameId = String.valueOf(game.gameID());
            String gameName = game.gameName();
            gameInfo.append(gameId).append(". ").append(gameName);
            if (++count < numGames) {
                gameInfo.append("\n");
            }
        }
        return gameInfo.toString();
    }

    public String join(String... params) throws ResponseException, IOException {
        if (params.length < 1) {
            return "Usage: join <ID> [WHITE|BLACK|<empty>]";
        }
        var id = params[0];
        var color = (params.length > 1) ? params[1] : null;
        serverFacade.joinGame(authToken, Integer.parseInt(id), color);
        chessBoard.drawChessBoard();
        if(color == null) {
            return "Joined game " + id + " as an observer";
        }
        return "Joined game " + id + " as " + color;
    }

    public String observe(String... params) throws ResponseException, IOException {
        if (params.length != 1) {
            return "Usage: observe <ID>";
        }
        var id = params[0];
        serverFacade.observeGame(authToken, Integer.parseInt(id));
        chessBoard.drawChessBoard();
        return "Joined game " + id + " as an observer";
    }

    public String register(String... params) throws ResponseException, IOException {
        if (params.length != 3) {
            return "Usage: register <USERNAME> <PASSWORD> <EMAIL>";
        }
        var username = params[0];
        var password = params[1];
        var email = params[2];
        authToken = serverFacade.register(username, password, email);
        state = State.LOGGED_IN;
        return "Logged in as " + username;
    }

    public String logout() throws ResponseException, IOException {
        state = State.LOGGED_OUT;
        serverFacade.logout(authToken);
        return "Logged out";
    }

    public String login(String... params) throws ResponseException, IOException {
        if (params.length != 2) {
            return "Usage: login <USERNAME> <PASSWORD>";
        }
        var username = params[0];
        var password = params[1];
        authToken = serverFacade.login(username, password);
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
