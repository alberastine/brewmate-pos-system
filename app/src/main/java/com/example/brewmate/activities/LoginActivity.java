package com.example.brewmate.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.brewmate.R;
import com.example.brewmate.utils.SessionManager;


public class LoginActivity extends AppCompatActivity {

    EditText usernameField, passwordField;
    Button loginButton;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionManager(LoginActivity.this);
        usernameField = findViewById(R.id.editTextUsername);
        passwordField = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                if (username.equals("admin") && password.equals("1234")) {
                    // Redirect to Admin Dashboard
                    Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else if (username.equals("cashier") && password.equals("1234")) {
                    // Redirect to Cashier Dashboard
                    Intent intent = new Intent(LoginActivity.this, CashierDashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
