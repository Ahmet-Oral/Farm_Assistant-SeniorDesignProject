package com.example.seniordesignproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Notes extends AppCompatActivity {
    private Button new_btn;
    private ListView listView;
    private String key_extra;
    ArrayAdapter<String> adapter;
    ArrayList<String> notes_list;


    private FirebaseDatabase database;
    private DatabaseReference ref;


    public void init(){
        Toolbar toolbar = findViewById(R.id.toolbar_notes);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new_btn = findViewById(R.id.notes_New_btn);
        listView = findViewById(R.id.notes_ListView);

        key_extra = getIntent().getStringExtra("key");

        notes_list = new ArrayList<>();

        adapter = new ArrayAdapter<String>(Notes.this,R.layout.notes_adapter_view,R.id.notes_adapter_view_Note,notes_list);

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users/"+userUid+"/Notes");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        init();

        // Pass the key of the field
        new_btn.setOnClickListener(v -> {
            Intent intent = new Intent(Notes.this, NotesNew.class);
            intent.putExtra("key",key_extra);
            startActivity(intent);
        });



    }


}