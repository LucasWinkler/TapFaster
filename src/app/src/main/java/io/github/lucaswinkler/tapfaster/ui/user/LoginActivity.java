package io.github.lucaswinkler.tapfaster.ui.user;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.github.lucaswinkler.tapfaster.R;
import io.github.lucaswinkler.tapfaster.data.DatabaseHelper;
import io.github.lucaswinkler.tapfaster.data.UserManager;
import io.github.lucaswinkler.tapfaster.data.models.User;
import io.github.lucaswinkler.tapfaster.ui.home.HomeActivity;
import io.github.lucaswinkler.tapfaster.ui.scoreboard.ScoreboardActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private DatabaseHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(this);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManager userManager = UserManager.getInstance();
                boolean success = userManager.login(
                        getApplicationContext(),
                        usernameEditText.getText().toString().trim(),
                        passwordEditText.getText().toString().trim());

                if (success) {
                    showLoginSuccess();
                    Bundle extras = getIntent().getExtras();
                    if (extras != null) {
                        int averageTime = extras.getInt("averageTime", 0);
                        if (averageTime > 0) {
                            if (userManager.isLoggedIn()) {
                                User user = userManager.getLoggedInUser();
                                if (user.getBestTime() <= 0 || averageTime < user.getBestTime()) {
                                    db.updateUser(user.getUsername(), averageTime);
                                    Toast.makeText(getApplicationContext(), "Updated score to " + averageTime + " ms", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(), ScoreboardActivity.class));
                                } else {
                                    Toast.makeText(getApplicationContext(), "You already have a better score", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }

                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                } else {
                    showLoginFailed();
                }
            }
        });
    }

    private void showLoginSuccess() {
        User user = UserManager.getInstance().getLoggedInUser();
        Toast.makeText(getApplicationContext(), getString(R.string.welcome, user.getUsername()), Toast.LENGTH_SHORT).show();
    }

    private void showLoginFailed() {
        Toast.makeText(getApplicationContext(), getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (UserManager.getInstance().isLoggedIn()) {
            getMenuInflater().inflate(R.menu.menu_logged_in, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_logged_out, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_home:
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                return true;
            case R.id.activity_scoreboard:
                startActivity(new Intent(getApplicationContext(), ScoreboardActivity.class));
                return true;
            case R.id.activity_login:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                return true;
            case R.id.activity_logout:
                UserManager.getInstance().logout(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
