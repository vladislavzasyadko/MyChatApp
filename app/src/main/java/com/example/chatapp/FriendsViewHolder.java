package com.example.chatapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsViewHolder extends RecyclerView.ViewHolder {

    TextView username;
    TextView date;
    CircleImageView image;
    ImageView online;

    public FriendsViewHolder(@NonNull View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.username_list);
        date = itemView.findViewById(R.id.status_list);
        image = itemView.findViewById(R.id.profile_pic_list);
        online = itemView.findViewById(R.id.online_status);
    }
}
