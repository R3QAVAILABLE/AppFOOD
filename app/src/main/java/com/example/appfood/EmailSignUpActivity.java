package com.example.appfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;



public class EmailSignUpActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;
    Button buttonReg;

    FirebaseAuth mAuth;

    ProgressBar progressBar;

    TextView textView;
    FirebaseFirestore mFirestore;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sign_up);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        editTextEmail = findViewById(R.id.email_edit_text);
        editTextPassword = findViewById(R.id.password_edit_text);
        buttonReg = findViewById(R.id.sign_up_button);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                startActivity(intent);
                finish();
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email,password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(EmailSignUpActivity.this,"Enter email",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(EmailSignUpActivity.this,"Enter password",Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(EmailSignUpActivity.this, "Account created.",
                                            Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        String userId = user.getUid();
                                        String name = extractNameFromEmail(email);
                                        saveUserDataToFirestore(userId, email, password, name);
                                    }

                                    Intent intent = new Intent(getApplicationContext(), LogIn.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(EmailSignUpActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

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

    private void saveUserDataToFirestore(String userId, String email, String password, String name) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", userId);
        userData.put("email", email);
        userData.put("password", password);
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

