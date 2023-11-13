package com.example.appfood.post;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder>  {

    public Context mcontext;
    public List<Post> postlist;
    private FirebaseFirestore firestore;
    FirebaseAuth fbAuth;
    FirebaseUser fbUser;
    FirebaseStorage storage;
    StorageReference storageRef;
    DatabaseReference db;
    int likesam;

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
    public void odswierz(){
        notifyDataSetChanged();
    }


    public void clearPost(){
        postlist.clear();
        notifyDataSetChanged();
    }
    public void removeOldPosts() {
        Calendar oneWeekAgo = Calendar.getInstance();
        oneWeekAgo.add(Calendar.DAY_OF_YEAR, -7);

        Iterator<Post> iterator = postlist.iterator();
        while (iterator.hasNext()) {
            Post post = iterator.next();
            try {
                if (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(post.getDate()).before(oneWeekAgo.getTime())) {
                    iterator.remove();
                }
            }
            catch (Exception e){

            }
        }

        notifyDataSetChanged(); // Zaktualizuj RecyclerView po usunięciu postów
    }
    public void removeNotYourPosts(){
        Iterator<Post> iterator = postlist.iterator();
        fbAuth=FirebaseAuth.getInstance();
        fbUser=fbAuth.getCurrentUser();
        while (iterator.hasNext()) {
            Post post = iterator.next();
            try {
                if (!(post.getAuthorId().equals((String) fbUser.getUid()))) {

                    iterator.remove();
                }
            }
            catch (Exception e){

            }
        }

    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view    = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.MyViewHolder holder, int position) {




        try {
            db = FirebaseDatabase.getInstance("https://appfood-87dbd-default-rtdb.europe-west1.firebasedatabase.app/").getReference("postylikes");
        } catch (Exception e) {
            Log.d("xyz", "bład");
        }


        Log.d("hfg",likesam+"");
        fbAuth=FirebaseAuth.getInstance();
        fbUser=fbAuth.getCurrentUser();
        String currentuserid=fbUser.getUid();
        holder.gotoedit.setVisibility(View.GONE);
        holder.gotoedit.setEnabled(false);
        Post post=postlist.get(position);
        holder.postid=post.getPostId();
        holder.description.setText("Przygotowanie:\n\n"+post.getDescription());
        holder.comments.setText(post.getComments()+" comments");
        holder.ingredients.setText("Składniki:\n\n"+post.getIngredients());
        holder.postname.setText(post.getName());
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://appfood-87dbd.appspot.com").child(post.getImageUrl());
        try{
            final File file=File.createTempFile("image","jpg");
            storageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap= BitmapFactory.decodeFile(file.getAbsolutePath());
                    holder.postimage.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("uyty","brak");

                }
            });
        }
        catch (Exception e){
            Log.d("uyty","brak");
        }



        firestore=FirebaseFirestore.getInstance();
        DocumentReference docRef = firestore.collection("users").document(post.getAuthorId());
        if(post.getAuthorId().equals(currentuserid)){
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

        isLikes(post.getPostId(), holder.like);
        likesamount(holder.likes,holder.postid);
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance("https://appfood-87dbd-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Likes").child(post.getPostId())
                            .child(fbUser.getUid()).setValue(true);
                }
                else{
                    FirebaseDatabase.getInstance("https://appfood-87dbd-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Likes").child(post.getPostId())
                            .child(fbUser.getUid()).removeValue();
                }
                notifyDataSetChanged();
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
        private TextView likes;
        private TextView comments;
        private TextView postname;
        private TextView ingredients;
        private String postid;
        private ImageView like;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            gotoedit=itemView.findViewById(R.id.goto_edit_post);
            postimage=itemView.findViewById(R.id.post_image);
            description=itemView.findViewById(R.id.description);
            layout=itemView.findViewById(R.id.mainpostlayout);
            userimage=itemView.findViewById(R.id.image_profile);
            username=itemView.findViewById(R.id.username);
            likes=itemView.findViewById(R.id.likes);
            comments=itemView.findViewById(R.id.comments);
            postname=itemView.findViewById(R.id.name);
            ingredients=itemView.findViewById(R.id.ingredients);
            like=itemView.findViewById(R.id.like);


            gotoedit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),Edit_delete_post.class);
                    intent.putExtra("postid",postid);
                    Log.d("fsdggsfgfds",postid);
                    v.getContext().startActivity(intent);
                }
            });


        }
    }
    private void isLikes(String postid, ImageView imageview){
        FirebaseUser fbu=FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference=FirebaseDatabase.getInstance("https://appfood-87dbd-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Likes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(fbu.getUid()).exists()){
                    imageview.setSelected(true);
                    imageview.setTag("liked");
                }
                else {
                    imageview.setSelected(false);
                    imageview.setTag("like");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
    private void likesamount (TextView likes, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance("https://appfood-87dbd-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Likes").child(postid);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likesam = Integer.valueOf(String.valueOf(snapshot.getChildrenCount()));
                DatabaseReference referenceposty = FirebaseDatabase.getInstance("https://appfood-87dbd-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("posty").child(postid);
                referenceposty.child("likes").setValue(likesam);
                likes.setText(snapshot.getChildrenCount()+" likes");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}
