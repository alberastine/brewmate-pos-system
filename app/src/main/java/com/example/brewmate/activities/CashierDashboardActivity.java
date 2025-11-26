package com.example.brewmate.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.brewmate.R;
import com.example.brewmate.utils.SessionManager;

import androidx.appcompat.widget.Toolbar;

public class CashierDashboardActivity extends AppCompatActivity {

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashier_dashboard);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        session = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Disable default title (removes "BrewMate")
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        TextView tvToolbarWelcome = findViewById(R.id.toolbar_welcome);
        // Get username from Intent
        String cashierName = getIntent().getStringExtra("username");
        if (cashierName == null) cashierName = "Cashier";

        // Use getString with placeholder
        tvToolbarWelcome.setText(getString(R.string.welcome_message, cashierName));

        findViewById(R.id.cardNewTransaction).setOnClickListener(view -> {
            String cashierNameStamp = getIntent().getStringExtra("username");
            if (cashierNameStamp == null) cashierNameStamp = "Cashier";

            Intent intent = new Intent(CashierDashboardActivity.this, TransactionActivity.class);
            intent.putExtra("username", cashierNameStamp);
            startActivity(intent);
        });
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
