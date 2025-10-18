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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brewmate.R;
import com.example.brewmate.adapters.UserAdapter;

import com.example.brewmate.models.User;
import com.example.brewmate.models.History;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.appcompat.widget.Toolbar;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ManageUsersActivity extends AppCompatActivity {

    private TextView tvToolbarSubtitle;
    private RecyclerView recyclerViewUsers;
    private List<User> cashierList = new ArrayList<>();
    private UserAdapter adapter;
    private LinearLayout addUserForm;
    private EditText etUsername, etFullName, etEmail, etPassword;
    private Button btnCancel, btnSubmit;
    private Gson gson = new Gson();

    private User editingUser = null;

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

        // 👇 Initialize form views
        addUserForm = findViewById(R.id.addUserForm);
        etUsername = findViewById(R.id.etUsername);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnCancel = findViewById(R.id.btnCancel);
        btnSubmit = findViewById(R.id.btnSubmit);

        loadUsers();

        // Cancel hides the form
        btnCancel.setOnClickListener(v -> {
            addUserForm.setVisibility(View.GONE);
            clearFormFields();
            editingUser = null; // cancel edit
        });

        // Submit creates a new user
        btnSubmit.setOnClickListener(v -> {
            if (editingUser != null) {
                updateExistingUser();  // ✅ Edit mode
            } else {
                addNewUser();          // ✅ Add mode
            }
        });

    }

    private void loadUsers() {
        SharedPreferences prefs = getSharedPreferences("users_pref", MODE_PRIVATE);
        String json = prefs.getString("users", "[]");
        Type listType = new TypeToken<List<User>>() {}.getType();
        List<User> allUsers = gson.fromJson(json, listType);

        cashierList.clear();
        for (User user : allUsers) {
            if ("cashier".equalsIgnoreCase(user.getRole())) {
                cashierList.add(user);
            }
        }

        tvToolbarSubtitle.setText(getString(R.string.cashiers_count, cashierList.size()));

        adapter = new UserAdapter(this, cashierList,
            userToDelete -> {
                    deleteUser(userToDelete);
            },
            userToEdit -> {
                    showEditForm(userToEdit);
            }
        );
        recyclerViewUsers.setAdapter(adapter);
    }

    private void showEditForm(User user) {
        editingUser = user;
        etUsername.setText(user.getUsername());
        etFullName.setText(user.getFullName());
        etEmail.setText(user.getEmail());
        etPassword.setText(user.getPassword());
        addUserForm.setVisibility(View.VISIBLE);
    }

    private void updateExistingUser() {
        String username = etUsername.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("users_pref", MODE_PRIVATE);
        String json = prefs.getString("users", "[]");
        Type listType = new TypeToken<List<User>>() {}.getType();
        List<User> allUsers = gson.fromJson(json, listType);

        for (User u : allUsers) {
            if (u.getId() == editingUser.getId()) {
                u.setUsername(username);
                u.setFullName(fullName);
                u.setEmail(email);
                u.setPassword(password);
                break;
            }
        }

        saveUsers(allUsers);
        loadUsers();
        saveHistory("Cashier Updated", fullName + " details were updated");

        Toast.makeText(this, "User updated", Toast.LENGTH_SHORT).show();
        addUserForm.setVisibility(View.GONE);
        clearFormFields();
        editingUser = null;
    }

    private void deleteUser(User userToDelete) {
        SharedPreferences prefs = getSharedPreferences("users_pref", MODE_PRIVATE);
        String json = prefs.getString("users", "[]");
        Type listType = new TypeToken<List<User>>() {}.getType();
        List<User> allUsers = gson.fromJson(json, listType);

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete " + userToDelete.getFullName() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // 👇 Deletion logic moved inside here
                    for (int i = 0; i < allUsers.size(); i++) {
                        if (allUsers.get(i).getId() == userToDelete.getId()) {
                            allUsers.remove(i);
                            break;
                        }
                    }

                    saveUsers(allUsers);
                    loadUsers();

                    saveHistory("Cashier deleted", userToDelete.getFullName() + " was removed from the team");

                    Toast.makeText(this, "Deleted " + userToDelete.getFullName(), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }



    private void saveUsers(List<User> allUsers) {
        SharedPreferences prefs = getSharedPreferences("users_pref", MODE_PRIVATE);
        prefs.edit().putString("users", gson.toJson(allUsers)).apply();
    }

    private void addNewUser() {
        String username = etUsername.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("users_pref", MODE_PRIVATE);
        String json = prefs.getString("users", "[]");
        Type listType = new TypeToken<List<User>>() {}.getType();
        List<User> allUsers = gson.fromJson(json, listType);

        int newId = allUsers.size() + 1;
        String createdAt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        User newUser = new User(newId, username, fullName, email, "cashier", password, createdAt);

        allUsers.add(newUser);
        saveUsers(allUsers);
        loadUsers();

        saveHistory("New cashier added", fullName + " joined the team");

        Toast.makeText(this, "New cashier added", Toast.LENGTH_SHORT).show();
        addUserForm.setVisibility(View.GONE);
        clearFormFields();
    }

    private void saveHistory(String action, String description) {
        SharedPreferences prefs = getSharedPreferences("history_pref", MODE_PRIVATE);
        String json = prefs.getString("history", "[]");
        Type listType = new TypeToken<List<History>>() {}.getType();
        List<History> historyList = gson.fromJson(json, listType);

        historyList.add(0, new History(action, description, System.currentTimeMillis())); // add on top
        prefs.edit().putString("history", gson.toJson(historyList)).apply();
    }

    private void clearFormFields() {
        etUsername.setText("");
        etFullName.setText("");
        etEmail.setText("");
        etPassword.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        MenuItem addItem = menu.findItem(R.id.action_add_user);
        if (addItem.getActionView() != null) {
            addItem.getActionView().setOnClickListener(v ->
                    toggleFormVisibility()
            );
        } else {
            addItem.setOnMenuItemClickListener(item -> {
                toggleFormVisibility();
                return true;
            });
        }

        // Show only Add User
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            item.setVisible(item.getItemId() == R.id.action_add_user);
        }

        return true;
    }

    private void toggleFormVisibility() {
        if (addUserForm.getVisibility() == View.GONE) {
            addUserForm.setVisibility(View.VISIBLE);
            editingUser = null;
        } else {
            addUserForm.setVisibility(View.GONE);
            clearFormFields();
        }
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
        }

        return super.onOptionsItemSelected(item);
    }

}
