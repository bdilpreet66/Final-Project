package com.mad3125.finalproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.mad3125.finalproject.R;
import com.mad3125.finalproject.data.Comment;

public class commentAdapter  extends FirestoreRecyclerAdapter<Comment, commentAdapter.commentViewHolder> {
    Context c;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public commentAdapter(@NonNull FirestoreRecyclerOptions<Comment> options, Context context) {
        super(options);
        c = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull commentAdapter.commentViewHolder holder, int position, @NonNull Comment model) {
        holder.comment.setText(model.desc);
        holder.rating.setText(model.rating);
        holder.by.setText("by: "+model.by);
    }

    @NonNull
    @Override
    public commentAdapter.commentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.commentlist,parent,false);
        return new commentAdapter.commentViewHolder(view);
    }

    class commentViewHolder extends RecyclerView.ViewHolder {
        TextView comment, rating, by;

        public commentViewHolder(@NonNull View itemView){
            super(itemView);
            comment = itemView.findViewById(R.id.textView8);
            rating = itemView.findViewById(R.id.textView12);
            by = itemView.findViewById(R.id.textView11);
        }
    }
}