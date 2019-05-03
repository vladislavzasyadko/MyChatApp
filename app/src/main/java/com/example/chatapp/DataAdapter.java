package com.example.chatapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<UsersViewHolder> {

    private FirebaseUser currentUser;
    private DatabaseReference reference;

    private ArrayList<Users> users;
    private ArrayList<String> user_ids;
    LayoutInflater inflater;
    Context context;
    View view;

    public DataAdapter(Context context, ArrayList<Users> users, ArrayList<String> user_ids) {
        this.users = users;
        this.user_ids = user_ids;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        this.view = inflater.inflate(R.layout.list_user_element, parent, false);

        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder viewHolder, int i) {
        Users user = users.get(i);
        final String user_id = user_ids.get(i);

        //reference = FirebaseDatabase.getInstance().getReference().child("Users");
        //assert currentUser != null;
        //String user_id = currentUser.getUid(i);

        viewHolder.username.setText(user.getName());
        viewHolder.status.setText(user.getStatus());
        if(!viewHolder.image.equals("default")) {
            Picasso.with(context).load(user.getImage()).placeholder(R.mipmap.user2).into(viewHolder.image);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent profile = new Intent(context, ProfileActivity.class);
                profile.putExtra("user_id", user_id);
                context.startActivity(profile);

            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
