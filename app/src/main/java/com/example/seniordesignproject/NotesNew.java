package com.example.seniordesignproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class NotesNew extends AppCompatActivity {


    private TextInputLayout field_til;
    private String keyExtra, note_key;
    private EditText note_et, field_et;
    private Button confirm_btn, cancel_btn;
    private FirebaseDatabase database;
    private DatabaseReference ref, ref_keys;
    private ArrayList<String> noteKeysFromDatabase;

    // detect where it came from, if it came from somewhere take that fields key
    public void init() {
        Toolbar toolbar = findViewById(R.id.toolbar_notes_new);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Note");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        keyExtra = getIntent().getStringExtra("key");

        field_til = findViewById(R.id.notes_new_textInputLayout);
        field_et = findViewById(R.id.notes_new_auto_complete_txt);
        note_et = findViewById(R.id.notes_new_Note_pt);

        confirm_btn = findViewById(R.id.notes_new_Confirm_btn);

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users/"+userUid+"/Notes");
        ref_keys = database.getReference("Users/"+userUid+"/Notes/"+keyExtra);

        noteKeysFromDatabase = new ArrayList<>();

        // Default key of the note is "Note", if key already exists it will be changed
        note_key = "Note";


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_new);
        init();


        // Get all of the keys from database to be used in NoteKeyDuplicateChecker()
        ref_keys.addValueEventListener(new ValueEventListener() {
            int i =0;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    noteKeysFromDatabase.add(ds.getKey());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        // If keyExtra is not null, set as hint of dropdown menu
        // Also set field_et too because we will use it to determine values that will be put to database
        if (keyExtra != null){
            field_til.setHint(keyExtra);
            field_til.setEnabled(false);
            field_et.setText(keyExtra);
        }else {
            field_til.setHint("Select Field");
        }

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(note_key);
                NoteKeyDuplicateChecker();
                System.out.println(note_key);
                CreateNewNote();

            }
        });



    }
    public void NoteKeyDuplicateChecker(){
        System.out.println("noteKeysFromDatabase: "+noteKeysFromDatabase);
        for (int i = 0; i < noteKeysFromDatabase.size(); i++){
            if(noteKeysFromDatabase.get(i).equals(note_key)){
                note_key= note_key + "-" + i;
                // Restart the loop and check again
                i = 0;
            }
        }
    }

    public void CreateNewNote(){

        HashMap map = new HashMap();
        map.put(note_key, note_et.getText().toString());

        ref.child(field_et.getText().toString()).updateChildren(map);



    }
}