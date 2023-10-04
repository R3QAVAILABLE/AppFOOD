package com.example.appfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;



public class EmailSignUpActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button signUpButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        //okok
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        signUpButton = findViewById(R.id.sign_up_button);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpWithEmail();
            }
        });
    }

    private void signUpWithEmail() {
        final String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Rejestracja zakończona powodzeniem
                            Toast.makeText(EmailSignUpActivity.this, "Rejestracja zakończona powodzeniem", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();
                                String name = extractNameFromEmail(email);
                                saveUserDataToFirestore(userId, email, name);
                            }
                            Intent intent = new Intent(EmailSignUpActivity.this, HomeScreen.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Obsługa błędu rejestracji
                            Toast.makeText(EmailSignUpActivity.this, "Wystąpił błąd podczas rejestracji", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private String extractNameFromEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex != -1) {
            return email.substring(0, atIndex);
        }
        return "";
    }

    private void saveUserDataToFirestore(String userId, String email, String name) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", userId);
        userData.put("email", email);
        userData.put("name", name);

        mFirestore.collection("users").document(userId)
                .set(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Zapisano dane użytkownika w Firestore
                        } else {
                            // Obsługa błędu zapisu danych
                        }
                    }
                });
    }
    public void previous (View view){
        startActivity(new Intent(EmailSignUpActivity.this,MainActivity.class));
    }
}

