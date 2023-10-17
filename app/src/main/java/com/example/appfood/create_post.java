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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class create_post extends AppCompatActivity {

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
        setContentView(R.layout.activity_create_post);
        View cofanie = findViewById(R.id.button_cancel);
        View zapisanie = findViewById(R.id.button_zapisz);
        postAdapter = new PostAdapter(this);
        recyclerView.setAdapter(postAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Post post = new Post("123", "user1", "123", "dfgdfsg", "123");

        postAdapter = new PostAdapter(this);
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


                /*dodajPosta("3. Spaghetti Aglio e Olio:\n" +
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

                 */

                dodajPosta(NazwaDania);
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
    private void dodajPosta(String description) {
        String msgid= UUID.randomUUID().toString();
        Post post=new Post(msgid,"user1","123",description, "123");

        postAdapter.addPost(post);

        databaseReferencePosty.child(msgid).setValue(post);
    }
}