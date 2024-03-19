package ui;

public class ServerFacade {
    private ClientCommunicator communicator;

    public ServerFacade(ClientCommunicator communicator) {
        this.communicator = communicator;
    }
}
