package com.example.brewmate.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brewmate.R;
import com.example.brewmate.adapters.ReportsAdapter;
import com.example.brewmate.adapters.TopProductsAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReportsActivity extends AppCompatActivity {

    private static final String SALES_PREF = "SalesHistory";

    private TextView tvWeeklySales, tvTotalOrders, tvAvgOrder, tvTotalCustomers;
    private LineChart lineChart;
    private BarChart barChart;
    private RecyclerView recyclerTopProducts, recyclerTransactions;

    private List<JSONObject> salesRecords = new ArrayList<>();
    private Map<String, Integer> productCountMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);
        }

        tvWeeklySales = findViewById(R.id.tvWeeklySales);
        tvTotalOrders = findViewById(R.id.tvTotalOrders);
        tvAvgOrder = findViewById(R.id.tvAvgOrder);
        tvTotalCustomers = findViewById(R.id.tvTotalCustomers);
        lineChart = findViewById(R.id.lineChart);
        barChart = findViewById(R.id.barChart);
        recyclerTopProducts = findViewById(R.id.recyclerTopProducts);
        recyclerTransactions = findViewById(R.id.recyclerTransactions);

        loadSalesData();
        calculateAndDisplayMetrics();
        setupCharts();
        setupRecyclerViews();
    }

    private void loadSalesData() {
        SharedPreferences prefs = getSharedPreferences(SALES_PREF, MODE_PRIVATE);
        String data = prefs.getString("sales_records", "[]");
        try {
            JSONArray array = new JSONArray(data);
            for (int i = 0; i < array.length(); i++) {
                salesRecords.add(array.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void calculateAndDisplayMetrics() {
        double weeklySales = 0;
        int totalOrders = 0;
        double totalRevenue = 0;
        int totalCustomers = salesRecords.size();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date oneWeekAgo = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy, hh:mm:ss a", Locale.getDefault());

        for (JSONObject record : salesRecords) {
            try {
                double total = record.getDouble("total");
                totalRevenue += total;

                String dateTime = record.getString("dateTime");
                Date saleDate = sdf.parse(dateTime);
                if (saleDate != null && saleDate.after(oneWeekAgo)) {
                    weeklySales += total;
                }

                JSONArray items = record.getJSONArray("items");
                for (int j = 0; j < items.length(); j++) {
                    JSONObject item = items.getJSONObject(j);
                    String name = item.getString("name");
                    int quantity = item.getInt("quantity");

                    totalOrders += quantity;
                    productCountMap.put(name, productCountMap.getOrDefault(name, 0) + quantity);
                }

            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }

        double avgOrder = totalCustomers > 0 ? totalRevenue / totalCustomers : 0;

        tvWeeklySales.setText(String.format("₱%.2f", weeklySales));
        tvTotalOrders.setText(String.valueOf(totalOrders));
        tvAvgOrder.setText(String.format("₱%.2f", avgOrder));
        tvTotalCustomers.setText(String.valueOf(totalCustomers));
    }

    private void setupCharts() {
        // --- Line Chart: Daily Sales Trend ---
        Map<String, Double> dailySalesMap = new TreeMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

        for (JSONObject record : salesRecords) {
            try {
                String dateTime = record.getString("dateTime");
                Date date = new SimpleDateFormat("MM/dd/yyyy, hh:mm:ss a", Locale.getDefault()).parse(dateTime);
                if (date != null) {
                    String day = sdf.format(date);
                    double total = record.getDouble("total");
                    dailySalesMap.put(day, dailySalesMap.getOrDefault(day, 0.0) + total);
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }

        List<Entry> lineEntries = new ArrayList<>();
        int index = 0;
        for (double total : dailySalesMap.values()) {
            lineEntries.add(new Entry(index++, (float) total));
        }

        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Daily Sales");
        lineDataSet.setColor(Color.parseColor("#6F4E37"));
        lineDataSet.setCircleColor(Color.parseColor("#6F4E37"));
        lineDataSet.setLineWidth(3f);
        lineDataSet.setCircleRadius(4f);

        lineChart.setData(new LineData(lineDataSet));
        lineChart.getDescription().setEnabled(false);
        lineChart.invalidate();

        // --- Bar Chart: Orders by Day ---
        Map<String, Integer> dailyOrdersMap = new TreeMap<>();
        for (JSONObject record : salesRecords) {
            try {
                String dateTime = record.getString("dateTime");
                Date date = new SimpleDateFormat("MM/dd/yyyy, hh:mm:ss a", Locale.getDefault()).parse(dateTime);
                if (date != null) {
                    String day = sdf.format(date);
                    JSONArray items = record.getJSONArray("items");
                    int totalQty = 0;
                    for (int j = 0; j < items.length(); j++) {
                        totalQty += items.getJSONObject(j).getInt("quantity");
                    }
                    dailyOrdersMap.put(day, dailyOrdersMap.getOrDefault(day, 0) + totalQty);
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }

        List<BarEntry> barEntries = new ArrayList<>();
        int x = 0;
        for (int count : dailyOrdersMap.values()) {
            barEntries.add(new BarEntry(x++, count));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Orders per Day");
        barDataSet.setColor(Color.parseColor("#E07A5F"));
        barChart.setData(new BarData(barDataSet));
        barChart.getDescription().setEnabled(false);
        barChart.invalidate();
    }

    private void setupRecyclerViews() {
        recyclerTopProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerTransactions.setLayoutManager(new LinearLayoutManager(this));

        // --- Top Selling Products ---
        List<String> topProducts = new ArrayList<>(productCountMap.keySet());
        topProducts.sort((a, b) -> productCountMap.get(b) - productCountMap.get(a));

        recyclerTopProducts.setAdapter(new TopProductsAdapter(topProducts));

        // --- Recent Transactions ---
        List<ReportsAdapter.Transaction> transactions = new ArrayList<>();
        for (JSONObject record : salesRecords) {
            try {
                String id = record.getString("receiptId");
                String cashier = record.getString("cashier");
                String dateTime = record.getString("dateTime");
                double total = record.getDouble("total");

                JSONArray items = record.getJSONArray("items");
                List<String> itemNames = new ArrayList<>();
                for (int j = 0; j < items.length(); j++) {
                    itemNames.add(items.getJSONObject(j).getString("name"));
                }

                transactions.add(new ReportsAdapter.Transaction(
                        id,
                        String.join(", ", itemNames),
                        dateTime,
                        cashier,
                        total
                ));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Collections.reverse(transactions);
        recyclerTransactions.setAdapter(new ReportsAdapter(transactions));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
