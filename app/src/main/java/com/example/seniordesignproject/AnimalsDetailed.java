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
import java.util.HashMap;

public class AnimalsDetailed extends AppCompatActivity implements ExampleDialog.ExampleDialogListener {
    Button addFeature_btn;
    String key_extra;

    ListView listView;
    ArrayList<Animal_Feature> features_list;

    FirebaseDatabase database;
    DatabaseReference ref;

    public void init(){
        Toolbar toolbar = findViewById(R.id.toolbar_animals_detailed);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Animals Detailed");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        key_extra = getIntent().getStringExtra("key");
        listView = findViewById(R.id.animals_detailed_ListView);
        features_list = new ArrayList<Animal_Feature>();
        addFeature_btn = findViewById(R.id.animals_detailed_AddFeature_btn);

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users/"+userUid+"/Animals");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animals_detailed);
        init();

        addFeature_btn.setOnClickListener(v -> {
            openDialog();
        });

        Animal_Feature_Adapter adapter = new Animal_Feature_Adapter(this, R.layout.animal_feature_adapter_view, features_list);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                features_list.clear();
                //System.out.println("on data change activated");
                for(DataSnapshot ds: snapshot.getChildren()){

                    if(ds.getKey().equals(key_extra)){
                        //System.out.println("ds match: "+ds);
                        //System.out.println("asd: " + ds.getChildren());
                        for(DataSnapshot i: ds.getChildren()){
                            //System.out.println("feature: " + i.getKey());
                            //System.out.println("value: " + i.getValue());
                            features_list.add(new Animal_Feature(i.getKey(),i.getValue().toString()));
                        }
                    }
                }
                adapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //System.out.println("feature: "+features_list.get(position).getFeature()+" value: "+ features_list.get(position).getValue());
                Intent intent = new Intent(AnimalsDetailed.this, AnimalsDetailedEdit.class);
                intent.putExtra("feature",features_list.get(position).getFeature());
                intent.putExtra("value",features_list.get(position).getValue());
                intent.putExtra("key",key_extra);
                startActivity(intent);
            }
        });

    }


    public void openDialog(){
        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void applyTexts(String feature, String value) {
        HashMap map = new HashMap();
        map.put(feature,value);
        //update the database
        ref.child(key_extra).updateChildren(map);
    }
}