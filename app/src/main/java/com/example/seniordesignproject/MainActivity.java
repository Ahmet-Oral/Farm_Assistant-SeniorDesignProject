package com.example.seniordesignproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private Button btn_weather, btn_agenda, btn_crops, btn_animals, btn_general_information, btn_settings;
    FirebaseAuth auth;

    public void init(){
        btn_weather = (Button) findViewById(R.id.main_Weather_btn);
        btn_agenda = (Button) findViewById(R.id.main_Agenda_btn);
        btn_crops = (Button) findViewById(R.id.main_Crops_btn);
        btn_animals = (Button) findViewById(R.id.main_Animals_btn);
        btn_general_information = (Button) findViewById(R.id.main_GeneralInfo_btn);
        btn_settings = (Button) findViewById(R.id.main_Settings_btn);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(MainActivity.this, Register.class));
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        btn_weather.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, WeatherForecast.class));});
        btn_agenda.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Agenda.class));});
        btn_crops.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Crops.class)); });
        btn_animals.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Animals.class)); });
        btn_general_information.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, General_Information.class)); });
        btn_settings.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Settings.class)); });

    }
}
