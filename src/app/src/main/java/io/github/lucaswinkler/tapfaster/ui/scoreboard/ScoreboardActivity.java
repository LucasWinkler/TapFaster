package io.github.lucaswinkler.tapfaster.ui.scoreboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.github.lucaswinkler.tapfaster.R;
import io.github.lucaswinkler.tapfaster.data.DatabaseHelper;
import io.github.lucaswinkler.tapfaster.data.UserManager;
import io.github.lucaswinkler.tapfaster.data.models.User;
import io.github.lucaswinkler.tapfaster.ui.home.HomeActivity;
import io.github.lucaswinkler.tapfaster.ui.user.LoginActivity;

public class ScoreboardActivity extends AppCompatActivity {
    private ListView itemsListView;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        itemsListView = findViewById(R.id.itemsListView);
        db = new DatabaseHelper(this);
        updateDisplay("");
    }

    private void updateDisplay(String searchUsername) {
        // TODO: Filter by the text in the search bar and order by the time
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        List<User> allUsers = db.getUsers();
        List<User> users = new ArrayList<User>();

        for (User user : users) {
            HashMap<String, String> map = new HashMap<String, String>();

            // If no best time then don't display on scoreboard
            if (user.getBestTime() <= 0) {
                continue;
            }

            if (!searchUsername.isEmpty()){
                if (user.getUsername().contains(searchUsername)) {
                    map.put("name", user.getUsername());
                    map.put("best_time", user.getBestTimeToString());
                }
            } else {
                map.put("name", user.getUsername());
                map.put("best_time", user.getBestTimeToString());
            }

            data.add(map);
        }

        int resource = R.layout.listview_item;
        String[] from = {"name", "best_time"};
        int[] to = {R.id.nameTextView, R.id.timeTextView};

        SimpleAdapter adapter = new SimpleAdapter(this, data, resource, from, to);
        itemsListView.setAdapter(adapter);
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