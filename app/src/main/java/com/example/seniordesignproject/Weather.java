package com.example.seniordesignproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

public class Weather extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        Toolbar toolbar = findViewById(R.id.toolbar_weather);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Weather");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}