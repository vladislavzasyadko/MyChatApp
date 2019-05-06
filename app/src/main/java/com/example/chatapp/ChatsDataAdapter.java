package com.example.chatapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatsDataAdapter extends RecyclerView.Adapter<UsersViewHolder>{

    private DatabaseReference reference;

    private DatabaseReference userData;

    private ArrayList<Users> chats;
    private ArrayList<String> users_ids;
    LayoutInflater inflater;
    Context context;
    View view;

    //private String user_name;

    public ChatsDataAdapter(Context context, ArrayList<Users> chats, ArrayList<String> users_ids) {
        this.chats = chats;
        this.users_ids = users_ids;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        System.out.println("BIG PIPI " + chats.size());
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        this.view = inflater.inflate(R.layout.list_user_element, parent, false);



        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UsersViewHolder viewHolder, int i) {
        Users chat = chats.get(i);

        final String user_id = users_ids.get(i);

        final String current_user_id =  FirebaseAuth.getInstance().getCurrentUser().getUid();

        //reference = FirebaseDatabase.getInstance().getReference().child("Friends").child(current_user_id);

        userData = FirebaseDatabase.getInstance().getReference().child("Users").child(users_ids.get(i));

        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user_name = dataSnapshot.child("username").getValue().toString();
                //String image = dataSnapshot.child("image").getValue().toString();
                //String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                viewHolder.username.setText(user_name);
                Picasso.with(context).load(thumb_image).placeholder(R.mipmap.user2).into(viewHolder.image);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        viewHolder.status.setText("Нажмите, чтобы перейти к чату.");

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent chat = new Intent(context, ChatActivity.class);
                chat.putExtra("user_id", user_id);
                chat.putExtra("user_name", viewHolder.username.getText().toString());
                context.startActivity(chat);

            }
        });

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }
}
