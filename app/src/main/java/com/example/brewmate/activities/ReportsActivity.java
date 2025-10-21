package com.example.brewmate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.brewmate.R;

import androidx.appcompat.widget.Toolbar;

public class ReportsActivity extends AppCompatActivity {

    private TextView tvToolbarSubtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);
        }

        // Inflate custom toolbar layout for inventory
        View customToolbar = getLayoutInflater().inflate(R.layout.custom_toolbar_sales_report, toolbar, false);
        toolbar.addView(customToolbar);
        tvToolbarSubtitle = customToolbar.findViewById(R.id.tvToolbarSubtitle);
        tvToolbarSubtitle.setText(getString(R.string.sales_report_sub_label));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);

        // Only show the items you want
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setVisible(false);
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
        }

        return super.onOptionsItemSelected(item);
    }
}