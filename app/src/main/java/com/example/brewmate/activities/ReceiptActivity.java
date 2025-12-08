package com.example.brewmate.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.example.brewmate.R;
import com.example.brewmate.models.Supply;
import com.example.brewmate.models.Product;
import com.example.brewmate.models.ProductSupply;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ReceiptActivity extends AppCompatActivity {

    private TextView tvSubtotal, tvTax, tvTotal, tvCashier, tvDateTime, receiptId;
    private LinearLayout itemContainer;
    private CardView cardNewTransaction, cardShareReceipt;

    private static final String PREF_NAME = "ProductPrefs";
    private static final String SALES_PREF = "SalesHistory";
    private static final String SUPPLY_PREFS_NAME = "SupplyPrefs";
    private static final String KEY_SUPPLIES = "supplies_list";
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // Initialize views
        receiptId = findViewById(R.id.receiptId);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTax = findViewById(R.id.tvTax);
        tvTotal = findViewById(R.id.tvTotal);
        tvCashier = findViewById(R.id.tvCashier);
        tvDateTime = findViewById(R.id.tvDateTime);
        itemContainer = findViewById(R.id.itemContainer);
        cardNewTransaction = findViewById(R.id.cardNewTransaction);
        cardShareReceipt = findViewById(R.id.cardShareReceipt);

        // Load cart data
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String cartJson = preferences.getString("cart", "[]");

        try {
            List<Product> cartItems = loadCartFromJson(cartJson);
            double subtotal = 0.0;

            // Generate a random receipt ID
            String generatedReceiptId = generateReceiptId();
            receiptId.setText("Receipt #" + generatedReceiptId);

            // Display each item
            for (Product product : cartItems) {
                String name = product.getName();
                double price = product.getPrice();
                int quantity = product.getQuantity();

                double totalPrice = price * quantity;
                subtotal += totalPrice;

                // Create item view
                LinearLayout itemLayout = new LinearLayout(this);
                itemLayout.setOrientation(LinearLayout.VERTICAL);

                TextView tvNamePrice = new TextView(this);
                tvNamePrice.setText(String.format(Locale.getDefault(), "%s                            ₱%.2f", name, totalPrice));
                tvNamePrice.setTextSize(16);

                TextView tvQuantity = new TextView(this);
                tvQuantity.setText(String.format(Locale.getDefault(), "%d × ₱%.2f", quantity, price));
                tvQuantity.setTextSize(14);
                tvQuantity.setTextColor(getResources().getColor(R.color.gray));

                View divider = new View(this);
                LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 2);
                dividerParams.setMargins(0, 8, 0, 8);
                divider.setLayoutParams(dividerParams);
                divider.setBackgroundColor(getResources().getColor(R.color.gray));

                itemLayout.addView(tvNamePrice);
                itemLayout.addView(tvQuantity);
                itemLayout.addView(divider);
                itemContainer.addView(itemLayout);
            }

            // Calculate totals
            double tax = subtotal * 0.08;
            double total = subtotal + tax;

            // Display totals
            tvSubtotal.setText(String.format(Locale.getDefault(), "₱%.2f", subtotal));
            tvTax.setText(String.format(Locale.getDefault(), "₱%.2f", tax));
            tvTotal.setText(String.format(Locale.getDefault(), "₱%.2f", total));

            // Get current cashier (replace with your actual logic)
            String cashierName = getIntent().getStringExtra("username");
            if (cashierName == null) cashierName = "Cashier"; // fallback
            tvCashier.setText("Cashier: " + cashierName);;

            // Display date/time
            String currentDateTime = new SimpleDateFormat("MM/dd/yyyy, hh:mm:ss a", Locale.getDefault())
                    .format(new Date());
            tvDateTime.setText(currentDateTime);

            // Save receipt details
            saveReceiptToSalesHistory(this, generatedReceiptId, new JSONArray(cartJson), subtotal, tax, total, cashierName, currentDateTime);

            deductSupplies(cartItems);

            preferences.edit().remove("cart").apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Buttons
        cardNewTransaction.setOnClickListener(v -> {
            // Clear cart and return to transaction screen
            preferences.edit().remove("cart").apply();
            Intent intent = new Intent(ReceiptActivity.this, TransactionActivity.class);
            startActivity(intent);
            finish();
        });

        cardShareReceipt.setOnClickListener(v -> {
            Toast.makeText(this, "Sharing receipt...", Toast.LENGTH_SHORT).show();
            // Add your sharing intent logic here
        });
    }

    // Generate random receipt ID
    private String generateReceiptId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder id = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            id.append(chars.charAt(random.nextInt(chars.length())));
        }
        return id.toString();
    }

    // Replace with actual cashier logic
    private String getCurrentCashier() {
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return pref.getString("cashierName", "Sarah Johnson");
    }

    // Save receipt data to SharedPreferences for future sales reports
    private void saveReceiptToSalesHistory(Context context, String receiptId, JSONArray cartItems,
                                           double subtotal, double tax, double total, String cashier, String dateTime) {
        SharedPreferences salesPref = context.getSharedPreferences(SALES_PREF, MODE_PRIVATE);
        String existingData = salesPref.getString("sales_records", "[]");

        try {
            JSONArray salesArray = new JSONArray(existingData);

            JSONObject newRecord = new JSONObject();
            newRecord.put("receiptId", receiptId);
            newRecord.put("items", cartItems);
            newRecord.put("subtotal", subtotal);
            newRecord.put("tax", tax);
            newRecord.put("total", total);
            newRecord.put("cashier", cashier);
            newRecord.put("dateTime", dateTime);

            salesArray.put(newRecord);

            salesPref.edit().putString("sales_records", salesArray.toString()).apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private List<Supply> loadSupplies() {
        SharedPreferences supplyPrefs = getSharedPreferences(SUPPLY_PREFS_NAME, MODE_PRIVATE);
        String json = supplyPrefs.getString(KEY_SUPPLIES, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Supply>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private void saveSupplies(List<Supply> supplies) {
        SharedPreferences supplyPrefs = getSharedPreferences(SUPPLY_PREFS_NAME, MODE_PRIVATE);
        supplyPrefs.edit().putString(KEY_SUPPLIES, gson.toJson(supplies)).apply();
    }

    private void deductSupplies(List<Product> cartItems) {
        List<Supply> supplies = loadSupplies();

        for (Product product : cartItems) {
            if (product.getSupplies() == null) continue;
            for (ProductSupply ps : product.getSupplies()) {
                for (Supply supply : supplies) {
                    if (ps.getSupplyId().equals(supply.getId())) {
                        double currentQty = parseQuantityToDouble(supply.getQuantity());
                        double newQty = Math.max(0, currentQty - (ps.getQuantityRequired() * product.getQuantity()));
                        supply.setQuantity(String.valueOf(newQty));
                        break;
                    }
                }
            }
        }

        saveSupplies(supplies);
    }

    private double parseQuantityToDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private List<Product> loadCartFromJson(String cartJson) {
        Type type = new TypeToken<List<Product>>() {}.getType();
        List<Product> list = gson.fromJson(cartJson, type);
        if (list == null) return new ArrayList<>();
        for (Product p : list) {
            if (p.getSupplies() == null) {
                p.setSupplies(new ArrayList<>());
            }
        }
        return list;
    }
}
