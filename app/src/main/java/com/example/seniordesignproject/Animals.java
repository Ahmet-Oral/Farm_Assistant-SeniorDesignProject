package com.example.seniordesignproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Animals extends AppCompatActivity {
    ArrayList<Animal_Field> animals_list;
    ArrayList<String> list_keys;

    ListView listview;
    Button btn_add;

    FirebaseDatabase database;
    DatabaseReference ref;


    public void init(){
        Toolbar toolbar = findViewById(R.id.toolbar_animals);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Animals");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_add = findViewById(R.id.animals_AddAnimals_btn);

        animals_list = new ArrayList<>();
        list_keys = new ArrayList<>();
        listview = findViewById(R.id.animals_listView);

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users/"+userUid+"/Animals-Crops");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animals);
        init();

        Animal_Field_Adapter adapter = new Animal_Field_Adapter(this, R.layout.animals_info_adapter_view, animals_list);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    if(ds.child("TYPE").getValue().toString().equals("Animal")){
                        animals_list.add(new Animal_Field(ds.child("Name").getValue().toString(),ds.child("NumberOfAnimals").getValue().toString()));
                        list_keys.add(ds.getKey());
                    }
                }
                listview.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        listview.setOnItemClickListener((parent, view, position, id) -> {
            adapter.notifyDataSetChanged();
            //System.out.println("db name: "+list_keys.get(position)+" name: " + animals_list.get(position).getName()  +" acres "+ animals_list.get(position).getNumber());
            Intent intent = new Intent(Animals.this, AnimalsDetailed.class);
            intent.putExtra("key", list_keys.get(position));
            startActivity(intent);
        });

        btn_add.setOnClickListener(v -> {
            startActivity(new Intent(Animals.this, AnimalsAdd.class));
        });


    }
}