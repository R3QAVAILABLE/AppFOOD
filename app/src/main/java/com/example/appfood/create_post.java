package com.example.appfood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appfood.activities.MainActivity;
import com.example.appfood.post.Post;
import com.example.appfood.post.PostAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class create_post extends AppCompatActivity {

    DatabaseReference databaseReferencePosty;
    FirebaseAuth fbAuth;
    FirebaseUser fbUser;
    ImageView image_post;
    View zdjecie;
    Uri imageUrl;
    StorageReference storageReference;
    String imagepath="brak";
    boolean fotoselected=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.danie);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.bottom_home:
                    startActivity(new Intent(getApplicationContext(), MainPostBrowserLayout.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                case R.id.bottom_top:
                    startActivity(new Intent(getApplicationContext(), tops.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                case R.id.danie:
                    return true;
                case R.id.profile:
                    startActivity(new Intent(getApplicationContext(), Profile.class));
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
        fbAuth=FirebaseAuth.getInstance();
        fbUser=fbAuth.getCurrentUser();
        View cofanie = findViewById(R.id.button_cancel);
        View zapisanie = findViewById(R.id.button_zapisz);
        zdjecie=findViewById(R.id.button_wybierz_zdjecie);
        image_post=findViewById(R.id.post_image);
        try {
            databaseReferencePosty = FirebaseDatabase.getInstance("https://appfood-87dbd-default-rtdb.europe-west1.firebasedatabase.app/").getReference("posty");
        } catch (Exception e) {
            Log.d("xyz", "bład");
        }
        zdjecie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        zapisanie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText nazwa_dania = findViewById(R.id.nazwa_dania);
                    String NazwaDania = nazwa_dania.getText().toString();
                EditText skladniki = findViewById(R.id.skladniki_dania);
                    String SkladnikiDania = skladniki.getText().toString();
                EditText instrukcja = findViewById(R.id.instrukcja_dania);
                    String InstrukcjaDania = instrukcja.getText().toString();
                    if(!NazwaDania.isEmpty()&&!SkladnikiDania.isEmpty()&&!InstrukcjaDania.isEmpty()&&fotoselected==true) {
                        dodajPosta(NazwaDania,InstrukcjaDania,SkladnikiDania);
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


    private void uploadimage(){


        String msgid= UUID.randomUUID().toString();
        String filename=msgid+".jpg";
        imagepath="images/"+filename;
        storageReference= FirebaseStorage.getInstance().getReference("images/"+filename);

        storageReference.putFile(imageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image_post.setImageURI(imageUrl);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    private void selectImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && data != null && data.getData() != null){
            imageUrl=data.getData();
            image_post.setImageURI(imageUrl);
            fotoselected=true;
        }
    }

    private void dodajPosta(String name, String description, String ingredients) {
        String msgid= UUID.randomUUID().toString();
        String date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Calendar.getInstance().getTime());
        uploadimage();
        Post post=new Post(msgid,fbUser.getUid(),imagepath,name,ingredients, description,date,1,1);
        databaseReferencePosty.child(msgid).setValue(post);
    }
}