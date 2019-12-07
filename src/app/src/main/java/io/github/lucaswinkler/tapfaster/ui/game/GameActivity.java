package io.github.lucaswinkler.tapfaster.ui.game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.github.lucaswinkler.tapfaster.R;
import io.github.lucaswinkler.tapfaster.data.DatabaseHelper;
import io.github.lucaswinkler.tapfaster.data.UserManager;
import io.github.lucaswinkler.tapfaster.data.models.User;
import io.github.lucaswinkler.tapfaster.ui.home.HomeActivity;
import io.github.lucaswinkler.tapfaster.ui.scoreboard.ScoreboardActivity;
import io.github.lucaswinkler.tapfaster.ui.user.LoginActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

enum GameState {
    START,
    WAIT,
    WARN,
    GO,
    RESULT,
    FINISH
}

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseHelper db;

    private Button btnTap;
    private Button btnSave;
    private TextView averageTextView;
    private TextView triesTextView;

    private static final int MAX_TRIES = 5;

    private GameState gameState;
    private Timer timer;
    private Random random;

    private long startTime;
    private long endTime;

    private int[] times;
    private int tries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        db = new DatabaseHelper(this);
        random = new Random();

        averageTextView = findViewById(R.id.averageTextView);
        triesTextView = findViewById(R.id.triesTextView);
        btnTap = findViewById(R.id.btnTap);
        btnSave = findViewById(R.id.btnSave);

        btnTap.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        setGameState(GameState.START);
    }

    private void initGame() {
        tries = 0;
        times = new int[MAX_TRIES];
        startTime = 0;
        endTime = 0;
        btnTap.setText(getResources().getString(R.string.game_start));
        btnSave.setVisibility(View.INVISIBLE);
        averageTextView.setText(getResources().getString(R.string.game_average, "0"));
        triesTextView.setText(getResources().getString(R.string.game_tries, Integer.toString(tries), Integer.toString(MAX_TRIES)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTap:
                playTurn();
                break;
            case R.id.btnSave:
                UserManager userManager = UserManager.getInstance();
                if (userManager.isLoggedIn()) {
                    User user = userManager.getLoggedInUser();
                    if (user.getBestTime() <= 0 || user.getBestTime() > getAverageTime()){
                        db.updateUser(user.getUsername(), getAverageTime());
                        Toast.makeText(getApplicationContext(), "Updated score to " + getAverageTime() + " ms", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), ScoreboardActivity.class));
                    }
                } else {
                    Intent loginActivityIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    loginActivityIntent.putExtra("averageTime", getAverageTime());
                    startActivity(loginActivityIntent);
                }
                break;
        }
    }

    private int getTimeElapsed() {
        return (int) (endTime - startTime);
    }

    private int getAverageTime() {
        int sum = 0;
        for (int time : times) sum += time;
        return Math.round(sum / tries);
    }

    private void setBackgroundColour(int colour) {
        btnTap.setBackgroundColor(colour);
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;

        switch (gameState) {
            case START:
                setBackgroundColour(getResources().getColor(R.color.colorGameBlue));
                initGame();
                break;
            case WAIT:
                setBackgroundColour(getResources().getColor(R.color.colorGameRed));
                btnTap.setText("Wait for green");
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setGameState(GameState.GO);
                            }
                        });
                    }
                }, random.nextInt(5000 - 1500) + 1500);
                break;
            case WARN:
                setBackgroundColour(getResources().getColor(R.color.colorGameBlue));
                btnTap.setText("Too soon! \n \n Tap to try again");
                timer.cancel();
                timer.purge();
                break;
            case GO:
                setBackgroundColour(getResources().getColor(R.color.colorGameGreen));
                startTime = System.currentTimeMillis();
                btnTap.setText("Tap!");
                break;
            case RESULT:
                setBackgroundColour(getResources().getColor(R.color.colorGameBlue));
                btnTap.setText(getResources().getString(R.string.game_result, Integer.toString(getTimeElapsed())));
                break;
            case FINISH:
                setBackgroundColour(getResources().getColor(R.color.colorGameBlue));
                btnTap.setText(getResources().getString(R.string.game_finish, Integer.toString(getTimeElapsed())));
                btnSave.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void playTurn() {
        switch (gameState) {
            case START:
            case WARN:
            case RESULT:
                setGameState(GameState.WAIT);
                break;
            case WAIT:
                setGameState(GameState.WARN);
                break;
            case GO:
                endTime = System.currentTimeMillis();
                times[tries] = getTimeElapsed();
                tries += 1;

                averageTextView.setText(getResources().getString(R.string.game_average, Integer.toString(getAverageTime())));
                triesTextView.setText(getResources().getString(R.string.game_tries, Integer.toString(tries), Integer.toString(MAX_TRIES)));

                if (tries >= MAX_TRIES) {
                    setGameState(GameState.FINISH);
                    break;
                }

                setGameState(GameState.RESULT);
                break;
            case FINISH:
                setGameState(GameState.START);
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
