package com.example.seniordesignproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class Agenda extends AppCompatActivity {
    Button calendar_btn;

    public void init(){
        Toolbar toolbar = findViewById(R.id.toolbar_agenda);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Agenda");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        calendar_btn = findViewById(R.id.agenda_calendar_btn);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);
        init();

        calendar_btn.setOnClickListener(v -> {
            startActivity(new Intent(Agenda.this, Calendar.class));});

    }
}