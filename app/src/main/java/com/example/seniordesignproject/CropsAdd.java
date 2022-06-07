package com.example.seniordesignproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CropsAdd extends AppCompatActivity {
    FirebaseDatabase rootNode;
    DatabaseReference ref;
    private EditText et_cropType, et_acres, et_totalTrees, et_name;

    private Button btn_add;
    public void init(){
        Toolbar toolbar = findViewById(R.id.toolbar_crops_add);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Crops");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btn_add = findViewById(R.id.cropsAdd_add_btn);
        et_cropType = findViewById(R.id.cropsAdd_cropType_pt);
        et_acres = findViewById(R.id.cropsAdd_acres_pt);
        et_totalTrees = findViewById(R.id.cropsAdd_treeNumber_pt);
        et_name = findViewById(R.id.cropsAdd_name_pt);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crops_add);
        init();
        btn_add.setOnClickListener(v -> addNewCrop());
    }

    public void addNewCrop(){
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String cropType = et_cropType.getText().toString();
        String acres = et_acres.getText().toString();
        String totalTrees = et_totalTrees.getText().toString();
        String name = et_name.getText().toString();
        rootNode = FirebaseDatabase.getInstance();
        ref = rootNode.getReference("Users/"+userUid+"/Animals-Crops");
        //adding table continents
        HashMap map = new HashMap();
        if (cropType.isEmpty()){
            Toast.makeText(CropsAdd.this, "Crop Type Can't be Empty!" , Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.isEmpty()){
            Toast.makeText(CropsAdd.this, "Name Can't be Empty!" , Toast.LENGTH_SHORT).show();
            return;
        }

        map.put("CropType", cropType);
        if (!acres.isEmpty()){
        map.put("Acres", acres);}
        if (!totalTrees.isEmpty()){
        map.put("NumberofTrees", totalTrees);}
        if (!name.isEmpty()){
            map.put("Name", name);}
        map.put("TYPE","Crop");
        //update the database
        ref.child(name).updateChildren(map);
        Toast.makeText(CropsAdd.this, "Crops Added Successfully" , Toast.LENGTH_SHORT).show();
        startActivity(new Intent(CropsAdd.this, Crops.class));
    }
}