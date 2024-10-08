package com.example.appfood;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.appfood.activities.MainActivity;
import com.example.appfood.post.Post;
import com.example.appfood.post.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainPostBrowserLayout extends AppCompatActivity {

    String idUzytkownik;
    DatabaseReference databaseReferencePosty;
    String post;
    PostAdapter postAdapter;
    String timeStamp;
    RecyclerView recyclerView;

    ImageView searchbtn;
    TextView searchtext;
    List<Post> tempwiadomoscilist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        //binding = ActivityMainPostBrowserLayoutBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());
        setContentView(R.layout.activity_main_post_browser_layout);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
          switch (item.getItemId()){
              case R.id.bottom_home:
                  return true;
              case R.id.bottom_top:
                  startActivity(new Intent(getApplicationContext(), tops.class));
                  overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                  finish();
                  return true;
              case R.id.profile:
                  startActivity(new Intent(getApplicationContext(), Profile.class));
                  overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                  finish();
                  return true;
              case R.id.danie:
                  startActivity(new Intent(getApplicationContext(), create_post.class));
                  overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                  finish();
                  return true;
              case R.id.chat_bot:
                  startActivity(new Intent(getApplicationContext(), MainActivity.class));
                  overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                  finish();
                  return true;

          }
          return false;
        });
        searchbtn=findViewById(R.id.search_btn);
        searchtext=findViewById(R.id.search);


        recyclerView = findViewById(R.id.recyclerview);
        postAdapter = new PostAdapter(this);
        recyclerView.setAdapter(postAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        /*
        Post post = new Post("123", "user1", "123", "dfgdfsg", "123","123","2023-11-06 14:33:21.475",1,1);
        tempwiadomoscilist.add(post);
        */
        postAdapter.refreshPosts(tempwiadomoscilist);


        try {
            databaseReferencePosty = FirebaseDatabase.getInstance("https://appfood-87dbd-default-rtdb.europe-west1.firebasedatabase.app/").getReference("posty");
        } catch (Exception e) {
            Log.d("xyz", "bład");
        }


       // replaceFragment(new MainPostBrowserLayout());

       /* bottomNavigationView.setBackground(null);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.home:
                    //replaceFragment(new MainPostBrowserLayout());
                    Intent intent = new Intent(this, MainPostBrowserLayout.class);
                    startActivity(intent);
                    break;

                case R.id.profile:
                   // replaceFragment(new Profile());
                    Intent intent1 = new Intent(this, Profile.class);
                    startActivity(intent1);
                    break;

                case R.id.danie:
                   // replaceFragment(new create_post());
                    Intent intent2 = new Intent(this, create_post.class);
                    startActivity(intent2);
                    break;


            }

            return true;
        });?*/


        fetchDataFromFirebase();

        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tekst;
                String wyszykiwane = searchtext.getText().toString();
                List<Post> tempserchpostlist = new ArrayList<>();


                for(Post p : tempwiadomoscilist){

                    tekst=p.getDescription();
                    Pattern pattern = Pattern.compile("#\\w+");
                    Matcher matcher = pattern.matcher(tekst);
                    while (matcher.find()) {
                        if(matcher.group().equals(wyszykiwane)){
                            tempserchpostlist.add(p);
                            Log.d("searchtest",p.getName());
                        }
                    }
                }
                if(tempserchpostlist.isEmpty()){
                    Toast.makeText(MainPostBrowserLayout.this, "Not found.", Toast.LENGTH_SHORT).show();
                }
                else {
                    postAdapter.refreshPosts(tempserchpostlist);
                }



            }
        });





    }
   /* private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }*/
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

                Collections.sort(tempwiadomoscilist);

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