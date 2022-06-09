package com.example.seniordesignproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
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
    private String dateExtra, where, nameExtra, keyExtra, fieldType, fieldKey, fieldName, date_jan_dd_yyyy_str, date_dd_MM_yyyy_str, date_duplicateCheck_str;
    private FirebaseDatabase database;
    private DatabaseReference ref,ref_fieldType;
    private ArrayList<String> field_list, datesFromDatabase;
    private AutoCompleteTextView autoCompleteTxt;
    private ArrayAdapter<String> adapterItems;
    private DatePickerDialog datePickerDialog;
    private Button addEvent_btn, datePicker_btn;
    private EditText task_et, field_et;
    private TextInputLayout field_til;

    public void init(){
        Toolbar toolbar = findViewById(R.id.newCalendarEvent_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Event");

        dateExtra = getIntent().getStringExtra("date");
        nameExtra = getIntent().getStringExtra("name");
        // Key of the field in case user came from a field type activity
        keyExtra = getIntent().getStringExtra("key");
        where = getIntent().getStringExtra("where");

        field_list = new ArrayList<>();
        field_list.add("None");
        autoCompleteTxt = findViewById(R.id.auto_complete_txt);
        addEvent_btn = findViewById(R.id.new_calendar_event_addEvent_btn);
        datesFromDatabase = new ArrayList<>();

        field_til = findViewById(R.id.textInputLayout);

        datePicker_btn = findViewById(R.id.calendar_new_event_DatePicker_btn);

        task_et = findViewById(R.id.calendar_new_event_Task_pt);
        field_et = findViewById(R.id.auto_complete_txt);



    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_new_event);
        init();
        initDatePicker();

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users/"+userUid+"/Animals-Crops");


        // if dateExtra is selected at calendar.class, Set date to the selected date
        if(dateExtra!=null){
            datePicker_btn.setText(dateExtra);
            // Assigning dateExtra to date_dd_MM_yyyy_str in case user doesn't changes the date
            date_dd_MM_yyyy_str = dateExtra;
            date_duplicateCheck_str = dateExtra;
        }else{
            datePicker_btn.setText("Select Date");
        }

        // If AnimalorCropType_extra is not null, set as hint of dropdown menu
        // Also set field_et too because we will use it to determine values that will be put to database
        if (nameExtra != null){
            field_til.setHint(nameExtra);
            field_til.setEnabled(false);
            field_et.setText(nameExtra);

        }



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
        // If duplicate is found, change it's key
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
                    if(datesFromDatabase.get(i).equals(date_duplicateCheck_str)){
                        date_duplicateCheck_str= date_duplicateCheck_str + "-" + i;
                        // Restart the loop and check again
                        i = 0;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        addEvent_btn.setOnClickListener(v -> {
            // If user did not select any date, return
            if (datePicker_btn.getText().toString().equals("Select Date")){
                Toast.makeText(CalendarNewEvent.this, "Date Cannot be Empty!" , Toast.LENGTH_SHORT).show();
                return;

            }
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
                    // If fieldType is not defined create Type, Field and FieldKey as "None"
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
                    ref.child(date_duplicateCheck_str).updateChildren(map1);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            // Convert date to milliseconds
            String myDate = date_dd_MM_yyyy_str;

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
            map.put("Date dd-MM-yyyy", date_dd_MM_yyyy_str);
            map.put("Task", task_et.getText().toString());

            // If fieldType is not selected or selected as "None", update their values to "None"
            // Because of the time difference between onDataChange function, this if will always be true at first
            //... but after onDataChange function detects fieldType is changed, it will update these values in split second
            // If I don't define these values, it will crash the program when we switch to calendar.class because
            //... calendar.class will try to find these values before than onDataChange function updates them
            if (fieldType == null || fieldType.equals("None")){
                map.put("Type","None");
                map.put("Field", "None");
                map.put("FieldKey","None");
               }


            // Create the event with dateExtra key (it is already checked for duplicates)
            ref = database.getReference("Users/"+userUid+"/Events");
            System.out.println("date_dd_MM_yyyy_str: "+date_dd_MM_yyyy_str);
            ref.child(date_duplicateCheck_str).updateChildren(map);
            Toast.makeText(CalendarNewEvent.this, "Event Successfully Added!" , Toast.LENGTH_SHORT).show();

            // If we came from a field activity, find out which one and go back to it
            Intent intent;
            if(where!=null){
                if (where.equals("animalsToDo")){
                    intent = new Intent(CalendarNewEvent.this, AnimalsToDo.class);
                    intent.putExtra("key",keyExtra);
                    startActivity(intent);
                }else if(where.equals("cropsToDo")){
                    intent = new Intent(CalendarNewEvent.this, CropsToDo.class);
                    intent.putExtra("key",keyExtra);
                    startActivity(intent);
                }

            }else {
                startActivity(new Intent(CalendarNewEvent.this, Calendar.class));
            }

        });

    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }



    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {
            // Increase month by 1 because january equals to 0
            month = month + 1;
            // Format date to JAN-9-2022 style to update datePicker_btn text
            date_jan_dd_yyyy_str = makeDateString(year,month,day);
            datePicker_btn.setText(date_jan_dd_yyyy_str);
            // Format date to dd_MM_yyyy to be able to check differences between dates
            // It is in same format as dates in database

            date_dd_MM_yyyy_str = makeDateString_dd_MM_yyyy(year,month,day);
            // Also update date_duplicateCheck_str
            date_duplicateCheck_str = date_dd_MM_yyyy_str;
            System.out.println("date_dd_MM_yyyy_str000: "+date_dd_MM_yyyy_str);

        };

        // If dateExtra is not defined get current date and use that
        // Else, dateExtra use dateExtra (if a date has been selected from calendar before switching to this class)
        if(dateExtra == null){
            java.util.Calendar cal = java.util.Calendar.getInstance();
            int year = cal.get(java.util.Calendar.YEAR);
            int month = cal.get(java.util.Calendar.MONTH);
            int day = cal.get(java.util.Calendar.DAY_OF_MONTH);
            int style = AlertDialog.THEME_HOLO_LIGHT;
            // Update DatePicker with new values
            datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);

        }else {
            // Split the date for displaying selected events date in datePicker_btn
            String[] tokens = dateExtra.split("-");
            int year = Integer.parseInt(tokens[2]);
            int month = Integer.parseInt(tokens[1])-1;
            int day = Integer.parseInt(tokens[0]);
            int style = AlertDialog.THEME_HOLO_LIGHT;
            // Update DatePicker with new values
            datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        }

    }

    // Convert date to dd_MM_yyyy format as str
    private String makeDateString_dd_MM_yyyy(int year, int month, int day) {
        String day_str, month_str, year_str;
        if(day<10){
            day_str = "0"+String.valueOf(day);
        }else {
            day_str = String.valueOf(day);
        }
        if(month<10){
            month_str = "0"+String.valueOf(month);
        }else {
            month_str = String.valueOf(month);
        }
        year_str = String.valueOf(year);
        return day_str+"-"+month_str+"-"+year_str;
    }

    // Convert date to JAN_dd_yyyy format as str
    private String makeDateString(int year, int month, int day) {
        return getMonthFormat(month) + "-" + day + "-" + year;
    }

    // Convert given int month value to str month value
    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }
}