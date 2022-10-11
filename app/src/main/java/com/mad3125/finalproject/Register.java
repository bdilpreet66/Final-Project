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


public class Register extends AppCompatActivity {
    EditText email;
    EditText password;


    public void register(View view){
        if (validateData(email.getText().toString(), password.getText().toString())){
            createFirebaseAccount(email.getText().toString(), password.getText().toString());
        }
    }

    public void gotToLogin(View view){
        Intent login_page = new Intent(this, MainActivity.class);
        startActivity(login_page);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        email = findViewById(R.id.editTextTextPersonName);
        password = findViewById(R.id.editTextTextPassword);
    }

    boolean validateData(String e, String p){
        if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
            email.setError("Please Enter a Valid Email Address!");
            return false;
        }
        if (p.length() < 8) {
            password.setError("Password Must be atleast 8 letters!");
            return false;
        }
        return true;
    }

    void createFirebaseAccount(String e, String p) {
        FirebaseAuth fba = FirebaseAuth.getInstance();
        fba.createUserWithEmailAndPassword(e,p).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Register.this, "The User Created Successfully", Toast.LENGTH_SHORT).show();
                    fba.getCurrentUser().sendEmailVerification();
                    fba.signOut();
                    Intent login_page = new Intent(Register.this, MainActivity.class);
                    startActivity(login_page);
                    finish();
                } else {
                    Toast.makeText(Register.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

//    FirebaseFirestore firestore;
//
//    public void register(View view){
//        if (validateData(email.getText().toString(), password.getText().toString())){
//            Map<String,Object> users = new HashMap<>();
//            users.put("Email", email.getText().toString());
//            users.put("Password", password.getText().toString());
//
//            firestore.collection("users").add(users).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                @Override
//                public void onSuccess(DocumentReference documentReference) {
//                    Toast.makeText(Register.this, "The User Created Successfully", Toast.LENGTH_SHORT).show();
//                    Intent login_page = new Intent(Register.this, MainActivity.class);
//                    startActivity(login_page);
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(Register.this, "The User Creation Failed", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register);
//
//        firestore = FirebaseFirestore.getInstance();
//        email = findViewById(R.id.editTextTextPersonName);
//        password = findViewById(R.id.editTextTextPassword);
//    }