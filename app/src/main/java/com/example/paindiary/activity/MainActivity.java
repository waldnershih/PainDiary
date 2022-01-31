package com.example.paindiary.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.paindiary.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        auth = FirebaseAuth.getInstance();


        String userEmail = getUserLogin();

        if (userEmail != null && !userEmail.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        }

        mBinding.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_txt = mBinding.editEmailAddress.getEditText().getText().toString();
                String password_txt = mBinding.editPassword.getEditText().getText().toString();

                if (email_txt.isEmpty() || password_txt.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Email and password cannot be empty!", Toast.LENGTH_SHORT).show();
                } else {
                    auth.signInWithEmailAndPassword(email_txt, password_txt).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                setUserLogin(email_txt);
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this, "Incorrect email or password!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        mBinding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private String getUserLogin() {
        SharedPreferences sharedPreferences = this.getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        return sharedPreferences.getString("Login",null);
    }

    private void setUserLogin(String email_txt) {
        SharedPreferences sharedPref = this.getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putString("Login", email_txt);
        spEditor.apply();
    }
}