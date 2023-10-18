package com.example.appfood;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.appfood.post.Post;
import com.example.appfood.post.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MainPostBrowserLayout extends AppCompatActivity {

    String idUzytkownik;
    DatabaseReference databaseReferencePosty;
    String post;
    PostAdapter postAdapter;
    String timeStamp;
    RecyclerView recyclerView;
    List<Post> tempwiadomoscilist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_post_browser_layout);
        recyclerView = findViewById(R.id.recyclerview);
        //idUzytkownik =getIntent().getStringExtra("id");
        //post= idUzytkownik + FirebaseAuth.getInstance().getUid();

        postAdapter = new PostAdapter(this);
        recyclerView.setAdapter(postAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Post post = new Post("123", "user1", "123", "dfgdfsg", "123","123","123",1,1);
        tempwiadomoscilist.add(post);
        postAdapter.refreshPosts(tempwiadomoscilist);
        try {
            databaseReferencePosty = FirebaseDatabase.getInstance("https://appfood-87dbd-default-rtdb.europe-west1.firebasedatabase.app/").getReference("posty");
        } catch (Exception e) {
            Log.d("xyz", "bład");
        }


        fetchDataFromFirebase();

        ImageView profile;
        ImageView newpost;

        profile = findViewById(R.id.goto_profile);
        newpost = findViewById(R.id.goto_new_post);


        newpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPostBrowserLayout.this, create_post.class);
                startActivity(intent);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPostBrowserLayout.this, Profile.class);
                startActivity(intent);
            }
        });

    }

    private void fetchDataFromFirebase() {
        // Ustaw ValueEventListener
        databaseReferencePosty.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Wyczyść listę przed dodaniem nowych danych
                tempwiadomoscilist.clear();


                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    // Pobierz dane z DataSnapshot
                    String authorId = postSnapshot.child("authorId").getValue(String.class);
                    int comments = postSnapshot.child("comments").getValue(int.class);
                    String description = postSnapshot.child("description").getValue(String.class);
                    String imageUrl = postSnapshot.child("imageUrl").getValue(String.class);
                    String ingredients = postSnapshot.child("ingredients").getValue(String.class);
                    int likes = postSnapshot.child("likes").getValue(int.class);
                    String name = postSnapshot.child("name").getValue(String.class);
                    String postId = postSnapshot.child("postId").getValue(String.class);
                    String tag = postSnapshot.child("tag").getValue(String.class);

                    // Utwórz obiekt Post
                    Post post = new Post( postId,  authorId,  imageUrl,  name,  ingredients,  description,  tag,  likes,  comments);

                    // Dodaj post do listy
                    tempwiadomoscilist.add(post);
                }
                postAdapter.refreshPosts(tempwiadomoscilist);
                // Tutaj możesz zaktualizować interfejs użytkownika, jeśli potrzebne
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Obsłuż błąd, jeśli wystąpiiii
            }
        });

    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Czy chcesz wyjść z aplikacji?")
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish(); // Zamykanie aplikacji
                    }
                })
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel(); // Zamykanie dialogu
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}