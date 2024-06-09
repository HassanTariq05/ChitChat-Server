import model.Chat;
import model.Keys;
import model.User;
import org.json.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class ServerThread extends Thread {
    private Socket socket;
    private ArrayList<ServerThread> threadList;
    private PrintWriter output;
    private BufferedReader input;
    private String clientName;


    public ServerThread(Socket socket, ArrayList<ServerThread> threads) {
        this.socket = socket;
        this.threadList = threads;
    }

    @Override
    public void run() {
        try {

            initializeInputStream();
            initializeOutputStream();

        } catch (Exception e) {
            System.out.println("Error occurred at server side: " + e.getMessage());
        }
    }

    private void initializeOutputStream() throws Exception {
        output = new PrintWriter(socket.getOutputStream(), true);
    }

    private void sendMessage(String message) {
        try {
            initializeOutputStream();
            System.out.println("Sending: " + message);
            output.println(message);
            System.out.println("Sent: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeInputStream() throws Exception {
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        receiveMessage();
    }

    private void receiveMessage() throws Exception {
        String message;
        while ((message = input.readLine()) != null) {
            System.out.println("Received: "+message);
            JSONObject jsonObject = new JSONObject(message);
            handleCommand(jsonObject);
        }
    }

    private void handleCommand(JSONObject json) {
        String command = json.getString("command");

        switch (command) {
            case "signup":
                handleSignup(json);
                break;
            case "login":
                handleLogin(json);
                break;
            case "send_chat_message":
                handleSendChatMessage(json);
                break;
            case "add_new_contact":
                handleAddNewChannel(json);
                break;
        }

    }

    private void handleSignup(JSONObject json) {
        String username = json.getString("username");
        if(username.isEmpty() || username.length() > 15) {
            sendError(json, "Username is either too short or long!");
        } else if(usernameExists(username)) {
            sendError(json, "Username already exist!");
        } else {
            registerUser(json);
        }
    }

    private Boolean usernameExists(String username) {
        System.out.println("users:");
        System.out.println(Server.users);
        Boolean found = false;
        for(int i=0; i < Server.users.size(); i++) {
            User user = Server.users.get(i);
            if(user.getUsername().equalsIgnoreCase(username)) {
                found = true;
            }
        }
        return found;
    }

    private void registerUser(JSONObject json) {

        User user = new User();
        user.setUsername(json.getString(Keys.KEY_USERNAME));
        user.setFullName(json.getString(Keys.KEY_FULL_NAME));
        user.setPassword(json.getString(Keys.KEY_PASSWORD));

        if(!Server.users.isEmpty()) {
            int id = Server.users.getLast().getId() + 1;
            user.setId(id);
        } else {
            user.setId(1);
        }

        Server.users.add(user);

        json.put("command", "registration_successful");
        json.put(Keys.KEY_ID, user.getId());

        sendMessage(json.toString());

        System.out.println("users after registration:");
        System.out.println(Server.users);
    }

    private void sendError(JSONObject jsonObject, String errorMessage) {
        jsonObject.put("command", "error");
        jsonObject.put("error_message", errorMessage);
        sendMessage(jsonObject.toString());
    }
    private void handleLogin(JSONObject jsonObject) {
        String username = jsonObject.getString(Keys.KEY_USERNAME);
        String password = jsonObject.getString(Keys.KEY_PASSWORD);

        for(int i = 0; i <Server.users.size(); i++) {
            User user = Server.users.get(i);
            if(username.equalsIgnoreCase(user.getUsername()) && password.equalsIgnoreCase(user.getPassword())) {
                jsonObject.put("command", "response_login_successful");
                jsonObject.put(Keys.KEY_ID, user.getId());
                jsonObject.put(Keys.KEY_FULL_NAME, user.getFullName());
                sendMessage(jsonObject.toString());
                return;
            }
        }
        sendError(jsonObject, "Invalid username or password");
        sendMessage(jsonObject.toString());
    }
    private void handleSendChatMessage(JSONObject jsonObject) {
        jsonObject.put("command", "response_send_chat_message");

        Chat chat = new Chat();
        chat.setChannelId(jsonObject.getInt(Keys.KEY_CHANNEL_ID));
        chat.setSenderId(jsonObject.getInt(Keys.KEY_SENDER_ID));
        chat.setReceiverId(jsonObject.getInt(Keys.KEY_RECEIVER_ID));
        chat.setMessage(jsonObject.getString(Keys.KEY_MESSAGE));
        chat.setTimestamp(jsonObject.getString(Keys.KEY_TIMESTAMP));
        Server.chatList.add(chat);

        System.out.println("Message added to chatList: " + jsonObject);
        System.out.println("Updated chatlist:" + Server.chatList);
        sendMessagetoAllThreads(jsonObject.toString());
    }
    private void sendMessagetoAllThreads(String message) {
        for (ServerThread thread : threadList) {
            thread.sendMessage(message);
        }
    }

    private void handleAddNewChannel(JSONObject jsonObject) {
        String username = jsonObject.getString("username");
        for(int i = 0; i < Server.users.size(); i++) {
            User user = Server.users.get(i);
            if(username.equalsIgnoreCase(user.getUsername())) {
                int id1 = jsonObject.getInt("id");
                int id2 = user.getId();

                String fullName1 = jsonObject.getString("fullName");
                String fullName2 = user.getFullName();

                Channel channel = new Channel();
                channel.userIds.add(id1);
                channel.userIds.add(id2);
                channel.userFullNames.add(fullName1);
                channel.userFullNames.add(fullName2);
                channel.setChannelId(Server.channels.size() + 1);
                if(!Server.channels.contains(channel) && !Objects.equals(id1, id2)) {
                    Server.channels.add(channel);
                    jsonObject.put("channelId", id2);
                    jsonObject.put("userFullNames", channel.userFullNames);
                    jsonObject.put("response", "successful");
                    break;
                }
            }
            jsonObject.put("response", "failure");
        }
        jsonObject.put("command", "response_add_new_contact");
        sendMessage(jsonObject.toString());
    }
}
