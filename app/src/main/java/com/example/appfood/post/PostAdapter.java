package com.example.appfood.post;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfood.Edit_delete_post;
import com.example.appfood.MainPostBrowserLayout;
import com.example.appfood.R;
import com.example.appfood.create_post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder>  {

    public Context mcontext;
    public List<Post> postlist;
    private FirebaseFirestore firestore;
    FirebaseAuth fbAuth;
    FirebaseUser fbUser;


    public PostAdapter(Context mcontext) {
        this.mcontext = mcontext;

    }

    public void refreshPosts(List<Post> lista){
        postlist =lista;
        notifyDataSetChanged();
    }

    public void addPost(Post post){
        postlist.add(post);
        notifyDataSetChanged();

    }

    public void clearPost(){
        postlist.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view    = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.MyViewHolder holder, int position) {
        fbAuth=FirebaseAuth.getInstance();
        fbUser=fbAuth.getCurrentUser();
        String currentuserid=fbUser.getUid();
        holder.gotoedit.setVisibility(View.GONE);
        holder.gotoedit.setEnabled(false);
        Post post=postlist.get(position);
        holder.description.setText(post.getDescription());
        firestore=FirebaseFirestore.getInstance();
        DocumentReference docRef = firestore.collection("users").document(post.getAuthor());
        if(post.getAuthor().equals(currentuserid)){
            holder.gotoedit.setVisibility(View.VISIBLE);
            holder.gotoedit.setEnabled(true);
        }

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        holder.username.setText(document.getString("name"));
                        //Log.d("qwerty", "DocumentSnapshot data: " + document.getData());
                    } else {
                        //Log.d("qwerty", "No such document");
                    }
                } else {
                    Log.d("qwerty", "get failed with ", task.getException());
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return postlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView description;
        private ImageView postimage;
        private RelativeLayout layout;
        private ImageView userimage;
        private TextView username;
        private ImageView gotoedit;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            gotoedit=itemView.findViewById(R.id.goto_edit_post);
            postimage=itemView.findViewById(R.id.post_image);
            description=itemView.findViewById(R.id.description);
            layout=itemView.findViewById(R.id.mainpostlayout);
            userimage=itemView.findViewById(R.id.image_profile);
            username=itemView.findViewById(R.id.username);

            gotoedit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),Edit_delete_post.class);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }


}
