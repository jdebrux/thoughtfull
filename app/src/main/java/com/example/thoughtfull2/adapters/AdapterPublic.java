package com.example.thoughtfull2.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thoughtfull2.R;
import com.example.thoughtfull2.models.ModelThoughtPost;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AdapterPublic extends RecyclerView.Adapter<AdapterPublic.MyHolder> {

    Context context;
    List<ModelThoughtPost> postList;

    String myUid;

    private DatabaseReference postsRef, archiveRef; //reference of posts

    public AdapterPublic(Context context, List<ModelThoughtPost> postList) {
        this.context = context;
        this.postList = postList;
        myUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        archiveRef = FirebaseDatabase.getInstance().getReference().child("Archive");
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_public, parent, false);

        return new MyHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        //get data
        final String uid = postList.get(position).getUid();
        String uName = postList.get(position).getuName();
        String uPp = postList.get(position).getuPp();
        String pId = postList.get(position).getpId();
        String pTitle = postList.get(position).getTitle();
        String pDescription = postList.get(position).getDescription();
        String startTime = postList.get(position).getStartTime();
        String endTime = postList.get(position).getEndTime();
        Boolean locked = Boolean.parseBoolean(postList.get(position).getpLocked());
        Boolean remind = Boolean.parseBoolean(postList.get(position).getpRemind());

        //convert time to dd/mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());

        calendar.setTimeInMillis(Long.parseLong(startTime));
        String pTime = DateFormat.format("dd/MM/yyyy HH:mm", calendar).toString();

        calendar.setTimeInMillis(Long.parseLong(endTime));
        long currentTime = System.currentTimeMillis();
        new CountDownTimer(Long.parseLong(endTime) - currentTime, 1000) {
            public void onTick(long millisUntilFinished) {
                long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                holder.pEndTv.setText(days + "d :" + hours + "h :" + minutes + "m :" + seconds + "s"); //You can compute the millisUntilFinished on hours/minutes/seconds
            }

            // When the task is over it will display finished there
            public void onFinish() {
                holder.pEndTv.setText("Finished");
                archiveThought(pId);
            }
        }.start();
        endTime = DateFormat.format("dd/MM/yyyy HH:mm", calendar).toString();

        //set data
        holder.uNameTv.setText(uName);
        holder.pTimeTv.setText(pTime);
        holder.pTitleTv.setText(pTitle);
        if (!locked) {
            holder.pDescriptionTv.setText(pDescription);
        } else {
            holder.pDescriptionTv.setVisibility(View.GONE);
        }

        //set user pp
        try {
            Picasso.get().load(uPp).placeholder(R.drawable.choco).rotate(90).into(holder.profilePicIv);
        } catch (Exception e) {

        }

        //handle button clicks
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreOptions(holder.moreBtn, uid, myUid, pId);
            }
        });

        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                String shareText = "Thought Title: " + pTitle + "\n Description: " + pDescription + "\n" + pTime;
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                context.startActivity(shareIntent);
            }
        });

    }

    private void showMoreOptions(ImageButton moreBtn, String uid, String myUid, final String pId) {
        PopupMenu popupMenu = new PopupMenu(context, moreBtn, Gravity.END);

        //the option to delete a post should only be available for the posts of the signed in user
        if (uid.equals(myUid)) {
            //add menu items
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete Post");
        }

        //item click listener
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == 0) {
                    //delete is pressed
                    deleteThought(pId);
                }
                return false;
            }
        });
        //show the menu
        popupMenu.show();

    }

    private void archiveThought(String pId) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Archiving Post...");
        //read data, move data, delete data
        postsRef.orderByChild("pId").equalTo(pId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ModelThoughtPost archiveThought = snapshot.getValue(ModelThoughtPost.class);
                //HashMap<String, Object> hashMap = new HashMap<>();
                //hashMap.put(archiveThought.getpId(), archiveThought);
                archiveRef.child(archiveThought.getpId()).setValue(archiveThought).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        pd.dismiss();
                        Toast.makeText(context, "Thought Archived...", Toast.LENGTH_SHORT).show();
                        deleteThought(pId);
                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteThought(String pId) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting Post...");
        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ds.getRef().removeValue(); //removes values from firebase where pId matches
                }
                //deleted
                Toast.makeText(context, "Your post has been deleted successfully.", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder {


        //views from row_post.xml
        //ImageView uPictureIv, pImageIv;
        TextView uNameTv, pTimeTv, pTitleTv, pDescriptionTv, pEndTv;
        ImageButton moreBtn, removeBtn, archiveBtn, shareBtn;
        ImageView profilePicIv;
        LinearLayout profileLayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            profilePicIv = itemView.findViewById(R.id.profilePicIv);
            profilePicIv.setClipToOutline(true);
            uNameTv = itemView.findViewById(R.id.uNameTv);
            pTimeTv = itemView.findViewById(R.id.pTimeTv);
            profilePicIv = itemView.findViewById(R.id.profilePicIv);
            pEndTv = itemView.findViewById(R.id.pEndTimeTv);
            pTitleTv = itemView.findViewById(R.id.pTitleTv);
            pDescriptionTv = itemView.findViewById(R.id.pDescriptionTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            removeBtn = itemView.findViewById(R.id.removeBtn);
            archiveBtn = itemView.findViewById(R.id.archiveBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
            profileLayout = itemView.findViewById(R.id.profileLayout);
        }
    }
}
