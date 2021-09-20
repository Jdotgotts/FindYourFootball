package com.example.jackg.findyourfootball;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;


public class signUpController extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_view);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mAuth = FirebaseAuth.getInstance();         // Getting current instance of Authentication API

        Button signUpBtn = findViewById(R.id.signUpBtn);
        TextView emailText = findViewById(R.id.signUpfield_email);
        TextView passwordText = findViewById(R.id.signUpfield_password);
        TextView displayText = findViewById(R.id.displayName);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Users");     // Reference to user node in database

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Checking if fields are empty
                if (emailText.getText().toString().equals("") || passwordText.getText().toString().equals("") || displayText.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(signUpController.this, "Please fill in all fields", Toast.LENGTH_LONG);
                    toast.show();
                } else {


                    String email = emailText.getText().toString();
                    String password = passwordText.getText().toString();
                    String displayNameText = displayText.getText().toString();

                    // Using API method to create account with email and password
                    // API checks if email is correctly formatted (@email.com)
                    // Also checks if password is secure, e.g. 6 characters long.
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(signUpController.this, (OnCompleteListener<AuthResult>) task -> {

                        // If account is created, set the display name of the user.
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayNameText)
                                    .build();

                            // Updating user profile

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");

                                                String userID = user.getUid();
                                                // Adding User to database, using user ID as key.
                                                User newUser = new User(displayNameText, email);
                                                myRef.child(userID).setValue(newUser);

                                                updateUI(user);
                                            }
                                        }
                                    });


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(signUpController.this, "Sign up failed. Please check fields & Password has to be equal or bigger than 6.",
                                    Toast.LENGTH_SHORT).show();

                        }


                    });

                }

            }
        });


    }

    // Open menu screen
    private void updateUI(FirebaseUser user) {
        Intent intent = new Intent(signUpController.this, MenuController.class);
        startActivity(intent);

    }


}
