package com.example.seniordesignproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotesGeneral extends AppCompatActivity {
    private TextInputLayout field_til;
    private EditText field_et;
    private ArrayList<String> field_list, notes_list;
    private ArrayAdapter<String> adapterItems;
    private AutoCompleteTextView autoCompleteTxt;
    private Button new_btn;
    private String noteKey_extra, field_key;

    private FirebaseDatabase database;
    private DatabaseReference ref,ref_fieldList;
    private ListView listView;
    ArrayAdapter<String> adapter;




    public void init() {
        Toolbar toolbar = findViewById(R.id.toolbar_notes_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        field_list = new ArrayList<>();
        notes_list = new ArrayList<>();
        field_list.add("General Notes");
        field_til = findViewById(R.id.notes_general_textInputLayout);
        field_et = findViewById(R.id.notes_general_auto_complete_txt);
        autoCompleteTxt = findViewById(R.id.notes_general_auto_complete_txt);
        new_btn= findViewById(R.id.notes_general_New_btn);

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users/"+userUid+"/Notes");
        ref_fieldList = database.getReference("Users/"+userUid+"/Animals-Crops");


        listView = findViewById(R.id.notes_general_ListView);
        adapter = new ArrayAdapter<String>(NotesGeneral.this,R.layout.notes_adapter_view,R.id.notes_adapter_view_Note,notes_list);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_general);
        init();
        // Get the notes from database
        listView.setAdapter(adapter);
        updateNoteValuesFromDatabase();
        adapter.notifyDataSetChanged();



        field_til.setHint("Select Field");

        // If field_et is not null, set text to it. Else, set text to "General Notes"
        if (noteKey_extra==null){
            field_et.setText("General Notes");

        }else{
            field_et.setText(noteKey_extra);
        }

        // Adapter for dropdown menu
        adapterItems = new ArrayAdapter<String>(this,R.layout.dropdown_menu, field_list);
        autoCompleteTxt.setAdapter(adapterItems);

        // Get list of fields from database and store them in field_list array list for dropdown menu
        ref_fieldList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    field_list.add(ds.child("Name").getValue().toString());
                }
                adapterItems.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



        // When user selects different field, fetch values for that field from database and update the ListView
        autoCompleteTxt.setOnItemClickListener((parent, view, position, id) -> {
            // Dropdown menu has the names for fields, we need to find selected fields key to fetch correct notes from database
            getTheKeyOfSelectedField(field_et.getText().toString());
            updateNoteValuesFromDatabase();
        });

        new_btn.setOnClickListener(v -> {
            noteKey_extra = field_et.getText().toString();
            Intent intent = new Intent(NotesGeneral.this, NotesNew.class);
            intent.putExtra("noteKey_extra",noteKey_extra);
            startActivity(intent);
        });


    }
    public void getTheKeyOfSelectedField(String fieldName){
        ref_fieldList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                field_key = "General Notes";
                for (DataSnapshot ds : snapshot.getChildren()) {
                    System.out.println("dsChild: "+ds.child("Name").getValue().toString());
                    if (fieldName.equals(ds.child("Name").getValue().toString())){
                        field_key = ds.getKey();
                    }
                }

                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void updateNoteValuesFromDatabase(){
        notes_list.clear();
        // Get the notes for the field selected in dropdown menu, default=general notes
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.getKey().equals(field_key)) {
                        for(DataSnapshot dsChild: ds.getChildren()){
                            notes_list.add(dsChild.getValue().toString());
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}