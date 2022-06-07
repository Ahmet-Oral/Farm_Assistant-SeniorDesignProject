package com.example.seniordesignproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

public class General_Information extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_information);

        Toolbar toolbar = findViewById(R.id.toolbar_general_information);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("General Information");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}