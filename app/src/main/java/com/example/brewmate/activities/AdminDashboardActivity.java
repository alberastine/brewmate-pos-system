package com.example.brewmate.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import com.example.brewmate.R;
import com.example.brewmate.utils.SessionManager;

import androidx.appcompat.widget.Toolbar;

public class AdminDashboardActivity extends AppCompatActivity {

    SessionManager session;

    private TextView tvDailySales;
    private TextView tvTotalOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        session = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.cardManageUsers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminDashboardActivity.this, ManageUsersActivity.class));
            }
        });

        findViewById(R.id.cardInventory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminDashboardActivity.this, InventoryActivity.class));
            }
        });

        findViewById(R.id.cardReports).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminDashboardActivity.this, ReportsActivity.class));
            }
        });

        // Get the reference to the TextView
        tvDailySales = findViewById(R.id.tvDailySales);

        // Example: simulate real data (e.g., from a database or API)
        double todaySales = 15230.50;

        // Format and set it
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
        String displayText = currencyFormat.format(todaySales);
        tvDailySales.setText(displayText);

        tvTotalOrders = findViewById(R.id.tvTotalOrders);

        // 🧾 Hard-coded total orders for now
        int totalOrders = 2543;

        // Set directly to TextView
        tvTotalOrders.setText(String.format(Locale.getDefault(), "%,d", totalOrders));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);

        // Hide the "Add User" button on the Admin Dashboard
        MenuItem addUserItem = menu.findItem(R.id.action_add_user);
        if (addUserItem != null) {
            addUserItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            session.clearSession();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
