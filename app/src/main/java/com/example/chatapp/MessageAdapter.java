package com.example.chatapp;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private ArrayList<Messages> messageList;
    private FirebaseAuth auth;

    View view;

    public MessageAdapter(ArrayList<Messages> messageList){
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.message_list_element, viewGroup, false);

        return new MessageViewHolder(view);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        TextView message_text;
        CircleImageView message_image;
        TextView message_user_name;


        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            message_text = itemView.findViewById(R.id.message_text);
            message_image = itemView.findViewById(R.id.message_image);
            message_user_name = itemView.findViewById(R.id.user_name_text);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, int i) {

        auth = FirebaseAuth.getInstance();

        String currentUser_id = auth.getCurrentUser().getUid();

        final Messages message = messageList.get(i);

        String from_user = message.getFrom();

        /*if(from_user.equals(currentUser_id)){

            messageViewHolder.message_text.setBackgroundResource(R.drawable.message_from);
            messageViewHolder.message_text.setTextColor(Color.BLACK);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) messageViewHolder.message_text.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            messageViewHolder.message_text.setLayoutParams(params);
            messageViewHolder.message_image.setVisibility(View.INVISIBLE);

        }else{
            messageViewHolder.message_text.setBackgroundResource(R.drawable.message);
            messageViewHolder.message_text.setTextColor(Color.WHITE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) messageViewHolder.message_text.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            messageViewHolder.message_text.setLayoutParams(params);
            messageViewHolder.message_image.setVisibility(View.VISIBLE);
        }*/
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                ref.child("Users").child(from_user).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String name = dataSnapshot.child("username").getValue().toString();
                        String image = dataSnapshot.child("thumb_image").getValue().toString();

                        messageViewHolder.message_user_name.setText(name);

                        Picasso.with(view.getContext()).load(image).placeholder(R.mipmap.user2).into(messageViewHolder.message_image);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        messageViewHolder.message_text.setText(message.getMessage());

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

}
