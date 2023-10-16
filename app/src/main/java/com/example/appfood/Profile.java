package com.example.appfood;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends AppCompatActivity {

    private TextView textViewName, textViewEmail;
    private FirebaseFirestore firestore;

    FirebaseAuth auth;
    Button button;

    TextView textView;

    FirebaseUser user;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        textView = findViewById(R.id.user_details);
        user = auth.getCurrentUser();

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

        if(user==null){
            Intent intent = new Intent(getApplicationContext(),LogIn.class);
            startActivity(intent);
            finish();
        }
        else{
            textView.setText(user.getEmail());

        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(),LogIn.class);
                startActivity(intent);
                finish();
            }
        });

    }
}