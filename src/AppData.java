import model.Chat;
import model.User;

import java.util.ArrayList;
import java.util.List;

public class AppData {
    private static AppData appDataInstance = null;
    private AppData() {
    }

    public static synchronized AppData getInstance() {
        if(appDataInstance == null) {
            appDataInstance = new AppData();
        }
        return  appDataInstance;
    }

    public List<User> users = new ArrayList<>();
    public List<Channel> channels = new ArrayList<>();
    public ArrayList<Chat> chatList = new ArrayList<>();
}
