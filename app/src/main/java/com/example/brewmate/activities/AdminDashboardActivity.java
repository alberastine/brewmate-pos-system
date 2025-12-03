package com.example.brewmate.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.util.Date;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AdminDashboardActivity extends AppCompatActivity {

    SessionManager session;

    private TextView tvDailySales;
    private TextView tvTotalOrders;
    private View btnClearHistory;
    private TextView tvEmptyHistory;
    private RecyclerView recyclerViewHistory;
    private HistoryAdapter historyAdapter;
    private List<History> historyList = new ArrayList<>();
    private SharedPreferences prefs;
    private Gson gson = new Gson();

    private static final String SALES_PREF = "SalesHistory";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        session = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        tvDailySales = findViewById(R.id.tvDailySales);
        tvTotalOrders = findViewById(R.id.tvTotalOrders);

        btnClearHistory = findViewById(R.id.btnClearHistory);
        tvEmptyHistory = findViewById(R.id.tvEmptyHistory);

        btnClearHistory.setOnClickListener(v -> showClearHistoryConfirmation());

        findViewById(R.id.cardManageUsers).setOnClickListener(v ->
                startActivity(new Intent(AdminDashboardActivity.this, ManageUsersActivity.class)));

        findViewById(R.id.cardInventory).setOnClickListener(v ->
                startActivity(new Intent(AdminDashboardActivity.this, InventoryActivity.class)));

        findViewById(R.id.cardReports).setOnClickListener(v ->
                startActivity(new Intent(AdminDashboardActivity.this, ReportsActivity.class)));

        findViewById(R.id.cardSupplies).setOnClickListener(v ->
                startActivity(new Intent(AdminDashboardActivity.this, ManageSuppliesActivity.class)));

        findViewById(R.id.cardSettings).setOnClickListener(v ->
                startActivity(new Intent(AdminDashboardActivity.this, ManageSettingsActivity.class)));

        TextView tvToolbarWelcome = findViewById(R.id.toolbar_welcome);
        String adminName = getIntent().getStringExtra("username");
        if (adminName == null) {
            adminName = session.getUsername(); // fallback from session
        }
        if (adminName == null) adminName = "Admin";
        tvToolbarWelcome.setText(getString(R.string.welcome_message, adminName));

        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));

        prefs = getSharedPreferences("history_pref", MODE_PRIVATE);
        loadHistory();

        historyAdapter = new HistoryAdapter(historyList);
        recyclerViewHistory.setAdapter(historyAdapter);

        // Load and display real daily metrics
        updateDailySalesAndOrders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory();
        updateHistoryVisibility();
        historyAdapter.updateData(historyList);
        updateDailySalesAndOrders();
    }

    private void updateDailySalesAndOrders() {
        SharedPreferences salesPref = getSharedPreferences(SALES_PREF, MODE_PRIVATE);
        String data = salesPref.getString("sales_records", "[]");

        double dailySales = 0;
        int totalOrders = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy, hh:mm:ss a", Locale.getDefault());
        SimpleDateFormat compareFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String today = compareFormat.format(new Date());

        try {
            JSONArray array = new JSONArray(data);
            for (int i = 0; i < array.length(); i++) {
                JSONObject record = array.getJSONObject(i);
                String dateTime = record.getString("dateTime");
                Date saleDate = sdf.parse(dateTime);

                if (saleDate != null && compareFormat.format(saleDate).equals(today)) {
                    dailySales += record.getDouble("total");

                    JSONArray items = record.getJSONArray("items");
                    for (int j = 0; j < items.length(); j++) {
                        totalOrders += items.getJSONObject(j).getInt("quantity");
                    }
                }
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
        tvDailySales.setText(currencyFormat.format(dailySales));
        tvTotalOrders.setText(String.format(Locale.getDefault(), "%,d", totalOrders));
    }

    private void loadHistory() {
        String json = prefs.getString("history", "[]");
        Type listType = new TypeToken<List<History>>() {}.getType();
        historyList = gson.fromJson(json, listType);
        if (historyList == null) {
            historyList = new ArrayList<>();
        }
    }

    private void clearHistory() {
        historyList.clear();
        prefs.edit().putString("history", "[]").apply();
        historyAdapter.updateData(historyList);
        Toast.makeText(this, "Activity history cleared", Toast.LENGTH_SHORT).show();
    }

    private void showClearHistoryConfirmation() {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete all the history? This action can't be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    clearHistory();
                    updateHistoryVisibility();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(true)
                .show();
    }

    private void updateHistoryVisibility() {
        if (historyList.isEmpty()) {
            recyclerViewHistory.setVisibility(View.GONE);
            tvEmptyHistory.setVisibility(View.VISIBLE);
        } else {
            recyclerViewHistory.setVisibility(View.VISIBLE);
            tvEmptyHistory.setVisibility(View.GONE);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setVisible(menu.getItem(i).getItemId() == R.id.action_logout);
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
