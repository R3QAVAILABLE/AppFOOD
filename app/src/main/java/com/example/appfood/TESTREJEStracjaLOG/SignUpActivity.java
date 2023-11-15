package com.example.appfood.TESTREJEStracjaLOG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.appfood.R;
import com.example.appfood.MainPostBrowserLayout;
import com.example.appfood.databinding.ActivitySignUpBinding;
import com.example.appfood.models.Users;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    private FirebaseAuth mAuth;    // authentication (firebase)
    FirebaseDatabase database;
    private GoogleSignInClient googleClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {               // onCreate Method

        super.onCreate(savedInstanceState);

        // binding
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // firebase
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://appfood-87dbd-default-rtdb.europe-west1.firebasedatabase.app/");

        // dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.layout_loading_dialog);
        AlertDialog dialog = builder.create();

        // Google login
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleClient = GoogleSignIn.getClient(this,options);

        // onClick Listener
        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();

                String uName = binding.usernameInput.getText().toString();
                String email = binding.emailInput.getText().toString();
                String pwd = binding.passwordInput.getText().toString();

                if(!uName.isEmpty() && !email.isEmpty() && !pwd.isEmpty()) {

                    mAuth.createUserWithEmailAndPassword(email,pwd).
                            addOnCompleteListener(task -> {
                                dialog.dismiss();
                                if(task.isSuccessful()) {
                                    Users user = new Users(uName, email, pwd);
                                    String id = task.getResult().getUser().getUid();
                                    database.getReference().child("Users").child(id).setValue(user);
                                    Toast.makeText(SignUpActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                                    // move to main activity
                                    Intent intent = new Intent(SignUpActivity.this, MainPostBrowserLayout.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(SignUpActivity.this, "Enter Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(mAuth.getCurrentUser()!=null) {
            // move to main activity
            Intent intent = new Intent(SignUpActivity.this, MainPostBrowserLayout.class);
            startActivity(intent);
            finish();
        }

        binding.txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // move to sign in activity
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.googleSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(SignUpActivity.this, "google btn clicked", Toast.LENGTH_SHORT).show();
                Intent intent = googleClient.getSignInIntent();
                startActivityForResult(intent,1234);

            }
        });

    }


    // GOOGLE LOGIN/SIGNUP CODE

    // int RC_SIGN_IN = 65;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1234){
            Task<GoogleSignInAccount> task= GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Users users = new Users();
                                    users.setUserId(user.getUid());
                                    users.setUsername(user.getDisplayName());
                                    users.setProfilePic(user.getPhotoUrl().toString());

                                    database.getReference().child("Users").child(user.getUid()).setValue(users);

                                    Intent intent = new Intent(getApplicationContext(), MainPostBrowserLayout.class);
                                    startActivity(intent);
                                    finish();

                                    Toast.makeText(SignUpActivity.this, "Welcome!! "+ user.getDisplayName(), Toast.LENGTH_SHORT).show();

                                }
                                else {
                                    Toast.makeText(SignUpActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            Intent intent = new Intent(getApplicationContext(), MainPostBrowserLayout.class);
            startActivity(intent);
            finish();
        }
    }

}