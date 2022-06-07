package com.example.seniordesignproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AnimalsDetailedEdit extends AppCompatActivity {
    EditText feature_et, value_et;
    Button delete_btn, cancel_btn, save_btn;
    FirebaseDatabase database;
    DatabaseReference ref;
    String userUid, feature_extra, value_extra, key_extra;

    public void init(){
        Toolbar toolbar = findViewById(R.id.toolbar_animals_detailed_edit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Feature");

        feature_et = findViewById(R.id.animalsDetailedEdit_features_pt);
        value_et = findViewById(R.id.animalsDetailedEdit_value_pt);

        delete_btn = findViewById(R.id.animalsDetailedEdit_delete_btn);
        cancel_btn = findViewById(R.id.animalsDetailedEdit_cancel_btn);
        save_btn = findViewById(R.id.animalsDetailedEdit_save_btn);

        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users/"+userUid+"/Animals");

        feature_extra = getIntent().getStringExtra("feature");
        value_extra = getIntent().getStringExtra("value");
        key_extra = getIntent().getStringExtra("key");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animals_detailed_edit);
        init();

        if(feature_extra.equals("AnimalType") || feature_extra.equals("NumberOfAnimals")){
            feature_et.setEnabled(false);

        }

        feature_et.setText(feature_extra);
        value_et.setText(value_extra);

        HashMap map = new HashMap();


        cancel_btn.setOnClickListener(v -> {
            Intent intent = new Intent(AnimalsDetailedEdit.this, AnimalsDetailed.class);
            intent.putExtra("key",key_extra);
            startActivity(intent);
        });
        save_btn.setOnClickListener(v -> {
            map.clear();
            map.put(feature_et.getText().toString(), value_et.getText().toString());

            //remove old value
            FirebaseDatabase.getInstance().getReference().child("Users/"+userUid+"/Animals/"+key_extra).child(feature_extra).removeValue();
            //update the database
            ref.child(key_extra).updateChildren(map);

            Intent intent = new Intent(AnimalsDetailedEdit.this, AnimalsDetailed.class);
            intent.putExtra("key",key_extra);
            startActivity(intent);
        });
        delete_btn.setOnClickListener(v -> {
            Intent intent = new Intent(AnimalsDetailedEdit.this, AnimalsDetailed.class);
            intent.putExtra("key",key_extra);
            startActivity(intent);
            FirebaseDatabase.getInstance().getReference().child("Users/"+userUid+"/Animals/"+key_extra).child(feature_extra).removeValue();
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });




        init();

    }
}