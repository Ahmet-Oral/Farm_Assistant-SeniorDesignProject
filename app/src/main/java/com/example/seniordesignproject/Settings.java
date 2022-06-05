package com.example.seniordesignproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class Settings extends AppCompatActivity {
    private Button btn_signout;
    FirebaseAuth auth;
    private TextView tv_userUid;

    public void init(){
        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_signout = (Button) findViewById(R.id.settings_signout_btn);
        auth = FirebaseAuth.getInstance();
        tv_userUid = (TextView) findViewById(R.id.settings_userUid_tv);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);



        init();
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        tv_userUid.setText("Currend user uid:" + userUid);



        btn_signout.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(Settings.this, Register.class));});
    }
}