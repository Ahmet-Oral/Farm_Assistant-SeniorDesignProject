package com.example.seniordesignproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CropsDetailed extends AppCompatActivity {
    TextView cropType_pt, treeNumber_pt, acres_pt, name_pt;
    FirebaseDatabase database;
    DatabaseReference ref;
    DatabaseReference refEdit;
    Button saveChanges_btn;
    String extra;

    public void init(){
        Toolbar toolbar = findViewById(R.id.toolbar_crops);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        database = FirebaseDatabase.getInstance();
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        extra = getIntent().getStringExtra("Name");
        ref = database.getReference("Users/"+userUid+"/Crops/"+extra);
        refEdit = database.getReference("Users/"+userUid+"/Crops/"+extra);
        cropType_pt = findViewById(R.id.cropsDetailed_cropType_pt);
        treeNumber_pt = findViewById(R.id.cropsDetailed_treeNumber_pt);
        acres_pt = findViewById(R.id.cropsDetailed_acres_pt);
        name_pt = findViewById(R.id.cropsDetailed_name_pt);
        saveChanges_btn = findViewById(R.id.crops_detailed_saveChanges_btn);
        getSupportActionBar().setTitle("Crops, "+extra);


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crops_detailed);
        init();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cropType_pt.setText(snapshot.child("CropType").getValue().toString());
                acres_pt.setText(snapshot.child("Acres").getValue().toString());
                treeNumber_pt.setText(snapshot.child("NumberofTrees").getValue().toString());
                name_pt.setText(snapshot.child("Name").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        saveChanges_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refEdit.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HashMap map = new HashMap();
                        map.put("CropType", cropType_pt.getText().toString());
                        map.put("Acres", acres_pt.getText().toString());
                        map.put("NumberofTrees", treeNumber_pt.getText().toString());
                        map.put("Name",name_pt.getText().toString());
                        //update the database
                        extra = name_pt.getText().toString();
                        refEdit.updateChildren(map);
                        startActivity(new Intent(CropsDetailed.this, Crops.class));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

    }
}