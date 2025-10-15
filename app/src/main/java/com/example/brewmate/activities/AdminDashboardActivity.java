package com.example.brewmate.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Type;

import com.example.brewmate.R;
import com.example.brewmate.adapters.HistoryAdapter;
import com.example.brewmate.models.History;
import com.example.brewmate.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



public class AdminDashboardActivity extends AppCompatActivity {

    SessionManager session;

    private TextView tvDailySales;
    private TextView tvTotalOrders;
    private RecyclerView recyclerViewHistory;
    private HistoryAdapter historyAdapter;
    private List<History> historyList = new ArrayList<>();
    private SharedPreferences prefs;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        session = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Disable default title (removes "BrewMate")
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

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

        TextView tvToolbarWelcome = findViewById(R.id.toolbar_welcome);
        // Get username from Intent
        String adminName = getIntent().getStringExtra("username");
        if (adminName == null) adminName = "Admin";

        // Use getString with placeholder
        tvToolbarWelcome.setText(getString(R.string.welcome_message, adminName));

        // History RecyclerView setup
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));

        prefs = getSharedPreferences("history_pref", MODE_PRIVATE);
        loadHistory();

        historyAdapter = new HistoryAdapter(historyList);
        recyclerViewHistory.setAdapter(historyAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload history whenever activity resumes
        loadHistory();
        historyAdapter.updateData(historyList);
    }

    private void loadHistory() {
        String json = prefs.getString("history", "[]");
        Type listType = new TypeToken<List<History>>() {}.getType();
        historyList = gson.fromJson(json, listType);
        if (historyList == null) {
            historyList = new ArrayList<>();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);

        // Only show the items you want
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            item.setVisible(item.getItemId() == R.id.action_logout);
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
