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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import com.example.brewmate.R;

public class ReceiptActivity extends AppCompatActivity {

    private TextView tvSubtotal, tvTax, tvTotal, tvCashier, tvDateTime, receiptId;
    private LinearLayout itemContainer;
    private CardView cardNewTransaction, cardShareReceipt;

    private static final String PREF_NAME = "ProductPrefs";
    private static final String SALES_PREF = "SalesHistory";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

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
            JSONArray cartArray = new JSONArray(cartJson);
            double subtotal = 0.0;

            // Generate a random receipt ID
            String generatedReceiptId = generateReceiptId();
            receiptId.setText("Receipt #" + generatedReceiptId);

            // Display each item
            for (int i = 0; i < cartArray.length(); i++) {
                JSONObject item = cartArray.getJSONObject(i);
                String name = item.getString("name");
                double price = item.getDouble("price");
                int quantity = item.getInt("quantity");

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
            saveReceiptToSalesHistory(this, generatedReceiptId, cartArray, subtotal, tax, total, cashierName, currentDateTime);

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

    // Replace with your actual cashier logic
    private String getCurrentCashier() {
        // Example: return from SharedPreferences or login session
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
}
