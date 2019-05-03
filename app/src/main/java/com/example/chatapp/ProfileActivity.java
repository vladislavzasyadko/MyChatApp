package com.example.chatapp;

import android.app.ProgressDialog;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    Toolbar profile_toolbar;

    ImageView profile_image;
    TextView profile_username;
    TextView profile_status;
    Button send_request;
    //FOR USER DATA LOADING
    DatabaseReference reference;
    //FOR FRIEND REQUEST STORING AND LOADING
    DatabaseReference friendsRequestreference;
    //FOR FRIEND FEATURE
    DatabaseReference friendDatabase;

    FirebaseUser currentUser;

    ProgressDialog progress;

    String current_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            //window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        profile_image = findViewById(R.id.profile_image);
        profile_username = findViewById(R.id.profile_username);
        profile_status = findViewById(R.id.profile_status);
        send_request = findViewById(R.id.send_request_btn);

        current_state = "not_friends";

        profile_toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(profile_toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progress = new ProgressDialog(this);
        progress.setTitle("Загружаем профиль");
        progress.setMessage("Подождите, пока мы загружаем данные...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();


        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        friendsRequestreference = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");
        friendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("username").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                //String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                profile_username.setText(name);
                profile_status.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.mipmap.user2).into(profile_image);


                friendsRequestreference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id)){

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if(req_type.equals("received")){

                                current_state = "req_received";
                                send_request.setText("ACCEPT FRIEND REQUEST");

                            }
                            else if(req_type.equals("sent")){

                                current_state = "req_sent";
                                send_request.setText("CANCEL FRIEND REQUEST");

                            }
                            progress.dismiss();
                        }else {

                            friendDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(user_id)){

                                        current_state = "friends";
                                        send_request.setText("UNFRIEND");

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            progress.dismiss();
                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        send_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                send_request.setEnabled(false);

                //-------SENDING FRIEND REQUEST-----------

                if(current_state.equals("not_friends")){

                    friendsRequestreference.child(currentUser.getUid()).child(user_id).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                friendsRequestreference.child(user_id).child(currentUser.getUid()).child("request_type")
                                        .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        send_request.setEnabled(true);
                                        current_state = "req_sent";
                                        send_request.setText("CANCEL FRIEND REQUEST");

                                    }
                                });
                            }
                            else{
                                Toast.makeText(ProfileActivity.this, "Failed Sending Request", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

                //------CANCELLING FRIEND REQUEST--------

                if(current_state.equals("req_sent")){

                    friendsRequestreference.child(currentUser.getUid()).child(user_id).
                            removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                friendsRequestreference.child(user_id).child(currentUser.getUid())
                                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        send_request.setEnabled(true);
                                        current_state = "not_friends";
                                        send_request.setText("SEND FRIEND REQUEST");

                                    }
                                });
                            }
                            else{
                                Toast.makeText(ProfileActivity.this, "Failed Cancelling Request", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

                //-------ACCEPT FRIEND REQUEST-------
                if(current_state.equals("req_received")){

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    friendDatabase.child(currentUser.getUid()).child(user_id).setValue(currentDate)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            friendDatabase.child(user_id).child(currentUser.getUid()).setValue(currentDate)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    //------DELETING FRIEND REQUEST AFTER ACCEPTING------
                                    friendsRequestreference.child(currentUser.getUid()).child(user_id).
                                            removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                friendsRequestreference.child(user_id).child(currentUser.getUid())
                                                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        send_request.setEnabled(true);
                                                        current_state = "friends";
                                                        send_request.setText("UNFRIEND");

                                                    }
                                                });
                                            }
                                            else{
                                                Toast.makeText(ProfileActivity.this, "Failed Accept", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                                    //------------------------------------

                                }
                            });

                        }
                    });
                }

                //--------------UNFRIEND USER----------

                //-------ACCEPT FRIEND REQUEST-------
                if(current_state.equals("friends")){

                    friendDatabase.child(currentUser.getUid()).child(user_id).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    friendDatabase.child(user_id).child(currentUser.getUid()).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    send_request.setEnabled(true);
                                                    current_state = "not_friends";
                                                    send_request.setText("SEND FRIEND REQUEST");

                                                }
                                            });

                                }
                            });
                }
                //-----------------

            }
        });



    }
}
