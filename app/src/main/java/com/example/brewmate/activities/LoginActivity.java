package com.example.brewmate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.brewmate.R;
import com.example.brewmate.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    EditText usernameField, passwordField;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameField = findViewById(R.id.editTextUsername);
        passwordField = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);

        initializeUsers(); // Ensure default users exist

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences prefs = getSharedPreferences("users_pref", MODE_PRIVATE);
                String usersJson = prefs.getString("users", null);

                if (usersJson != null) {
                    Gson gson = new Gson();
                    Type userListType = new TypeToken<ArrayList<User>>() {}.getType();
                    List<User> users = gson.fromJson(usersJson, userListType);

                    boolean loggedIn = false;

                    for (User user : users) {
                        if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                            loggedIn = true;

                            Intent intent;
                            if (user.getRole().equals("admin")) {
                                intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                            } else {
                                intent = new Intent(LoginActivity.this, CashierDashboardActivity.class);
                            }

                            // Pass the logged-in username
                            intent.putExtra("username", user.getUsername());
                            startActivity(intent);
                            finish();
                            break;
                        }
                    }

                    if (!loggedIn) {
                        Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    // Initialize default users and add any missing ones
    private void initializeUsers() {
        SharedPreferences prefs = getSharedPreferences("users_pref", MODE_PRIVATE);
        String usersJson = prefs.getString("users", null);

        Gson gson = new Gson();
        List<User> users;

        if (usersJson == null) {
            users = new ArrayList<>();
        } else {
            Type userListType = new TypeToken<ArrayList<User>>() {}.getType();
            users = gson.fromJson(usersJson, userListType);
        }

        // Add default users if missing
        addUserIfNotExists(users, new User("Alberastine", "admin", "1234"));
        addUserIfNotExists(users, new User("Shaina", "admin", "1234"));
        addUserIfNotExists(users, new User("Serafin", "cashier", "1234"));

        // Save updated users list
        prefs.edit().putString("users", gson.toJson(users)).apply();
    }

    // Helper method to add a user only if they don’t exist
    private void addUserIfNotExists(List<User> users, User newUser) {
        for (User user : users) {
            if (user.getUsername().equals(newUser.getUsername())) {
                return; // Already exists
            }
        }
        users.add(newUser);
    }
}
