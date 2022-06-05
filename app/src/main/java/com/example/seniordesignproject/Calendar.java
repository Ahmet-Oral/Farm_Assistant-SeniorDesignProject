package com.example.seniordesignproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Calendar extends AppCompatActivity {
    CompactCalendarView compactCalendar;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());
    private SimpleDateFormat dateFormatDay = new SimpleDateFormat("yyyy-MM-dd");
    List eventDatesList = new ArrayList<String>();
    List eventInfoList = new ArrayList<String>();
    ArrayList<String> database_events_dates;
    ArrayList<String> database_events_tasks;
    FirebaseDatabase database;
    DatabaseReference ref;
    ArrayAdapter<String> adapter;
    ListView listView;
    Button new_btn;
    String dateC;
    List<Event> events;

    public void init(){
        Toolbar toolbar = findViewById(R.id.toolbar_crops);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String currentDate = dateFormatMonth.format(new Date());
        getSupportActionBar().setTitle(currentDate);
        //same one as used in crops (crop_info activity)
        adapter = new ArrayAdapter<String>(Calendar.this,R.layout.crop_info,R.id.cropInfo,eventInfoList);
        listView = findViewById(R.id.calendar_listview);
        new_btn = findViewById(R.id.calendar_new_btn);
        database = FirebaseDatabase.getInstance();
        database_events_tasks = new ArrayList<>();
        database_events_dates = new ArrayList<>();
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref = database.getReference("Users/"+userUid+"/Events");
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        init();
        compactCalendar = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    //Store dates for all events in database
                    database_events_tasks.add(ds.child("Task").getValue().toString());
                    database_events_dates.add(ds.child("Date").getValue().toString());
                                    }
                for (int i = 0; i < database_events_dates.size(); i++){
                    Long newEventDate = Long.parseLong(database_events_dates.get(i));
                    String newEventTask = database_events_tasks.get(i);
                    System.out.println("date-task " + newEventDate+" - "+newEventTask);

                    compactCalendar.addEvent(new Event(Color.RED,newEventDate,newEventTask));
                }
                events = compactCalendar.getEventsForMonth(1653349643000L);
                System.out.println("events from db1: " + events);
                for (int i = 0; i < events.size(); i++){
                    String eventDate = dateFormatDay.format(events.get(i).getTimeInMillis());
                    eventDatesList.add(eventDate);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                //clear info list
                eventInfoList.clear();

                //Date Clicked
                dateC = dateFormatDay.format(dateClicked);

                //Check if clicked date has an event in it. If so, add event info to arr list
                for (int i = 0; i < eventDatesList.size(); i++){
                    if(eventDatesList.get(i).equals(dateC)){
                        eventInfoList.add(events.get(i).getData());
                    }
                }
                //Display events
                listView.setAdapter(adapter);

            }
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                getSupportActionBar().setTitle(dateFormatMonth.format(firstDayOfNewMonth));

            }
        });

        new_btn.setOnClickListener(v -> {
            if (dateC == null) {
                Toast.makeText(Calendar.this, "Please Select a Date!" , Toast.LENGTH_SHORT).show();
            }else {
                Intent intent = new Intent(Calendar.this, NewCalendarEvent.class);
                intent.putExtra("date",dateC);
                startActivity(intent);

            }
        });

    }
}