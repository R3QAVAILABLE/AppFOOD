package com.example.appfood;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.example.appfood.activities.MainActivity;
import com.example.appfood.activities.SignInActivity;
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
        imageViewUploaded = findViewById(R.id.imageViewUploaded);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);
        back = findViewById(R.id.button_yourposts);




        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

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

        buttonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePicker();
            }
        });

        // Ładowanie obrazka z Firebase Storage
        loadProfileImage();
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewUploaded.setImageURI(imageUri);
            imageViewUploaded.setVisibility(View.VISIBLE);

            // Zapisywanie obrazka do Firebase Storage
            uploadImageToFirebaseStorage();
        }
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

    private void uploadImageToFirebaseStorage() {
        if (imageUri != null) {
            StorageReference storageRef = storage.getReference().child("profile_images/" + auth.getCurrentUser().getUid() + ".jpg");

            storageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Przesłanie obrazka pomyślnie
                            Toast.makeText(Profile.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                            // Teraz można pobrać URL obrazka i zapisać go w Firestore
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            // Obsłuż błąd
                            Toast.makeText(Profile.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void loadProfileImage() {
        StorageReference storageRef = storage.getReference().child("profile_images/" + auth.getCurrentUser().getUid() + ".jpg");

        final long MAX_BUFFER_SIZE = 5 * 1024 * 1024;
        storageRef.getBytes(MAX_BUFFER_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Konwersja tablicy bajtów na obiekt Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                // Wyświetlenie obrazka w ImageView
                imageViewUploaded.setImageBitmap(bitmap);
                imageViewUploaded.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                // Obsłuż błąd, jeśli obrazka nie udało się pobrać
                Log.e("Profile", "Failed to load profile image", e);
                Toast.makeText(Profile.this, "Failed to load profile image", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Profile.this, MainPostBrowserLayout.class);
        startActivity(intent);
    }
}
