package com.example.chatapp;

import android.app.ProgressDialog;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    DatabaseReference reference;

    EditText login;
    EditText password;

    Toolbar sign_in_toolbar;

    ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            //window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_sign_in);

        Button log_btn = findViewById(R.id.signInButton);

        login = findViewById(R.id.login_field);
        password = findViewById(R.id.log_password_field);

        mAuth = FirebaseAuth.getInstance();

        sign_in_toolbar = (Toolbar) findViewById(R.id.sign_in_toolbar);
        setSupportActionBar(sign_in_toolbar);
        getSupportActionBar().setTitle("Sign In");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sign_in_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(SignInActivity.this, StartActivity.class);
                startActivity(back);
                finish();
            }
        });


        progress = new ProgressDialog(this);




        log_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_login = login.getText().toString();
                String txt_password = password.getText().toString();
                if(TextUtils.isEmpty(txt_login)|| TextUtils.isEmpty(txt_password)){
                    Toast.makeText(SignInActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else{
                    progress.setTitle("Авторизация...");
                    progress.setMessage("Подождите, пока мы входим в Ваш аккаунт :)");
                    progress.setCanceledOnTouchOutside(false);
                    progress.show();
                    login(txt_login, txt_password);
                }
            }
        });


    }

    private void login(String login, String password){
        mAuth.signInWithEmailAndPassword(login, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progress.dismiss();
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            finish();
                        } else {
                            progress.hide();
                            Toast.makeText(SignInActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}
