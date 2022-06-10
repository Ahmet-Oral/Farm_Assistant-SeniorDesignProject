package com.example.seniordesignproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Agenda extends AppCompatActivity implements View.OnClickListener{
    private Button calendar_btn, notes_btn, inventory_btn;

    public void init(){
        Toolbar toolbar = findViewById(R.id.toolbar_agenda);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Agenda");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        calendar_btn = findViewById(R.id.agenda_Calendar_btn);
        notes_btn = findViewById(R.id.agenda_Notes_btn);
        inventory_btn = findViewById(R.id.agenda_Inventory_btn);

        calendar_btn.setOnClickListener(this);
        notes_btn.setOnClickListener(this);
        inventory_btn.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);
        init();

//        calendar_btn.setOnClickListener(v -> {
//            startActivity(new Intent(Agenda.this, Calendar.class));});
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.agenda_Calendar_btn:
                startActivity(new Intent(Agenda.this, Calendar.class));
                break;
            case R.id.agenda_Notes_btn:
                startActivity(new Intent(Agenda.this, NotesGeneral.class));
                break;
            case R.id.agenda_Inventory_btn:

                break;
    }
    }
}