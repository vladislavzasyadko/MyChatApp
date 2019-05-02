package com.example.chatapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersViewHolder extends RecyclerView.ViewHolder {

    TextView username;
    TextView status;
    CircleImageView image;

    public UsersViewHolder(@NonNull View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.username_list);
        status = itemView.findViewById(R.id.status_list);
        image = itemView.findViewById(R.id.profile_pic_list);
    }
}
