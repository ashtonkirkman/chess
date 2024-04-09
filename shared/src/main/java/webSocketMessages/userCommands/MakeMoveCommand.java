package webSocketMessages.userCommands;
import chess.*;

public class MakeMoveCommand extends UserGameCommand {
    private final ChessMove move;
    private final int gameID;

    public MakeMoveCommand(String authToken, int gameID, ChessMove move) {
        super(authToken);
        this.commandType = CommandType.MAKE_MOVE;
        this.gameID = gameID;
        this.move = move;
    }

    public int getGameID() {
        return gameID;
    }

    public ChessMove getMove() {
        return move;
    }
}
