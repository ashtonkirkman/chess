import client.ui.Client;

public class Main {
    public static void main(String[] args) {

        var serverUrl = "http://localhost:8080";
        String portNumber = "8080";
        if (args.length == 1) {
            serverUrl = args[0];
        } else if (args.length == 2) {
            portNumber = args[1];
            serverUrl = "http://" + args[0] + ":" + portNumber;
        }

        var client = new Client(serverUrl);
        client.run();
    }
}