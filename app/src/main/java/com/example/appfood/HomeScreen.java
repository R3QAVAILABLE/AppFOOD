package com.example.appfood;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeScreen extends AppCompatActivity {

        FirebaseAuth auth;
        Button button;

        TextView textView;

        FirebaseUser user;
        @SuppressLint("MissingInflatedId")
        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.homescreenlayout);
            Button buttonNavigate;
            auth = FirebaseAuth.getInstance();
            button = findViewById(R.id.logout);
            textView = findViewById(R.id.user_details);
            user = auth.getCurrentUser();

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


            buttonNavigate = findViewById(R.id.buttonprofile);

            // Obsługa kliknięcia w przycisk
            buttonNavigate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Tworzenie intencji do przeniesienia użytkownika do innej aktywności
                    Intent intent = new Intent(HomeScreen.this, Profile.class);

                    // Rozpoczęcie nowej aktywności
                    startActivity(intent);
                }
            });


        }
    }
