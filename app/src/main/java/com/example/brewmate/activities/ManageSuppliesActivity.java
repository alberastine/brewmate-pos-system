package com.example.brewmate.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brewmate.R;
import com.example.brewmate.adapters.SupplyAdapter;
import com.example.brewmate.models.Supply;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ManageSuppliesActivity extends AppCompatActivity  implements SupplyAdapter.OnSupplyActionListener  {

    private LinearLayout addSupplyForm;
    private Button btnCancel, btnSubmit;
    private EditText etSupplierName, etSupplyName, etSupplyQty, etLowStockThreshold;

    private RecyclerView recyclerSupplies;
    private SupplyAdapter supplyAdapter;
    private List<Supply> supplyList = new ArrayList<>();

    // SharedPreferences variables
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "SupplyPrefs";
    private static final String KEY_SUPPLIES = "supplies_list";
    private Gson gson = new Gson();

    private Supply editingSupply = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_supplies);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);
        }

        addSupplyForm = findViewById(R.id.addSupplyForm);
        btnCancel = findViewById(R.id.btnCancel);
        btnSubmit = findViewById(R.id.btnSubmit);
        etSupplierName = findViewById(R.id.etSupplierName);
        etSupplyName = findViewById(R.id.etSupplyName);
        etSupplyQty = findViewById(R.id.etSupplyQty);
        etLowStockThreshold = findViewById(R.id.etLowStockThreshold);
        recyclerSupplies = findViewById(R.id.recyclerSupplies);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        loadSupplies();

        // Setup RecyclerView
        supplyAdapter = new SupplyAdapter(supplyList, this);
        recyclerSupplies.setLayoutManager(new LinearLayoutManager(this));
        recyclerSupplies.setAdapter(supplyAdapter);

        btnCancel.setOnClickListener(v -> {
            toggleFormVisibility();
            clearForm();
        });

        btnSubmit.setOnClickListener(v -> saveSupply());

    }

    private void saveSupply() {
        String supplier = etSupplierName.getText().toString().trim();
        String item = etSupplyName.getText().toString().trim();
        String qty = etSupplyQty.getText().toString().trim();
        String thresholdStr = etLowStockThreshold.getText().toString().trim();

        if (supplier.isEmpty() || item.isEmpty() || qty.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double parsedQty;
        try {
            parsedQty = Double.parseDouble(qty);
            if (parsedQty < 0) {
                Toast.makeText(this, "Quantity cannot be negative", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException ex) {
            Toast.makeText(this, "Please enter a numeric quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        double threshold = 0;
        if (!thresholdStr.isEmpty()) {
            try {
                threshold = Double.parseDouble(thresholdStr);
                if (threshold < 0) {
                    Toast.makeText(this, "Low stock threshold cannot be negative", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException ex) {
                Toast.makeText(this, "Enter a numeric low stock threshold", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (editingSupply != null) {
            // --- EDIT MODE ---
            // Update the existing object directly
            editingSupply.setSupplierName(supplier);
            editingSupply.setSupplyName(item);
            editingSupply.setQuantity(String.valueOf(parsedQty));
            editingSupply.setLowStockThreshold(threshold);

            Toast.makeText(this, "Supply Updated", Toast.LENGTH_SHORT).show();

            // Reset editing state
            editingSupply = null;
        } else {
            // --- ADD MODE ---
            String id = UUID.randomUUID().toString();
            Supply newSupply = new Supply(id, supplier, item, String.valueOf(parsedQty), threshold);
            supplyList.add(newSupply);

            Toast.makeText(this, "Supply Added", Toast.LENGTH_SHORT).show();
        }

        saveListToPrefs();
        supplyAdapter.updateList(supplyList);

        clearForm();
        addSupplyForm.setVisibility(View.GONE);
    }

    // INTERFACE METHOD: Called when Edit button in Recycler is clicked
    @Override
    public void onEdit(Supply supply) {
        this.editingSupply = supply; // Set the global variable

        // Populate Form
        etSupplierName.setText(supply.getSupplierName());
        etSupplyName.setText(supply.getSupplyName());
        etSupplyQty.setText(supply.getQuantity());
        etLowStockThreshold.setText(String.valueOf(supply.getLowStockThreshold()));

        // Change Button Text to indicate update
        btnSubmit.setText("Update Supply");

        // Show Form
        if (addSupplyForm.getVisibility() == View.GONE) {
            addSupplyForm.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDelete(Supply supply) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Supply")
                .setMessage("Are you sure you want to delete " + supply.getSupplyName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    supplyList.remove(supply);
                    saveListToPrefs();
                    supplyAdapter.updateList(supplyList);
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveListToPrefs() {
        String json = gson.toJson(supplyList);
        sharedPreferences.edit().putString(KEY_SUPPLIES, json).apply();
    }

    private void loadSupplies() {
        String json = sharedPreferences.getString(KEY_SUPPLIES, null);
        if (json != null) {
            Type type = new TypeToken<List<Supply>>(){}.getType();
            supplyList = gson.fromJson(json, type);
        } else {
            supplyList = new ArrayList<>();
        }

        int lowCount = 0;
        for (Supply s : supplyList) {
            if (Double.isNaN(s.getLowStockThreshold())) {
                s.setLowStockThreshold(0);
            }
            double qtyVal = parseDoubleSafe(s.getQuantity());
            if (s.getLowStockThreshold() > 0 && qtyVal <= s.getLowStockThreshold()) {
                lowCount++;
            }
        }

        if (lowCount > 0) {
            Toast.makeText(this, lowCount + " supplies are low. Please restock.", Toast.LENGTH_LONG).show();
        }
    }

    private double parseDoubleSafe(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private void clearForm() {
        etSupplierName.setText("");
        etSupplyName.setText("");
        etSupplyQty.setText("");
        etLowStockThreshold.setText("");

        btnSubmit.setText("Add Supply");

        editingSupply = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        MenuItem addItem = menu.findItem(R.id.action_add_supply);
        if (addItem.getActionView() != null) {
            addItem.getActionView().setOnClickListener(v -> toggleFormVisibility());
        } else {
            addItem.setOnMenuItemClickListener(item -> {
                toggleFormVisibility();
                return true;
            });
        }

        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            item.setVisible(item.getItemId() == R.id.action_add_supply);
        }

        return true;
    }

    private void toggleFormVisibility() {
        if (addSupplyForm.getVisibility() == View.GONE) {
            addSupplyForm.setVisibility(View.VISIBLE);
            btnSubmit.setText(R.string.add_product_sub_btn_label);
        } else {
            addSupplyForm.setVisibility(View.GONE);
        }
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