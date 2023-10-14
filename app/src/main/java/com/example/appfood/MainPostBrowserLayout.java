package com.example.appfood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.widget.ImageView;

import com.example.appfood.post.Post;
import com.example.appfood.post.PostAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainPostBrowserLayout extends AppCompatActivity {

    String idUzytkownik;
    DatabaseReference databaseReferencePosty;
    String post;
    PostAdapter postAdapter;
    String timeStamp;
    RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_post_browser_layout);
        recyclerView=findViewById(R.id.recyclerview);
        //idUzytkownik =getIntent().getStringExtra("id");
        //post= idUzytkownik + FirebaseAuth.getInstance().getUid();

        postAdapter=new PostAdapter(this);
        recyclerView.setAdapter(postAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
      //databaseReferencePosty= FirebaseDatabase.getInstance("https://projekt-chat-9295e-default-rtdb.europe-west1.firebasedatabase.app/").getReference("czaty").child(post);
     //   postAdapter.clearPost();

        List<Post> tempwiadomoscilist=new ArrayList<>();
        tempwiadomoscilist.add(new Post("1","testauthor1","123","1. Sałatka Caprese:\n" +
                "Składniki:\n" +
                "\n" +
                "4 pomidory\n" +
                "200g mozzarelli\n" +
                "Świeża bazylia\n" +
                "Sól, pieprz, oliwa z oliwek\n" +
                "Przygotowanie:\n" +
                "\n" +
                "Pokrój pomidory i mozzarellę w plastry.\n" +
                "Układaj na talerzu naprzemiennie: pomidory, mozzarella i liście bazylii.\n" +
                "Posól, popieprz i polej oliwą z oliwek.1","123"));
        tempwiadomoscilist.add(new Post("2","testauthor2","123","2. Omlet z Warzywami:\n" +
                "Składniki:\n" +
                "\n" +
                "4 jajka\n" +
                "1 papryka\n" +
                "1 pomidor\n" +
                "50g sera żółtego\n" +
                "Sól, pieprz, masło\n" +
                "Przygotowanie:\n" +
                "\n" +
                "Roztrzep jajka w misce, dodaj sól i pieprz.\n" +
                "Pokrój paprykę i pomidora w kostkę, podsmaż na maśle.\n" +
                "Wlej roztrzepane jajka do patelni, dodaj warzywa i starty ser.\n" +
                "Smaż na małym ogniu do zastygnięcia.","123"));
        tempwiadomoscilist.add(new Post("2","testauthor3","123","3. Spaghetti Aglio e Olio:\n" +
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
                "Posyp posiekaną natką pietruszki.","123"));

        postAdapter.refreshPosts(tempwiadomoscilist);

        ImageView profile;
        profile=findViewById(R.id.goto_profile);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPostBrowserLayout.this, Profile.class);
                startActivity(intent);
            }
        });

    }
}