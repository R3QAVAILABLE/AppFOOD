package com.example.appfood;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends AppCompatActivity {

    private TextView textViewName, textViewEmail;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // Inicjalizacja widoków
        textViewName = findViewById(R.id.textViewName);
        textViewEmail = findViewById(R.id.textViewEmail);

        // Inicjalizacja Firestore
        firestore = FirebaseFirestore.getInstance();

        // Pobieranie danych z Firestore
        firestore.collection("users")
                .document("your_user_document_id") // Zastąp 'your_user_document_id' odpowiednim ID użytkownika
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String name = document.getString("name");
                                String email = document.getString("email");

                                // Wyświetlenie informacji o użytkowniku w widoku
                                textViewName.setText("Imię: " + name);
                                textViewEmail.setText("E-mail: " + email);
                            } else {
                                Log.d("UserProfileActivity", "Brak dokumentu");
                            }
                        } else {
                            Log.d("UserProfileActivity", "Błąd pobierania dokumentu", task.getException());
                        }
                    }
                });
    }
}