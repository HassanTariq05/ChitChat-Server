import App.env;
import model.Chat;
import model.User;

import java.sql.*;
import java.util.ArrayList;

public class SQLAdapter {
    public static void addUserToSql(int id, String username, String fullName, String password){
        try {
            Connection conn = DriverManager.getConnection(env.JDBC_URL, env.JDBC_USER, env.JDBC_PASSWORD);
            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO `Server`.`Users`(`user_id`, `user_username`,`user_fullname`, `user_password`) VALUES (?, ?, ?, ?)");
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, fullName);
            preparedStatement.setString(4, password);

            preparedStatement.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addChannelToSql(int channelId, ArrayList<Integer> userIds, String channelName,
                                       ArrayList<String> userFullNames,ArrayList<String> usernames){
        try {
            Connection conn = DriverManager.getConnection
                    (env.JDBC_URL, env.JDBC_USER, env.JDBC_PASSWORD);

            PreparedStatement preparedStatement = conn.prepareStatement
                    ("INSERT INTO `Server`.`Channels`(`channel_channelId`,\n" +
                            "`channel_userIds`,\n" +
                            "`channel_channelName`,\n" +
                            "`channel_userFullNames`,\n" +
                            "`channel_usernames`)" +
                            " VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setInt(1, channelId);
            preparedStatement.setString(2, String.valueOf(userIds));
            preparedStatement.setString(3, channelName);
            preparedStatement.setString(4, String.valueOf(userFullNames));
            preparedStatement.setString(5, String.valueOf(usernames));

            preparedStatement.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addChatToSql(int channelId, int senderId, int receiverId,
                                       String message, String timestamp){
        try {
            Connection conn = DriverManager.getConnection
                    (env.JDBC_URL, env.JDBC_USER, env.JDBC_PASSWORD);

            PreparedStatement preparedStatement = conn.prepareStatement
                    ("INSERT INTO `Server`.`Chats`(`chat_channelId`,\n" +
                            "`chat_senderId`,\n" +
                            "`chat_receiverId`,\n" +
                            "`chat_message`,\n" +
                            "`chat_timestamp`)" +
                            " VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setInt(1, channelId);
            preparedStatement.setInt(2, senderId);
            preparedStatement.setInt(3, receiverId);
            preparedStatement.setString(4, message);
            preparedStatement.setString(5, timestamp);

            preparedStatement.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void getUserListFromSql() {
        try {
            Connection conn = DriverManager.getConnection(env.JDBC_URL, env.JDBC_USER, env.JDBC_PASSWORD);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Server.Users");

            while (rs.next()) {
                String[] rowData = {
                        rs.getString("user_id"),
                        rs.getString("user_username"),
                        rs.getString("user_fullname"),
                        rs.getString("user_password")
                };
                User user = new User();
                user.setId(Integer.parseInt(rowData[0]));
                user.setUsername(rowData[1]);
                user.setFullName(rowData[2]);
                user.setPassword(rowData[3]);
                AppData.getInstance().users.add(user);
            }
            System.out.println("Loaded Users from Sql!");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void getChannelListFromSql() {
        try {
            Connection conn = DriverManager.getConnection(env.JDBC_URL, env.JDBC_USER, env.JDBC_PASSWORD);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Server.Channels");

            while (rs.next()) {
                String[] rowData = {
                        rs.getString("channel_channelId"),
                        rs.getString("channel_userIds"),
                        rs.getString("channel_channelName"),
                        rs.getString("channel_userFullNames"),
                        rs.getString("channel_usernames")
                };

                Channel channel = new Channel();
                channel.setChannelId(Integer.parseInt(rowData[0]));


                String userIdsString = rowData[1];
                String cleanedString = userIdsString.replaceAll("\\[|\\]|\\s", "");
                String[] stringIds = cleanedString.split(",");
                ArrayList<Integer> userIds = new ArrayList<>();
                for (String id : stringIds) {
                    userIds.add(Integer.parseInt(id));
                }
                channel.setUserIds(userIds);
                channel.setChannelName(rowData[2]);

                String userFullnamesStr = rowData[3];
                String cleanedString1 = userFullnamesStr.replaceAll("\\[|\\]|\\s", "");
                String[] stringNames = cleanedString1.split(",");
                ArrayList<String> userFullNames = new ArrayList<>();
                for (String name : stringNames) {
                    userFullNames.add(name);
                }
                channel.setUserFullNames(userFullNames);

                String usernamesStr = rowData[4];
                String cleanedString2 = usernamesStr.replaceAll("\\[|\\]|\\s", "");
                String[] stringUsernames = cleanedString2.split(",");
                ArrayList<String> usernames = new ArrayList<>();
                for (String username : stringUsernames) {
                    usernames.add(username);
                }
                channel.setUsernames(usernames);

                AppData.getInstance().channels.add(channel);
            }
            System.out.println("Loaded Channels from Sql!");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void getChatListFromSql() {
        try {
            Connection conn = DriverManager.getConnection(env.JDBC_URL, env.JDBC_USER, env.JDBC_PASSWORD);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Server.Chats");

            while (rs.next()) {
                String[] rowData = {
                        rs.getString("chat_channelId"),
                        rs.getString("chat_senderId"),
                        rs.getString("chat_receiverId"),
                        rs.getString("chat_message"),
                        rs.getString("chat_timestamp")
                };

                Chat chat = new Chat();
                chat.setChannelId(Integer.parseInt(rowData[0]));
                chat.setSenderId(Integer.parseInt(rowData[1]));
                chat.setReceiverId(Integer.parseInt(rowData[2]));
                chat.setMessage(rowData[3]);
                chat.setTimestamp(rowData[4]);
                AppData.getInstance().chatList.add(chat);
            }
            System.out.println("Loaded Chats from Sql!");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
