package com.example.mychatapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {
   private Toolbar mToolbar;
   private RecyclerView FindFriendRecyclerList;
   private DatabaseReference UsersrRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        UsersrRef= FirebaseDatabase.getInstance().getReference().child("users");
        FindFriendRecyclerList=(RecyclerView)findViewById(R.id.find_friend_recycler_list);
        FindFriendRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        mToolbar=(Toolbar)findViewById(R.id.find_friends_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(UsersrRef,Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts,FindFriendViewHolder> adapter=
                new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendViewHolder findFriendViewHolder, final int i, @NonNull Contacts contacts)
                    {

                        findFriendViewHolder.userName.setText(contacts.getName());
                        findFriendViewHolder.userStatus.setText(contacts.getStatus());
                   // Picasso.get().load(contacts.getImage()).into(findFriendViewHolder.profileImage);
                       if(!contacts.getImage().equals("")) {
                           Glide.with(FindFriendsActivity.this).load(contacts.getImage()).placeholder(R.drawable.profile_image).into(findFriendViewHolder.profileImage);
                            Log.e("findfrndimg", contacts.getImage()); }

                       findFriendViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               String visit_user_id=getRef(i).getKey();
                               Intent profileIntent=new Intent(FindFriendsActivity.this,ProfileActivity.class);
                               profileIntent.putExtra("visit_user_id",visit_user_id);
                               startActivity(profileIntent);
                           }
                       });
                    }

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                       FindFriendViewHolder viewHolder=new FindFriendViewHolder(view);

                       return viewHolder;
                    }
                };
        FindFriendRecyclerList.setAdapter(adapter);
        adapter.startListening();
    }
    public static class FindFriendViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName,userStatus;
        CircleImageView profileImage;
        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.user_profile_name);
            userStatus=itemView.findViewById(R.id.user_status);
            profileImage=itemView.findViewById(R.id.users_profile_image);
        }
    }
}
