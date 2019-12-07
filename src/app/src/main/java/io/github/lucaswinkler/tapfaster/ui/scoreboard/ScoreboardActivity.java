package io.github.lucaswinkler.tapfaster.ui.scoreboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.github.lucaswinkler.tapfaster.R;
import io.github.lucaswinkler.tapfaster.data.DatabaseHelper;
import io.github.lucaswinkler.tapfaster.data.UserManager;
import io.github.lucaswinkler.tapfaster.data.models.User;
import io.github.lucaswinkler.tapfaster.ui.game.GameActivity;
import io.github.lucaswinkler.tapfaster.ui.home.HomeActivity;
import io.github.lucaswinkler.tapfaster.ui.user.LoginActivity;

public class ScoreboardActivity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseHelper db;

    private ListView itemsListView;
    private EditText editTextName;
    private Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        db = new DatabaseHelper(this);

        itemsListView = findViewById(R.id.itemsListView);
        editTextName = findViewById(R.id.nameEditText);
        btnSearch = findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(this);

        updateDisplay("");
    }

    private void updateDisplay(String searchUsername) {
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        List<User> users = db.getUsers();
        List<User> filteredUsers = new ArrayList<>();

        for (User user : users) {
            if (user.getBestTime() <= 0) {
                continue;
            }

            if (searchUsername.isEmpty()){
                filteredUsers.add(user);
            } else {
                if (user.getUsername().toLowerCase().contains(searchUsername.toLowerCase())){
                    filteredUsers.add(user);
                }
            }
        }

        for (User user : filteredUsers) {
            HashMap<String, String> map = new HashMap<>();
            map.put("name", user.getUsername());
            map.put("best_time", user.getBestTimeToString());
            data.add(map);
        }

        int resource = R.layout.listview_item;
        String[] from = {"name", "best_time"};
        int[] to = {R.id.nameTextView, R.id.timeTextView};

        SimpleAdapter adapter = new SimpleAdapter(this, data, resource, from, to);
        itemsListView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSearch:
                updateDisplay(editTextName.getText().toString().trim());
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