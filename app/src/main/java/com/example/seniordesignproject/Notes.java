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
    private String key_extra, where_extra;
    ArrayAdapter<String> adapter;
    ArrayList<String> notes_list;


    private FirebaseDatabase database;
    private DatabaseReference ref;


    public void init(){
        Toolbar toolbar = findViewById(R.id.toolbar_notes);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notes");
        // Using setNavigation icon and listener because we need to pass key_extra back
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(v -> {
            if (where_extra.equals("Animal")){
                Intent intent = new Intent(Notes.this, AnimalsDetailed.class);
                intent.putExtra("key",key_extra);
                startActivity(intent);
            }

        });

        key_extra = getIntent().getStringExtra("key");
        where_extra = getIntent().getStringExtra("where");

        new_btn = findViewById(R.id.notes_New_btn);
        listView = findViewById(R.id.notes_ListView);



        notes_list = new ArrayList<>();

        adapter = new ArrayAdapter<String>(Notes.this,R.layout.notes_adapter_view,R.id.notes_adapter_view_Note,notes_list);

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users/"+userUid+"/Notes/"+key_extra);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        init();


        // Get notes_list from database
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    notes_list.add(ds.getValue().toString());
                }
                listView.setAdapter(adapter);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        new_btn.setOnClickListener(v -> {
            Intent intent = new Intent(Notes.this, NotesNew.class);
            // Pass the key of the field and which activity user came from
            intent.putExtra("key",key_extra);
            intent.putExtra("where",where_extra);
            startActivity(intent);
        });





    }


}