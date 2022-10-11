package com.mad3125.finalproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.mad3125.finalproject.R;
import com.mad3125.finalproject.RecordList;
import com.mad3125.finalproject.data.Restaturant;

public class restaurantAdapter extends FirestoreRecyclerAdapter<Restaturant, restaurantAdapter.restaurantViewHolder> {
    Context c;
    RecordList RL;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public restaurantAdapter(@NonNull FirestoreRecyclerOptions<Restaturant> options, Context context, RecordList rl) {
        super(options);
        c = context;
        RL = rl;
    }

    @Override
    protected void onBindViewHolder(@NonNull restaurantViewHolder holder, int position, @NonNull Restaturant model) {
        holder.textView5.setText(model.title);
        holder.textView6.setText("by: "+model.by);
        Glide.with(c).load(model.img1).into(holder.imageView);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RL.viewLocation(model);
            }
        });
    }

    @NonNull
    @Override
    public restaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.imagelist,parent,false);
        return new restaurantViewHolder(view);
    }

    class restaurantViewHolder extends RecyclerView.ViewHolder {
        TextView textView5, textView6;
        ImageView imageView;
        Button button;

        public restaurantViewHolder(@NonNull View itemView){
            super(itemView);
            textView5 = itemView.findViewById(R.id.textView5);
            textView6 = itemView.findViewById(R.id.textView6);
            imageView = itemView.findViewById(R.id.imageView);
            button = itemView.findViewById(R.id.button);
        }
    }
}
