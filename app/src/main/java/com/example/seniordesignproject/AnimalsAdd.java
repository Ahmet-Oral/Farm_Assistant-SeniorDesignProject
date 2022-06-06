package com.example.seniordesignproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class AnimalsAdd extends AppCompatActivity implements ExampleDialog.ExampleDialogListener {
    private Button addFeature_btn;
    ListView listView;
    ArrayList<Animal_Feature> features_list;



    public void init(){
        Toolbar toolbar = findViewById(R.id.toolbar_animals_add);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Animals");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = findViewById(R.id.animals_add_listview);
        features_list = new ArrayList<Animal_Feature>();
        addFeature_btn = findViewById(R.id.animals_add_addFeature_btn);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animals_add);
        init();

        Animal_Feature_Adapter adapter = new Animal_Feature_Adapter(this, R.layout.animal_feature_adapter_view, features_list);

        features_list.add(new Animal_Feature("feature1","value1"));

        addFeature_btn.setOnClickListener(v -> {
            openDialog();
            adapter.notifyDataSetChanged();

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
}