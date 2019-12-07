package io.github.lucaswinkler.tapfaster.ui.game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.github.lucaswinkler.tapfaster.R;
import io.github.lucaswinkler.tapfaster.data.DatabaseHelper;
import io.github.lucaswinkler.tapfaster.data.UserManager;
import io.github.lucaswinkler.tapfaster.ui.home.HomeActivity;
import io.github.lucaswinkler.tapfaster.ui.scoreboard.ScoreboardActivity;
import io.github.lucaswinkler.tapfaster.ui.user.LoginActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Arrays;
import java.util.Random;
import java.util.Timer;

enum GameState {
    START,
    WAIT,
    WARN,
    GO,
    FINISH
}

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseHelper db;

    private Button btnTest;

    private static final int MAX_TRIES = 5;

    private GameState gameState;
    private Timer timer;
    private Random random;

    private long startTime;
    private long endTime;

    private int times[];
    private int triesLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        db = new DatabaseHelper(this);
        timer = new Timer();
        random = new Random();

        btnTest = findViewById(R.id.btnTest);
        btnTest.setOnClickListener(this);

        setGameState(GameState.START);
    }

    private void initGame() {
        triesLeft = MAX_TRIES;
        times = new int[5];
        startTime = -1;
        endTime = -1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTest:
                playTurn();
                break;
        }
    }

    private long getTimeElapsedInMilliseconds() {
        return (endTime - startTime) / 1000000;
    }

    private int getAverageTime() {
        int sum = 0;
        for (int time : times) sum += time;
        return Math.round(sum / MAX_TRIES);
    }

    private void setGameState(GameState gameState) {
        this.gameState = gameState;

        switch (gameState) {
            case START:
                btnTest.setBackgroundColor(getResources().getColor(R.color.colorGameBlue));
                initGame();
                break;
            case WAIT:
                btnTest.setBackgroundColor(getResources().getColor(R.color.colorGameRed));
                break;
            case WARN:
                btnTest.setBackgroundColor(getResources().getColor(R.color.colorGameBlue));
                break;
            case GO:
                btnTest.setBackgroundColor(getResources().getColor(R.color.colorGameGreen));
                startTime = System.nanoTime();
                break;
            case FINISH:
                btnTest.setBackgroundColor(getResources().getColor(R.color.colorGameBlue));
                break;
        }
    }

    private void playTurn() {
        if (triesLeft <= 0) {
            setGameState(GameState.FINISH);
        }

        switch (gameState) {
            case START:
                initGame();
                break;
            case WAIT:
                break;
            case WARN:
                setGameState(GameState.GO);
                break;
            case GO:
                endTime = System.nanoTime();
                triesLeft--;
                int elapsedTime = (int) getTimeElapsedInMilliseconds();
                // TODO: Display time and tries left
                break;
            case FINISH:
                int averageTime = getAverageTime();
                // TODO: Display average and save score button
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
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
