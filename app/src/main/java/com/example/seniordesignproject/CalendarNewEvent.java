package com.example.seniordesignproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CalendarNewEvent extends AppCompatActivity {
    String dateExtra, fieldType, fieldKey, fieldName;
    TextView date_tv;
    FirebaseDatabase database;
    DatabaseReference ref;
    DatabaseReference ref_fieldType;
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
        date_tv = findViewById(R.id.calendar_new_event_DateDisplay_tv);
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users/"+userUid+"/Animals-Crops");
        field_list = new ArrayList<>();
        field_list.add("None");
        autoCompleteTxt = findViewById(R.id.auto_complete_txt);
        addEvent_btn = findViewById(R.id.new_calendar_event_addEvent_btn);
        datesFromDatabase = new ArrayList<>();

        task_et = findViewById(R.id.calendar_new_event_Task_pt);
        field_et = findViewById(R.id.auto_complete_txt);
        date_tv = findViewById(R.id.calendar_new_event_DateDisplay_tv);



    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_new_event);
        init();

        // Set date textView to the selected date
        // dateExtra is selected at calendar.class
        date_tv.setText(dateExtra);

        // Get list of fields from database and store them in field_list array list for dropdown menu
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    field_list.add(ds.child("Name").getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Define dropdown menu using values from field_lists
        adapterItems = new ArrayAdapter<String>(this,R.layout.dropdown_menu,field_list);
        autoCompleteTxt.setAdapter(adapterItems);




        // Get all the dates for checking duplicates
        // If duplicate is found, change it's key by adding for loop counter
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref = database.getReference("Users/"+userUid+"/Events");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    // Get all of the event keys
                    datesFromDatabase.add(ds.getKey());
                }
                // If selected date key equals to any other existing key, change its name
                for (int i = 0; i < datesFromDatabase.size(); i++){
                    if(datesFromDatabase.get(i).equals(dateExtra)){
                        dateExtra= dateExtra + "-" + i;
                        i = 0;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        addEvent_btn.setOnClickListener(v -> {
            // If task is empty return
            if (task_et.getText().toString().isEmpty()){
                Toast.makeText(CalendarNewEvent.this, "Task Cannot be Empty!" , Toast.LENGTH_SHORT).show();
                task_et.requestFocus();
                return;
            }
            database = FirebaseDatabase.getInstance();

            // Get field type to determine color in calender view
            ref_fieldType = database.getReference("Users/"+userUid+"/Animals-Crops");
            ref_fieldType.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds: snapshot.getChildren()){
                        // Search Animals-Crops for the given fieldName and get it's type and key
                        if (ds.child("Name").getValue().toString().equals(field_et.getText().toString())){
                            fieldType = ds.child("TYPE").getValue().toString();
                            fieldKey = ds.getKey();
                            fieldName = ds.child("Name").getValue().toString();
                        }
                    }
                    HashMap map1 = new HashMap();
                    // If fieldType is not defined create Type and FieldKey as "None"
                    if (fieldType!=null){
                        map1.put("Type",fieldType);
                        map1.put("FieldKey",fieldKey);
                        map1.put("Field",fieldName);

                    }else {
                        map1.put("Type","None");
                        map1.put("FieldKey","None");
                        map1.put("Field","None");
                    }
                    // Create the event with dateExtra key (it is already checked for duplicates)
                    ref = database.getReference("Users/"+userUid+"/Events");
                    ref.child(dateExtra).updateChildren(map1);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            // Convert date to milliseconds
            String myDate = date_tv.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date date = null;
            try {
                date = sdf.parse(myDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long millis = date.getTime();


            HashMap map = new HashMap();
            map.put("Date", millis);
            map.put("Date dd-MM-yyyy", date_tv.getText().toString());
            map.put("Task", task_et.getText().toString());

            // If fieldType is not selected or selected as "None", update their values to "None"
            // Because of the time difference between onDataChange function, this if will always be true at first
            //... but after onDataChange function detects fieldType is changed, it will update these values in split second
            // If I don't define these values, it will crash the program when we switch to calendar.class because
            //... calendar.class will try to find these values before than onDataChange function updates them
            if (fieldType == null || fieldType.equals("None")){
                System.out.println("Aaaaaaaaaaaaaaaaaaaaaaaa");
                map.put("Type","None");
                map.put("Field", "None");
                map.put("FieldKey","None");
               }


            // Create the event with dateExtra key (it is already checked for duplicates)
            ref = database.getReference("Users/"+userUid+"/Events");
            ref.child(dateExtra).updateChildren(map);
            Toast.makeText(CalendarNewEvent.this, "Event Successfully Added!" , Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CalendarNewEvent.this, Calendar.class));

        });

    }

}