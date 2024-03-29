package ui;

import exception.ResponseException;
import model.GameData;
import server.Server;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import chess.*;
import dataAccess.*;

import static java.util.Objects.isNull;
import static ui.EscapeSequences.*;

public class Client {
    private DrawChessBoard chessBoard;
    private ServerFacade serverFacade;
    private final String serverUrl;
    private String authToken;
    private State state = State.LOGGED_OUT;
    private Server server = new Server();
    private int gameID;
    private ChessGame game;

    public Client(String serverUrl) {
        this.chessBoard = new DrawChessBoard();
        this.serverFacade = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public void start(String serverUrl, String portNumber) {

        int port = Integer.parseInt(portNumber);
        server.run(port);

        var client = new Client(serverUrl);
        client.run();
        server.stop();
    }

    public void run() {
        System.out.println("Welcome to 240 chess. Type Help to get started.");
        Scanner scanner = new Scanner(System.in);
        String line = "";
        while (!line.equals("quit") || state == State.PLAYING) {
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
        } else if (state == State.LOGGED_OUT){
            System.out.print("\n" + "[LOGGED_OUT] >>> ");
        } else {
            System.out.print("\n" + "[PLAYING] >>> ");
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
            else if (state == State.LOGGED_IN){
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
            else {
                return switch(cmd) {
                    case "leave" -> leave();
                    case "help" -> help();
//                    case "redraw" -> redraw();
                    case "move" -> move(params);
//                    case "resign" -> resign();
//                    case "highlight" -> highlight();
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
            String whiteUser = game.whiteUsername();
            String blackUser = game.blackUsername();
            gameInfo.append(gameId).append(". ").append(gameName).append(", White: ").append(isNull(whiteUser) ? "None" : whiteUser).append(", Black: ").append(isNull(blackUser) ? "None" : blackUser);
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
        if (color == null || color.equalsIgnoreCase("white")) {
//            DrawChessBoard.drawChessBoard("white");
        }
        else {
//            DrawChessBoard.drawChessBoard("black");
        }
        gameID = Integer.parseInt(id);
        state = State.PLAYING;
        if(color == null) {
            return "Joined game " + id + " as an observer";
        }
        return "Joined game " + id + " as " + color;
    }

    public String observe(String... params) throws ResponseException, IOException, DataAccessException {
        if (params.length != 1) {
            return "Usage: observe <ID>";
        }
        var id = params[0];
        serverFacade.observeGame(authToken, Integer.parseInt(id));
        gameID = Integer.parseInt(id);
        var gameData = server.gameDAO.getGame(gameID);
        game = gameData.game();
        DrawChessBoard.drawChessBoard("white");
        state = State.PLAYING;
        gameID = Integer.parseInt(id);
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

    public String leave() {
        state = State.LOGGED_IN;
        return "You have left the game.";
    }

    public String move(String... params) throws DataAccessException, InvalidMoveException {
        if (params.length < 2) {
            return "Usage: move <FROM> <TO> <PROMOTION|<empty>>";
        }
        var from = params[0];
        var fromPosition = parsePosition(from);
        var to = params[1];
        var toPosition = parsePosition(to);
        var promotion = (params.length > 2) ? params[2] : null;
        var gameData = server.gameDAO.getGame(gameID);
        game = gameData.game();
        game.makeMove(new ChessMove(fromPosition, toPosition, null));
        System.out.println(game);
        gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
        server.gameDAO.updateGame(gameData);
        return "Moved from " + from + " to " + to + " with promotion " + promotion;
    }

    public ChessPosition parsePosition(String position) {
        if (position.length() != 2) {
            throw new IllegalArgumentException("Invalid position: " + position);
        }
        var column = position.charAt(0) - 'a' + 1;
        var row = position.charAt(1) - '1' + 1;
        return new ChessPosition(row, column);
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
