package com.example.seniordesignproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    private  Button btn_createAcc;
    private EditText et_name, et_register_email, et_register_password;
    private TextView et_alreadyHaveAcc;
    private FirebaseAuth auth;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;

    public void init(){
        btn_createAcc = (Button) findViewById(R.id.register_createaccount_btn);
        et_alreadyHaveAcc = (TextView) findViewById(R.id.register_alreadyhaveaccount_txtview);
        et_name = (EditText) findViewById(R.id.register_name_et);
        et_register_email = (EditText) findViewById(R.id.register_email_et);
        et_register_password = (EditText) findViewById(R.id.register_password_et);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();

        et_alreadyHaveAcc.setOnClickListener(v -> startActivity(new Intent(Register.this, Login.class)));
        btn_createAcc.setOnClickListener(v -> createUser());

    }
    private void createUser(){
        auth = FirebaseAuth.getInstance();
        String name = et_name.getText().toString();
        String email = et_register_email.getText().toString();
        String password = et_register_password.getText().toString();
        if (TextUtils.isEmpty(email)){
            et_register_email.setError("Email cannot be empty!");
            et_register_email.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            et_register_password.setError("Password cannot be empty!");
            et_register_password.requestFocus();
        }else{
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(Register.this, "User Registered Successfully!", Toast.LENGTH_SHORT).show();

                        //if registration is successful -> login and save user to database
                        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(Register.this, "Logged In!", Toast.LENGTH_SHORT).show();
                                    String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    rootNode = FirebaseDatabase.getInstance();
                                    reference = rootNode.getReference("Users"); // can also be used to create a table
                                    //adding table continents
                                    HashMap map = new HashMap();
                                    map.put("Name", name);
                                    map.put("E-mail", email);
                                    map.put("Password", password);
                                    //update the database
                                    reference.child(userUid+"/User Details").updateChildren(map);
                                    //go to main if all is successful
                                    startActivity(new Intent(Register.this, MainActivity.class));
                                }else{
                                    Toast.makeText(Register.this, "Login Failed!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        Toast.makeText(Register.this, "Registration Failed!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
            });
    }
    }

    }
