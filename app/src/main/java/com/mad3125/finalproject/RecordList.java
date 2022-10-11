package com.mad3125.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mad3125.finalproject.adapters.restaurantAdapter;
import com.mad3125.finalproject.data.Restaturant;

public class RecordList extends AppCompatActivity {
    RecyclerView recyclerView;
    restaurantAdapter ra;

    public void logout(View view){
        FirebaseAuth fba = FirebaseAuth.getInstance();
        fba.signOut();
        startActivity(new Intent(RecordList.this,MainActivity.class));
        finish();
    }

    public void addLocation(View view){
        startActivity(new Intent(RecordList.this,addLocation.class));
    }

    public void viewLocation(Restaturant model){
        Intent viewRestaurant = new Intent(RecordList.this,viewLocation.class);
        Bundle basket = new Bundle();
        basket.putString("ref",model.ref);
        basket.putString("title",model.title);
        basket.putString("desc",model.desc);
        basket.putString("longitude",model.longitude);
        basket.putString("latitude",model.latitude);
        basket.putString("img1",model.img1);
        basket.putString("img2",model.img2);
        basket.putString("by",model.by);
        basket.putString("img3",model.img3);
        viewRestaurant.putExtras(basket);
        startActivity(viewRestaurant);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);
        getSupportActionBar().hide();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null){
            startActivity(new Intent(RecordList.this,MainActivity.class));
            finish();
        }

        recyclerView = findViewById(R.id.iesavedList);
        Log.d("test_data", "setupRecyclerView: test");
        setupRecyclerView();
    }

    void setupRecyclerView(){
        Query query = FirebaseFirestore.getInstance().collectionGroup("restaurants");
        Log.d("test_data", "setupRecyclerView: " + query);
        FirestoreRecyclerOptions<Restaturant> options = new FirestoreRecyclerOptions.Builder<Restaturant>().setQuery(query,Restaturant.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ra = new restaurantAdapter(options, this, RecordList.this);
        recyclerView.setAdapter(ra);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ra.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ra.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ra.notifyDataSetChanged();
    }
}