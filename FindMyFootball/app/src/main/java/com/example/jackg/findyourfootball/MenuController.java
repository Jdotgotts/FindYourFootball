package com.example.jackg.findyourfootball;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.support.v7.widget.SearchView;
import android.widget.EditText;
import android.widget.SearchView.OnQueryTextListener;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class MenuController extends AppCompatActivity {

    private SearchView searchView;
    private SearchView.SearchAutoComplete searchAutoComplete;
    private ArrayAdapter<String> adapter;
    FirebaseDatabase database;
    private ArrayList<String> searchNames = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_view);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        database = FirebaseDatabase.getInstance();

        Button goLeaguePage = findViewById(R.id.leagueBtn);
        Button venueBtn = findViewById(R.id.venueBtn);
        Button fixturesBtn = findViewById(R.id.fixtureBtn);
        Button footballClubBtn = findViewById(R.id.footballClubBtn);


        // Go select league page
        goLeaguePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuController.this, SelectLeagueController.class);


                startActivity(intent);
            }
        });

        // Go select venue page
        venueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuController.this, SelectVenueController.class);

                startActivity(intent);

            }
        });
        // Go select fixture page
        fixturesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuController.this, SelectFixtureController.class);

                startActivity(intent);

            }
        });
        // Go select club page
        footballClubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuController.this, SelectClubController.class);

                startActivity(intent);

            }
        });


    }


    // Navigation bar method, used for search and favourites.
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.search);
        MenuItem favouriteIcon = menu.findItem(R.id.action_favorite);

        // Open favourites screen is selected
        favouriteIcon.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent intent = new Intent(MenuController.this, FavouritesController.class);

                startActivity(intent);

                return false;
            }
        });

        // Loop through database of football league, football clubs and venues, adding each name to an ArrayList
        // Use ArrayList for autocomplete search bar.
        DatabaseReference myRef = database.getReference("Football Leagues");


        // Firebase Database Method
        // Allows extraction of nodes
        // Asynchronous method
        // Listener is always listening to database
        // Adds each node to 'DataSnapshot' object. Object provides methods to extract the data, such as 'getValue()'
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
               // Gets a node if it has been added to database
                searchNames.add(dataSnapshot.getKey());

                for(DataSnapshot getClubs : dataSnapshot.getChildren()){

                    searchNames.add((String) getClubs.getValue());
                }

                // Removing duplicates from ArrayList
                Set<String> removeDuplicates =  new HashSet<>();
                removeDuplicates.addAll(searchNames);
                searchNames.clear();
                searchNames.addAll(removeDuplicates);


                adapter = new ArrayAdapter<>(MenuController.this, android.R.layout.simple_list_item_1, searchNames);

                searchAutoComplete.setAdapter(adapter);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // Extracts node if value is changed in database
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // Extracts node if value is removed in database
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // Extracts node if value is moved in database
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        DatabaseReference myRef2 = database.getReference("Venues");
        // Firebase Database Method
        // Allows extraction of nodes
        // Asynchronous method
        // Listener is always listening to database
        myRef2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Venue venue = dataSnapshot.getValue(Venue.class);
                searchNames.add(venue.getName());
                Set<String> removeDuplicates =  new HashSet<>();
                removeDuplicates.addAll(searchNames);
                searchNames.clear();
                searchNames.addAll(removeDuplicates);

                adapter = new ArrayAdapter<>(MenuController.this, android.R.layout.simple_list_item_1, searchNames);
                searchAutoComplete.setAdapter(adapter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // Extracts node if value is changed in database
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                    // Extracts node if value is removed in database
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // Extracts node if value is moved in database
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Instantiating search bar auto complete
        // Inherits from AutoCompleteTextView
        searchView = (SearchView) item.getActionView();
        searchAutoComplete =  searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setDropDownHeight(150);
        searchAutoComplete.setDropDownAnchor(R.id.search);
        searchAutoComplete.setThreshold(2);
        searchAutoComplete.setVerticalScrollbarPosition(1);


        // If search query is submitted, send intent with 'ACTION_SEARCH' to search activity
        // This intent flag informs activity that it is a search request.
        // Pass query text over activity in intent
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String suggestiveText = (String) parent.getItemAtPosition(position);
               searchAutoComplete.setText(suggestiveText);
                Intent searchIntent = new Intent(MenuController.this, searchResultsActivity.class);
                searchIntent.setAction(Intent.ACTION_SEARCH);
                searchIntent.putExtra(SearchManager.QUERY, suggestiveText);
                startActivity(searchIntent);
            }
        });

        // Setting a search manager to search widget
        // Provides system to search functions
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        ComponentName cn = new ComponentName(this, searchResultsActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));


        return true;
    }


}
