package model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public final class User {
    int id;
    String username;
    String fullName;
    String password;

    public static String[][] userContacts = new String[100][];
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