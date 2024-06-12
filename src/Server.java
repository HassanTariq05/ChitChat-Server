import model.Chat;
import model.User;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static List<User> users = new ArrayList<>();
    public static List<Channel> channels = new ArrayList<>();
    public static ArrayList<Chat> chatList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        HTTPServer.initializeServer();
        SQLAdapter.getUserListFromSql();
        SQLAdapter.getChannelListFromSql();
        SQLAdapter.getChatListFromSql();

        ArrayList<ServerThread> threadList = new ArrayList<>();
        try (ServerSocket serversocket = new ServerSocket(6001)) {
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
