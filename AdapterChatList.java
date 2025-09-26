package com.spacester.tweetsterupdate.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.tweetsterupdate.R;
import com.spacester.tweetsterupdate.activity.ChatActivity;
import com.spacester.tweetsterupdate.model.ModelChat;
import com.spacester.tweetsterupdate.model.ModelUser;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.MyHolder> {

    final Context context;
    final List<ModelUser> userList;

    public AdapterChatList(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(context).inflate(R.layout.user_view, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        String hisUid = userList.get(position).getId();
        String dp = userList.get(position).getPhoto();
        String name = userList.get(position).getName();

        FirebaseDatabase.getInstance().getReference().child("Ban").child(hisUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                     ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                                    params.height = 0;
                                    holder.itemView.setLayoutParams(params);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Users").child(hisUid).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    holder.mName.setText(name);


                    try{
                        Picasso.get().load(dp).into(holder.mDp);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.avatar).into(holder.mDp);
                    }

                    String mVerified = Objects.requireNonNull(snapshot.child("verified").getValue()).toString();

                    if (mVerified.isEmpty()){
                        holder.verified.setVisibility(View.GONE);
                    }else {
                        holder.verified.setVisibility(View.VISIBLE);
                    }


                    if (userList.get(position).getStatus().equals("online")){
                        holder.status.setVisibility(View.VISIBLE);
                    }else {
                        holder.status.setVisibility(View.GONE);
                    }

                    holder.itemView.setOnClickListener(v -> {
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("id", hisUid);
                        context.startActivity(intent);
                    });
                }else {
                       ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                                    params.height = 0;
                                    holder.itemView.setLayoutParams(params);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //UserInfo
        FirebaseDatabase.getInstance().getReference().child("Users").child(userList.get(position).getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Typing
                if (Objects.requireNonNull(snapshot.child("typingTo").getValue()).toString().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                    holder.message.setText("Typing...");
                }else {
                    //LastMessage
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
                    reference.addValueEventListener(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            holder.message.setText("No Message");
                            for (DataSnapshot ds: snapshot.getChildren()){
                                ModelChat chat = ds.getValue(ModelChat.class);
                                if (chat == null){
                                    continue;
                                }
                                String sender = chat.getSender();
                                String receiver = chat.getReceiver();
                                if(sender == null || receiver == null){
                                    continue;
                                }
                                if (chat.getReceiver().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && chat.getSender().equals(userList.get(position).getId()) || chat.getReceiver().equals(userList.get(position).getId()) && chat.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    switch (chat.getType()) {
                                        case "image":
                                            holder.message.setText("Sent a photo");
                                            break;
                                        case "video":
                                            holder.message.setText("Sent a video");
                                            break;
                                        case "post":
                                            holder.message.setText("Sent a post");
                                            break;
                                        case "gif":
                                            holder.message.setText("Sent a GIF");
                                            break;
                                        case "audio":
                                            holder.message.setText("Sent a audio");
                                        case "doc":
                                            holder.message.setText("Sent a document");
                                            break;
                                        case "location":
                                            holder.message.setText("Sent a location");
                                            break;
                                        case "party":
                                            holder.message.setText("Sent a party invitation");
                                            break;
                                        case "reel":
                                            holder.message.setText("Sent a reel");
                                            break;
                                        case "story":
                                        case "high":
                                            holder.message.setText("Sent a story");
                                            break;
                                        default:
                                            holder.message.setText(chat.getMsg());
                                            break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final CircleImageView mDp;
        final RelativeLayout status;
        final TextView mName;
        final TextView message;
        final ImageView verified;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            mDp = itemView.findViewById(R.id.circleImageView2);
            status = itemView.findViewById(R.id.relativeLayout);
            mName = itemView.findViewById(R.id.name);
            verified = itemView.findViewById(R.id.verified);
            message = itemView.findViewById(R.id.username);
        }
    }

}
