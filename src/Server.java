import App.env;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    public static void main(String[] args) throws IOException {
        HTTPServer.initializeServer();
        SQLAdapter.getUserListFromSql();
        SQLAdapter.getChannelListFromSql();
        SQLAdapter.getChatListFromSql();

        ArrayList<ServerThread> threadList = new ArrayList<>();
        try (ServerSocket serversocket = new ServerSocket(env.SERVER_SOCKET_PORT)) {
            System.out.println("Waiting for Clients...");
            while (true) {
                Socket socket = serversocket.accept();
                ServerThread serverThread = new ServerThread(socket, threadList);
                threadList.add(serverThread);
                serverThread.start();
            }
        } catch (Exception e) {
            System.out.println("Error occurred in main: " + e.getMessage());
        }
    }
}
