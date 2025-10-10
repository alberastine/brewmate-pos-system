package com.example.brewmate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.example.brewmate.R;

import androidx.appcompat.widget.Toolbar;

public class ReportsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}