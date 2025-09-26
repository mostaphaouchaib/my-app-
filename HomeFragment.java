package com.spacester.tweetsterupdate.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoast.StyleableToast;
import com.spacester.tweetsterupdate.R;
import com.spacester.tweetsterupdate.adapter.AdapterLive;
import com.spacester.tweetsterupdate.adapter.AdapterPost;
import com.spacester.tweetsterupdate.adapter.AdapterStory;
import com.spacester.tweetsterupdate.model.ModelLive;
import com.spacester.tweetsterupdate.model.ModelPost;
import com.spacester.tweetsterupdate.model.ModelStory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    List<ModelPost> postList;
    AdapterPost adapterPost;
    RecyclerView post;
    List<String> followingList;
    String myId;
    FirebaseAuth mAuth;

    ProgressBar progressBar;
    List<String> followingSList;
    private AdapterStory story;
    private List<ModelStory> storyList;
    RecyclerView storyView;

    List<String> followingVList;
    private AdapterLive live;
    TextView found;
    private List<ModelLive> modelLives;
    RecyclerView liveView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //UserId
        found = view.findViewById(R.id.found);
        mAuth = FirebaseAuth.getInstance();
        myId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        progressBar = view.findViewById(R.id.pb);

        post = view.findViewById(R.id.post);

        storyView = view.findViewById(R.id.storyView);
        liveView = view.findViewById(R.id.liveView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        storyView.setLayoutManager(linearLayoutManager);
        storyList = new ArrayList<>();
        story = new AdapterStory(getContext(), storyList);
        storyView.setAdapter(story);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        liveView.setLayoutManager(linearLayoutManager2);
        modelLives = new ArrayList<>();

        postList= new ArrayList<>();
        checkFollowing();
        checkSFollowing();
        checkVFollowing();

        return view;
    }

    private void checkFollowing(){
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                followingList.add(myId);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }
                loadPost();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadPost() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        post.setLayoutManager(layoutManager);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    for (String id : followingList){
                        if (Objects.requireNonNull(modelPost).getId().equals(id)){
                            postList.add(modelPost);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                    adapterPost = new AdapterPost(getActivity(), postList);
                    post.setAdapter(adapterPost);
                    adapterPost.notifyDataSetChanged();

                }
                if (adapterPost.getItemCount() == 0){
                    found.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void checkSFollowing(){
        followingSList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingSList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingSList.add(snapshot.getKey());
                }
                readStory();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readStory(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long timecurrent = System.currentTimeMillis();
                storyList.clear();
                storyList.add(new ModelStory("",0,0,"", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()));
                for (String id : followingSList){
                    int countStory = 0;
                    ModelStory modelStory = null;
                    for (DataSnapshot snapshot1 : snapshot.child(id).getChildren()){
                        modelStory = snapshot1.getValue(ModelStory.class);
                        if (timecurrent > Objects.requireNonNull(modelStory).getTimestart() && timecurrent < modelStory.getTimeend()){
                            countStory++;
                        }
                    }
                    if (countStory > 0){
                        storyList.add(modelStory);
                    }
                }
                story.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void checkVFollowing(){
        followingVList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingVList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingVList.add(snapshot.getKey());
                }
                readLive();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readLive(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Live");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelLives.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelLive modelLive = ds.getValue(ModelLive.class);
                    for (String id : followingVList){
                        if (modelLive.getUserid().equals(id)){
                            if (!modelLive.getUserid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                modelLives.add(modelLive);
                            }
                        }
                    }
                    live = new AdapterLive(getActivity(), modelLives);
                    liveView.setAdapter(live);
                    live.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}