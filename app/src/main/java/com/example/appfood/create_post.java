package com.example.appfood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appfood.post.Post;
import com.example.appfood.post.PostAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class create_post extends AppCompatActivity {

    DatabaseReference databaseReferencePosty;
    FirebaseAuth fbAuth;
    FirebaseUser fbUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        fbAuth=FirebaseAuth.getInstance();
        fbUser=fbAuth.getCurrentUser();
        View cofanie = findViewById(R.id.button_cancel);
        View zapisanie = findViewById(R.id.button_zapisz);
        try {
            databaseReferencePosty = FirebaseDatabase.getInstance("https://appfood-87dbd-default-rtdb.europe-west1.firebasedatabase.app/").getReference("posty");
        } catch (Exception e) {
            Log.d("xyz", "bład");
        }

        zapisanie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText nazwa_dania = findViewById(R.id.nazwa_dania);
                    String NazwaDania = nazwa_dania.getText().toString();
                EditText skladniki = findViewById(R.id.skladniki_dania);
                    String SkladnikiDania = skladniki.getText().toString();
                EditText instrukcja = findViewById(R.id.instrukcja_dania);
                    String InstrukcjaDania = instrukcja.getText().toString();
                    if(!NazwaDania.isEmpty()&&!SkladnikiDania.isEmpty()&&!InstrukcjaDania.isEmpty()) {
                        dodajPosta(NazwaDania, SkladnikiDania,InstrukcjaDania);
                        Intent intent = new Intent(create_post.this, MainPostBrowserLayout.class);
                        startActivity(intent);
                    }
            }
        });
        cofanie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(create_post.this, MainPostBrowserLayout.class);
                startActivity(intent);
            }
        });
    }
    private void dodajPosta(String name, String description, String ingredients) {
        String msgid= UUID.randomUUID().toString();
        Post post=new Post(msgid,fbUser.getUid(),"123",name,ingredients, description,"123",1,1);
        databaseReferencePosty.child(msgid).setValue(post);
    }
}