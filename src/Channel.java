import java.util.ArrayList;

public class Channel {
    ArrayList<Integer> userIds = new ArrayList<>();

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    int channelId;

    String channelName;

    ArrayList<String> userFullNames = new ArrayList<>();

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
                '}';
    }
}
