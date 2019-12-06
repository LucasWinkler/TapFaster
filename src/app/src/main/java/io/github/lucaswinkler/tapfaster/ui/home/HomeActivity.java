package io.github.lucaswinkler.tapfaster.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.github.lucaswinkler.tapfaster.R;
import io.github.lucaswinkler.tapfaster.data.UserManager;
import io.github.lucaswinkler.tapfaster.ui.game.GameActivity;
import io.github.lucaswinkler.tapfaster.ui.scoreboard.ScoreboardActivity;
import io.github.lucaswinkler.tapfaster.ui.user.LoginActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnPlay = findViewById(R.id.activity_home_play_btn);
        btnPlay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_home_play_btn:
                Intent loginActivityIntent = new Intent(this, GameActivity.class);
                startActivity(loginActivityIntent);
                break;
        }
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
