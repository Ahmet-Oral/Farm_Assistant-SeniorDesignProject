package com.example.seniordesignproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class CropsAdd extends AppCompatActivity implements ExampleDialog.ExampleDialogListener {
    private Button addFeature_btn, addAnimal_btn;
    private EditText animalType_et, animalNumber_et, fieldName_et;
    private String temp_feature, temp_value;

    private ListView listView;
    private ArrayList<Animal_Feature> features_list;

    private FirebaseDatabase database;
    private DatabaseReference ref;

    public void init() {
        Toolbar toolbar = findViewById(R.id.toolbar_crops_add);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Crops");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = findViewById(R.id.crops_add_ListView);
        features_list = new ArrayList<Animal_Feature>();

        addFeature_btn = findViewById(R.id.crops_add_AddFeature_btn);
        addAnimal_btn = findViewById(R.id.crops_add_AddAnimal_btn);

        animalType_et = findViewById(R.id.crops_add_Type_pt);
        animalNumber_et = findViewById(R.id.crops_add_Number_pt);
        fieldName_et = findViewById(R.id.crops_add_Name_pt);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        setContentView(R.layout.activity_crops_add);
        init();

        addAnimal_btn.setOnClickListener(v -> {
            addNewAnimal();
        });

        Animal_Feature_Adapter adapter = new Animal_Feature_Adapter(this, R.layout.animal_feature_adapter_view, features_list);
        //features_list.add(new Animal_Feature("feature1","value1"));

        addFeature_btn.setOnClickListener(v -> {
            openDialog();
        });

        listView.setAdapter(adapter);


        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users/" + userUid + "/Temp");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.child("temp_feature") != null) {
                            temp_feature = ds.child("temp_feature").getValue().toString();
                        }
                        if (ds.child("temp_value") != null) {
                            temp_value = ds.child("temp_value").getValue().toString();
                        }
                    }
                    if (temp_feature != null & temp_value != null) {
                        features_list.add(new Animal_Feature(temp_feature, temp_value));
                    }
                    FirebaseDatabase.getInstance().getReference().child("Users/" + userUid + "/Temp").child("CropFeatures").removeValue();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


    public void openDialog() {
        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void applyTexts(String feature, String value) {
        // If feature or value is empty, give alert
        if (feature.equals("")||value.equals("")){
            Toast.makeText(CropsAdd.this, "Feature or Value Cannot be Empty!" , Toast.LENGTH_SHORT).show();
            return;
        }
        //feature and value are the strings taken from the pop up dialog
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users/" + userUid);
        HashMap map = new HashMap();
        map.put("temp_feature", feature);
        map.put("temp_value", value);

        //update the database
        ref.child("Temp/CropFeatures").updateChildren(map);
    }

    public void addNewAnimal() {
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String animalType = animalType_et.getText().toString();
        String animalNumber = animalNumber_et.getText().toString();
        String fieldName = fieldName_et.getText().toString();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users/" + userUid + "/Animals-Crops");
        //adding table continents
        HashMap map = new HashMap();
        if (animalType.isEmpty()) {
            Toast.makeText(CropsAdd.this, "Crop Type Can't be Empty!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            map.put("CropType", animalType);
        }

        if (animalNumber.isEmpty()) {
            Toast.makeText(CropsAdd.this, "Acres Can't be Empty!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            map.put("Acres", animalNumber);
        }
        if (fieldName.isEmpty()) {
            Toast.makeText(CropsAdd.this, "Field Name Can't be Empty!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            map.put("Name", fieldName);
        }
        map.put("TYPE", "Crop");

        for (int i = 0; i < features_list.size(); i++) {
            map.put(features_list.get(i).getFeature(), features_list.get(i).getValue());
        }

        //update the database
        ref.child(fieldName).updateChildren(map);
        Toast.makeText(CropsAdd.this, "Crops Added Successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(CropsAdd.this, Crops.class));
    }
}