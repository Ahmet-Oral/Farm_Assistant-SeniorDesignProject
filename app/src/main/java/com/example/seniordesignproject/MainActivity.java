package com.example.seniordesignproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Button btn_agenda, btn_crops, btn_animals, btn_settings;
    private TextView welcome_tv, temperature_tv, date_tv;
    private ImageButton btn_weather;
    private SimpleDateFormat dateFormatDay = new SimpleDateFormat("dd.MM.yyyy");
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference ref;



    public void init(){
        btn_weather = (ImageButton) findViewById(R.id.main_Weather_btn);
        btn_agenda = (Button) findViewById(R.id.main_Agenda_btn);
        btn_crops = (Button) findViewById(R.id.main_Crops_btn);
        btn_animals = (Button) findViewById(R.id.main_Animals_btn);
        btn_settings = (Button) findViewById(R.id.main_Settings_btn);
        auth = FirebaseAuth.getInstance();
        welcome_tv = findViewById(R.id.main_Welcome_tv);
        temperature_tv = findViewById(R.id.main_Temperature_tv);
        date_tv = findViewById(R.id.main_Date_tv);






    }

    @Override
    protected void onStart() {
        super.onStart();
        // Disable night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        FirebaseUser user = auth.getCurrentUser();
        System.out.println("1");
        if(user == null){
            startActivity(new Intent(MainActivity.this, Register.class));
        }


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("2");
        init();
        // Get weather to display
        FindWeather();
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users/"+userUid+"/User Details");






        // Get users name for the welcome text
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                welcome_tv.setText("Welcome "+ snapshot.child("Name").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        btn_weather.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, WeatherForecast.class));});
        btn_agenda.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Agenda.class));});
        btn_crops.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Crops.class)); });
        btn_animals.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Animals.class)); });
        btn_settings.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Settings.class)); });




    }

    public void FindWeather()
    {
        final String city = "Mugla";
        String url ="https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=035059e14913707c7da6334071b65b61&units=metric";
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //find temperature
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject object = jsonObject.getJSONObject("main");
                            double temp = object.getDouble("temp");
                            temperature_tv.setText(temp+"°C");
                            System.out.println("Json: "+jsonObject);

                            Date date = new Date();
                            date_tv.setText(dateFormatDay.format(date));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }
}
