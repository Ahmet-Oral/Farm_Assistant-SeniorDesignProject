package com.example.seniordesignproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
    private Button addFeature_btn, delete_btn, toDo_btn, notes_btn;
    private String key_extra, where_extra, name_extra;

    private ListView listView;
    private ArrayList<Animal_Feature> features_list;

    private FirebaseDatabase database;
    private DatabaseReference ref;

    String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public void init(){
        Toolbar toolbar = findViewById(R.id.toolbar_animals_detailed);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Animals Detailed");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the key for selected animal field to take it's values from database
        key_extra = getIntent().getStringExtra("key");
        listView = findViewById(R.id.animals_detailed_ListView);
        features_list = new ArrayList<Animal_Feature>();
        addFeature_btn = findViewById(R.id.animals_detailed_AddFeature_btn);
        delete_btn = findViewById(R.id.animals_detailed_Delete_btn);
        toDo_btn = findViewById(R.id.animals_detailed_ToDo_btn);
        notes_btn = findViewById(R.id.animals_detailed_Notes_btn);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animals_detailed);
        init();

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users/"+userUid+"/Animals-Crops");

        notes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnimalsDetailed.this, Notes.class);
                // Pass the key of selected field
                intent.putExtra("key",key_extra);
                where_extra = "Animal";
                intent.putExtra("where",where_extra);
                intent.putExtra("name", name_extra);
                startActivity(intent);
            }
        });
        toDo_btn.setOnClickListener(v -> {
            Intent intent = new Intent(AnimalsDetailed.this, AnimalsToDo.class);
            // Pass the key of selected field
            intent.putExtra("key",key_extra);

            // Pass the AnimalorCropType to AnimalsToDo.class, which will also be passed to CalendarNewEvent.class

            for(Animal_Feature af: features_list){
                System.out.println("animalsDetailed af.getFeature(): "+af.getFeature());
                if (af.getFeature().equals("Name")){
                    intent.putExtra("name",af.getValue());
                }
            }
            startActivity(intent);
        });
        // Open custom dialog to add new features
        addFeature_btn.setOnClickListener(v -> {
            openDialog();
        });

        delete_btn.setOnClickListener(v -> {
            // Delete the node using the key taken from Animals.class
            confirmDialog("delete_btn");

        });

        // Define the adapter and use features_list for content
        Animal_Feature_Adapter adapter = new Animal_Feature_Adapter(this, R.layout.animal_feature_adapter_view, features_list);


        // Create new Animal_Feature object using the key taken from Animals.class
        //... and store these objects in features_list to be used in listView adapter
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                features_list.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    if(ds.getKey().equals(key_extra)){
                        for(DataSnapshot i: ds.getChildren()){
                            // Don't add TYPE because it is only used for program to detect its type, user shouldn't see this feature
                            if (!i.getKey().equals("TYPE")){
                                features_list.add(new Animal_Feature(i.getKey(),i.getValue().toString()));
                            }
                            // Set title of the action bar with selected fields name
                            if (i.getKey().equals("Name")){
                                name_extra = i.getValue().toString();
                                getSupportActionBar().setTitle(i.getValue().toString());
                            }
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

        // Pass the key, feature and it's value to the next activity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

    // Take the values from the dialog and add new feature to the database
    @Override
    public void applyTexts(String feature, String value) {
        HashMap map = new HashMap();
        map.put(feature,value);
        //update the database
        ref.child(key_extra).updateChildren(map);
    }

    public void confirmDialog(String buttonInfo){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setMessage("Delete "+ name_extra +"?");
        builder.setPositiveButton("Confirm",
                (dialog, which) -> {
                    if (buttonInfo.equals("delete_btn")){
                        FirebaseDatabase.getInstance().getReference().child("Users/"+userUid+"/Animals-Crops").child(key_extra).removeValue();
                        startActivity(new Intent(AnimalsDetailed.this, Animals.class));                    }
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