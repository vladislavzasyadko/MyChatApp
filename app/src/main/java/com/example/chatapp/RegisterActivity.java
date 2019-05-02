package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {



    //FireBase authentication
    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    //Register Layout
    private EditText email;
    private EditText new_login;
    private EditText password;
    private EditText passwordApproval;
    private Toolbar reg_toolbar;


    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            //window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        RegisterActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.activity_register);

        reg_toolbar = (Toolbar) findViewById(R.id.reg_toolbar);
        setSupportActionBar(reg_toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        reg_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(RegisterActivity.this, StartActivity.class);
                startActivity(back);
                finish();
            }
        });

        progress = new ProgressDialog(this);



        Button register = findViewById(R.id.registerButton);

        email = findViewById(R.id.email_field_reg);
        new_login = findViewById(R.id.reg_name_field);
        password = findViewById(R.id.password_field);
        passwordApproval = findViewById(R.id.password_field2);

        password.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                password.requestLayout();
                RegisterActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);

                return false;
            }
        });




        mAuth = FirebaseAuth.getInstance();



        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String txt_login = new_login.getText().toString();
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                String txt_passwordApproval = passwordApproval.getText().toString();


                if(TextUtils.isEmpty(txt_login) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                    Toast.makeText(RegisterActivity.this, "all fields are required", Toast.LENGTH_SHORT).show();
                } else if(!txt_password.equals(txt_passwordApproval)){
                    Toast.makeText(RegisterActivity.this, "passwords are not equal", Toast.LENGTH_SHORT).show();
                } else{
                    progress.setTitle("Регистрируем Вас.");
                    progress.setMessage("Подождите, пока мы создаем Вам аккаунт :)");
                    progress.setCanceledOnTouchOutside(false);
                    progress.show();
                    register(txt_email, txt_login, txt_password);
                }

            }
        });

    }

    private void register(String email, final String login, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            assert firebaseUser != null;
                            String user_id = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("username", login);
                            userMap.put("status", "I'm using this ChatApp!");
                            userMap.put("image", "default");
                            userMap.put("thumb_image", "default");

                            reference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progress.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });

                        } else{
                            progress.hide();
                            Toast.makeText(RegisterActivity.this, "You can't register with this email and password.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
