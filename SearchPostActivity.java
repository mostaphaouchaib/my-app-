package com.spacester.tweetsterupdate.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoast.StyleableToast;
import com.spacester.tweetsterupdate.NightMode;
import com.spacester.tweetsterupdate.R;
import com.spacester.tweetsterupdate.adapter.AdapterPost;
import com.spacester.tweetsterupdate.model.ModelPost;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchPostActivity extends AppCompatActivity {

    RecyclerView trendingRv;

    //Post
    AdapterPost adapterPost;
    List<ModelPost> postList;

    ProgressBar pb;

    TextView found;
    NightMode sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new NightMode(this);
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_post);
        found = findViewById(R.id.found);
        trendingRv = findViewById(R.id.trendingRv);
        pb = findViewById(R.id.pb);

        ImageView imageView3 = findViewById(R.id.imageView3);
        imageView3.setOnClickListener(v -> onBackPressed());


        EditText editText = findViewById(R.id.editText);

        pb.setVisibility(View.VISIBLE);


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())){
                    filterPost(s.toString());
                    pb.setVisibility(View.VISIBLE);
                }else {
                    getAllTrend();
                    pb.setVisibility(View.VISIBLE);
                }

            }
        });


        //Post
        postList = new ArrayList<>();
        getAllTrend();

        String tag = getIntent().getStringExtra("tag");
        if (!tag.isEmpty()){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
           ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    postList.clear();
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ModelPost modelPost = ds.getValue(ModelPost.class);
                        if (Objects.requireNonNull(modelPost).getText().toLowerCase().contains(tag.toLowerCase()) || modelPost.getType().contains(tag.toLowerCase())){
                            postList.add(modelPost);
                            pb.setVisibility(View.GONE);
                        }
                        adapterPost = new AdapterPost(getApplicationContext(), postList);
                        trendingRv.setAdapter(adapterPost);
                    }
                    if (dataSnapshot.getChildrenCount() == 0){
                        found.setVisibility(View.VISIBLE);
                        pb.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            editText.setText(tag);
            pb.setVisibility(View.VISIBLE);
        }else {
            onBackPressed();
        }


    }
    private void filterPost(String query) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
       ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    if (Objects.requireNonNull(modelPost).getText().toLowerCase().contains(query.toLowerCase()) || modelPost.getType().contains(query.toLowerCase())){
                        postList.add(modelPost);
                        pb.setVisibility(View.GONE);
                    }
                    adapterPost = new AdapterPost(getApplicationContext(), postList);
                    trendingRv.setAdapter(adapterPost);
                }
                if (dataSnapshot.getChildrenCount() == 0){
                    found.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getAllTrend() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        trendingRv.setLayoutManager(layoutManager);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    postList.add(modelPost);
                    pb.setVisibility(View.GONE);
                }
                adapterPost = new AdapterPost(getApplicationContext(), postList);
                trendingRv.setAdapter(adapterPost);
                adapterPost.notifyDataSetChanged();


                if (dataSnapshot.getChildrenCount() == 0){
                    found.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text(databaseError.getMessage())
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000)
                        .solidBackground()
                        .gravity(0)
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });
    }


}