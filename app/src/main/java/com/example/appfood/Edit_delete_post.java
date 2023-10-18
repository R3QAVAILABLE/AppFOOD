package com.example.appfood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Edit_delete_post extends AppCompatActivity {
    View cofanie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delete_post);
        cofanie = findViewById(R.id.button_cancel);

        cofanie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Edit_delete_post.this,MainPostBrowserLayout.class);
                startActivity(intent);
            }
        });
    }
}