package com.example.seniordesignproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
    FirebaseDatabase database;
    DatabaseReference ref;
    private Button btn_add;

    public void init(){
        Toolbar toolbar = findViewById(R.id.toolbar_animals);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Animals");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btn_add = findViewById(R.id.animals_addAnimals_btn);


        animals_list = new ArrayList<>();
        list_keys = new ArrayList<>();
        listview = findViewById(R.id.animals_listView);

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users/"+userUid+"/Animals");


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animals);
        init();

        AnimalListAdapter adapter = new AnimalListAdapter(this, R.layout.animals_info_listview, animals_list);


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    animals_list.add(new Animal_Field(ds.child("AnimalType").getValue().toString(),ds.child("NumberOfAnimals").getValue().toString()));
                    list_keys.add(ds.getKey());

                }
                listview.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        listview.setOnItemClickListener((parent, view, position, id) -> {
            adapter.notifyDataSetChanged();
            System.out.println("db name: "+list_keys.get(position)+" name: " + animals_list.get(position).getName()  +" acres "+ animals_list.get(position).getNumber());

        });

        btn_add.setOnClickListener(v -> {
            startActivity(new Intent(Animals.this, AnimalsAdd.class));});






    }
}