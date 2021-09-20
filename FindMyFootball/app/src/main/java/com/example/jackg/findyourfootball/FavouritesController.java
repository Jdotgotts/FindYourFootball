package com.example.jackg.findyourfootball;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavouritesController extends AppCompatActivity {


    private ArrayList<Venue> venueFavourites = new ArrayList<>();
    private ArrayList<String> clubFavourites = new ArrayList<>();
    private ArrayList<String> leagueFavourites = new ArrayList<>();
    private FirebaseAuth mAuth;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    DatabaseReference myRef;
    private SearchView searchView;
    private SearchView.SearchAutoComplete searchAutoComplete;
    private ArrayList<String> searchNames = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites_list);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");         // Getting database reference
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        expListView = findViewById(R.id.favouriteList);


        TextView favouriteTitle = findViewById(R.id.FavouriteTitle);
        if (user != null) {
            String userDisplayName = user.getDisplayName();
            favouriteTitle.setText(userDisplayName + " Favourites List");
            DatabaseReference myRef2 = myRef.child(user.getUid());


            // Gets single node from database
            // Firebase method
            myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Instantiates User object with value from database
                    // Uses objects getters and setters to store data in to objects variables
                    // Database nodes and object variables must match.
                    User user = dataSnapshot.getValue(User.class);
                    venueFavourites = user.getVenueFavouritesList();            // Getting users favourites
                    clubFavourites = user.getClubFavouritesList();
                    leagueFavourites = user.getLeagueFavouritesList();

                    prepareListData();      // Updating list


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }


    }

    private void prepareListData() {

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        ArrayList<String> venueNames = new ArrayList<>();

        // Check if favourites list is not null
        // if not, add to list.

        if (venueFavourites != null) {
            listDataHeader.add("Favourite Venue's");
            for (Venue venue : venueFavourites) {
                venueNames.add(venue.getName());
            }

            listDataChild.put(listDataHeader.get(0), venueNames);

        }

        if (clubFavourites != null) {

            // If clubs favourites is not null add a clubs header
            // Clubs are always going to be median index

            listDataHeader.add("Favourite Club's");
            int size = listDataHeader.size() / 2;
            listDataChild.put(listDataHeader.get(size), clubFavourites);

        }

        if (leagueFavourites != null) {
            listDataHeader.add("Favourite League's and Cup's");
            int size = listDataHeader.size() - 1;
            listDataChild.put(listDataHeader.get(size), leagueFavourites);

        }


        // Setting adapter for list view, using headers arrayList and child Hash Map
        listAdapter = new ExpandableList(FavouritesController.this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
    }




    public class ExpandableList extends BaseExpandableListAdapter {
        private Context context;
        private List<String> listHeader;

        private HashMap<String, List<String>> listChild;

        // Constructor for list
        public ExpandableList(Context context, List<String> listDataHeader,
                              HashMap<String, List<String>> listChildData) {
            this.context = context;
            this.listHeader = listDataHeader;
            this.listChild = listChildData;
        }

        // Gets child value using indexes of header position and child position
        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this.listChild.get(this.listHeader.get(groupPosition))
                    .get(childPosititon);
        }

        @Override
        // Method to get ID number of child
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override

        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            // Create child view method

            // Getting child value from hashMap, using method 'getChild'
            final String childText = (String) getChild(groupPosition, childPosition);

            // If no view, inflate the custom layout file 'list_view_row'
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_view_row, null);
            }

            TextView txtListChild = convertView.findViewById(R.id.rowText);

            txtListChild.setText(childText);

            String favouriteName = txtListChild.getText().toString();

            ImageView deleteIcon = convertView.findViewById(R.id.deleteIcon);
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {


                                        // Method if user clicks delete icon on a row.
                                        // Each row has individual listener attached.
                                        // Loops through each ArrayList of favourites if not null
                                        // Looks for equal value, removing if found.
                                        // Adds back to User object and pushes to database.
                                        // Updates list view
                                        DatabaseReference myRef2 = myRef.child(user.getUid());

                                        myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                User user = dataSnapshot.getValue(User.class);


                                                if (clubFavourites != null) {
                                                    for (int i = 0; i < clubFavourites.size(); i++) {
                                                        if (clubFavourites.get(i).equals(favouriteName)) {

                                                            clubFavourites.remove(i);
                                                        }
                                                    }
                                                }

                                                if (venueFavourites != null) {

                                                    for (int i = 0; i < venueFavourites.size(); i++) {
                                                        if (venueFavourites.get(i).getName().equals(favouriteName)) {

                                                            venueFavourites.remove(i);
                                                        }
                                                    }
                                                }

                                                if (leagueFavourites != null) {

                                                    for (int i = 0; i < leagueFavourites.size(); i++) {
                                                        if (leagueFavourites.get(i).equals(favouriteName)) {

                                                            leagueFavourites.remove(i);
                                                        }
                                                    }

                                                }

                                                user.setLeagueFavouritesList(leagueFavourites);
                                                user.setVenueFavouritesList(venueFavourites);
                                                user.setClubFavouritesList(clubFavourites);

                                                myRef2.setValue(user);

                                                Toast toast = Toast.makeText(FavouritesController.this, "Successfully removed", Toast.LENGTH_SHORT);
                                                toast.show();

                                                prepareListData();

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:

                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure you want to remove " + txtListChild.getText().toString() + " from your favourites?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();


                }
            });


            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this.listChild.get(this.listHeader.get(groupPosition)).size();
            // Get number of children in a header group
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.listHeader.get(groupPosition);
        }
        //Get header text
        @Override
        public int getGroupCount() {
            return this.listHeader.size();
        }
        // Get number of headers

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }
        // Get ID number of a header
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            // Get text of header according to position of list.
            //If no view, inflate custom layout
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_view_header, null);
            }


            TextView lblListHeader = (TextView) convertView
                    .findViewById(R.id.headerText);
            lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerTitle);


            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }




    // Navigation bar method, used for search and favourites.
    // Explained in menuController
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

                Intent intent = new Intent(FavouritesController.this, FavouritesController.class);

                startActivity(intent);

                return false;
            }
        });

        // Loop through database of football league, football clubs and venues, adding each name to an ArrayList
        // Use ArrayList for autocomplete search bar.

        DatabaseReference myRef = database.getReference("Football Leagues");

        // Listener for database, loops through each child node from reference.
        // Adds each node to 'DataSnapshot' object. Object provides methods to extract the data, such as 'getValue()'

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                searchNames.add(dataSnapshot.getKey());

                for(DataSnapshot getClubs : dataSnapshot.getChildren()){

                    searchNames.add((String) getClubs.getValue());
                }

                // Removing duplicates from ArrayList

                Set<String> removeDuplicates =  new HashSet<>();
                removeDuplicates.addAll(searchNames);
                searchNames.clear();
                searchNames.addAll(removeDuplicates);


                adapter = new ArrayAdapter<>(FavouritesController.this, android.R.layout.simple_list_item_1, searchNames);

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

        myRef2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Venue venue = dataSnapshot.getValue(Venue.class);
                searchNames.add(venue.getName());
                Set<String> removeDuplicates =  new HashSet<>();
                removeDuplicates.addAll(searchNames);
                searchNames.clear();
                searchNames.addAll(removeDuplicates);

                adapter = new ArrayAdapter<>(FavouritesController.this, android.R.layout.simple_list_item_1, searchNames);
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


        // Instantiating search bar auto complete
        searchView = (SearchView) item.getActionView();
        searchAutoComplete =  searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setDropDownHeight(150);
        searchAutoComplete.setDropDownAnchor(R.id.search);
        searchAutoComplete.setThreshold(2);


        // If search query is submitted, send intent with 'ACTION_SEARCH' to search activity
        // This intent flag informs activity that it is a search request.
        // Pass query text over activity in intent
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String suggestiveText = (String) parent.getItemAtPosition(position);
                searchAutoComplete.setText(suggestiveText);
                Intent searchIntent = new Intent(FavouritesController.this, searchResultsActivity.class);
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
