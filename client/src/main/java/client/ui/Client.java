package client.ui;

import client.websocket.ServerMessageObserver;
import client.websocket.WebSocketCommunicator;
import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;
import server.Server;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import chess.*;
import dataAccess.*;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;

import static java.util.Objects.isNull;

public class Client implements ServerMessageObserver {
    private DrawChessBoard chessBoard;
    private ServerFacade serverFacade;
    private final String serverUrl;
    private String authToken;
    private State state = State.LOGGED_OUT;
//    private Server server = new Server();
    private AuthDAO authDAO = new MySqlAuthDAO();
    private GameDAO gameDAO = new MySqlGameDAO();
    private UserDAO userDAO = new MySqlUserDAO();
    private int gameID;
    private ChessGame game;
    private String perspective;
    private WebSocketCommunicator ws;

    public Client(String serverUrl) {
        this.chessBoard = new DrawChessBoard();
        this.serverFacade = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
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
                if (result != null) {
                    System.out.println(result);
                }
            } catch (Throwable e) {
                var msg = e.getMessage();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        if (state == State.LOGGED_IN) {
            System.out.print("[LOGGED_IN] >>> " + "\n");
        } else if (state == State.LOGGED_OUT){
            System.out.print(/*"\n" + */"[LOGGED_OUT] >>> " + "\n");
        } else if (state == State.PLAYING){
            System.out.print(/*"\n" + */"[PLAYING] >>> " + "\n");
        } else {
            System.out.print(/*"\n" + */"[OBSERVING] >>> " + "\n");
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
            else if (state == State.PLAYING){
                return switch(cmd) {
                    case "leave" -> leave();
                    case "help" -> help();
                    case "redraw" -> redraw();
                    case "move" -> move(params);
                    case "resign" -> resign();
                    case "highlight" -> highlight(params);
                    default -> "Unknown command: " + cmd;
                };
            }
            else {
                return switch(cmd) {
                    case "leave" -> leave();
                    case "help" -> help();
                    case "move" -> "As an observer, you cannot make a move.";
                    case "redraw" -> redraw();
                    case "highlight" -> highlight(params);
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

    public String redraw() {
        DrawChessBoard.drawChessBoard(game, perspective, null);
        return null;
    }

    public String join(String... params) throws ResponseException, IOException, DataAccessException {
        this.ws = new WebSocketCommunicator(serverUrl, this);
        if (params.length < 1) {
            return "Usage: join <ID> [WHITE|BLACK|<empty>]";
        }
        var id = params[0];
        var color = (params.length > 1) ? params[1] : null;
        serverFacade.joinGame(authToken, Integer.parseInt(id), color);
        gameID = Integer.parseInt(id);
        perspective = color;
        ChessGame.TeamColor playerColor = (color == null) ? null : (color.equalsIgnoreCase("white") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK);
        var gameData = gameDAO.getGame(gameID);
        game = gameData.game();
        gameID = Integer.parseInt(id);
        if(color == null) {
            state = State.OBSERVING;
            ws.observe(gameID, authToken);
            waitForNotify();
            return "Joined game " + id + " as an observer";
        }
        state = State.PLAYING;
        ws.join(gameID, authToken, playerColor);
        waitForNotify();
        return "Joined game " + id + " as " + color;
    }

    public String observe(String... params) throws ResponseException, IOException, DataAccessException {
        this.ws = new WebSocketCommunicator(serverUrl, this);
        if (params.length != 1) {
            return "Usage: observe <ID>";
        }
        var id = params[0];
        serverFacade.observeGame(authToken, Integer.parseInt(id));
        gameID = Integer.parseInt(id);
        var gameData = gameDAO.getGame(gameID);
        game = gameData.game();
        DrawChessBoard.drawChessBoard(game, "white", null);
        state = State.OBSERVING;
        gameID = Integer.parseInt(id);
        ws.observe(gameID, authToken);
        waitForNotify();
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

    public String leave() throws ResponseException, DataAccessException {
        state = State.LOGGED_IN;
        GameData gameData = gameDAO.getGame(gameID);
        game = gameData.game();
        String username = authDAO.getUsername(authToken);
        if (gameData.whiteUsername() != null && gameData.whiteUsername().equals(username)) {
            gameDAO.updateGame(new GameData(gameID, null, gameData.blackUsername(), gameData.gameName(), game));
            ws.leave(gameID, authToken);
        }
        else if (gameData.blackUsername() != null && gameData.blackUsername().equals(username)) {
            gameDAO.updateGame(new GameData(gameID, gameData.whiteUsername(), null, gameData.gameName(), game));
            ws.leave(gameID, authToken);
        }
        else {
            ws.leave(gameID, authToken);
        }
        return "You have left the game.";
    }

    public String move(String... params) throws ResponseException, DataAccessException, InvalidMoveException {
        if (params.length < 2) {
            return "Usage: move <FROM> <TO> <PROMOTION|<empty>>";
        }
        var from = params[0];
        var fromPosition = parsePosition(from);
        var to = params[1];
        var toPosition = parsePosition(to);
        var promotion = (params.length > 2) ? params[2] : null;
        var gameData = gameDAO.getGame(gameID);
        game = gameData.game();
        ws.move(gameID, authToken, new ChessMove(fromPosition, toPosition, null));
        waitForNotify();
        return null;
//        return "Moved from " + from + " to " + to;
    }

    public String resign() throws ResponseException, DataAccessException {
        GameData gameData = gameDAO.getGame(gameID);
        game = gameData.game();
        String username = authDAO.getUsername(authToken);
        if (gameData.whiteUsername() != null && gameData.whiteUsername().equals(username)) {
            gameDAO.updateGame(new GameData(gameID, null, gameData.blackUsername(), gameData.gameName(), game));
            ws.resign(gameID, authToken);
        }
        else if (gameData.blackUsername() != null && gameData.blackUsername().equals(username)) {
            gameDAO.updateGame(new GameData(gameID, gameData.whiteUsername(), null, gameData.gameName(), game));
            ws.resign(gameID, authToken);
        }
        else {
            ws.resign(gameID, authToken);
        }
        state = State.LOGGED_IN;
        return "You have resigned.";
    }

    public String highlight(String ... params) throws DataAccessException {
        if (params.length < 1) {
            return "Usage: highlight <POSITION>";
        }
        var position = params[0];
        var chessPosition = parsePosition(position);
        var gameData = gameDAO.getGame(gameID);
        game = gameData.game();
        DrawChessBoard.drawChessBoard(game, perspective, chessPosition);
        return "Highlighted position " + position;
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
            return EscapeSequences.SET_TEXT_COLOR_BLUE + "  register <USERNAME> <PASSWORD> <EMAIL>" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - to create an account\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  login <USERNAME> <PASSWORD>" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - to play chess\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  quit" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - playing chess\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  help" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - with possible commands\n" + EscapeSequences.RESET_TEXT;

        }
        else if (state == State.LOGGED_IN) {
            return EscapeSequences.SET_TEXT_COLOR_BLUE + "  create <NAME>" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - a game\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  list" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - games\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  join <ID> [WHITE|BLACK|<empty>]" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - a game\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  observe <ID>" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - a game\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  logout" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - when you are done\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  quit" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - playing chess\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  help" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - with possible commands\n" + EscapeSequences.RESET_TEXT;
        }
        else if (state == State.PLAYING) {
            return EscapeSequences.SET_TEXT_COLOR_BLUE + "  leave" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - the game\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  help" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - with possible commands\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  redraw" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - the board\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  move <FROM> <TO> <PROMOTION|<empty>>" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - a piece\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  highlight <POSITION>" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - a square\n" + EscapeSequences.RESET_TEXT +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  resign" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - the game\n" + EscapeSequences.RESET_TEXT;
        }
        else {
            return EscapeSequences.SET_TEXT_COLOR_BLUE + "  leave" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - the game\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  help" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - with possible commands\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  redraw" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - the board\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "  highlight <POSITION>" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - a square\n" + EscapeSequences.RESET_TEXT;
        }
    }

    private synchronized void waitForNotify() {
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void notify(String message) {
        ServerMessage sm = new Gson().fromJson(message, ServerMessage.class);
        switch (sm.getServerMessageType()) {
            case ERROR -> {
                ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
                System.out.println(errorMessage.getErrorMessage());
                notifyAll();
                break;
            }
            case NOTIFICATION -> {
                NotificationMessage notificationMessage = new Gson().fromJson(message, NotificationMessage.class);
                System.out.println(notificationMessage.getMessage());
                notifyAll();
                break;
            }
            case LOAD_GAME -> {
                LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
                game = loadGameMessage.getGame();
                DrawChessBoard.drawChessBoard(game, perspective, null);
                notifyAll();
                break;
            }
        }
    }
}
