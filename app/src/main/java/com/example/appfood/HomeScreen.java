package com.example.appfood;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

    public class HomeScreen extends AppCompatActivity {
        @SuppressLint("MissingInflatedId")
        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.homescreenlayout);
            Button buttonNavigate;


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
