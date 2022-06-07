package com.example.seniordesignproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Crops extends AppCompatActivity {
    private Button btn_add;
    ListView listView;
    FirebaseDatabase database;
    DatabaseReference ref;
    ArrayList<String> list;
    ArrayList<String> crop_types_list;

    ArrayAdapter<String> adapter;

    public void init(){
        Toolbar toolbar = findViewById(R.id.toolbar_crops);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Crops");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        btn_add = findViewById(R.id.crops_new_btn);
        listView = findViewById(R.id.crops_listView);
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users/"+userUid+"/Animals-Crops");
        list = new ArrayList<>();
        crop_types_list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(Crops.this,R.layout.crop_info,R.id.cropInfo,list);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crops);
        init();

        btn_add.setOnClickListener(v -> {
            startActivity(new Intent(Crops.this, CropsAdd.class));});


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    if(ds.child("TYPE").getValue().toString().equals("Crop")){
                        list.add(ds.child("Name").getValue().toString() + " - "+ ds.child("Acres").getValue().toString()+" Acres");
                        crop_types_list.add(ds.getKey().toString());
                    }

                }
                listView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(Crops.this, CropsDetailed.class);
            intent.putExtra("Name",crop_types_list.get(position));
            startActivity(intent);
        });
    }
}