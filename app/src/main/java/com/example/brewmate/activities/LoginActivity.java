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

        initializeUsers(); // Initialize default users

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

                            if (user.getRole().equals("admin")) {
                                startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                            } else if (user.getRole().equals("cashier")) {
                                startActivity(new Intent(LoginActivity.this, CashierDashboardActivity.class));
                            }

                            finish(); // Close login activity
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

    // Initialize default users in SharedPreferences
    private void initializeUsers() {
        SharedPreferences prefs = getSharedPreferences("users_pref", MODE_PRIVATE);
        String usersJson = prefs.getString("users", null);

        if (usersJson == null) {
            List<User> users = new ArrayList<>();
            users.add(new User("Alberastine", "admin", "1234"));
            users.add(new User("Serafin", "cashier", "1234"));

            Gson gson = new Gson();
            usersJson = gson.toJson(users);

            prefs.edit().putString("users", usersJson).apply();
        }
    }
}
