package com.example.appfood;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        try {
            databaseReferencePosty = FirebaseDatabase.getInstance("https://appfood-87dbd-default-rtdb.europe-west1.firebasedatabase.app/").getReference("posty");
        } catch (Exception e) {
            Log.d("xyz", "bład");
        }

        Post post = new Post("123", "user1", "123", "dfgdfsg", "123");
        tempwiadomoscilist.add(post);
        postAdapter.refreshPosts(tempwiadomoscilist);
        fetchDataFromFirebase();
/*
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<Post> tempwiadomoscilist=new ArrayList<>();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Post posty=dataSnapshot.getValue(Post.class);
                    tempwiadomoscilist.add(posty);
                    //msgAdapter.dodaj(wiadomosci);
                }


                postAdapter.refreshPosts(tempwiadomoscilist);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };


        databaseReferencePosty.addValueEventListener(postListener);


 */


        ImageView profile;
        ImageView newpost;
        profile = findViewById(R.id.goto_profile);
        newpost = findViewById(R.id.goto_new_post);

        newpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodajPosta("3. Spaghetti Aglio e Olio:\n" +
                        "Składniki:\n" +
                        "\n" +
                        "200g spaghetti\n" +
                        "4 ząbki czosnku\n" +
                        "50ml oliwy z oliwek\n" +
                        "Szczypta płatków chili, natka pietruszki\n" +
                        "Przygotowanie:\n" +
                        "\n" +
                        "Ugotuj spaghetti wg wskazówek na opakowaniu.\n" +
                        "Podsmaż czosnek w oliwie na złoty kolor, dodaj płatki chili.\n" +
                        "Dodaj ugotowane spaghetti i wymieszaj.\n" +
                        "Posyp posiekaną natką pietruszki.");
                dodajPosta("2. Spaghetti Aglio e Olio:\n" +
                        "Składniki:\n" +
                        "\n" +
                        "200g spaghetti\n" +
                        "4 ząbki czosnku\n" +
                        "50ml oliwy z oliwek\n" +
                        "Szczypta płatków chili, natka pietruszki\n" +
                        "Przygotowanie:\n" +
                        "\n" +
                        "Ugotuj spaghetti wg wskazówek na opakowaniu.\n" +
                        "Podsmaż czosnek w oliwie na złoty kolor, dodaj płatki chili.\n" +
                        "Dodaj ugotowane spaghetti i wymieszaj.\n" +
                        "Posyp posiekaną natką pietruszki.");

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
                    String author = postSnapshot.child("author").getValue(String.class);
                    String authorImageUrl = postSnapshot.child("authorImageUrl").getValue(String.class);
                    String description = postSnapshot.child("description").getValue(String.class);
                    String imageUrl = postSnapshot.child("imageUrl").getValue(String.class);
                    String postId = postSnapshot.child("postId").getValue(String.class);

                    // Utwórz obiekt Post
                    Post post = new Post(postId,author, authorImageUrl, description, imageUrl);

                    // Dodaj post do listy
                    tempwiadomoscilist.add(post);
                }
                postAdapter.refreshPosts(tempwiadomoscilist);
                // Tutaj możesz zaktualizować interfejs użytkownika, jeśli potrzebne
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Obsłuż błąd, jeśli wystąpi
            }
        });
    }


    private void dodajPosta(String opis) {
        String msgid= UUID.randomUUID().toString();
        Post post=new Post(msgid,"user1","123",opis,"123");

        postAdapter.addPost(post);

        databaseReferencePosty.child(msgid).setValue(post);
    }
}