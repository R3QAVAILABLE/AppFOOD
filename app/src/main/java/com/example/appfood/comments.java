package com.example.appfood;

import androidx.annotation.NonNull;
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

import com.example.appfood.comment.Comment;
import com.example.appfood.comment.CommentsAdapter;
import com.example.appfood.post.Post;
import com.example.appfood.post.PostAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class comments extends AppCompatActivity {

    EditText commenttext;
    ImageView sendcomment;

    String postid;
    FirebaseAuth fbAuth;
    FirebaseUser fbUser;
    RecyclerView recyclerView;
    CommentsAdapter commentsAdapter;
    List<Comment> tempcommentslist = new ArrayList<>();
    DatabaseReference databaseReferenceComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        fbAuth=FirebaseAuth.getInstance();
        fbUser=fbAuth.getCurrentUser();


        commenttext=findViewById(R.id.commenttosend);
        sendcomment=findViewById(R.id.sendcomment);
        Intent intent=getIntent();
        postid=intent.getStringExtra("postid");
        sendcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment();
            }
        });
        recyclerView = findViewById(R.id.recyclercomments);
        commentsAdapter = new CommentsAdapter(this);
        recyclerView.setAdapter(commentsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentsAdapter.refreshPosts(tempcommentslist);

        try {
            databaseReferenceComments = FirebaseDatabase.getInstance("https://appfood-87dbd-default-rtdb.europe-west1.firebasedatabase.app/").getReference("comments").child(postid);
        } catch (Exception e) {
            Log.d("xyz", "bład");
        }


        fetchDataFromFirebase();

    }

    private void fetchDataFromFirebase() {


        databaseReferenceComments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tempcommentslist.clear();


                for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                    // Pobierz dane z DataSnapshot
                    String commenttext = commentSnapshot.child("commenttext").getValue(String.class);
                    String authorId = commentSnapshot.child("authorid").getValue(String.class);
                    String date=commentSnapshot.child("date").getValue(String.class);

                    // Utwórz obiekt Post
                    Comment comment = new Comment(authorId,date,commenttext);

                    // Dodaj post do listy
                    tempcommentslist.add(comment);
                }

                Collections.sort(tempcommentslist);

                commentsAdapter.refreshPosts(tempcommentslist);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Obsłuż błąd, jeśli wystąpiiii
            }
        });

    }



    private void addComment() {
        DatabaseReference reference= FirebaseDatabase.getInstance("https://appfood-87dbd-default-rtdb.europe-west1.firebasedatabase.app/").getReference("comments").child(postid);
         String msgid= UUID.randomUUID().toString();
        String date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Calendar.getInstance().getTime());
        Comment comment=new Comment(fbUser.getUid(),date,commenttext.getText().toString());
        reference.child(msgid).setValue(comment);
        commenttext.setText("");
    }
}