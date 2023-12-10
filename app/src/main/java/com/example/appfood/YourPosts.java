package com.example.appfood;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfood.post.Post;
import com.example.appfood.post.PostAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class YourPosts extends AppCompatActivity {

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
        setContentView(R.layout.activity_your_posts);
        recyclerView = findViewById(R.id.recyclerview);
        postAdapter = new PostAdapter(this);
        recyclerView.setAdapter(postAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter.refreshPosts(tempwiadomoscilist);


        try {
            databaseReferencePosty = FirebaseDatabase.getInstance("https://appfood-87dbd-default-rtdb.europe-west1.firebasedatabase.app/").getReference("posty");
        } catch (Exception e) {
            Log.d("xyz", "bład");
        }


        fetchDataFromFirebase();

        ImageView profile;




    }

    private void fetchDataFromFirebase() {


        databaseReferencePosty.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                    String date = postSnapshot.child("date").getValue(String.class);

                    // Utwórz obiekt Post
                    Post post = new Post( postId,  authorId,  imageUrl,  name,  ingredients,  description,  date,  likes,  comments);

                    // Dodaj post do listy
                    tempwiadomoscilist.add(post);
                }
                postAdapter.removeNotYourPosts();
                Collections.sort(tempwiadomoscilist, new Comparator<Post>() {
                    @Override
                    public int compare(Post post1, Post post2) {
                        return Integer.compare(post2.getLikes(), post1.getLikes());
                    }
                });
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
        Intent intent = new Intent(YourPosts.this, MainPostBrowserLayout.class);
        startActivity(intent);
    }

}