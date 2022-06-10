package com.example.seniordesignproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private Button btn_login;
    private EditText et_login_email, et_login_password;
    private TextView tv_forgetpasswd;
    private FirebaseAuth auth;


    public void init(){
        btn_login = (Button) findViewById(R.id.login_login_btn);
        auth = FirebaseAuth.getInstance();
        tv_forgetpasswd = (TextView) findViewById(R.id.login_forgetpasswprd_txtview);
        et_login_email = (EditText) findViewById(R.id.login_email_et);
        et_login_password = (EditText) findViewById(R.id.login_password_et);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

        Toolbar toolbar = findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Reset maili ekle
        // tv_forgetpasswd.setOnClickListener(v -> startActivity(new Intent(Login.this,)));

        btn_login.setOnClickListener(v -> {
            loginUser(); });

    }
    private void loginUser(){
        String email = et_login_email.getText().toString();
        String password = et_login_password.getText().toString();
            if (TextUtils.isEmpty(email)){
            et_login_email.setError("Email cannot be empty!");
            et_login_email.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            et_login_password.setError("Password cannot be empty!");
            et_login_password.requestFocus();
    }else{
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Logged In!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, MainActivity.class));
                        }else{
                            Toast.makeText(Login.this, "Login Failed!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
    }
}