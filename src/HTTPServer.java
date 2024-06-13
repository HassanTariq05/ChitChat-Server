import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import model.Chat;
import model.User;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class HTTPServer {
    public static void initializeServer() throws IOException {
        InetAddress localAddress = InetAddress.getByName("127.0.0.1");
        HttpServer server = HttpServer.create(new InetSocketAddress(localAddress, 8080), 0);
        server.createContext("/users/", new GetAllUsersHttpHandler());
        server.createContext("/channels/", new ClientHttpHandler());
        server.createContext("/chats/", new ClientChannelChatHttpHandler());

        server.start();
    }
}

class ClientHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] pathSplits = path.split("/");

        if (pathSplits.length < 3 || !pathSplits[2].startsWith("Client-ID=")) {
            exchange.sendResponseHeaders(400, 0);
            try (OutputStream stream = exchange.getResponseBody()) {
                stream.write("Invalid path format".getBytes());
            }
            return;
        }

        String clientIdStr = pathSplits[2].substring("Client-ID=".length());
        int userId;
        try {
            userId = Integer.parseInt(clientIdStr);
        } catch (NumberFormatException e) {
            exchange.sendResponseHeaders(400, 0);
            try (OutputStream stream = exchange.getResponseBody()) {
                stream.write("Invalid Client-ID".getBytes());
            }
            return;
        }

        List<Channel> filteredList = new ArrayList<>();
        for (Channel channel : AppData.getInstance().channels) {
            if (channel.userIds.contains(userId)) {
                filteredList.add(channel);
            }
        }

        String response = "{\"clientChannels\":" + filteredList + "}";
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream stream = exchange.getResponseBody()) {
            stream.write(response.getBytes());
        }
    }
}
class ClientChannelChatHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] pathSplits = path.split("/");

        if (pathSplits.length < 3 || !pathSplits[2].startsWith("Channel-ID=")) {
            exchange.sendResponseHeaders(400, 0);
            try (OutputStream stream = exchange.getResponseBody()) {
                stream.write("Invalid path format".getBytes());
            }
            return;
        }

        String channelIdStr = pathSplits[2].substring("Channel-ID=".length());
        int channelId;
        try {
            channelId = Integer.parseInt(channelIdStr);
        } catch (NumberFormatException e) {
            exchange.sendResponseHeaders(400, 0);
            try (OutputStream stream = exchange.getResponseBody()) {
                stream.write("Invalid Channel-ID".getBytes());
            }
            return;
        }

        ArrayList<Chat> filteredChat = new ArrayList<>();
        for(Chat ch : AppData.getInstance().chatList) {
            if(channelId == ch.getChannelId()) {
                filteredChat.add(ch);
            }
        }

        String response = "{\"channelChat\":" + filteredChat + "}";
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream stream = exchange.getResponseBody()) {
            stream.write(response.getBytes());
        }
    }
}

class GetAllUsersHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] pathSplits = path.split("/");

        if (pathSplits.length < 3 || !pathSplits[2].startsWith("Client-ID=")) {
            exchange.sendResponseHeaders(400, 0);
            try (OutputStream stream = exchange.getResponseBody()) {
                stream.write("Invalid path format".getBytes());
            }
            return;
        }

        String clientIdStr = pathSplits[2].substring("Client-ID=".length());
        int clientId;
        try {
            clientId = Integer.parseInt(clientIdStr);
        } catch (NumberFormatException e) {
            exchange.sendResponseHeaders(400, 0);
            try (OutputStream stream = exchange.getResponseBody()) {
                stream.write("Invalid Client-ID".getBytes());
            }
            return;
        }

        ArrayList<User> userArrayList = new ArrayList<>();
        for (User user : AppData.getInstance().users) {
            if (clientId != user.getId()) {
                User userCopy = new User(user);
                userCopy.setPassword("****");
                userArrayList.add(userCopy);
            }
        }

        String response = "{\"allUserList\":" + userArrayList + "}";
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream stream = exchange.getResponseBody()) {
            stream.write(response.getBytes());
        }
    }
}




