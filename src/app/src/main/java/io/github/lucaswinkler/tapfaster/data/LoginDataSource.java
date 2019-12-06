package io.github.lucaswinkler.tapfaster.data;

import android.content.Context;
import android.content.Intent;

import io.github.lucaswinkler.tapfaster.data.model.User;
import io.github.lucaswinkler.tapfaster.ui.home.HomeActivity;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    public Result<User> login(Context context, String username, String password) {
        DatabaseHelper db = new DatabaseHelper(context);

        try {
            if (!db.isValidLogin(username, password)) {
                throw new IOException("Invalid login information.");
            }

            return new Result.Success<>(db.getUser(username, password));
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout(Context context) {
        Intent homeActivityIntent = new Intent(context, HomeActivity.class);
        context.startActivity(homeActivityIntent);
    }
}
