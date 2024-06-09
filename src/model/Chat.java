package model;

import org.json.JSONObject;

import java.util.Date;

public class Chat {
    int senderId;
    int receiverId;

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    int channelId;
    String message;
    String timestamp;
    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "{" +
                "\"channelId\":" + channelId +
                ",\"senderId\":" + senderId +
                ",\"receiverId\":" + receiverId +
                ",\"message\":\"" + message + '\"' +
                ",\"timestamp\":" + "\"" + timestamp + "\"" +
                '}';
    }

    public JSONObject toJSONObject() {
        return new JSONObject(toString());
    }

    public static Chat convert(String data) {
        JSONObject jsonObject = new JSONObject(data);
        Chat chat = new Chat();
        chat.message = jsonObject.getString("message");
        chat.senderId = jsonObject.getInt("senderId");
        chat.receiverId = jsonObject.getInt("receiverId");
        return chat;
    }
}
