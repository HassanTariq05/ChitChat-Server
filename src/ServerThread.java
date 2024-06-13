import model.Chat;
import model.Keys;
import model.User;
import org.json.JSONArray;
import org.json.JSONObject;

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
        String fullname = json.getString("fullname");
        String password = json.getString("password");

        if (fullname.length() > 20 || fullname.length() < 3) {
            sendError(json, "Full name should be between 3 & 20 characters!");
            return;
        }

        if (username.length() < 8 || !username.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            sendError(json, "Username should be alpha numeric and long!");
            return;
        }
        if (password.length() < 8 || !password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            sendError(json, "Password should be alpha numeric and long!");
            return;
        }
        if (usernameExists(username)) {
            sendError(json, "Username already exists!");
            return;
        }

        registerUser(json);
    }


    private Boolean usernameExists(String username) {
        System.out.println("users:");
        System.out.println(AppData.getInstance().users);
        Boolean found = false;
        for(int i=0; i < AppData.getInstance().users.size(); i++) {
            User user = AppData.getInstance().users.get(i);
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

        if(!AppData.getInstance().users.isEmpty()) {
            int id = AppData.getInstance().users.getLast().getId() + 1;
            user.setId(id);
        } else {
            user.setId(1);
        }

        AppData.getInstance().users.add(user);
        SQLAdapter.addUserToSql(user.getId(), user.getUsername(), user.getFullName(), user.getPassword());

        json.put("command", "registration_successful");
        json.put(Keys.KEY_ID, user.getId());

        sendMessage(json.toString());

        System.out.println("users after registration:");
        System.out.println(AppData.getInstance().users);
    }

    private void sendError(JSONObject jsonObject, String errorMessage) {
        jsonObject.put("command", "error");
        jsonObject.put("error_message", errorMessage);
        sendMessage(jsonObject.toString());
    }
    private void handleLogin(JSONObject jsonObject) {
        String username = jsonObject.getString(Keys.KEY_USERNAME);
        String password = jsonObject.getString(Keys.KEY_PASSWORD);

        for(int i = 0; i <AppData.getInstance().users.size(); i++) {
            User user = AppData.getInstance().users.get(i);
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
        AppData.getInstance().chatList.add(chat);

        SQLAdapter.addChatToSql(chat.getChannelId(), chat.getSenderId(), chat.getReceiverId(), chat.getMessage(), chat.getTimestamp());

        System.out.println("Message added to chatList: " + jsonObject);
        System.out.println("Updated chatlist:" + AppData.getInstance().chatList);
        sendMessageToAllThreads(jsonObject.toString());
    }
    private void sendMessageToAllThreads(String message) {
        for (ServerThread thread : threadList) {
            thread.sendMessage(message);
        }
    }

    private void handleAddNewChannel(JSONObject jsonObject) {
        String username = jsonObject.getString("username");
        String myUsername = jsonObject.getString("myUsername");
        for(int i = 0; i < AppData.getInstance().users.size(); i++) {
            User user = AppData.getInstance().users.get(i);
            if(username.equalsIgnoreCase(user.getUsername())) {
                int id1 = jsonObject.getInt("id");
                int id2 = user.getId();

                String fullName1 = jsonObject.getString("fullName");
                String fullName2 = user.getFullName();

                Channel channel = new Channel();
                channel.userIds.add(id1);
                channel.userIds.add(id2);
                channel.userFullNames.add("\""+ fullName1+"\"");
                channel.userFullNames.add("\""+ fullName2 + "\"");
                channel.usernames.add("\""+ username +"\"");
                channel.usernames.add("\""+ myUsername + "\"");
                channel.setChannelId(AppData.getInstance().channels.size() + 1);
                if(!AppData.getInstance().channels.contains(channel) && !Objects.equals(id1, id2)) {
                    AppData.getInstance().channels.add(channel);
                    SQLAdapter.addChannelToSql(channel.channelId, channel.userIds, "", channel.userFullNames, channel.usernames);
                    jsonObject.put("channelId", id2);
                    JSONArray userFullNamesArray = new JSONArray(channel.userFullNames);
                    jsonObject.put("userFullNames", userFullNamesArray);
                    jsonObject.put("response", "successful");
                    sendContactToAllThread(jsonObject);
                    break;
                }
            }
            jsonObject.put("response", "failure");
        }
        jsonObject.put("command", "response_add_new_contact");
        sendMessage(jsonObject.toString());
    }

    private void sendContactToAllThread(JSONObject jsonObject) {
        for (ServerThread thread : threadList) {
            jsonObject.put("command", "contact_added_to_server");
            thread.sendMessage(jsonObject.toString());
            System.out.println("JSON:" + jsonObject);
        }
    }
}
