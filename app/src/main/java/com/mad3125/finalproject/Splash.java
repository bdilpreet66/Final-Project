package com.mad3125.finalproject;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash extends AppCompatActivity {

    ActivityResultLauncher<String> camPerms = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if(!result) {
                Toast.makeText(Splash.this, "Permissions not granted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                fine_location.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
    });

    ActivityResultLauncher<String> fine_location = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if(!result) {
                Toast.makeText(Splash.this, "Permissions not granted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                coarse_location.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
        }
    });

    ActivityResultLauncher<String> coarse_location = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if(!result) {
                Toast.makeText(Splash.this, "Permissions not granted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                internet.launch(Manifest.permission.INTERNET);
            }
        }
    });

    ActivityResultLauncher<String> internet = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if(!result) {
                Toast.makeText(Splash.this, "Permissions not granted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser == null){
                            startActivity(new Intent(Splash.this,MainActivity.class));
                        } else {
                            startActivity(new Intent(Splash.this,RecordList.class));
                        }
                        finish();
                    }
                }, 2000);
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        camPerms.launch(Manifest.permission.CAMERA);
    }
}