package com.example.thoughtfull2;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

public class AddThoughtActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference userDbRef;
    ActionBar actionBar;

    //views
    EditText titleEt, descriptionEt, endDateET, endTimeET ;
    Button uploadBtn, voiceMemoBtn;
    Switch remindMeSw, lockSw;

    //user info
    String name, email, uid, pp;

    Calendar endCal;

    //progress bar
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_thought);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Add New thought");

        //enable back button in actionbar
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        pd = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        actionBar.setSubtitle(email);

        endCal = Calendar.getInstance();

        //get some info of current user to include in post
        userDbRef = FirebaseDatabase.getInstance().getReference("Users");
        Query query = userDbRef.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    name = "" + ds.child("name").getValue();
                    email = "" + ds.child("email").getValue();
                    pp = "" + ds.child("image").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //init views
        titleEt = findViewById(R.id.titleEt);
        descriptionEt = findViewById(R.id.descriptionEt);
        endTimeET = findViewById(R.id.endTimeET);
        endDateET = findViewById(R.id.endDateET);
        voiceMemoBtn = findViewById(R.id.voiceMemoBtn);
        remindMeSw = findViewById(R.id.remindMeSw);
        lockSw = findViewById(R.id.lockSw);
        uploadBtn = findViewById(R.id.pUploadBtn);

        //upload button click listener
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get data
                String title = titleEt.getText().toString().trim();
                String description = descriptionEt.getText().toString().trim();
                String endTime = endTimeET.getText().toString().trim();
                String endDate = endDateET.getText().toString().trim();

                String endDateTime = String.valueOf(endCal.getTimeInMillis());

                Boolean remindMe = remindMeSw.isChecked();
                Boolean lockThought = lockSw.isChecked();
                //add voice memo info, switch values

                if (TextUtils.isEmpty(title)) {
                    Toast.makeText(AddThoughtActivity.this, "Enter a Title...", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(description)) {
                    Toast.makeText(AddThoughtActivity.this, "Enter a Description...", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (TextUtils.isEmpty(endTime)) {
                    Toast.makeText(AddThoughtActivity.this, "Enter an End Time...", Toast.LENGTH_SHORT).show();
                } else {
                    uploadData(title, description, endDateTime, remindMe, lockThought);
                }
            }
        });
    }

    private void uploadData(final String title, final String description, final String endDateTime, Boolean remindMe, Boolean lockThought) {
        pd.setMessage("Publishing Post...");
        pd.show();

        //for post-id, post-publish-time
        final String timeStamp = String.valueOf(System.currentTimeMillis());

        String filePathAndName = "Posts/" + "post_" + timeStamp;

        //post without image
        HashMap<Object, String> hashMap = new HashMap<>();
        //put post info
        hashMap.put("uid", uid);
        hashMap.put("uName", name);
        hashMap.put("uEmail", email);
        hashMap.put("uPp", pp);
        hashMap.put("pId", timeStamp);
        hashMap.put("title", title);
        hashMap.put("description", description);
        hashMap.put("endTime", endDateTime);
        hashMap.put("startTime", timeStamp);
        hashMap.put("pRemind", String.valueOf(remindMe));
        hashMap.put("pLocked", String.valueOf(lockThought));

        //path to storage post data
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        //put data in this ref
        ref.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        Toast.makeText(AddThoughtActivity.this, "Thought Published", Toast.LENGTH_SHORT).show();
                        titleEt.setText("");
                        descriptionEt.setText("");
                        endDateET.setText("");
                        endTimeET.setText("");
                        remindMeSw.setChecked(false);
                        lockSw.setChecked(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed adding post in database
                        pd.dismiss();
                        Toast.makeText(AddThoughtActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }

    private void checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //user is signed in stay here
            email = user.getEmail();
            uid = user.getUid();
        } else {
            //user not signed in, go to main activity
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //go to previous activities
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);


        menu.findItem(R.id.action_add_post).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //get item id
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showTimePickerDialog(View v) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

           @Override
           public void onTimeSet(TimePicker timePicker, int hour, int min) {
               endCal.set(Calendar.HOUR, hour);
               endCal.set(Calendar.MINUTE, min);
               endCal.set(Calendar.SECOND, 0);

               endTimeET.setText(hour + ":" + min);

           }
       }, hour, minute, true);
       timePickerDialog.show();
    }

    public void showDatePickerDialog(View v) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                endCal.set(Calendar.YEAR, year);
                endCal.set(Calendar.MONTH, month);
                endCal.set(Calendar.DAY_OF_MONTH, day);

                endDateET.setText(year + "/" + month + "/" + day);
            }
        };
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
        datePickerDialog.show();
    }
}
