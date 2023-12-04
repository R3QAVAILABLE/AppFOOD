package com.example.appfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.appfood.post.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Edit_delete_post extends AppCompatActivity {
    View usun;
    View cofanie;
    View zapisz;
    DatabaseReference databaseReferencePosty;
    String postid;
    String NazwaDania;
    String SkladnikiDania;
    String InstrukcjaDania;
    String imageUrl;
    String userid;
    String tag;
    int likes;
    int comments;
    String newOpis;
    String newNazwa;
    String newSkladniki;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delete_post);
        DatabaseReference reference= FirebaseDatabase.getInstance("https://appfood-87dbd-default-rtdb.europe-west1.firebasedatabase.app/").getReference("comments");

        cofanie = findViewById(R.id.button_cancel);
        usun=findViewById(R.id.button_usun);
        zapisz=findViewById(R.id.button_zapisz);
        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        EditText nazwa_dania = findViewById(R.id.nazwa_dania);

        EditText skladniki = findViewById(R.id.skladniki_dania);

        EditText instrukcja = findViewById(R.id.instrukcja_dania);

        zapisz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newNazwa=nazwa_dania.getText().toString();
                newOpis=instrukcja.getText().toString();
                newSkladniki=skladniki.getText().toString();
                if(!newNazwa.isEmpty()&&!newSkladniki.isEmpty()&&!newOpis.isEmpty()) {
                    edytujPosta(newNazwa,newOpis, newSkladniki);
                    Intent intent = new Intent(Edit_delete_post.this, MainPostBrowserLayout.class);
                    startActivity(intent);
                }
            }
        });


        usun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imgurltodelete;
                FirebaseStorage storage;
                StorageReference storageRef;
                storage = FirebaseStorage.getInstance();
                storageRef = storage.getReferenceFromUrl("gs://appfood-87dbd.appspot.com").child(imageUrl);

// Delete the file
                storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                    }
                });
                reference.child(postid).removeValue();
                databaseReferencePosty.child(postid).removeValue();
                Intent intent = new Intent(Edit_delete_post.this, MainPostBrowserLayout.class);
                startActivity(intent);
            }
        });
        cofanie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Edit_delete_post.this, MainPostBrowserLayout.class);
                startActivity(intent);
            }
        });
        try {
            databaseReferencePosty = FirebaseDatabase.getInstance("https://appfood-87dbd-default-rtdb.europe-west1.firebasedatabase.app/").getReference("posty");
        } catch (Exception e) {
            Log.d("xyz", "bład");
        }
        databaseReferencePosty.child(postid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {

                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    NazwaDania=String.valueOf(task.getResult().child("name").getValue());
                    SkladnikiDania=String.valueOf(task.getResult().child("ingredients").getValue());
                    InstrukcjaDania=String.valueOf(task.getResult().child("description").getValue());
                    imageUrl=String.valueOf(task.getResult().child("imageUrl").getValue());
                    userid=String.valueOf(task.getResult().child("authorId").getValue());
                    tag=String.valueOf(task.getResult().child("date").getValue());
                    likes=task.getResult().child("likes").getValue(Integer.class);
                    comments=task.getResult().child("comments").getValue(Integer.class);
                    nazwa_dania.setText(NazwaDania);
                    skladniki.setText(SkladnikiDania);
                    instrukcja.setText(InstrukcjaDania);
                }
            }
        });


    }
    private void edytujPosta(String name, String description, String ingredients) {
        Post post=new Post(postid,userid,imageUrl,name,ingredients, description,tag,likes,comments);
        databaseReferencePosty.child(postid).setValue(post);
    }


}