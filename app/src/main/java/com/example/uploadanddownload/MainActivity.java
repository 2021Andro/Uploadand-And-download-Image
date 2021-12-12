package com.example.uploadanddownload;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_CODE = 1;
    private static final String TAG = "MyTag";
    private ImageView image;
    private ProgressBar progressBar;
    private Uri imageUri;

    private StorageReference imageRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);


        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


    }

    public void buttonUpload(View view) {


        progressBar.setVisibility(View.VISIBLE);

        StorageReference imagesFolder = storageRef.child("Image Folder");

        imageRef = imagesFolder.child(UUID.randomUUID().toString());

        imageRef.putFile(imageUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {

                            progressBar.setVisibility(View.GONE);

                            image.setImageResource(R.drawable.ic_launcher_background);

                            Log.d(TAG, "Image on uploaded");

                        } else {

                            progressBar.setVisibility(View.GONE);

                            image.setImageResource(R.drawable.ic_launcher_background);

                            Log.d(TAG, "Image not uploaded");

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressBar.setVisibility(View.GONE);
                image.setImageResource(R.drawable.ic_launcher_background);

                Log.d(TAG, "Exception : " + e.getMessage());
            }
        });


    }

    public void buttonDownload(View view) {

        progressBar.setVisibility(View.VISIBLE);

        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'

                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "Image download : Path --> "+imageRef.getDownloadUrl().toString());

                String url = uri.toString();

                Glide.with(getApplicationContext()).load(url).into(image);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "Image download Exception : " + exception.getMessage());
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        progressBar.setVisibility(View.GONE);

    }

    @Override
    protected void onResume() {
        super.onResume();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

                intent.setType("image/*");

                startActivityForResult(Intent.createChooser(intent, "title"), IMAGE_CODE);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_CODE) {

            if (resultCode == RESULT_OK) {
                imageUri = data.getData();

                image.setImageURI(imageUri);

            }
            if (resultCode == RESULT_CANCELED) {

                image.setImageResource(R.drawable.ic_launcher_background);

            }
        }
    }
}