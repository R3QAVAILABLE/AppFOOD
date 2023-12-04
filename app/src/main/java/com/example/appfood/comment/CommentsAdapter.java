package com.example.appfood.comment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfood.R;
import com.example.appfood.post.Post;
import com.example.appfood.post.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder>{
    public Context mcontext;
    public List<Comment> commentslist;
    private FirebaseFirestore firestore;
    FirebaseAuth fbAuth;
    FirebaseUser fbUser;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference storageProfileRef;
    DatabaseReference db;

    public CommentsAdapter(Context mcontext) {
        this.mcontext = mcontext;

    }



    public void refreshPosts(List<Comment> lista){
        commentslist =lista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view    = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_activity,parent,false);

        return new CommentsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.MyViewHolder holder, int position) {

        Comment comment=commentslist.get(position);
        holder.commenttext.setText(comment.getCommenttext());
        firestore=FirebaseFirestore.getInstance();
        DocumentReference docRef = firestore.collection("users").document(comment.getAuthorid());


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
        storage = FirebaseStorage.getInstance();
        storageProfileRef = storage.getReferenceFromUrl("gs://appfood-87dbd.appspot.com").child("profile_images/"+comment.getAuthorid()+".jpg");

        try{
            final File file=File.createTempFile("image","jpg");
            storageProfileRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap= BitmapFactory.decodeFile(file.getAbsolutePath());
                    holder.userimage.setImageBitmap(bitmap);
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

    }

    @Override
    public int getItemCount() {
        return commentslist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView userimage;
        private TextView username;
        private TextView commenttext;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userimage=itemView.findViewById(R.id.author_image_profile);
            username=itemView.findViewById(R.id.commentauthor);
            commenttext=itemView.findViewById(R.id.commenttext);

        }
    }

}
