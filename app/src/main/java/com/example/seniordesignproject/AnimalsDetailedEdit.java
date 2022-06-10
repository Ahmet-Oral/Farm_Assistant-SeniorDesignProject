package com.example.seniordesignproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AnimalsDetailedEdit extends AppCompatActivity implements View.OnClickListener {
    String userUid, feature_extra, value_extra, key_extra;
    EditText feature_et, value_et;
    Button delete_btn, cancel_btn, save_btn;

    FirebaseDatabase database;
    DatabaseReference ref;

    public void init(){
        Toolbar toolbar = findViewById(R.id.toolbar_animals_detailed_edit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Feature");
        // Using setNavigation icon and listener because we need to pass key_extra back
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(AnimalsDetailedEdit.this, AnimalsDetailed.class);
            intent.putExtra("key",key_extra);
            startActivity(intent);
        });


        feature_et = findViewById(R.id.animals_detailed_edit_Features_pt);
        value_et = findViewById(R.id.animals_detailed_edit_Value_pt);

        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users/"+userUid+"/Animals-Crops");

        feature_extra = getIntent().getStringExtra("feature");
        value_extra = getIntent().getStringExtra("value");
        key_extra = getIntent().getStringExtra("key");

        delete_btn = findViewById(R.id.animals_detailed_edit_Delete_btn);
        cancel_btn = findViewById(R.id.animals_detailed_edit_Cancel_btn);
        save_btn = findViewById(R.id.animals_detailed_edit_Save_btn);

        delete_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
        save_btn.setOnClickListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animals_detailed_edit);
        init();

        // These features key can't be edited because they are required in other activities
        if(feature_extra.equals("AnimalType") || feature_extra.equals("NumberOfAnimals") || feature_extra.equals("Name")){
            feature_et.setEnabled(false);
        }

        // Set feature and it's value with parameters taken from AnimalsDetailed.class
        feature_et.setText(feature_extra);
        value_et.setText(value_extra);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.animals_detailed_edit_Delete_btn:
                // These features key can't be deleted because they are required in other activities
                if(feature_extra.equals("AnimalType") || feature_extra.equals("NumberOfAnimals") || feature_extra.equals("Name")){
                    Toast.makeText(AnimalsDetailedEdit.this, "Can't Delete This Feature", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Delete the feature (Find it by using the key_extra and feature_extra values taken from AnimalsDetailed.class)
                confirmDialog("delete_btn");
                break;

            case R.id.animals_detailed_edit_Cancel_btn:
                Intent intent2 = new Intent(AnimalsDetailedEdit.this, AnimalsDetailed.class);
                intent2.putExtra("key",key_extra);
                startActivity(intent2);
                break;

            case R.id.animals_detailed_edit_Save_btn:
                HashMap map = new HashMap();
                // Put new feature and it's value to the hashmap
                map.put(feature_et.getText().toString(), value_et.getText().toString());

                // If feature's name is updated, remove old feature
                if(!feature_et.getText().toString().equals(feature_extra)){
                    FirebaseDatabase.getInstance().getReference().child("Users/"+userUid+"/Animals-Crops/"+key_extra).child(feature_extra).removeValue();
                }

                // Update the database with new value
                ref.child(key_extra).updateChildren(map);

                Intent intent3 = new Intent(AnimalsDetailedEdit.this, AnimalsDetailed.class);
                // Pass the key_extra back to AnimalsDetailed.class because it needs to know which node will be displayed
                intent3.putExtra("key",key_extra);
                startActivity(intent3);
                break;

        }
    }
    public void confirmDialog(String buttonInfo){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setMessage("Delete "+ feature_extra +"?");
        builder.setPositiveButton("Confirm",
                (dialog, which) -> {
                    if (buttonInfo.equals("delete_btn")){
                        FirebaseDatabase.getInstance().getReference().child("Users/"+userUid+"/Animals-Crops/"+key_extra).child(feature_extra).removeValue();
                        Intent intent = new Intent(AnimalsDetailedEdit.this, AnimalsDetailed.class);
                        intent.putExtra("key",key_extra);
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}