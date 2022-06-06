package com.example.seniordesignproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class AnimalsAdd extends AppCompatActivity implements ExampleDialog.ExampleDialogListener {
    private Button addFeature_btn, addAnimal_btn;
    ListView listView;
    ArrayList<Animal_Feature> features_list;
    String dialog_feature;
    String dialog_value;
    FirebaseDatabase database;
    DatabaseReference ref;
    private EditText animalType_et, animalNumber_et, fieldName_et;




    public void init(){
        Toolbar toolbar = findViewById(R.id.toolbar_animals_add);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Animals");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = findViewById(R.id.animals_add_listview);
        features_list = new ArrayList<Animal_Feature>();
        addFeature_btn = findViewById(R.id.animals_add_addFeature_btn);
        addAnimal_btn = findViewById(R.id.animals_add_addAnimal_btn);
        dialog_feature = getIntent().getStringExtra("feature");
        dialog_value = getIntent().getStringExtra("value");
        System.out.println("extras: " + dialog_feature+ " "+ dialog_value);

        animalType_et = findViewById(R.id.animals_add_type_pt);
        animalNumber_et = findViewById(R.id.animals_add_number_pt);
        fieldName_et = findViewById(R.id.animals_add_name_pt);


        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users/"+userUid+"/Animals");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animals_add);
        init();
        addAnimal_btn.setOnClickListener(v -> {
            addNewAnimal();
        });

        Animal_Feature_Adapter adapter = new Animal_Feature_Adapter(this, R.layout.animal_feature_adapter_view, features_list);

        features_list.add(new Animal_Feature("feature1","value1"));

        addFeature_btn.setOnClickListener(v -> {
            openDialog();
            System.out.println("before: " + dialog_feature+ " "+ dialog_value);

            if (dialog_feature != null) {
                features_list.add(new Animal_Feature(dialog_feature,dialog_value));
                adapter.notifyDataSetChanged();
            }


        });
        listView.setAdapter(adapter);


    }
    public void openDialog(){
        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void applyTexts(String feature, String value) {
        //feature and value are the strings taken from the pop up dialog
        System.out.println("feature: " + feature+" value: "+value);


    }

    public void addNewAnimal(){
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String animalType = animalType_et.getText().toString();
        String animalNumber = animalNumber_et.getText().toString();
        String fieldName = fieldName_et.getText().toString();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users/"+userUid+"/Animals");
        //adding table continents
        HashMap map = new HashMap();
        if (animalType.isEmpty()){
            Toast.makeText(AnimalsAdd.this, "Animal Type Can't be Empty!" , Toast.LENGTH_SHORT).show();
            return;
        }else {
            map.put("AnimalType", animalType);
        }

        if (animalNumber.isEmpty()){
            Toast.makeText(AnimalsAdd.this, "Animal Number Can't be Empty!" , Toast.LENGTH_SHORT).show();
            return;
        }else {
        map.put("NumberOfAnimals", animalNumber);
        }
        if (fieldName.isEmpty()){
            Toast.makeText(AnimalsAdd.this, "Field Name Can't be Empty!" , Toast.LENGTH_SHORT).show();
            return;
        }else {
            map.put("FieldName", fieldName);
        }

        //update the database
        ref.child(animalType).updateChildren(map);
        Toast.makeText(AnimalsAdd.this, "Animals Added Successfully" , Toast.LENGTH_SHORT).show();
        startActivity(new Intent(AnimalsAdd.this, Animals.class));
    }
}