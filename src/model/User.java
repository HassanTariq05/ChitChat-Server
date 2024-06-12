package model;

public final class User {
    private int id;
    private String username;
    private String fullName;
    private String password;

    public static String[][] userContacts = new String[100][];

    public User() {}

    public User(User user) {
        this.id = user.id;
        this.username = user.username;
        this.fullName = user.fullName;
        this.password = user.password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ",\"username\":\"" + username + '\"' +
                ",\"fullname\":\"" + fullName + '\"' +
                ",\"password\":\"" + password + '\"' +
                '}';
    }
}
