package com.example.appfood;

import static android.opengl.ETC1.encodeImage;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.appfood.activities.MainActivity;
import com.example.appfood.activities.SignInActivity;
import com.example.appfood.utils.Constants;
import com.example.appfood.utils.PreferenceManger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Profile extends Activity {
    private ImageView imageViewAvatar;
    private TextView textViewEmail;
    private TextView textViewName;
    private TextView textViewUsername;
    private EditText editTextNewUsername;
    private Button buttonSaveUsername;
    private Button buttonUploadImage;
    private ImageView imageViewUploaded;
    private Uri imageUri;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private static final int PICK_IMAGE_REQUEST = 1;

    private static final int IMAGE_QUALITY = 50;

    private PreferenceManger preferenceManger;

    private Button button;
    private TextView textView;

    private FirebaseUser user;

    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.bottom_home:
                    startActivity(new Intent(getApplicationContext(), MainPostBrowserLayout.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                case R.id.bottom_top:
                    startActivity(new Intent(getApplicationContext(), tops.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                case R.id.profile:
                    return true;
                case R.id.danie:
                    startActivity(new Intent(getApplicationContext(), create_post.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                case R.id.chat_bot:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
            }
            return false;
        });


        textViewEmail = findViewById(R.id.textViewEmail);
        textViewName = findViewById(R.id.textViewName);
        editTextNewUsername = findViewById(R.id.editTextNewUsername);
        buttonSaveUsername = findViewById(R.id.buttonSaveUsername);
        imageViewUploaded = findViewById(R.id.profileImage);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);
        back = findViewById(R.id.button_yourposts);
        preferenceManger = new PreferenceManger(getApplicationContext());






        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();
            loadUserDetailsForProfile(userId);
        }
        buttonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, YourPosts.class);
                startActivity(intent);
                finish();
            }
        });

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LogIn.class);
            startActivity(intent);
            finish();
        }


        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            firestore.collection("users").document(userId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String email = documentSnapshot.getString("email");
                                String name = documentSnapshot.getString("name");



                                textViewEmail.setText("E-mail: " + email);
                                textViewName.setText("Username: " + name);
                            }
                        }
                    });
        }

        buttonSaveUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newUsername = editTextNewUsername.getText().toString();
                if (!newUsername.isEmpty()) {
                    textViewName.setText("Username: " + newUsername);
                    updateUsernameInFirestore(newUsername);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageViewUploaded.setImageBitmap(bitmap);
                updateImageInFirestore(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void updateImageInFirestore(Bitmap bitmap) {
        String encodedImage = encodeImage(bitmap);

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            firestore.collection("users").document(userId)
                    .update("image", encodedImage)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Image updated successfully in Firestore
                            Toast.makeText(Profile.this, "Image updated successfully", Toast.LENGTH_SHORT).show();
                            // Load the updated image using Glide
                        } else {
                            // Handle unsuccessful image update
                            Toast.makeText(Profile.this, "Failed to update image", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void loadUserDetailsForProfile(String userId) {
        firestore.collection("users").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Fetch user image from the document
                                String userImage = document.getString(Constants.KEY_IMAGE);

                                // Decode and set the user image
                                byte[] bytes = Base64.decode(userImage, Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                                if (imageViewUploaded != null) {
                                    imageViewUploaded.setImageBitmap(bitmap);
                                } else {
                                    Log.e("ProfileActivity", "ImageView is null");
                                }
                            } else {
                                Log.d("ProfileActivity", "No such document");
                            }
                        } else {
                            Log.d("ProfileActivity", "get failed with ", task.getException());
                        }
                    }
                });
    }





    private void updateUsernameInFirestore(String newUsername) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            firestore.collection("users").document(userId)
                    .update("name", newUsername)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Zaktualizowano nazwę użytkownika pomyślnie
                                Toast.makeText(Profile.this, "Username updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                // Obsłuż błąd
                                Toast.makeText(Profile.this, "Failed to update username", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }




    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Profile.this, MainPostBrowserLayout.class);
        startActivity(intent);
    }
}
