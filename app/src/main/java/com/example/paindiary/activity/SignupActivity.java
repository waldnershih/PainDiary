package com.example.paindiary.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.paindiary.converter.DateStringConverter;
import com.example.paindiary.databinding.ActivitySignupBinding;
import com.example.paindiary.entity.PainRecord;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private ActivitySignupBinding sBinding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sBinding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(sBinding.getRoot());

        final Intent intent = getIntent();
        auth = FirebaseAuth.getInstance();



        sBinding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email_txt =  sBinding.editEmail.getEditText().getText().toString();
                String password_txt = sBinding.editPassword.getEditText().getText().toString();
                String password2_txt = sBinding.confirmPassword.getEditText().getText().toString();

                if (checkSignUpDetail(email_txt, password_txt, password2_txt)) {
                    auth.createUserWithEmailAndPassword(email_txt, password_txt)
                            .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        intent.setClass(SignupActivity.this, MainActivity.class);
                                        setUserLogin(email_txt);
                                        startActivity(intent);
                                    }else {
                                        Toast.makeText(SignupActivity.this, "Invalid email or password form!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        sBinding.returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.setClass(SignupActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public boolean checkSignUpDetail(String email_txt, String password_txt, String password2_txt) {
        if (email_txt.isEmpty() || password_txt.isEmpty() || password_txt.isEmpty()) {
            Toast.makeText(SignupActivity.this, "Email and password cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password_txt.length() < 6) {
            Toast.makeText(SignupActivity.this, "Password must be at least 6 characters!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password_txt.equals(password2_txt)) {
            Toast.makeText(SignupActivity.this, "Password and Confirmed Password need to be matched!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String getUserLogin() {
        SharedPreferences sharedPref = this.getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        return sharedPref.getString("Login", null);
    }

    private void setUserLogin(String email_txt) {
        SharedPreferences sharedPref = this.getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putString("Login", email_txt);
        spEditor.apply();
    }
}