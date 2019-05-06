package com.example.chatapp;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private RecyclerView chats_list;

    private String currentUser_id;

    private View mainView;

    private ChatsDataAdapter chatsDataAdapter;

    ArrayList<Users> chats = new ArrayList<>();
    ArrayList<String> users_ids = new ArrayList<>();




    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_chats, container, false);

        currentUser_id = FirebaseAuth.getInstance().getCurrentUser().getUid();




        chats_list = mainView.findViewById(R.id.chats_list);
        chats_list.setHasFixedSize(true);
        chats_list.setLayoutManager(new LinearLayoutManager(getContext()));

        chatsDataAdapter = new ChatsDataAdapter( getContext(), chats, users_ids);

        chats_list.setAdapter(chatsDataAdapter);

        return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(chats.size() == 0) {
            loadUsers();
        }
    }

    private void loadUsers(){
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference();


        chatRef.child("Chats").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.hasChild(currentUser_id)){
                    final String chat_user_id = dataSnapshot.getRef().getKey();

                    chatRef.child("Users").child(chat_user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                           Users user = dataSnapshot.getValue(Users.class);

                           chats.add(user);
                           users_ids.add(chat_user_id);

                            System.out.println("DIALOG " + chat_user_id);

                            System.out.println("USERSDIALOG " + user.getUsername());

                            chatsDataAdapter.notifyDataSetChanged();


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
