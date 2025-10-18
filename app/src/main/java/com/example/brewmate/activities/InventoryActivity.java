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
import android.widget.TextView;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brewmate.R;
import com.example.brewmate.adapters.ProductAdapter;
import com.example.brewmate.models.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InventoryActivity extends AppCompatActivity implements ProductAdapter.OnProductActionListener {

    private TextView tvToolbarSubtitle;
    private TextView tvCoffeeCategory, tvColdCategory, tvPastryCategory;
    private LinearLayout addProductForm;
    private Button btnCancel, btnSubmit;
    private EditText etProductName, etPrice, etCategory;

    private Spinner spinnerCategory;
    private String selectedCategory = "";

    private RecyclerView coffeeRecycler, coldRecycler, pastryRecycler;
    private ProductAdapter coffeeAdapter, coldAdapter, pastryAdapter;
    private List<Product> productList = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ProductPrefs";
    private static final String KEY_PRODUCTS = "products";
    private Gson gson = new Gson();
    private Product editingProduct = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        tvCoffeeCategory = findViewById(R.id.tvCoffeeCategory);
        tvColdCategory = findViewById(R.id.tvColdCategory);
        tvPastryCategory = findViewById(R.id.tvPastryCategory);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);
        }

        // Inflate custom toolbar layout for inventory
        View customToolbar = getLayoutInflater().inflate(R.layout.custom_toolbar_manage_users, toolbar, false);
        toolbar.addView(customToolbar);
        tvToolbarSubtitle = customToolbar.findViewById(R.id.tvToolbarSubtitle);

        addProductForm = findViewById(R.id.addProductForm);
        btnCancel = findViewById(R.id.btnCancel);
        btnSubmit = findViewById(R.id.btnSubmit);
        etProductName = findViewById(R.id.etProductName);
        etPrice = findViewById(R.id.etPrice);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        coffeeRecycler = findViewById(R.id.recycler_coffee);
        coldRecycler = findViewById(R.id.recycler_cold_drinks);
        pastryRecycler = findViewById(R.id.recycler_pastries);

        // Find actual RecyclerViews in your layout when you add them
        // coffeeRecycler = findViewById(R.id.recycler_coffee);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadProducts();
        updateCategoryCounts();
        setupCategorySpinner();

        coffeeAdapter = new ProductAdapter(this, filterByCategory("Coffee"), this);
        coldAdapter = new ProductAdapter(this, filterByCategory("Cold Drinks"), this);
        pastryAdapter = new ProductAdapter(this, filterByCategory("Pastries"), this);

        setupRecycler(coffeeRecycler, coffeeAdapter);
        setupRecycler(coldRecycler, coldAdapter);
        setupRecycler(pastryRecycler, pastryAdapter);

        btnCancel.setOnClickListener(v -> {
            addProductForm.setVisibility(View.GONE);
            clearForm();
        });

        btnSubmit.setOnClickListener(v -> saveProduct());
    }

    private void updateCategoryCounts() {
        int coffeeCount = filterByCategory("Coffee").size();
        int coldCount = filterByCategory("Cold Drinks").size();
        int pastryCount = filterByCategory("Pastries").size();

        tvCoffeeCategory.setText(getString(R.string.category_with_count, "Coffee", coffeeCount));
        tvColdCategory.setText(getString(R.string.category_with_count, "Cold Drinks", coldCount));
        tvPastryCategory.setText(getString(R.string.category_with_count, "Pastries", pastryCount));
    }

    private void setupCategorySpinner() {
        String[] categories = {"Coffee", "Cold Drinks", "Pastries"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedCategory = "";
            }
        });
    }

    private void setupRecycler(RecyclerView recyclerView, ProductAdapter adapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void saveProduct() {
        String name = etProductName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String category = selectedCategory;

        if (name.isEmpty() || priceStr.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);

        if (editingProduct != null) {
            editingProduct.setName(name);
            editingProduct.setPrice(price);
            editingProduct.setCategory(category);
        } else {
            String id = UUID.randomUUID().toString();
            Product newProduct = new Product(id, name, price, category);
            productList.add(newProduct);
        }

        saveProductsToPrefs();
        tvToolbarSubtitle.setText(getString(R.string.products_count, productList.size()));
        refreshAdapters();
        updateCategoryCounts();
        clearForm();
        addProductForm.setVisibility(View.GONE);
        editingProduct = null;

        btnSubmit.setText(R.string.add_product_sub_btn_label);
    }

    private void clearForm() {
        etProductName.setText("");
        etPrice.setText("");
        spinnerCategory.setSelection(0);
    }

    private void saveProductsToPrefs() {
        String json = gson.toJson(productList);
        sharedPreferences.edit().putString(KEY_PRODUCTS, json).apply();
    }

    private void loadProducts() {
        String json = sharedPreferences.getString(KEY_PRODUCTS, null);
        if (json != null) {
            Type type = new TypeToken<List<Product>>(){}.getType();
            productList = gson.fromJson(json, type);
        } else {
            productList = new ArrayList<>();
        }

        tvToolbarSubtitle.setText(getString(R.string.products_count, productList.size()));
    }

    private void refreshAdapters() {
        coffeeAdapter.updateList(filterByCategory("Coffee"));
        coldAdapter.updateList(filterByCategory("Cold Drinks"));
        pastryAdapter.updateList(filterByCategory("Pastries"));
    }

    private List<Product> filterByCategory(String category) {
        List<Product> filtered = new ArrayList<>();
        for (Product p : productList) {
            if (p.getCategory().equalsIgnoreCase(category)) {
                filtered.add(p);
            }
        }
        return filtered;
    }

    @Override
    public void onEdit(Product product) {
        editingProduct = product;
        btnSubmit.setText(R.string.edt_product_sub_bnt_label);
        etProductName.setText(product.getName());
        etPrice.setText(String.valueOf(product.getPrice()));

        // Set spinner selection based on category
        ArrayAdapter adapter = (ArrayAdapter) spinnerCategory.getAdapter();
        int position = adapter.getPosition(product.getCategory());
        spinnerCategory.setSelection(position);

        addProductForm.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDelete(Product product) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(R.string.delete_confirmation_title)
                .setMessage(getString(R.string.delete_confirmation_message, product.getName()))
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    productList.remove(product);
                    saveProductsToPrefs();
                    refreshAdapters();
                    updateCategoryCounts();
                    tvToolbarSubtitle.setText(getString(R.string.products_count, productList.size()));
                    Toast.makeText(this, R.string.product_deleted, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        MenuItem addItem = menu.findItem(R.id.action_add_product);
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
            item.setVisible(item.getItemId() == R.id.action_add_product);
        }

        return true;
    }

    private void toggleFormVisibility() {
        if (addProductForm.getVisibility() == View.GONE) {
            addProductForm.setVisibility(View.VISIBLE);
            btnSubmit.setText(R.string.add_product_sub_btn_label);
        } else {
            addProductForm.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, AdminDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
