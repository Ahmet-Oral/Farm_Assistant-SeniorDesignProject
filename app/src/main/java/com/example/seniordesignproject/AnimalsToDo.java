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

public class AnimalsToDo extends AppCompatActivity {
    private ListView listView;
    private ArrayList<Animal_Todo_obj> todo_list;
    private String key_extra, name_extra;
    private Button add_btn;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    public void init(){
        Toolbar toolbar = findViewById(R.id.toolbar_animals_to_do);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(", To-Do");
        // Using setNavigation icon and listener because we need to pass key_extra back
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(AnimalsToDo.this, AnimalsDetailed.class);
            intent.putExtra("key",key_extra);
            startActivity(intent);
        });

        listView = findViewById(R.id.animals_to_do_ListView);
        todo_list = new ArrayList<>();

        // Get the key of the selected field from last activity
        key_extra = getIntent().getStringExtra("key");
        // Get fields name to pass on CalendarNewEvent in case user clicks new event
        name_extra = getIntent().getStringExtra("name");

        // Put fields name to action bar
        getSupportActionBar().setTitle(name_extra+", To-Do");

        add_btn = findViewById(R.id.animals_to_do_Add_Btn);

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference("Users/"+userUid+"/Events");



    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animals_to_do);
        init();
        Animal_Todo_Adapter adapter = new Animal_Todo_Adapter(this, R.layout.animals_todo_adapter_view, todo_list);

        add_btn.setOnClickListener(v -> {
            Intent intent = new Intent(AnimalsToDo.this, CalendarNewEvent.class);
            intent.putExtra("key",key_extra);
            intent.putExtra("where","animalsToDo");
            intent.putExtra("name",name_extra);
            startActivity(intent);
        });

        // Get all of the events of selected field by using the key_extra taken from last activity
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                todo_list.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    if(ds.child("FieldKey").getValue().toString().equals(key_extra)){
                        // Add events "date" and "task" to display in listView
                        // Also add that events Key so we can access it in next activity when clicked from listview
                        todo_list.add(new Animal_Todo_obj(ds.child("Task").getValue().toString(), ds.child("Date dd-MM-yyyy").getValue().toString(),ds.getKey()));
                        adapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AnimalsToDo.this, CalendarEventDetails.class);
                // Pas the date, task and key of the clicked event
                // Also pass this class name
                intent.putExtra("where","animalsToDo");
                intent.putExtra("ClickedDate",todo_list.get(position).getDate());
                intent.putExtra("ClickedTask", todo_list.get(position).getTask());
                intent.putExtra("ClickedEventKey",todo_list.get(position).getTodo_obj_key());
                intent.putExtra("key",key_extra);
                startActivity(intent);

            }
        });

    }
}