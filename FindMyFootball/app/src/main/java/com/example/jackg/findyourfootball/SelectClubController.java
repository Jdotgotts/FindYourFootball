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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SelectClubController extends AppCompatActivity {


    private ListView listView;
    private ArrayList<String> footballClubs = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private TextView clubTitle;
    private ArrayList<String> favouriteClubs = new ArrayList<>();
    CustomAdapter customAdapter = new CustomAdapter();
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    private ArrayList<String> searchNames = new ArrayList<>();
    private SearchView searchView;
    private SearchView.SearchAutoComplete searchAutoComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_club_view);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Football Leagues");

        listView = findViewById(R.id.fixtureListView);
        mAuth = FirebaseAuth.getInstance();


        Bundle extras = getIntent().getExtras();

        // Checking if a football league/cup has been selected
        if (extras != null) {

            final String footballLeague = extras.getString("Passed League");

            TextView clubTitle = findViewById(R.id.clubsTitle);
            clubTitle.setText(footballLeague + " Clubs");


            // Football club nodes in database are children under the parent node called by the league/cup name

            DatabaseReference myRef2 = myRef.child(footballLeague);



            // Called if a row is selected, passes the club name and league name to the SelectFixtureController class.

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(SelectClubController.this, SelectFixtureController.class);
                    TextView listViewName = view.findViewById(R.id.itemText);
                    String clubName = listViewName.getText().toString();
                    intent.putExtra("Passed League", footballLeague);
                    intent.putExtra("Passed Club", clubName);
                    startActivity(intent);


                }
            });

            // Loop through the child nodes of a football league/cup
            // Accessing each club name
            myRef2.addChildEventListener(new ChildEventListener() {
                @Override
                // Firebase Database Method
                // Allows extraction of nodes
                // Asynchronous method
                // Listener is always listening to database
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    String clubNames = (String) dataSnapshot.getValue();

                    // Adding to arraylist and sorting by A-Z.
                    footballClubs.add(clubNames);

                    Collections.sort(footballClubs);

                    listView.setAdapter(customAdapter);


                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        // Extracts node is value is changed in database
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


        } else {
            // No league was selected
            // Get football clubs from the premier league



            // If user selects a football club row, get football league/cup and football club name
            // Open and pass to selectFixtureController activity
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(SelectClubController.this, SelectFixtureController.class);
                    TextView listViewName = view.findViewById(R.id.itemText);
                    String clubName = listViewName.getText().toString();
                    intent.putExtra("Passed League", "Premier League");
                    intent.putExtra("Passed Club", clubName);
                    startActivity(intent);


                }
            });


            DatabaseReference myRef2 = myRef.child("Premier League");

            // Loop through child nodes of premier league
            // Accessing each club name
            myRef2.addChildEventListener(new ChildEventListener() {
                @Override
                // Firebase Database Method
                // Allows extraction of nodes
                // Asynchronous method
                // Listener is always listening to database
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    String clubNames = (String) dataSnapshot.getValue();

                    // Adding to arraylist and sorting by a-z.
                    footballClubs.add(clubNames);

                    Collections.sort(footballClubs);


                    listView.setAdapter(customAdapter);

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    // Extracts node is value is changed in database
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

    }

    // Each row is a view, allowing the manipulation of a specific view when created.
    // Each of the methods in the adapter are directed to the view that was created with them.
    // Each widget is also isolated to a view, allowing the extraction of data in a view.

    class CustomAdapter extends BaseAdapter {

        @Override
        // Method to return how many clubs there are
        // The size of the arraylist is how many rows are created
        public int getCount() {
            return footballClubs.size();
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

            TextView clubName = convertView.findViewById(R.id.itemText);
            clubName.setTextColor(Color.BLACK);
            clubName.setText(footballClubs.get(position));

                // Get current logged in user
            FirebaseUser user = mAuth.getCurrentUser();


            if (user != null) {
                // Get user from database
                // Using user ID number for reference to node
                final DatabaseReference myRef2 = database.getReference("Users");
                final DatabaseReference myRef3 = myRef2.child(user.getUid());

                myRef3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User getUser = dataSnapshot.getValue(User.class);

                        // If the user has already got a club favourites list
                        if (getUser.getClubFavouritesList() != null) {

                            // Loop through the favourites list, if the favourites list contain the name of the club in the list view row
                            // Add the favourite club name to an Arraylist
                            // Changes favourites icon to inform user that the club has already been added to their favourites
                            // Adds tag to inform system it can be deleted from favourites
                            for (String clubs : getUser.getClubFavouritesList()) {
                                if (clubs.equals(clubName.getText().toString())) {
                                    favouriteClubs.add(clubs);
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

                // If favourites icon is selected
            favouriteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get current logged in user
                    FirebaseUser user = mAuth.getCurrentUser();


                    if (user != null) {
                        // Get user from database
                        // Using user ID number for reference to node
                        final DatabaseReference myRef = database.getReference("Users");
                        final DatabaseReference myRef2 = myRef.child(user.getUid());

                        myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User getUser = dataSnapshot.getValue(User.class);

                                // Get tag from favourite icon
                                String isFavourite = (String) favouriteIcon.getTag();

                                // If the user has already got a club favourites list
                                if (getUser.getClubFavouritesList() != null) {

                                    // Checks if icon is a confirmation of favourite icon
                                    // If it is not, add the football club to an arraylist
                                    // Change the icon to has been added to favourites
                                    // Change tag to inform system it can be deleted from favourites
                                    if (isFavourite.equals("notFavourite")) {
                                        favouriteClubs = getUser.getClubFavouritesList();
                                        favouriteClubs.add(clubName.getText().toString());
                                        getUser.setClubFavouritesList(favouriteClubs);

                                        favouriteIcon.setImageResource(R.drawable.favourite_confirmation_icon);
                                        favouriteIcon.setTag("isFavourite");


                                            // Checks if favourite icon is confirmation of favourite icon
                                            // If so, finds the club name in the arraylist and removes it
                                            // Changes the favourites icon to inform the user it is not favoured anymore
                                            // Changes tag to inform system it can be favoured again.
                                    } else if (isFavourite.equals("isFavourite")) {
                                        for (int i = 0; i < favouriteClubs.size(); i++) {
                                            if (favouriteClubs.get(i).equals(clubName.getText().toString())) {
                                                favouriteClubs.remove(i);
                                                getUser.setClubFavouritesList(favouriteClubs);

                                                favouriteIcon.setImageResource(R.drawable.favourites_icon);
                                                favouriteIcon.setTag("notFavourite");

                                            }
                                        }

                                    }
                                } else {
                                    // If user does not have any club favourites, add the arraylist to the user object
                                    favouriteClubs.add(clubName.getText().toString());
                                    getUser.setClubFavouritesList(favouriteClubs);
                                    favouriteIcon.setImageResource(R.drawable.favourite_confirmation_icon);
                                    favouriteIcon.setTag("isFavourite");
                                }

                                // Update user in database
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

                Intent intent = new Intent(SelectClubController.this, FavouritesController.class);

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


                adapter = new ArrayAdapter<>(SelectClubController.this, android.R.layout.simple_list_item_1, searchNames);

                searchAutoComplete.setAdapter(adapter);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // Extracts node is value is changed in database
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

        myRef2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Venue venue = dataSnapshot.getValue(Venue.class);
                searchNames.add(venue.getName());
                Set<String> removeDuplicates = new HashSet<>();
                removeDuplicates.addAll(searchNames);
                searchNames.clear();
                searchNames.addAll(removeDuplicates);

                adapter = new ArrayAdapter<>(SelectClubController.this, android.R.layout.simple_list_item_1, searchNames);
                searchAutoComplete.setAdapter(adapter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // Extracts node is value is changed in database
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
                Intent searchIntent = new Intent(SelectClubController.this, searchResultsActivity.class);
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

