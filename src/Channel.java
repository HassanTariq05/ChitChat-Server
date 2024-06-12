import java.util.ArrayList;

public class Channel {
    public void setUserIds(ArrayList<Integer> userIds) {
        this.userIds = userIds;
    }

    ArrayList<Integer> userIds = new ArrayList<>();

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    int channelId;

    String channelName;

    public void setUserFullNames(ArrayList<String> userFullNames) {
        this.userFullNames = userFullNames;
    }

    ArrayList<String> userFullNames = new ArrayList<>();

    public void setUsernames(ArrayList<String> usernames) {
        this.usernames = usernames;
    }

    ArrayList<String> usernames = new ArrayList<>();

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public String toString() {
        return "{" +
                "\"userIds\":" + userIds +
                ", \"channelId\":" + channelId +
                ", \"channelName\":\"" + channelName + "\"" +
                ", \"userFullNames\":" + userFullNames +
                ", \"usernames\":" + usernames +
                '}';
    }
}
