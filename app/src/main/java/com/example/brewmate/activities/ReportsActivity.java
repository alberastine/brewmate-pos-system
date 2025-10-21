package com.example.brewmate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.graphics.Color;

import com.example.brewmate.R;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.brewmate.adapters.ReportsAdapter;
import com.example.brewmate.adapters.TopProductsAdapter;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ReportsActivity extends AppCompatActivity {

    private TextView tvToolbarSubtitle;
    private LineChart lineChart;
    private BarChart barChart;
    private RecyclerView recyclerTopProducts, recyclerTransactions;

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

        lineChart = findViewById(R.id.lineChart);
        barChart = findViewById(R.id.barChart);
        recyclerTopProducts = findViewById(R.id.recyclerTopProducts);
        recyclerTransactions = findViewById(R.id.recyclerTransactions);

        setupLineChart();
        setupBarChart();
        setupRecyclerViews();
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

    private void setupLineChart() {
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1, 1200));
        entries.add(new Entry(2, 1400));
        entries.add(new Entry(3, 1100));
        entries.add(new Entry(4, 1600));
        entries.add(new Entry(5, 1800));
        entries.add(new Entry(6, 2100));
        entries.add(new Entry(7, 1900));

        LineDataSet dataSet = new LineDataSet(entries, "Sales");
        dataSet.setColor(Color.parseColor("#6F4E37"));
        dataSet.setCircleColor(Color.parseColor("#6F4E37"));
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(4f);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.invalidate();
    }

    private void setupBarChart() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1, 45));
        entries.add(new BarEntry(2, 52));
        entries.add(new BarEntry(3, 38));
        entries.add(new BarEntry(4, 58));
        entries.add(new BarEntry(5, 67));
        entries.add(new BarEntry(6, 78));
        entries.add(new BarEntry(7, 71));

        BarDataSet barDataSet = new BarDataSet(entries, "Orders");
        barDataSet.setColor(Color.parseColor("#E07A5F"));
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.invalidate();
    }

    private void setupRecyclerViews() {
        recyclerTopProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerTransactions.setLayoutManager(new LinearLayoutManager(this));

        // --- Top Products ---
        List<String> topProducts = Arrays.asList(
                "Latte",
                "Cappuccino",
                "Americano",
                "Mocha",
                "Espresso"
        );
        recyclerTopProducts.setAdapter(new TopProductsAdapter(topProducts));

        // --- Transactions ---
        List<ReportsAdapter.Transaction> transactions = new ArrayList<>();
        transactions.add(new ReportsAdapter.Transaction("#1234", "2 Lattes, 1 Croissant", "2:45 PM", "Sarah", 13.50));
        transactions.add(new ReportsAdapter.Transaction("#1233", "1 Americano, 1 Cookie", "2:30 PM", "Mike", 5.00));
        transactions.add(new ReportsAdapter.Transaction("#1232", "3 Cappuccinos", "2:15 PM", "Sarah", 12.00));
        transactions.add(new ReportsAdapter.Transaction("#1231", "1 Mocha, 1 Muffin", "2:00 PM", "Emma", 7.75));
        transactions.add(new ReportsAdapter.Transaction("#1230", "2 Espressos", "1:45 PM", "Mike", 5.00));

        recyclerTransactions.setAdapter(new ReportsAdapter(transactions));
    }

}