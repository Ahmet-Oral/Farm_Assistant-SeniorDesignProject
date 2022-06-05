package com.example.seniordesignproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

public class Animals extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animals);

        Toolbar toolbar = findViewById(R.id.toolbar_animals);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Animals");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}