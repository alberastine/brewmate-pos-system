package com.example.brewmate.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brewmate.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.brewmate.models.Product;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


import com.example.brewmate.adapters.MenuProductAdapter;

public class TransactionActivity extends AppCompatActivity {

    private RecyclerView recyclerCoffee, recyclerCold, recyclerPastry;
    private static final String PREF_NAME = "ProductPrefs";
    private static final String KEY_PRODUCTS = "products";
    private SharedPreferences prefs;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);
        }

        recyclerCoffee = findViewById(R.id.recycler_menu_coffee);
        recyclerCold = findViewById(R.id.recycler_menu_cold_drinks);
        recyclerPastry = findViewById(R.id.recycler_menu_pastries);

        prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        setupRecyclerViews();
        loadAndDisplayProducts();
    }

    private void setupRecyclerViews() {
        recyclerCoffee.setLayoutManager(new LinearLayoutManager(this));
        recyclerCold.setLayoutManager(new LinearLayoutManager(this));
        recyclerPastry.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadAndDisplayProducts() {
        List<Product> allProducts = loadProducts();

        // If SharedPreferences has no data, do nothing (no fallback)
        if (allProducts == null || allProducts.isEmpty()) {
            recyclerCoffee.setAdapter(new MenuProductAdapter(this, new ArrayList<>()));
            recyclerCold.setAdapter(new MenuProductAdapter(this, new ArrayList<>()));
            recyclerPastry.setAdapter(new MenuProductAdapter(this, new ArrayList<>()));
            return;
        }

        // Filter by category
        List<Product> coffeeList = allProducts.stream()
                .filter(p -> p.getCategory().equalsIgnoreCase("Coffee"))
                .collect(Collectors.toList());

        List<Product> coldList = allProducts.stream()
                .filter(p -> p.getCategory().equalsIgnoreCase("Cold Drinks"))
                .collect(Collectors.toList());

        List<Product> pastryList = allProducts.stream()
                .filter(p -> p.getCategory().equalsIgnoreCase("Pastries"))
                .collect(Collectors.toList());

        recyclerCoffee.setAdapter(new MenuProductAdapter(this, coffeeList));
        recyclerCold.setAdapter(new MenuProductAdapter(this, coldList));
        recyclerPastry.setAdapter(new MenuProductAdapter(this, pastryList));
    }

    private List<Product> loadProducts() {
        String json = prefs.getString(KEY_PRODUCTS, null);
        if (json == null) return new ArrayList<>();

        Type type = new TypeToken<List<Product>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private void saveProducts(List<Product> products) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_PRODUCTS, gson.toJson(products));
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);

        // Only show the items you want
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            item.setVisible(item.getItemId() == R.id.action_view_cart);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, CashierDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            return true;
        }

        if (item.getItemId() == R.id.action_view_cart) {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}