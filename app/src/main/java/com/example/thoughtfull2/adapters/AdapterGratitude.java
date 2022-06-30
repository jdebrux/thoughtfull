package com.example.thoughtfull2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thoughtfull2.R;
import com.example.thoughtfull2.models.GratitudeModel;

import java.util.ArrayList;

public class AdapterGratitude extends RecyclerView.Adapter<AdapterGratitude.ViewHolder> {

    // variable for our array list and context
    private ArrayList<GratitudeModel> gratitudeModelArrayList;
    private Context context;

    // constructor
    public AdapterGratitude(ArrayList<GratitudeModel> courseModalArrayList, Context context) {
        this.gratitudeModelArrayList = courseModalArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // on below line we are inflating our layout
        // file for our recycler view items.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gratitude_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // on below line we are setting data
        // to our views of recycler view item.
        GratitudeModel modal = gratitudeModelArrayList.get(position);
        holder.descriptionTv.setText(modal.getDescription());
    }

    @Override
    public int getItemCount() {
        // returning the size of our array list
        return gratitudeModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // creating variables for our text views.
        private TextView descriptionTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
        }
    }
}
