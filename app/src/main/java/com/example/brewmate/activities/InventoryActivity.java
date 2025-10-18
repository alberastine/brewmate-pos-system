package com.example.brewmate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.brewmate.R;

import androidx.appcompat.widget.Toolbar;

public class InventoryActivity extends AppCompatActivity {

    private LinearLayout addProductForm;
    private Button btnCancel, btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);
        }

        addProductForm = findViewById(R.id.addProductForm);
        btnCancel = findViewById(R.id.btnCancel);
        btnSubmit = findViewById(R.id.btnSubmit);


        // Cancel hides the form
        btnCancel.setOnClickListener(v -> {
            addProductForm.setVisibility(View.GONE);
        });

        // Submit creates a new user
        btnSubmit.setOnClickListener(v -> {
            Toast.makeText(this, "Submit clicked", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        MenuItem addItem = menu.findItem(R.id.action_add_product);
        if (addItem.getActionView() != null) {
            addItem.getActionView().setOnClickListener(v ->
//                    Toast.makeText(this, "add product", Toast.LENGTH_SHORT).show()
                            toggleFormVisibility()
            );
        } else {
            addItem.setOnMenuItemClickListener(item -> {
                toggleFormVisibility();
                return true;
            });
        }

        // Show only Add Product
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            item.setVisible(item.getItemId() == R.id.action_add_product);
        }

        return true;
    }

    private void toggleFormVisibility() {
        if (addProductForm.getVisibility() == View.GONE) {
            addProductForm.setVisibility(View.VISIBLE);
        } else {
            addProductForm.setVisibility(View.GONE);
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
