package com.example.seniordesignproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.protobuf.StringValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class NewCalendarEvent extends AppCompatActivity {
    String dateExtra;
    TextView date_tv;
    FirebaseDatabase database;
    DatabaseReference ref;
    ArrayList<String> field_list;
    AutoCompleteTextView autoCompleteTxt;
    ArrayAdapter<String> adapterItems;
    Button addEvent_btn;
    EditText task_et, field_et;
    ArrayList<String> datesFromDatabase;

    public void init(){
        Toolbar toolbar = findViewById(R.id.newCalendarEvent_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Event");

        dateExtra = getIntent().getStringExtra("date");
        date_tv = findViewById(R.id.newCalendarEvent_date_tv);
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users/"+userUid+"/Crops");
        field_list = new ArrayList<>();
        field_list.add("None");
        autoCompleteTxt = findViewById(R.id.auto_complete_txt);
        addEvent_btn = findViewById(R.id.new_calendar_event_addEvent_btn);
        datesFromDatabase = new ArrayList<>();


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_calendar_event);
        init();
        date_tv.setText(dateExtra);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    //list of fields for dropdown menu
                    field_list.add(ds.getKey().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        adapterItems = new ArrayAdapter<String>(this,R.layout.list_item,field_list);
        autoCompleteTxt.setAdapter(adapterItems);

        autoCompleteTxt.setOnItemClickListener((parent, view, position, id) -> {
            String field = parent.getItemAtPosition(position).toString();
        });

        task_et = findViewById(R.id.newCalendarEvent_task_tv);
        field_et = findViewById(R.id.auto_complete_txt);
        date_tv = findViewById(R.id.newCalendarEvent_date_tv);

        String myDate = date_tv.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(myDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millis = date.getTime();
        System.out.println("millis  "+millis);

        // Get all the dates for checking duplicates
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref = database.getReference("Users/"+userUid+"/Events");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    datesFromDatabase.add(ds.getKey());
                }
                // If selected date key equals to any other existing key, change its name
                for (int i = 0; i < datesFromDatabase.size(); i++){
                    System.out.println("dateExtra out if: " + dateExtra);
                    if(datesFromDatabase.get(i).equals(dateExtra)){
                        dateExtra= dateExtra + "-" + i;
                        i = 0;
                        System.out.println("dateExtra in if: " + dateExtra);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });




        addEvent_btn.setOnClickListener(v -> {
            if (task_et.getText().toString().isEmpty()){
                Toast.makeText(NewCalendarEvent.this, "Task Cannot be Empty!" , Toast.LENGTH_SHORT).show();
                task_et.requestFocus();
                return;
            }
            database = FirebaseDatabase.getInstance();
            ref = database.getReference("Users/"+userUid+"/Events");

            //adding table continents
            HashMap map = new HashMap();
            map.put("Date", millis);
            map.put("Date yyyy-mm-dd", date_tv.getText().toString());
            // If field is not selected assign "None" to it
            if (field_et.getText().toString().isEmpty()){
                map.put("Field", "None");
            }
            else {
                map.put("Field", field_et.getText().toString());
            }
            map.put("Task", task_et.getText().toString());

            //update the database
            System.out.println("dateExtra before push: " + dateExtra);
            ref.child(dateExtra).updateChildren(map);
            Toast.makeText(NewCalendarEvent.this, "Event Successfully Added!" , Toast.LENGTH_SHORT).show();
            startActivity(new Intent(NewCalendarEvent.this, Calendar.class));

        });

    }

}