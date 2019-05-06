package com.example.chatapp;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String user_id;
    private String currentUser_id;
    private String user_name;
    private Toolbar chat_toolbar;

    private DatabaseReference rootRef;
    private FirebaseAuth auth;

    private TextView chat_user_name;
    private CircleImageView chat_user_image;

    private ImageButton chat_add_pic;
    private EditText chat_text_msg;
    private ImageButton chat_send_btn;

    private RecyclerView messagesList;
    private SwipeRefreshLayout swipeRefreshLayout;

    private final ArrayList<Messages> messages = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private MessageAdapter messageAdapter;


    private static final int TOTAL_TO_LOAD = 10;
    private static final int MAX_TEXT_LENGTH = 60;
    private int currentPage = 1;
    private int itemPosition = 0;
    private String lastKey = "";
    private String prevLastKey = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rootRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        currentUser_id = auth.getCurrentUser().getUid();


        user_id = getIntent().getStringExtra("user_id");
        user_name = getIntent().getStringExtra("user_name");


        chat_toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(chat_toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle(user_name);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View action_bar_view = inflater.inflate(R.layout.chat_app_bar, null);

        actionBar.setCustomView(action_bar_view);

        //-----------INITIATING TOOLBAR CONTENT-----------//
        chat_user_name = findViewById(R.id.chat_user_name);
        chat_user_image = findViewById(R.id.chat_user_image);


        //-----------INITIATING CHAT BOTTOM CONTENT------//
        //chat_add_pic = findViewById(R.id.add_pic_chat_btn);
        chat_text_msg = findViewById(R.id.chat_text_msg);
        chat_send_btn = findViewById(R.id.chat_send_msg);

        //------------INITIATING MESSAGES LIST--------//
        messageAdapter = new MessageAdapter(messages);

        messagesList = findViewById(R.id.messages_list);
        //swipeRefreshLayout = findViewById(R.id.message_swipe);
        layoutManager = new LinearLayoutManager(this);

        messagesList.setHasFixedSize(true);
        messagesList.setLayoutManager(layoutManager);
        messagesList.setAdapter(messageAdapter);

        loadMessages();

        chat_user_name.setText(user_name);

        rootRef.child("Users").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                Picasso.with(ChatActivity.this).load(thumb_image).placeholder(R.mipmap.user2).into(chat_user_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        rootRef.child("Chats").child(currentUser_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(user_id)){
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chats/" + currentUser_id + "/" + user_id, chatAddMap);
                    chatUserMap.put("Chats/" + user_id + "/" + currentUser_id, chatAddMap);

                    rootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if(databaseError != null){
                                System.out.println("Error is " + databaseError.getMessage().toString());
                            }
                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        chat_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMessage();
                chat_text_msg.setText("");
            }
        });

        /*swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //currentPage++;

                itemPosition = 0;

                loadMoreMessages();
            }
        });*/








    }

    /*private void loadMoreMessages() {

        DatabaseReference messageRef = rootRef.child("messages").child(currentUser_id).child(user_id);

        Query messageQuery = messageRef.orderByKey().endAt(lastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Messages message = dataSnapshot.getValue(Messages.class);



                if(prevLastKey.equals(dataSnapshot.getKey())){

                    messages.add(itemPosition++, message);
                } else{

                    prevLastKey = lastKey;
                }


                if(itemPosition == 1){
                    lastKey = dataSnapshot.getKey();
                }




                messageAdapter.notifyDataSetChanged();
                layoutManager.scrollToPositionWithOffset(10,0);

                swipeRefreshLayout.setRefreshing(false);




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
    }*/

    private void loadMessages() {

        DatabaseReference messageRef = rootRef.child("messages").child(currentUser_id).child(user_id);

        Query messageQuery = messageRef;//.limitToLast(currentPage * TOTAL_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        Messages message = dataSnapshot.getValue(Messages.class);
                        String key = dataSnapshot.getKey();


                        /*itemPosition++;

                        if(itemPosition == 1){

                            prevLastKey = key;
                            lastKey = key;
                        }*/

                        messages.add(message);
                        messageAdapter.notifyDataSetChanged();
                        messagesList.scrollToPosition(messages.size() - 1);

                        //swipeRefreshLayout.setRefreshing(false);


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

    private void sendMessage() {

        String message = chat_text_msg.getText().toString();

        if(message.length() > MAX_TEXT_LENGTH){

            Toast.makeText(this, "Слишком длинное сообщение", Toast.LENGTH_SHORT).show();

        }else if(!TextUtils.isEmpty(message)){

            String current_user_ref = "messages/" + currentUser_id + "/" + user_id;
            String chat_user_ref =  "messages/" + user_id + "/" + currentUser_id;

            DatabaseReference user_message_push = rootRef.child("messages")
                    .child(currentUser_id).child(user_id).push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", currentUser_id);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            rootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                    if(databaseError != null){
                        System.out.println("Error is " + databaseError.getMessage().toString());
                    }
                }
            });

        }
    }


}
