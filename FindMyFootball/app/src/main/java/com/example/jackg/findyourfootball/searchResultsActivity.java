package com.example.jackg.findyourfootball;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by jackg on 06/04/2018.
 */

public class searchResultsActivity extends Activity {
    boolean isFound = false;
    FirebaseDatabase database;
    DatabaseReference myRef, myRef2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());


    }

    @Override
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        // Checks if intent that opened activity has flag called 'ACTION_SEARCH'
        // Allows class to know that the action is a search query

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            String searchQuery = intent.getStringExtra(SearchManager.QUERY);
            // Getting the entered search query text

            // Loop through the football leagues or cups nodes in database, looking for a match of search query
            // While looping through the leagues or cups nodes, loop through the child nodes of them
            // Child nodes contain the football clubs from that competition
            // Looking for a match in the child values
            // If no value is found, loop through venues nodes in database, looking for a value that matches the query
            // End of loops, if no value is found, previous activity is opened. User is informed

            database = FirebaseDatabase.getInstance();

            myRef = database.getReference("Football Leagues");
            // Firebase Database Method
            // Allows extraction of nodes
            // Asynchronous method
            // Listener is always listening to database
            myRef.addChildEventListener(new ChildEventListener() {
                @Override

                public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                    if (dataSnapshot.getKey().toLowerCase().equals(searchQuery.toLowerCase())) {
                        isFound = true;  // Boolean flag to indicate a value has been found
                        Intent intent = new Intent(searchResultsActivity.this, SelectClubController.class);
                        intent.putExtra("Passed League", dataSnapshot.getKey());
                        startActivity(intent);

                        finish(); // Close class if value has been found. Ends task of looking through database


                    } else {

                        for (DataSnapshot getClubs : dataSnapshot.getChildren()) {
                            String clubName = (String) getClubs.getValue();
                            if (clubName.toLowerCase().equals(searchQuery.toLowerCase())) {
                                isFound = true;
                                Intent intent = new Intent(searchResultsActivity.this, SelectFixtureController.class);
                                intent.putExtra("Passed League", dataSnapshot.getKey());
                                intent.putExtra("Passed Club", clubName);
                                startActivity(intent);

                                finish();
                            }
                        }
                    }

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    // Extracts node if value is changed in database
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            myRef2 = database.getReference("Venues");

            // Firebase Database Method
            // Allows extraction of nodes
            // Asynchronous method
            // Listener is always listening to database
            myRef2.addChildEventListener(new ChildEventListener() {
                @Override

                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Venue venue = dataSnapshot.getValue(Venue.class);
                    if (venue.getName().toLowerCase().equals(searchQuery.toLowerCase())) {
                        isFound = true;
                        Intent intent = new Intent(searchResultsActivity.this, VenueController.class);
                        intent.putExtra("Passed Key", dataSnapshot.getKey());
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        // Extracts node if value is changed in database
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });





            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                        // If a value has not been found, previous activity is opened. User is informed.
                    if (!isFound) {
                        onBackPressed();
                        Toast toast = Toast.makeText(searchResultsActivity.this, "No results found", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }

}




