package model;

public record JoinGameRequestAuth(String authToken, int gameID, String playerColor) {
}
