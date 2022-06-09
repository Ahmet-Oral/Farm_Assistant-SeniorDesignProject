package com.example.seniordesignproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
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
    private String key_extra, note_key, where_extra;
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
        // Using setNavigation icon and listener because we need to pass key_extra back
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(NotesNew.this, Notes.class);
            intent.putExtra("key",key_extra);
            intent.putExtra("where",where_extra);
            startActivity(intent);
        });


        key_extra = getIntent().getStringExtra("key");
        // Pas where_extra in case user clicks back 2 times in toolbar
        where_extra = getIntent().getStringExtra("where");


        field_til = findViewById(R.id.notes_new_textInputLayout);
        field_et = findViewById(R.id.notes_new_auto_complete_txt);
        note_et = findViewById(R.id.notes_new_Note_pt);

        confirm_btn = findViewById(R.id.notes_new_Confirm_btn);
        cancel_btn = findViewById(R.id.notes_new_Cancel_btn);

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users/"+userUid+"/Notes");
        ref_keys = database.getReference("Users/"+userUid+"/Notes/"+key_extra);

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


        // If key_extra is not null, set as hint of dropdown menu
        // Also set field_et too because we will use it to determine values that will be put to database
        if (key_extra != null){
            field_til.setHint(key_extra);
            field_til.setEnabled(false);
            field_et.setText(key_extra);
        }else {
            field_til.setHint("Select Field");
        }

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteKeyDuplicateChecker();
                CreateNewNote();

                // Pass the key back so Notes.class will know which notes to display
                Intent intent = new Intent(NotesNew.this, Notes.class);
                intent.putExtra("key",key_extra);
                intent.putExtra("where",where_extra);
                startActivity(intent);
                            }
        });
        cancel_btn.setOnClickListener(v -> {
            // Pass the key back so Notes.class will know which notes to display
            Intent intent = new Intent(NotesNew.this, Notes.class);
            intent.putExtra("key",key_extra);
            intent.putExtra("where",where_extra);
            startActivity(intent);
        });


    }
    public void NoteKeyDuplicateChecker(){
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