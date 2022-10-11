package com.mad3125.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    EditText email;
    EditText password;

    public void gotToRegister(View view){
        Intent register_page = new Intent(this, Register.class);
        startActivity(register_page);
    }

    public void login(View view){
        if (validateData(email.getText().toString(), password.getText().toString())){
            loginFirebaseAccount(email.getText().toString(), password.getText().toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();


        email = findViewById(R.id.editTextTextPersonName);
        password = findViewById(R.id.editTextTextPassword);
    }

    void loginFirebaseAccount(String e, String p){
        FirebaseAuth fba = FirebaseAuth.getInstance();
        fba.signInWithEmailAndPassword(e,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if (fba.getCurrentUser().isEmailVerified()){
                        Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        Intent list_page = new Intent(MainActivity.this, RecordList.class);
                        startActivity(list_page);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Please Check your emails to verify the email address", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    boolean validateData(String e, String p){
        if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
            email.setError("Please Enter a Valid Email Address!");
            return false;
        }
        if (p.length() < 8) {
            password.setError("Password too Short!");
            return false;
        }
        return true;
    }
}