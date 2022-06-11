package com.example.seniordesignproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Calendar extends AppCompatActivity {
    private CompactCalendarView compactCalendar;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());
    private SimpleDateFormat dateFormatDay = new SimpleDateFormat("dd-MM-yyyy");
    private List eventDatesList = new ArrayList<String>();
    private List eventInfoList = new ArrayList<String>();

    private ArrayList<Calendar_Event_obj> eventObj_list;

    private ArrayList<String> database_events_dates,database_events_tasks, database_events_types, database_events_keys, database_events_fields;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private Button new_btn;
    private String dateC, todaysDate, day_str, month_str;
    private List<Event> events;

    public void init(){
        Toolbar toolbar = findViewById(R.id.toolbar_calendar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String currentDate = dateFormatMonth.format(new Date());
        getSupportActionBar().setTitle(currentDate);
        //same one as used in crops (crop_info activity)

        listView = findViewById(R.id.calendar_ListView);
        new_btn = findViewById(R.id.calendar_New_btn);
        database_events_tasks = new ArrayList<>();
        database_events_dates = new ArrayList<>();
        database_events_types = new ArrayList<>();
        database_events_keys = new ArrayList<>();
        database_events_fields = new ArrayList<>();
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users/"+userUid+"/Events");

        eventObj_list = new ArrayList<>();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        init();
        compactCalendar = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);



        // Using same adapter view as we used in animals_todo
        Calendar_Event_Adapter adapter = new Calendar_Event_Adapter(this, R.layout.todo_adapter_view, eventObj_list);


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear lists to avoid duplicates
                database_events_tasks.clear();
                database_events_dates.clear();
                database_events_types.clear();
                database_events_keys.clear();
                database_events_fields.clear();
                eventDatesList.clear();
                System.out.println("snapshot: "+snapshot);
                for(DataSnapshot ds: snapshot.getChildren()){
                    //Store values for all events in database
                    database_events_tasks.add(ds.child("Task").getValue().toString());
                    database_events_dates.add(ds.child("Date").getValue().toString());
                    database_events_types.add(ds.child("Type").getValue().toString());
                    database_events_fields.add(ds.child("Field").getValue().toString());
                    database_events_keys.add(ds.getKey());
                    eventDatesList.add(ds.child("Date dd-MM-yyyy").getValue().toString());

                }
                // Create events with values taken from the database
                for (int i = 0; i < database_events_dates.size(); i++){
                    Long newEventDate = Long.parseLong(database_events_dates.get(i));
                    String newEventTask = database_events_tasks.get(i);
                    //System.out.println("date-task " + newEventDate+" - "+newEventTask);
                    if (database_events_types.get(i).equals("Animal")){
                        int colorCalendarYellow = ContextCompat.getColor(Calendar.this, R.color.colorCalendarYellow);
                        compactCalendar.addEvent(new Event(colorCalendarYellow,newEventDate,newEventTask));
                    }else if(database_events_types.get(i).equals("Crop")){
                        int colorCalendarGreen = ContextCompat.getColor(Calendar.this, R.color.colorCalendarGreen);
                        compactCalendar.addEvent(new Event(colorCalendarGreen,newEventDate,newEventTask));
                    }else{
                        int colorCalendarRed = ContextCompat.getColor(Calendar.this, R.color.colorCalendarRed);
                        compactCalendar.addEvent(new Event(colorCalendarRed,newEventDate,newEventTask));
                    }
                }

                // To get today's date and list the events of today because listing events don't trigger without clicking to calendar
                Date date = new Date();
                todaysDate = dateFormatDay.format(date);
                for (int i = 0; i < eventDatesList.size(); i++){
                    if(eventDatesList.get(i).equals(todaysDate)){
                        eventInfoList.add(database_events_tasks.get(i));
                        // Create eventObjects for the listView
                        eventObj_list.add(new Calendar_Event_obj(database_events_tasks.get(i),database_events_fields.get(i),database_events_keys.get(i)));
                    }
                    adapter.notifyDataSetChanged();
                }
                // Display events
                listView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                // Clear info list
                eventInfoList.clear();
                eventObj_list.clear();

                // Date Clicked - dateC = str
                dateC = dateFormatDay.format(dateClicked);

                // Check if clicked date has an event in it. If so, create new eventObject and add it to eventObj_list for the listView
                for (int i = 0; i < eventDatesList.size(); i++){
                    if(eventDatesList.get(i).equals(dateC)){
                        // Also update event info list (but this list should not be used, clean the code)
                        eventInfoList.add(database_events_tasks.get(i));

                        // Create eventObjects for the listView
                        eventObj_list.add(new Calendar_Event_obj(database_events_tasks.get(i),database_events_fields.get(i),database_events_keys.get(i)));
                    }
                    adapter.notifyDataSetChanged();
                }
                // Display events
                listView.setAdapter(adapter);

            }
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                getSupportActionBar().setTitle(dateFormatMonth.format(firstDayOfNewMonth));
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Calendar.this, CalendarEventDetails.class);

                // If user does click todays event without selecting date, dateC will not be initialized
                // So if dateC is null, it means user clicked todays event and dateC should be todaysDate
                if (dateC==null){
                    intent.putExtra("ClickedDate",todaysDate);

                }else{
                    intent.putExtra("ClickedDate",dateC);

                }
                intent.putExtra("ClickedTask", eventInfoList.get(position).toString());
                intent.putExtra("ClickedEventKey",eventObj_list.get(position).getEvent_obj_key());
                startActivity(intent);
            }
        });


        new_btn.setOnClickListener(v -> {
            Intent intent = new Intent(Calendar.this, CalendarNewEvent.class);
            intent.putExtra("date",dateC);
            startActivity(intent);

        });

    }
}