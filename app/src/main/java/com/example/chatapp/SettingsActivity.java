package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    //Database
    private DatabaseReference reference;
    private FirebaseUser currentUser;

    //Storage
    private StorageReference storageReference;


    //LAYOUT
    private CircleImageView profilePic;
    private TextView display_name;
    private TextView status_field;
    private Button change_image;
    private Button change_info;
    private Toolbar settings_toolbar;
    private ProgressDialog image_load_progress;

    private static final int GALLERY_PICK = 1;

    private static final int MAX_LENGTH = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            //window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_settings);

        setLayout();

        settings_toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(settings_toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        String user_id = currentUser.getUid();

        storageReference = FirebaseStorage.getInstance().getReference();

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("username").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                //String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                display_name.setText(name);
                status_field.setText(status);
                Picasso.with(SettingsActivity.this).load(image).placeholder(R.mipmap.user2).into(profilePic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setLayout() {
        profilePic = findViewById(R.id.settings_image);
        display_name = findViewById(R.id.settings_display_name);
        status_field = findViewById(R.id.settings_status);
        change_image = findViewById(R.id.setting_button_img_change);
        change_info = findViewById(R.id.setting_button_status_change);

        change_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = display_name.getText().toString();
                String status = status_field.getText().toString();
                Intent change = new Intent(SettingsActivity.this, ChangeInfoActivity.class);
                change.putExtra("name", name);
                change.putExtra("status", status);
                startActivity(change);

            }
        });

        change_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallery, "SELECT IMAGE"), GALLERY_PICK);

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();//READY TO CROP THE IMAGE

            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                image_load_progress = new ProgressDialog(SettingsActivity.this);
                image_load_progress.setTitle("Загружаем изображение");
                image_load_progress.setMessage("Подождите, пока мы обновляем вашу профильную фотографию");
                image_load_progress.setCanceledOnTouchOutside(false);
                image_load_progress.show();

                Uri resultUri = result.getUri();

                File thumb_file = new File(resultUri.getPath());

                String currentUid = currentUser.getUid();

                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxHeight(200)
                        .setMaxWidth(200)
                        .compressToBitmap(thumb_file);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                final StorageReference filepath = storageReference.child("Profile_Images").child(currentUid + ".jpg");
                final StorageReference thumb_filepath = storageReference.child("Profile_Images").child("thumbs").child(currentUid + ".jpg");
                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                final String download_link = uri.toString();

                                UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        thumb_filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String thumb_download_url = uri.toString();

                                                Map updateMap = new HashMap<>();
                                                updateMap.put("image", download_link);
                                                updateMap.put("thumb_image", thumb_download_url);


                                                reference.updateChildren(updateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            image_load_progress.dismiss();
                                                            Toast.makeText(SettingsActivity.this, "Successfully Uploaded!", Toast.LENGTH_SHORT).show();

                                                        }
                                                    }
                                                });


                                            }
                                        });
                                    }
                                });



                            }
                        });
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(SettingsActivity.this, (CharSequence) error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }*/
}
