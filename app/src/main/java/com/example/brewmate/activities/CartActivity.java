package com.example.brewmate.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brewmate.R;
import com.example.brewmate.adapters.CartProductAdapter;
import com.example.brewmate.models.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerCart;
    private TextView tvSubtotal, tvTax, tvTotal;
    private CardView checkoutButton;
    private SharedPreferences prefs;
    private Gson gson = new Gson();
    private static final String PREF_NAME = "ProductPrefs";
    private static final String KEY_CART = "cart";
    private List<Product> cartList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);
        }

        // View initialization
        recyclerCart = findViewById(R.id.recyclerCart);
        recyclerCart.setLayoutManager(new LinearLayoutManager(this));
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTax = findViewById(R.id.tvTax);
        tvTotal = findViewById(R.id.tvTotal);
        checkoutButton = findViewById(R.id.checkoutButton);

        // Load saved cart
        prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        cartList = loadCart();

        // Setup adapter
        CartProductAdapter adapter = new CartProductAdapter(this, cartList);
        recyclerCart.setAdapter(adapter);

        // Disable checkout button if cart is empty
        updateCheckoutButtonState();

        // Show totals
        updateTotals();

        // checkout button listener
        String cashierNameTemp = getIntent().getStringExtra("username");
        final String cashierNameFinal = (cashierNameTemp != null) ? cashierNameTemp : "Cashier";

        checkoutButton.setOnClickListener(v -> {
            if (cartList == null || cartList.isEmpty()) {
                Toast.makeText(this, "Cart is empty. Add items first!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(CartActivity.this, ReceiptActivity.class);
            intent.putExtra("username", cashierNameFinal); // pass it along
            startActivity(intent);
        });
    }

    private void updateCheckoutButtonState() {
        if (cartList == null || cartList.isEmpty()) {
            checkoutButton.setEnabled(false);
            checkoutButton.setCardBackgroundColor(getResources().getColor(R.color.gray));
        } else {
            checkoutButton.setEnabled(true);
            checkoutButton.setCardBackgroundColor(getResources().getColor(R.color.coffee_brown));
        }
    }


    private List<Product> loadCart() {
        String json = prefs.getString(KEY_CART, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Product>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private double calculateSubtotal() {
        double subtotal = 0.0;
        for (Product p : cartList) {
            subtotal += p.getPrice() * p.getQuantity();
        }
        return subtotal;
    }

    private void updateTotals() {
        double subtotal = calculateSubtotal();
        double tax = subtotal * 0.08;
        double total = subtotal + tax;

        tvSubtotal.setText(String.format("₱%.2f", subtotal));
        tvTax.setText(String.format("₱%.2f", tax));
        tvTotal.setText(String.format("₱%.2f", total));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);

        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            item.setVisible(item.getItemId() == R.id.action_delete_cart_items);
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
        }  else if (item.getItemId() == R.id.action_delete_cart_items) {
            // Clear cart data from SharedPreferences
            prefs.edit().remove(KEY_CART).apply();

            // Clear cart list and update UI
            cartList.clear();
            recyclerCart.getAdapter().notifyDataSetChanged();
            updateTotals();
            updateCheckoutButtonState();

            Toast.makeText(this, "Cart emptied successfully", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
