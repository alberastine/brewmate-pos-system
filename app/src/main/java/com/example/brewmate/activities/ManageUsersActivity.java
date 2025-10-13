package com.example.brewmate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.TextView;
import android.view.View;

import android.graphics.drawable.Drawable;
import android.widget.FrameLayout;

import com.example.brewmate.R;

import androidx.appcompat.widget.Toolbar;

public class ManageUsersActivity extends AppCompatActivity {

    private TextView tvToolbarSubtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable back button on the left
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left); // Left arrow icon
        }

        // Inflate custom toolbar layout
        View customToolbar = getLayoutInflater().inflate(R.layout.custom_toolbar_manage_users, toolbar, false);
        toolbar.addView(customToolbar);

        // Reference the subtitle TextView
        tvToolbarSubtitle = customToolbar.findViewById(R.id.tvToolbarSubtitle);

        // Hard-coded value for now
        int numberOfCashiers = 3; // This will come from database later
        tvToolbarSubtitle.setText(getString(R.string.cashiers_count, numberOfCashiers));

        FrameLayout editFrame = findViewById(R.id.edit_frame);
        FrameLayout deleteFrame = findViewById(R.id.delete_frame);

        // Make sure each background drawable is independent
        Drawable editBg = editFrame.getBackground().mutate();
        Drawable deleteBg = deleteFrame.getBackground().mutate();
        editFrame.setBackground(editBg);
        deleteFrame.setBackground(deleteBg);

        // Click listeners
        editFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle edit button click
            }
        });

        deleteFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle delete button click
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);

        // Find the Add User menu item
        MenuItem addItem = menu.findItem(R.id.action_add_user);

        // Set a custom layout
        addItem.setActionView(R.layout.custom_add_user);

        // Get the action view safely
        if (addItem.getActionView() != null) {
            addItem.getActionView().setOnClickListener(v -> {
                Toast.makeText(this, "Add User clicked", Toast.LENGTH_SHORT).show();
            });
        }

        // Only show the items you want
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            item.setVisible(item.getItemId() == R.id.action_add_user);

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // Back button clicked → go to AdminDashboardActivity
            Intent intent = new Intent(this, AdminDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.action_add_user) {
            // Add User button clicked
            Toast.makeText(this, "Add User", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
