package com.example.chatapp;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UsersActivity extends AppCompatActivity {

    private Toolbar user_toolbar;
    private RecyclerView users_list;

    private DatabaseReference reference;
    private String currentUser;

    DataAdapter dataAdapter;

    ArrayList<Users> users = new ArrayList<>();
    ArrayList<String> user_ids = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        user_toolbar = findViewById(R.id.users_toolbar);
        setSupportActionBar(user_toolbar);
        getSupportActionBar().setTitle("Пользователи");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();


        users_list = findViewById(R.id.user_list);
        users_list.setHasFixedSize(true);
        users_list.setLayoutManager(new LinearLayoutManager(this));

        dataAdapter = new DataAdapter( this, users, user_ids);

        users_list.setAdapter(dataAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(users.size() == 0)
        loadUsers();
    }

    private void loadUsers(){
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String user_id = dataSnapshot.getRef().getKey();
                Users user = dataSnapshot.getValue(Users.class);
                if(!user_id.equals(currentUser)) {
                    users.add(user);
                    user_ids.add(user_id);
                }

                //WE DON'T WANT TO SEE OURSELVES IN USER LIST TO SEND FRIEND REQUEST

                //System.out.println("USER_ID" + user_id);

                dataAdapter.notifyDataSetChanged();
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
