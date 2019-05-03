package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ChangeInfoActivity extends AppCompatActivity {

    private Toolbar change_toolbar;
    private EditText change_name;
    private EditText change_status;
    private Button save_changes_btn;

    private ProgressDialog progress;

    //Firebase
    private DatabaseReference reference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            //window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        change_toolbar = findViewById(R.id.change_info_toolbar);
        setSupportActionBar(change_toolbar);
        getSupportActionBar().setTitle("Change Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        String user_id = currentUser.getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);



        change_name = findViewById(R.id.change_name);
        change_status = findViewById(R.id.change_status);
        save_changes_btn = findViewById(R.id.save_info_btn);

        String hint_name = getIntent().getStringExtra("name");
        String hint_status = getIntent().getStringExtra("status");

        change_name.setText(hint_name);
        change_status.setText(hint_status);

        save_changes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress = new ProgressDialog(ChangeInfoActivity.this);
                progress.setTitle("Сохраняем изменения...");
                progress.setMessage("Подождите, пока мы добавим все изменения в ваш аккаунт:)");
                progress.show();
                String status = change_status.getText().toString();
                String name = change_name.getText().toString();
                if(TextUtils.isEmpty(status)||TextUtils.isEmpty(name))
                {
                    Toast.makeText(ChangeInfoActivity.this, "all fields are required", Toast.LENGTH_SHORT).show();
                }
                else{

                    Map changeMap = new HashMap<>();
                    changeMap.put("status", status);
                    changeMap.put("username", name);

                    reference.updateChildren(changeMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isComplete()){

                                progress.dismiss();

                            } else{
                                progress.hide();
                                Toast.makeText(ChangeInfoActivity.this, "An Error Occured", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }


            }
        });


    }
}
