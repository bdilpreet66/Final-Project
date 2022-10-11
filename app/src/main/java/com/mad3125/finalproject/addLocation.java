package com.mad3125.finalproject;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class addLocation extends AppCompatActivity {
    EditText title, description;
    ImageButton img1, img2, img3;
    String img_name1, img_name2, img_name3;
    String longitude = "0.0";
    String latitude = "0.0";

    ActivityResultLauncher<Intent> mTakePhoto1, mTakePhoto2, mTakePhoto3;
    Uri cam_uri;
    FirebaseFirestore firestore;
    FirebaseAuth fba = FirebaseAuth.getInstance();
    StorageReference sr;
    ProgressDialog pd;

    public void saveData(View view) {
        if (validateData(title.getText().toString(), description.getText().toString())){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
            Date now = new Date();
            String filename = formatter.format(now);
            Map<String,Object> location = new HashMap<>();
            location.put("title", title.getText().toString());
            location.put("desc", description.getText().toString());
            location.put("longitude", longitude);
            location.put("latitude", latitude);
            location.put("img1", img_name1);
            location.put("img2", img_name2);
            location.put("img3", img_name3);
            location.put("by", fba.getCurrentUser().getEmail());
            location.put("ref", filename+"_doc_ref");

            firestore.collection("restaurants").add(location).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(addLocation.this, "The Restaturent added Successfully!", Toast.LENGTH_SHORT).show();
                        Intent records = new Intent(addLocation.this, RecordList.class);
                        startActivity(records);
                    } else {
                        Toast.makeText(addLocation.this, "Failed While Adding the restaurant!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void logout(View view) {
        fba.signOut();
        startActivity(new Intent(addLocation.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        getSupportActionBar().hide();
        firestore = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(addLocation.this, MainActivity.class));
            finish();
        }

        Context c = this;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            latitude = "" + location.getLatitude();
                            longitude = "" + location.getLongitude();
                        }
                    }
                });
        }

        title = (EditText) findViewById(R.id.editTextTextPersonName2);
        description = (EditText) findViewById(R.id.editTextTextMultiLine);

        img1 = (ImageButton) findViewById(R.id.imageButton);
        img2 = (ImageButton) findViewById(R.id.imageButton2);
        img3 = (ImageButton) findViewById(R.id.imageButton3);

        mTakePhoto1 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            pd = new ProgressDialog(c);
                            pd.setTitle("Uploading Image");
                            pd.show();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
                            Date now = new Date();
                            String filename = formatter.format(now);
                            img_name1 = "images/"+filename;
                            sr = FirebaseStorage.getInstance().getReference(img_name1);
                            sr.putFile(cam_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    FirebaseStorage.getInstance().getReference().child(img_name1).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            // Got the download URL for 'users/me/profile.png'
                                            img_name1 = uri.toString();
                                            if (pd.isShowing()){
                                                pd.dismiss();
                                            }
                                            img1.setImageURI(cam_uri);
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if (pd.isShowing()){
                                        pd.dismiss();
                                    }
                                    Toast.makeText(c, "Failed To Upload Image", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
                cam_uri = c.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cam_uri);
                mTakePhoto1.launch(cameraIntent);
            }
        });

        mTakePhoto2 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {

                            pd = new ProgressDialog(c);
                            pd.setTitle("Uploading Image");
                            pd.show();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
                            Date now = new Date();
                            String filename = formatter.format(now);
                            img_name2 = "images/"+filename;
                            sr = FirebaseStorage.getInstance().getReference(img_name2);
                            sr.putFile(cam_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    FirebaseStorage.getInstance().getReference().child(img_name2).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            img_name2 = uri.toString();
                                            if (pd.isShowing()){
                                                pd.dismiss();
                                            }
                                            img2.setImageURI(cam_uri);
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if (pd.isShowing()){
                                        pd.dismiss();
                                    }
                                    Toast.makeText(c, "Failed To Upload Image", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });

        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
                cam_uri = c.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cam_uri);
                mTakePhoto2.launch(cameraIntent);
            }
        });

        mTakePhoto3 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {

                            pd = new ProgressDialog(c);
                            pd.setTitle("Uploading Image");
                            pd.show();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
                            Date now = new Date();
                            String filename = formatter.format(now);
                            img_name3 = "images/" + filename;
                            sr = FirebaseStorage.getInstance().getReference(img_name3);
                            sr.putFile(cam_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    FirebaseStorage.getInstance().getReference().child(img_name3).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            // Got the download URL for 'users/me/profile.png'
                                            img_name3 = uri.toString();
                                            if (pd.isShowing()){
                                                pd.dismiss();
                                            }
                                            img3.setImageURI(cam_uri);
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if (pd.isShowing()){
                                        pd.dismiss();
                                    }
                                    Toast.makeText(c, "Failed To Upload Image", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });

        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
                cam_uri = c.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cam_uri);
                mTakePhoto3.launch(cameraIntent);
            }
        });
    }

    boolean validateData(String t, String desc) {
        if (t.length() < 1) {
            title.setError("Title is required!");
            return false;
        }
        if (desc.length() < 1) {
            description.setError("Description is required!");
            return false;
        }
        if (img_name1.length() < 1) {
            description.setError("Please Select an image!");
            return false;
        }
        return true;
    }
}