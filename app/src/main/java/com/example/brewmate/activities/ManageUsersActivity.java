package com.example.brewmate.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brewmate.R;
import com.example.brewmate.adapters.UserAdapter;
import com.example.brewmate.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.appcompat.widget.Toolbar;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ManageUsersActivity extends AppCompatActivity {

    private TextView tvToolbarSubtitle;
    private RecyclerView recyclerViewUsers;
    private List<User> cashierList = new ArrayList<>();
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);
        }

        // Inflate your custom toolbar layout (kept exactly the same)
        View customToolbar = getLayoutInflater().inflate(R.layout.custom_toolbar_manage_users, toolbar, false);
        toolbar.addView(customToolbar);
        tvToolbarSubtitle = customToolbar.findViewById(R.id.tvToolbarSubtitle);

        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        // Load users from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("users_pref", MODE_PRIVATE);
        String json = prefs.getString("users", "[]");
        Gson gson = new Gson();
        Type listType = new TypeToken<List<User>>() {}.getType();
        List<User> allUsers = gson.fromJson(json, listType);

        // Filter only cashiers
        cashierList.clear();
        for (User user : allUsers) {
            if ("cashier".equalsIgnoreCase(user.getRole())) {
                cashierList.add(user);
            }
        }

        // Update toolbar subtitle count dynamically
        tvToolbarSubtitle.setText(getString(R.string.cashiers_count, cashierList.size()));

        // Set adapter
        adapter = new UserAdapter(this, cashierList);
        recyclerViewUsers.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        MenuItem addItem = menu.findItem(R.id.action_add_user);
        if (addItem.getActionView() != null) {
            addItem.getActionView().setOnClickListener(v ->
                    Toast.makeText(this, "Add User clicked", Toast.LENGTH_SHORT).show());
        }

        // Show only Add User
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            item.setVisible(item.getItemId() == R.id.action_add_user);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent intent = new Intent(this, AdminDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.action_add_user) {
            Toast.makeText(this, "Add User", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
