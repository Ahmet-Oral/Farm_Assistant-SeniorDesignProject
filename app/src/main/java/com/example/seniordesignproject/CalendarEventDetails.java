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

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CalendarEventDetails extends AppCompatActivity {
    private Button datePicker_btn, save_btn, cancel_btn, delete_btn;
    private DatePickerDialog datePickerDialog;
    private String keyExtra, where_extra, date_jan_dd_yyyy_str, clickedDate_extra, clickedTask_extra, eventKey_str, newType_str, date_dd_MM_yyyy_str, clickedEventKey_extra;
    private EditText task_et, field_et;
    private TextInputLayout field_til;

    private ArrayList<String> field_list;
    private ArrayAdapter<String> adapterItems;
    private AutoCompleteTextView autoCompleteTxt;

    private FirebaseDatabase database;
    private DatabaseReference ref,ref_fieldList;
    
    public void init() {
        Toolbar toolbar = findViewById(R.id.toolbar_calendar_event_details);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Event Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        clickedDate_extra = getIntent().getStringExtra("ClickedDate");
        clickedTask_extra = getIntent().getStringExtra("ClickedTask");
        clickedEventKey_extra = getIntent().getStringExtra("ClickedEventKey");
        // Key of the field where we came from, will be empty if we came from Calendar
        keyExtra = getIntent().getStringExtra("key");
        // Name of where we came from, will be used to go back
        where_extra = getIntent().getStringExtra("where");


        task_et = findViewById(R.id.calendar_event_details_Task_pt);
        task_et.setText(clickedTask_extra);

        field_til = findViewById(R.id.calendar_event_details_textInputLayout);
        field_et = findViewById(R.id.calendar_event_details_auto_complete_txt);

        field_list = new ArrayList<>();
        field_list.add("None");
        autoCompleteTxt = findViewById(R.id.calendar_event_details_auto_complete_txt);


        save_btn = findViewById(R.id.calendar_event_details_Save_btn);
        cancel_btn = findViewById(R.id.calendar_event_details_Cancel_btn);
        delete_btn = findViewById(R.id.calendar_event_details_DeleteEvent_btn);
        datePicker_btn = findViewById(R.id.calendar_event_details_DatePicker_btn);
        datePicker_btn.setText(clickedDate_extra);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_event_details);
        init();
        initDatePicker();

        // If we came from animals or crops, lock the field selection because it will be already selected
        if (keyExtra!=null){
            field_til.setEnabled(false);
        }

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users/"+userUid+"/Events");


        // Get list of fields from database and store them in field_list array list for dropdown menu
        ref_fieldList = database.getReference("Users/"+userUid+"/Animals-Crops");
        ref_fieldList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    field_list.add(ds.child("Name").getValue().toString());
                }
                adapterItems.notifyDataSetChanged();
                System.out.println("fieldlist: "+field_list);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Define dropdown menu using values from field_lists
        adapterItems = new ArrayAdapter<String>(this,R.layout.dropdown_menu, field_list);
        autoCompleteTxt.setAdapter(adapterItems);


        // Get the key and store (this part is a mess, clean the code!)
        // Update the dropdown menu's hint
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    // Get the key of an event with given date and task value
                    // These extra values are taken from the listView in the calendar.class
                    if (ds.getKey().equals(clickedEventKey_extra)){
                        // Store the event key to be able to use while updating the database
                        eventKey_str = ds.getKey();
                        // Default hintText for dropdow is "Select Field"
                        // If hintText for dropdown is default string, change it to the field from selected event
                        if (field_til.getHint().toString().equals("Select Field")){
                            field_til.setHint(ds.child("Field").getValue().toString());
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        save_btn.setOnClickListener(v -> {
            HashMap map = new HashMap();
            // Update Field, Type and FieldKey only if new field is selected from spinner
            if (!field_et.getText().toString().isEmpty()){
                System.out.println("aaaaaaaaaaaaaaaaaaaa");
                map.put("Field", field_et.getText().toString());

                // Check TYPE of selected field to be able to update it correctly
                // Get FieldKey and update it
                ref_fieldList = database.getReference("Users/"+userUid+"/Animals-Crops");
                ref_fieldList.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            // If selected field is defined in Animals-Crops node
                            if(ds.child("Name").getValue().toString().equals(field_et.getText().toString())){
                                // Get TYPE and Key of that field
                                newType_str = ds.child("TYPE").getValue().toString();
                                clickedEventKey_extra = ds.getKey();
                            }
                            // If selected field is found in Animal-Crops, update database for new values
                            // Else, update it as "None"
                            HashMap map1 = new HashMap();
                            if (newType_str!=null){
                                map1.put("Type",newType_str);
                            }else {
                                map1.put("Type","None");
                            }
                            if (clickedEventKey_extra!=null){
                                map1.put("FieldKey",clickedEventKey_extra);
                            }else {
                                map1.put("FieldKey","None");
                            }
                            // Use event_Key_str to update selected event
                            ref = database.getReference("Users/"+userUid+"/Events");
                            ref.child(eventKey_str).updateChildren(map1);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            // Update task if task value is changed
            if (!clickedTask_extra.equals(task_et.getText().toString())){
                map.put("Task",task_et.getText().toString());
            }

            // Update Date and Date dd-MM-yyyy if date has been changed
            if (date_dd_MM_yyyy_str != null){
                // Convert dd-MM-yyyy date to milliseconds
                String myDate = date_dd_MM_yyyy_str;
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date date = null;
                try {
                    date = sdf.parse(myDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long millis = date.getTime();

                map.put("Date",millis);
                map.put("Date dd-MM-yyyy",date_dd_MM_yyyy_str);

            }

            //update the database using eventKey_str to update selected event
            ref.child(eventKey_str).updateChildren(map);
            Intent intent;

            // If where_extra!=null, it means we came from somewhere else than Calendar
            // Find where we came from using where_extra and go back to there
            if (where_extra!=null){
                if (where_extra.equals("animalsToDo")){
                    intent = new Intent(CalendarEventDetails.this, AnimalsToDo.class);
                    intent.putExtra("key",keyExtra);
                    startActivity(intent);
                }
            }else {
                intent = new Intent(CalendarEventDetails.this, Calendar.class);
                startActivity(intent);
            }

        });

        cancel_btn.setOnClickListener(v -> {
            Intent intent;
            // If where_extra!=null, it means we came from somewhere else than Calendar
            // Find where we came from using where_extra and go back to there
            if (where_extra!=null){
                if (where_extra.equals("animalsToDo")){
                    intent = new Intent(CalendarEventDetails.this, AnimalsToDo.class);
                    intent.putExtra("key",keyExtra);
                    startActivity(intent);
                }
            }else {
                intent = new Intent(CalendarEventDetails.this, Calendar.class);
                startActivity(intent);
            }
        });

        delete_btn.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference().child("Users/"+userUid+"/Events").child(eventKey_str).removeValue();
            Intent intent;
            // If where_extra!=null, it means we came from somewhere else than Calendar
            // Find where we came from using where_extra and go back to there
            if (where_extra!=null){
                if (where_extra.equals("animalsToDo")){
                    intent = new Intent(CalendarEventDetails.this, AnimalsToDo.class);
                    intent.putExtra("key",keyExtra);
                    startActivity(intent);
                }
            }else {
                intent = new Intent(CalendarEventDetails.this, Calendar.class);
                startActivity(intent);
            }

        });
    }


    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                // Increase month by 1 because january equals to 0
                month = month + 1;
                // Format date to JAN-9-2022 style to update datePicker_btn text
                date_jan_dd_yyyy_str = makeDateString(year,month,day);
                datePicker_btn.setText(date_jan_dd_yyyy_str);
                // Format date to dd_MM_yyyy to be able to check differences between dates
                // It is in same format as dates in database
                date_dd_MM_yyyy_str = makeDateString_dd_MM_yyyy(year,month,day);
            }
        };

        // Split the date for displaying current events date in datePicker_btn
        String[] tokens = clickedDate_extra.split("-");
        int year = Integer.parseInt(tokens[2]);
        int month = Integer.parseInt(tokens[1])-1;
        int day = Integer.parseInt(tokens[0]);
        int style = AlertDialog.THEME_HOLO_LIGHT;
        // Update DatePicker with new values
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
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

    // Convert given int month value to str monht value
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