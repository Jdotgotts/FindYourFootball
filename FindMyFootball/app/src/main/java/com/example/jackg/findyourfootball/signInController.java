package com.example.jackg.findyourfootball;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class signInController extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_view);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        TextView goSetUpPage = findViewById(R.id.setUpAccountBtn);
        Button loginBtn = findViewById(R.id.loginBtn);
        TextView emailText = findViewById(R.id.field_email);
        TextView passwordText = findViewById(R.id.field_password);

        mAuth = FirebaseAuth.getInstance();   // Get instance of Firebase Authentication API

        goSetUpPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open sign up screen
                Intent intent = new Intent(signInController.this, signUpController.class);
                startActivity(intent);

            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Checking if fields are empty
                if (emailText.getText().toString().equals("") || passwordText.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(signInController.this, "Please fill in all fields", Toast.LENGTH_LONG);
                    toast.show();
                } else {

                    String email = emailText.getText().toString();
                    String password = passwordText.getText().toString();

                    // Sign in to account, using email and password
                    // Check Authentication API for existing user.
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(signInController.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        // If logged in, get current user
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(signInController.this, "Sign in failed, please check fields",
                                                Toast.LENGTH_SHORT).show();

                                    }


                                }
                            });


                }


            }


        });
    }

    private void updateUI(FirebaseUser user) {
        // Open menu screen
        Intent intent = new Intent(signInController.this, MenuController.class);
        startActivity(intent);

    }
}


