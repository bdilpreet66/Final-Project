package com.mad3125.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mad3125.finalproject.adapters.commentAdapter;
import com.mad3125.finalproject.adapters.restaurantAdapter;
import com.mad3125.finalproject.data.Comment;
import com.mad3125.finalproject.data.Restaturant;

import java.util.HashMap;
import java.util.Map;

public class viewLocation extends AppCompatActivity {
    TextView description;
    ImageView img1,img2,img3;
    EditText rating, comment;
    FirebaseFirestore firestore;
    FirebaseAuth fba = FirebaseAuth.getInstance();
    String resID;
    RecyclerView recyclerView;
    commentAdapter ca;

    public void submit_comment(View view){
        if(validateData(Double.valueOf(rating.getText().toString()))){
            Map<String,Object> comments = new HashMap<>();
            comments.put("rating", rating.getText().toString());
            comments.put("desc", comment.getText().toString());
            comments.put("by", fba.getCurrentUser().getEmail());
            comments.put("ref", resID);

            firestore.collection("comments").add(comments).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(viewLocation.this, "Comment Added!", Toast.LENGTH_SHORT).show();
                        rating.setText("0.0");
                        comment.setText("");
                        ca.notifyDataSetChanged();
                    } else {
                        Toast.makeText(viewLocation.this, "Error While Posting a comment!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void logout(View view){
        FirebaseAuth fba = FirebaseAuth.getInstance();
        fba.signOut();
        startActivity(new Intent(viewLocation.this,MainActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_location);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null){
            startActivity(new Intent(viewLocation.this,MainActivity.class));
            finish();
        }
        firestore = FirebaseFirestore.getInstance();

        Bundle bundle = this.getIntent().getExtras();
        getSupportActionBar().setTitle(bundle.getString("title"));
        description = (TextView) findViewById(R.id.desc);
        img1 = (ImageView) findViewById(R.id.image1);
        img2 = (ImageView) findViewById(R.id.image2);
        img3 = (ImageView) findViewById(R.id.image3);
        rating = (EditText) findViewById(R.id.editTextNumberDecimal2);
        comment = (EditText) findViewById(R.id.comment);
        resID = bundle.getString("ref");

        description.setText(bundle.getString("desc"));
        Glide.with(this).load(bundle.getString("img1")).into(img1);
        Glide.with(this).load(bundle.getString("img2")).into(img2);
        Glide.with(this).load(bundle.getString("img3")).into(img3);


        Button map = findViewById(R.id.view_map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewMap = new Intent(viewLocation.this,gMap.class);
                Bundle basket = new Bundle();
                basket.putString("title",bundle.getString("title"));
                basket.putString("lat",bundle.getString("latitude"));
                basket.putString("lng",bundle.getString("longitude"));
                viewMap.putExtras(basket);
                startActivity(viewMap);
            }
        });

        recyclerView = findViewById(R.id.commentList);
        setupRecyclerView();

    }

    void setupRecyclerView(){
        Map<String,Object> q = new HashMap<>();
        q.put("ref",resID);
        Query query = FirebaseFirestore.getInstance().collection("comments").whereEqualTo("ref",resID.toString());
        Log.d("test_query", "setupRecyclerView: "+query);
        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>().setQuery(query,Comment.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ca = new commentAdapter(options, this);
        recyclerView.setAdapter(ca);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ca.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ca.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ca.notifyDataSetChanged();
    }

    boolean validateData(Double r) {
        if ( !((Double.compare(r, Double.valueOf(-1.0)) > 0) && (Double.compare(Double.valueOf(5.1), r) >0 ))) {
            rating.setError("Rating must be between 0 and 5!");
            return false;
        }
        return true;
    }
}