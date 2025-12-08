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
import com.example.brewmate.models.ProductSupply;
import com.example.brewmate.models.Supply;
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
    private EditText etProductName, etPrice, etCategory, etSupplyPerUnit;

    private Spinner spinnerCategory, spinnerSupply;
    private String selectedCategory = "";
    private Supply selectedSupply = null;

    private RecyclerView coffeeRecycler, coldRecycler, pastryRecycler;
    private ProductAdapter coffeeAdapter, coldAdapter, pastryAdapter;
    private List<Product> productList = new ArrayList<>();
    private List<Supply> supplyList = new ArrayList<>();
    private List<ProductSupply> selectedSupplies = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ProductPrefs";
    private static final String KEY_PRODUCTS = "products";
    private static final String SUPPLY_PREFS_NAME = "SupplyPrefs";
    private static final String KEY_SUPPLIES = "supplies_list";
    private Gson gson = new Gson();
    private Product editingProduct = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

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
        etSupplyPerUnit = findViewById(R.id.etSupplyPerUnit);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerSupply = findViewById(R.id.spinnerSupply);
        TextView tvSelectedSupplies = findViewById(R.id.tvSelectedSupplies);
        Button btnAddSupplyToProduct = findViewById(R.id.btnAddSupplyToProduct);

        coffeeRecycler = findViewById(R.id.recycler_coffee);
        coldRecycler = findViewById(R.id.recycler_cold_drinks);
        pastryRecycler = findViewById(R.id.recycler_pastries);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadProducts();
        loadSupplies();
        updateCategoryCounts();
        setupCategorySpinner();
        setupSupplySpinner();

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
        btnAddSupplyToProduct.setOnClickListener(v -> {
            addSupplyToSelection(tvSelectedSupplies);
        });
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

    private void setupSupplySpinner() {
        List<String> supplyNames = new ArrayList<>();
        for (Supply supply : supplyList) {
            supplyNames.add(supply.getSupplyName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                supplyNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSupply.setAdapter(adapter);

        spinnerSupply.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < supplyList.size()) {
                    selectedSupply = supplyList.get(position);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedSupply = null;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSupplies();
        setupSupplySpinner();
        TextView tvSelectedSupplies = findViewById(R.id.tvSelectedSupplies);
        updateSelectedSuppliesLabel(tvSelectedSupplies);
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
            Toast.makeText(this, "Please fill product name, price and category", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedSupplies.isEmpty()) {
            Toast.makeText(this, "Add at least one supply for this product", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);

        if (editingProduct != null) {
            editingProduct.setName(name);
            editingProduct.setPrice(price);
            editingProduct.setCategory(category);
            editingProduct.setSupplies(new ArrayList<>(selectedSupplies));
        } else {
            String id = UUID.randomUUID().toString();
            Product newProduct = new Product(
                    id,
                    name,
                    price,
                    category,
                    new ArrayList<>(selectedSupplies)
            );
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
        spinnerSupply.setSelection(0);
        etSupplyPerUnit.setText("");
        selectedSupplies.clear();
        TextView tvSelectedSupplies = findViewById(R.id.tvSelectedSupplies);
        updateSelectedSuppliesLabel(tvSelectedSupplies);
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
            // Ensure supplies list is not null after deserialization
            for (Product p : productList) {
                if (p.getSupplies() == null) {
                    p.setSupplies(new ArrayList<>());
                }
            }
        } else {
            productList = new ArrayList<>();
        }

        tvToolbarSubtitle.setText(getString(R.string.products_count, productList.size()));
    }

    private void loadSupplies() {
        SharedPreferences supplyPrefs = getSharedPreferences(SUPPLY_PREFS_NAME, Context.MODE_PRIVATE);
        String json = supplyPrefs.getString(KEY_SUPPLIES, null);
        if (json != null) {
            Type type = new TypeToken<List<Supply>>(){}.getType();
            supplyList = gson.fromJson(json, type);
        } else {
            supplyList = new ArrayList<>();
        }

        if (supplyList.isEmpty()) {
            Toast.makeText(this, "Please add supplies before creating products", Toast.LENGTH_LONG).show();
        }
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
        selectedSupplies = new ArrayList<>(product.getSupplies() == null ? new ArrayList<>() : product.getSupplies());
        TextView tvSelectedSupplies = findViewById(R.id.tvSelectedSupplies);
        updateSelectedSuppliesLabel(tvSelectedSupplies);

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
        if (supplyList.isEmpty()) {
            Toast.makeText(this, "Add supplies first to link with a product", Toast.LENGTH_SHORT).show();
            return;
        }

        if (addProductForm.getVisibility() == View.GONE) {
            addProductForm.setVisibility(View.VISIBLE);
            btnSubmit.setText(R.string.add_product_sub_btn_label);
        } else {
            addProductForm.setVisibility(View.GONE);
        }
    }

    private void addSupplyToSelection(TextView tvSelectedSupplies) {
        String supplyPerUnitStr = etSupplyPerUnit.getText().toString().trim();
        if (selectedSupply == null || supplyPerUnitStr.isEmpty()) {
            Toast.makeText(this, "Select a supply and quantity to add", Toast.LENGTH_SHORT).show();
            return;
        }

        double supplyPerUnit;
        try {
            supplyPerUnit = Double.parseDouble(supplyPerUnitStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter a valid supply quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        if (supplyPerUnit <= 0) {
            Toast.makeText(this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        // Replace if already exists
        boolean replaced = false;
        for (int i = 0; i < selectedSupplies.size(); i++) {
            ProductSupply ps = selectedSupplies.get(i);
            if (ps.getSupplyId().equals(selectedSupply.getId())) {
                ps.setQuantityRequired(supplyPerUnit);
                ps.setSupplyName(selectedSupply.getSupplyName());
                replaced = true;
                break;
            }
        }

        if (!replaced) {
            ProductSupply ps = new ProductSupply(null, selectedSupply.getId(), selectedSupply.getSupplyName(), supplyPerUnit);
            selectedSupplies.add(ps);
        }

        etSupplyPerUnit.setText("");
        updateSelectedSuppliesLabel(tvSelectedSupplies);
    }

    private void updateSelectedSuppliesLabel(TextView tvSelectedSupplies) {
        if (selectedSupplies.isEmpty()) {
            tvSelectedSupplies.setText(getString(R.string.no_supplies_selected));
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (ProductSupply ps : selectedSupplies) {
            builder.append(ps.getSupplyName()).append(" (").append(ps.getQuantityRequired()).append(")\n");
        }
        tvSelectedSupplies.setText(builder.toString().trim());
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
