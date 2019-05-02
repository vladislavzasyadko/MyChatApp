package com.example.chatapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<UsersViewHolder> {

    private ArrayList<Users> users;
    LayoutInflater inflater;
    Context context;

    public DataAdapter(Context context, ArrayList<Users> users) {
        this.users = users;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = inflater.inflate(R.layout.list_user_element, parent, false);

        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder viewHolder, int i) {
        Users user = users.get(i);
        viewHolder.username.setText(user.getName());
        viewHolder.status.setText(user.getStatus());
        if(!viewHolder.image.equals("default")) {
            Picasso.with(context).load(user.getImage()).into(viewHolder.image);
        }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
