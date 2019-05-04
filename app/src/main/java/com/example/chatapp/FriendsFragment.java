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

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView friends_list;

    private DatabaseReference friendsDatabase;

    private String currentUser_id;

    private View mainView;

    private FriendsDataAdapter friendsDataAdapter;

    ArrayList<Friends> friends = new ArrayList<>();
    ArrayList<String> friends_ids = new ArrayList<>();




    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_friends, container, false);

        currentUser_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        friendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(currentUser_id);




        friends_list = mainView.findViewById(R.id.friends_list);
        friends_list.setHasFixedSize(true);
        friends_list.setLayoutManager(new LinearLayoutManager(getContext()));

        friendsDataAdapter = new FriendsDataAdapter( getContext(), friends, friends_ids);

        friends_list.setAdapter(friendsDataAdapter);

        return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(friends.size() == 0)
            loadUsers();
    }

    private void loadUsers(){
        friendsDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String friend_id = dataSnapshot.getRef().getKey();
                Friends friend = dataSnapshot.getValue(Friends.class);
                friends.add(friend);
                friends_ids.add(friend_id);

                friendsDataAdapter.notifyDataSetChanged();
                //users_list.smoothScrollToPosition(users.size());//промотка вниз списка пользователей
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
