package com.example.thoughtfull2;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thoughtfull2.adapters.AdapterGratitude;
import com.example.thoughtfull2.models.GratitudeModel;

import java.util.ArrayList;

public class ViewGratitudes extends AppCompatActivity {

    // creating variables for our array list,
    // dbhandler, adapter and recycler view.
    private ArrayList<GratitudeModel> gratitudeModelArrayList;
    private DatabaseHandler dbHandler;
    private AdapterGratitude adapterGratitude;
    private RecyclerView coursesRV;
    ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_gratitude);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Gratitudes");

        //enable back button in actionbar
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // initializing our all variables.
        gratitudeModelArrayList = new ArrayList<>();
        dbHandler = new DatabaseHandler(ViewGratitudes.this);

        // getting our course array
        // list from db handler class.
        gratitudeModelArrayList = dbHandler.readCourses();

        // on below line passing our array lost to our adapter class.
        adapterGratitude = new AdapterGratitude(gratitudeModelArrayList, ViewGratitudes.this);
        coursesRV = findViewById(R.id.gratitudeRv);

        // setting layout manager for our recycler view.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewGratitudes.this, RecyclerView.VERTICAL, false);
        coursesRV.setLayoutManager(linearLayoutManager);

        // setting our adapter to recycler view.
        coursesRV.setAdapter(adapterGratitude);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //go to previous activities
        return super.onSupportNavigateUp();
    }

}
