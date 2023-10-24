package com.example.appfood;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        textViewEmail = findViewById(R.id.textViewEmail);
        textViewName = findViewById(R.id.textViewName);
        textViewUsername = findViewById(R.id.textViewNazwa);
        editTextNewUsername = findViewById(R.id.editTextNewUsername);
        buttonSaveUsername = findViewById(R.id.buttonSaveUsername);
        imageViewUploaded = findViewById(R.id.imageViewUploaded);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);

        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

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
                                String username = documentSnapshot.getString("username");

                                textViewEmail.setText("E-mail: " + email);
                                textViewName.setText("Name: " + name);
                                textViewUsername.setText("Username: " + username);
                            }
                        }
                    });
        }

        buttonSaveUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newUsername = editTextNewUsername.getText().toString();
                if (!newUsername.isEmpty()) {
                    textViewUsername.setText("Username: " + newUsername);
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
                    .update("username", newUsername)
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

        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Pobieranie URL obrazka z Firebase Storage
                String imageUrl = uri.toString();

                // Wyświetlenie obrazka w ImageView
                imageViewUploaded.setImageURI(Uri.parse(imageUrl));
                imageViewUploaded.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                // Obsłuż błąd, jeśli obrazka nie udało się pobrać
                Toast.makeText(Profile.this, "Failed to load profile image", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
