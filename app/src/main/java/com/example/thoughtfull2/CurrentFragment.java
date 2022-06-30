package com.example.thoughtfull2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thoughtfull2.adapters.AdapterThought;
import com.example.thoughtfull2.models.ModelThoughtPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CurrentFragment extends Fragment {

    //firebase auth
    FirebaseAuth firebaseAuth;

    RecyclerView recyclerView;
    List<ModelThoughtPost> thoughtList;
    AdapterThought adapterThought;
    String uid;

    public CurrentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current, container, false);

        //init
        firebaseAuth = FirebaseAuth.getInstance();

        //recycler view and its properties
        recyclerView = view.findViewById(R.id.thoughtRecyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //show newest post first, for this load from next
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set layout to recyclerview
        recyclerView.setLayoutManager(layoutManager);

        //init post list
        thoughtList = new ArrayList<>();

        checkUserStatus();
        loadThoughts();

        //init view from xml
        return view;
    }

    private void loadThoughts() {
        //path of all posts
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

        //uid is used to get list of posts from that user
        Query query = ref.orderByChild("uid").equalTo(uid);

        //get all data from this ref
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                thoughtList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelThoughtPost modelThought = ds.getValue(ModelThoughtPost.class);

                    thoughtList.add(modelThought);

                    //adapter
                    adapterThought = new AdapterThought(getActivity(), thoughtList);
                    //set adapter to recyclerview
                    recyclerView.setAdapter(adapterThought);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchPosts(final String searchQuery) {
        //path of all posts
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

        //uid is used to get list of posts from that user
        Query query = ref.orderByChild("uid").equalTo(uid);

        //get all data from this ref
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                thoughtList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelThoughtPost modelThoughtPost = ds.getValue(ModelThoughtPost.class);

                    if (modelThoughtPost.getTitle().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            modelThoughtPost.getDescription().toLowerCase().contains(searchQuery.toLowerCase())) {
                        thoughtList.add(modelThoughtPost);
                    }

                    //adapter
                    adapterThought = new AdapterThought(getActivity(), thoughtList);
                    //set adapter to recyclerview
                    recyclerView.setAdapter(adapterThought);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //in case of error
                Toast.makeText(getActivity(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //user is signed in stay here
            uid = user.getUid();
        } else {
            //user not signed in, go to main activity
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);//to show option menu in fragment
        super.onCreate(savedInstanceState);
    }

    /*inflate options menu*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflating menu
        inflater.inflate(R.menu.menu_main, menu);

        //searchview to search posts by title or description
        MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        //search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //called when the user presses the search button
                if (!TextUtils.isEmpty(s)) {
                    searchPosts(s);
                } else {
                    loadThoughts();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //called as user presses each character
                if (!TextUtils.isEmpty(s)) {
                    searchPosts(s);
                } else {
                    loadThoughts();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    /*Handle menu item clicks*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //get item id
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        if (id == R.id.action_add_post) {
            startActivity(new Intent(getActivity(), AddThoughtActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}