package com.example.appfood;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostAdapter {

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView post_image;
        public TextView username,description;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
