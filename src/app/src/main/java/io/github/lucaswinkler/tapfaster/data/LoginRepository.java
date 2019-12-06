package io.github.lucaswinkler.tapfaster.data;

import android.content.Context;
import android.view.View;

import io.github.lucaswinkler.tapfaster.data.model.User;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private LoginDataSource dataSource;

    private User user = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout(Context context) {
        user = null;
        dataSource.logout(context);
    }

    private void setLoggedInUser(User user) {
        this.user = user;
    }

    public Result<User> login(Context context, String username, String password) {
        Result<User> result = dataSource.login(context, username, password);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<User>) result).getData());
        }
        return result;
    }
}
