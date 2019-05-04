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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendsDataAdapter extends RecyclerView.Adapter<FriendsViewHolder> {

    //private FirebaseUser currentUser;
    private DatabaseReference reference;

    private DatabaseReference friendUserData;

    private ArrayList<Friends> friends;
    private ArrayList<String> friends_ids;
    LayoutInflater inflater;
    Context context;
    View view;

    public FriendsDataAdapter(Context context, ArrayList<Friends> friends, ArrayList<String> friends_ids) {
        this.friends = friends;
        this.friends_ids = friends_ids;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        this.view = inflater.inflate(R.layout.list_user_element, parent, false);



        return new FriendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendsViewHolder viewHolder, int i) {
        Friends friend = friends.get(i);

        final String friend_id = friends_ids.get(i);

        String current_user_id =  FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Friends").child(current_user_id);

        friendUserData = FirebaseDatabase.getInstance().getReference().child("Users").child(friends_ids.get(i));

        friendUserData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("username").getValue().toString();
                //String image = dataSnapshot.child("image").getValue().toString();
                //String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                if(dataSnapshot.hasChild("online")){
                    Boolean online_status = (boolean) dataSnapshot.child("online").getValue();
                    if(online_status.equals(true)){
                        viewHolder.online.setVisibility(View.VISIBLE);
                        System.out.println("MANAMEJEFF MANAMEjEFF MANAMEFEJJ");
                    }

                }else{
                    viewHolder.online.setVisibility(View.INVISIBLE);
                }

                viewHolder.username.setText(name);
                Picasso.with(context).load(thumb_image).placeholder(R.mipmap.user2).into(viewHolder.image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        viewHolder.date.setText(friend.getDate());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence options[] = new CharSequence[]{"Открыть профиль", "Отправить сообщение"};

                final AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Выберите опцию");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(which == 0 ){

                            Intent friendsProfile = new Intent(context, ProfileActivity.class);
                            friendsProfile.putExtra("user_id", friend_id);
                            context.startActivity(friendsProfile);
                        }
                        if(which == 1){

                            Intent chat = new Intent(context, ChatActivity.class);
                            chat.putExtra("user_id", friend_id);
                            context.startActivity(chat);

                        }

                    }
                });



            }
        });

    }

    @Override
    public int getItemCount() {
        return friends.size();
    }
}

