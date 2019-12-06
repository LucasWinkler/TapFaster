package io.github.lucaswinkler.tapfaster.data;

import android.content.Context;
import android.content.Intent;

import java.io.IOException;

import io.github.lucaswinkler.tapfaster.data.models.User;
import io.github.lucaswinkler.tapfaster.ui.home.HomeActivity;

public class UserManager {
    private static volatile UserManager instance;
    private User user = null;

    private UserManager() {
    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout(Context context) {
        user = null;
        Intent homeActivityIntent = new Intent(context, HomeActivity.class);
        context.startActivity(homeActivityIntent);
    }

    private void setLoggedInUser(User user) {
        this.user = user;
    }
    public User getLoggedInUser() {
        return this.user;
    }

    public boolean login(Context context, String username, String password) {
        DatabaseHelper db = new DatabaseHelper(context);
        boolean result;
        User user = null;

        try {
            // If no user then register a new one
            boolean isValid = db.isValidLogin(username, password);
            if (!isValid) {
                if (db.checkUserExists(username)) {
                    throw new IOException("Username already taken");
                } else {
                    db.addUser(username, password);
                }
            }
            user = db.getUser(username);
            result = user != null;
        } catch (Exception e) {
            result = false;
        }

        if (result) {
            setLoggedInUser(user);
        }
        return result;
    }
}
