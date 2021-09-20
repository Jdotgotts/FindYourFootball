package com.example.jackg.findyourfootball;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SelectLeagueController extends AppCompatActivity {


    private ListView listView;
    private ArrayList<String> footballLeagues = new ArrayList<>();
    private ArrayList<String> favouriteLeagues = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    CustomAdapter customAdapter = new CustomAdapter();
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    private ArrayList<String> searchNames = new ArrayList<>();
    private SearchView searchView;
    private SearchView.SearchAutoComplete searchAutoComplete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_football_league_view);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();


        final DatabaseReference myRef = database.getReference("Football Leagues");

        listView = findViewById(R.id.leagueListView);


        // Passes football league/cup name to SelectClubController activity if row is selected
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SelectLeagueController.this, SelectClubController.class);

                TextView listViewName = view.findViewById(R.id.itemText);
                String leagueName = listViewName.getText().toString();
                intent.putExtra("Passed League", leagueName);
                startActivity(intent);

            }


        });


        // Iterates over league/cup children, extracting the parent node name
        // 'getKey' obtains the parent node name

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String leagueNames = dataSnapshot.getKey();
                footballLeagues.add(leagueNames);

                listView.setAdapter(customAdapter);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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


    }

    // Each row is a view, allowing the manipulation of a specific view when created.
    // Each of the methods in the adapter are directed to the view that was created with them.
    // Each widget is also isolated to a view, allowing the extraction of data in a view.


    class CustomAdapter extends BaseAdapter {

        @Override
        // Method to return how many leagues/cups there are
        // The size of the arraylist is how many rows are created
        public int getCount() {
            return footballLeagues.size();
        }

        @Override
        // Get the object inside the row
        public Object getItem(int position) {
            return null;
        }

        @Override
        // Get a ID number of the row
        public long getItemId(int position) {
            return 0;
        }

        @Override
        // Create the rows of the list view
        public View getView(int position, View convertView, ViewGroup parent) {

            // Inflate the custom layout inside the row
            convertView = getLayoutInflater().inflate(R.layout.list_view_item, null);

            ImageView favouriteIcon = convertView.findViewById(R.id.favouriteIcon);

            TextView leagueName = convertView.findViewById(R.id.itemText);
            leagueName.setTextColor(Color.BLACK);
            leagueName.setText(footballLeagues.get(position));

            // Get current logged in user
            FirebaseUser user = mAuth.getCurrentUser();


            if (user != null) {

                // Get user from database using user ID number from Firebase Authentication

                final DatabaseReference myRef2 = database.getReference("Users");
                final DatabaseReference myRef3 = myRef2.child(user.getUid());


                // Firebase Database Method
                // Retrieves a single node from the reference
                myRef3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User getUser = dataSnapshot.getValue(User.class);

                        // If user already has a league/cup favourites list
                        if (getUser.getLeagueFavouritesList() != null) {

                            // Loop through the favourites list, checking if any of the values in the list are in the list view
                            // If a match is found, change the favourite icon to a confirmation icon.
                            // Informs the user what leagues/cups have been added to their favourites already
                            // Also changes the tag, informing the system the league/cup can be removed for the favourites.

                            for (String leagues : getUser.getLeagueFavouritesList()) {
                                if (leagues.equals(leagueName.getText().toString())) {
                                    favouriteLeagues.add(leagues);
                                    favouriteIcon.setImageResource(R.drawable.favourite_confirmation_icon);
                                    favouriteIcon.setTag("isFavourite");
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            favouriteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Getting current logged in user
                    FirebaseUser user = mAuth.getCurrentUser();


                    if (user != null) {
                        // Get user from database using user ID number from Firebase Authentication
                        final DatabaseReference myRef = database.getReference("Users");
                        final DatabaseReference myRef2 = myRef.child(user.getUid());

                        // Firebase Database Method
                        // Retrieves a single node from the reference
                        myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User getUser = dataSnapshot.getValue(User.class);

                                // If user already has a league/cup favourites list
                                if (getUser.getLeagueFavouritesList() != null) {

                                    // Get tag name of favourite icon
                                    String isFavourite = (String) favouriteIcon.getTag();


                                    // Checking the favourites icon to see if the league/cup has already been added to the favourites
                                    // If it has not, get the league/cup name from the view and adds to an arraylist
                                    // Updates the favourites icon and tag to inform the user and system that the league/cup has been added to the favourites
                                    // Adds the league to the user objects favourites
                                    if (isFavourite.equals("notFavourite")) {
                                        favouriteLeagues = getUser.getLeagueFavouritesList();
                                        favouriteLeagues.add(leagueName.getText().toString());
                                        getUser.setLeagueFavouritesList(favouriteLeagues);

                                        favouriteIcon.setImageResource(R.drawable.favourite_confirmation_icon);
                                        favouriteIcon.setTag("isFavourite");

                                    }

                                    // Checking the favourites icon to see if the league/cup has already been added to the favourites
                                    // If it has, remove the league/cup from the arraylist
                                    // Add this arraylist to the user object, updating the old favourites
                                    // Change the favourites icon to inform the user and system that the league/cup can be added to favourites again.
                                    else if (isFavourite.equals("isFavourite")) {
                                        for (int i = 0; i < favouriteLeagues.size(); i++) {
                                            if (favouriteLeagues.get(i).equals(leagueName.getText().toString())) {
                                                favouriteLeagues.remove(i);
                                                getUser.setLeagueFavouritesList(favouriteLeagues);

                                                favouriteIcon.setImageResource(R.drawable.favourites_icon);
                                                favouriteIcon.setTag("notFavourite");

                                            }
                                        }

                                    }


                                } else {

                                    // If the user does not have a list of league/cup favourites
                                    // Add the league/cup to the arraylist and add to the user object
                                    favouriteLeagues.add(leagueName.getText().toString());
                                    getUser.setLeagueFavouritesList(favouriteLeagues);
                                    favouriteIcon.setImageResource(R.drawable.favourite_confirmation_icon);
                                    favouriteIcon.setTag("isFavourite");
                                }

                                //Update the user in the database.
                                myRef2.setValue(getUser);


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

                }
            });

            return convertView;
        }
    }

    // Method to create navigation bar
    // Used for search icon and favourites icon
    // Explained in MenuController

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.search);
        MenuItem favouriteIcon = menu.findItem(R.id.action_favorite);

        favouriteIcon.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent intent = new Intent(SelectLeagueController.this, FavouritesController.class);

                startActivity(intent);

                return false;
            }
        });

        DatabaseReference myRef = database.getReference("Football Leagues");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                searchNames.add(dataSnapshot.getKey());

                for (DataSnapshot getClubs : dataSnapshot.getChildren()) {

                    searchNames.add((String) getClubs.getValue());
                }


                Set<String> removeDuplicates = new HashSet<>();
                removeDuplicates.addAll(searchNames);
                searchNames.clear();
                searchNames.addAll(removeDuplicates);


                adapter = new ArrayAdapter<>(SelectLeagueController.this, android.R.layout.simple_list_item_1, searchNames);

                searchAutoComplete.setAdapter(adapter);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
                Set<String> removeDuplicates = new HashSet<>();
                removeDuplicates.addAll(searchNames);
                searchNames.clear();
                searchNames.addAll(removeDuplicates);

                adapter = new ArrayAdapter<>(SelectLeagueController.this, android.R.layout.simple_list_item_1, searchNames);
                searchAutoComplete.setAdapter(adapter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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


        searchView = (SearchView) item.getActionView();
        searchAutoComplete = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setDropDownHeight(150);
        searchAutoComplete.setDropDownAnchor(R.id.search);
        searchAutoComplete.setThreshold(2);


        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String suggestiveText = (String) parent.getItemAtPosition(position);
                searchAutoComplete.setText(suggestiveText);
                Intent searchIntent = new Intent(SelectLeagueController.this, searchResultsActivity.class);
                searchIntent.setAction(Intent.ACTION_SEARCH);
                searchIntent.putExtra(SearchManager.QUERY, suggestiveText);
                startActivity(searchIntent);
            }
        });

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        ComponentName cn = new ComponentName(this, searchResultsActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));


        return true;
    }
}
