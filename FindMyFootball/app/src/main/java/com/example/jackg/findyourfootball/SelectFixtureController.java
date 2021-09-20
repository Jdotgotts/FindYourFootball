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
import android.text.Html;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class SelectFixtureController extends AppCompatActivity {


    private ListView listView;
    private ArrayList<String> fixtureNamesList = new ArrayList<>();
    private ArrayList<String> homeTeam = new ArrayList<>();
    private ArrayList<String> awayTeam = new ArrayList<>();
    private ArrayList<String> fixtureTime = new ArrayList<>();
    private ArrayList<String> fixtureDate = new ArrayList<>();
    CustomAdapter customAdapter = new CustomAdapter();
    private ArrayList<String> searchNames = new ArrayList<>();
    private SearchView searchView;
    private SearchView.SearchAutoComplete searchAutoComplete;
    private ArrayAdapter<String> adapter;
    HashMap<String, String> fixtureMap = new HashMap();
    private String theFixtureName;
    String footballLeague;
    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_fixture_view);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        database = FirebaseDatabase.getInstance();

        listView = findViewById(R.id.fixtureListView);


        Bundle extras = getIntent().getExtras();


        // Checking if user has selected a football club

        if (extras != null) {
            // Get fixtures for selected football club if not null

            footballLeague = extras.getString("Passed League");
            final String footballClub = extras.getString("Passed Club");


            TextView fixtureTitle = findViewById(R.id.fixturesTitle);
            fixtureTitle.setText(footballClub + " Fixtures");


            final DatabaseReference myRef = database.getReference(footballLeague + " Fixtures");

            // Firebase Database Method
            // Allows extraction of nodes
            // Asynchronous method
            // Listener is always listening to database
            // Looping through football fixture nodes from database reference
            myRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    // Fixture object is created by database return value
                    // Setters automatically input data in to variables if names match up.
                    Fixture fixture = dataSnapshot.getValue(Fixture.class);


                    DateFormat format = new SimpleDateFormat("EEE dd MMM, yy", Locale.ENGLISH);
                    try {


                        // Getting current date
                        Date date = format.parse(fixture.getDate().replaceAll("(?<=\\d)(st|nd|rd|th)", ""));
                        Date currentDate = new Date();
                        format.format(currentDate);


                        // Getting date object from two weeks of current date
                        Calendar c = Calendar.getInstance();
                        c.setTime(currentDate);
                        c.add(Calendar.WEEK_OF_MONTH, 2);
                        Date dateInAWeek = c.getTime();



                        theFixtureName = fixture.getHomeTeam() + " vs " + fixture.getAwayTeam();

                        // Checking if the fixture contains the passed football club and the date of the fixture is after the current date
                        // Stops application getting old fixtures
                        if (theFixtureName.contains(footballClub) && date.after(currentDate)) {

                            // Adding fixture key and fixture name to a hash map
                            // Used to pass over to selectVenueController class.
                            fixtureMap.put(dataSnapshot.getKey(), theFixtureName);

                            // Making passed football club appear bold on list view
                            // Improves readability
                            if (fixture.getHomeTeam().contains(footballClub)) {
                                String homeTeamBold = "<b>" + fixture.getHomeTeam() + "</b>";
                                homeTeam.add(homeTeamBold);
                                awayTeam.add(fixture.getAwayTeam());
                            } else if (fixture.getAwayTeam().contains(footballClub)) {
                                String awayTeamBold = "<b>" + fixture.getAwayTeam() + "</b>";
                                awayTeam.add(awayTeamBold);
                                homeTeam.add(fixture.getHomeTeam());
                            }

                            // Adding fixture data to arraylists
                            // Used to list view adapter
                            fixtureTime.add(fixture.getTime());
                            fixtureDate.add(fixture.getDate());
                            fixtureNamesList.add(theFixtureName);


                            listView.setAdapter(customAdapter);


                        }


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


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


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent(SelectFixtureController.this, SelectVenueController.class);

                    TextView homeTeam = view.findViewById(R.id.homeTeam);
                    TextView awayTeam = view.findViewById(R.id.awayTeam);

                    // Extracting the home team and away team from the selected view (row)
                    // Appending the teams together to make a fixture name
                    // Comparing the fixture name to the values in the Hash map
                    // If found, pass the hash map key which is a key for the fixture
                    // Passed football league and the fixture name also.
                    // Open selectVenueController activity

                    String listViewName = homeTeam.getText().toString() + " vs " + awayTeam.getText().toString();

                    for (Map.Entry<String, String> entry : fixtureMap.entrySet()) {
                        if (entry.getValue().equals(listViewName)) {

                            intent.putExtra("Passed Key", entry.getKey());

                        }
                    }


                    intent.putExtra("Passed League", footballLeague);
                    intent.putExtra("Passed Fixture", listViewName);
                    startActivity(intent);


                }
            });

                // Spinner used for filtering
                // Each switch case follows same structure
                // The date of each fixture is checked according to the filter option

            Spinner spinner = findViewById(R.id.fixtureFilter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String filterOption = (String) parent.getItemAtPosition(position);

                    // Getting text from spinner
                    // Switch statement for the filter option
                    switch (filterOption) {
                        case "Today":


                            // Clear arraylists and update list view
                            homeTeam.clear();
                            awayTeam.clear();
                            fixtureTime.clear();
                            fixtureDate.clear();

                            customAdapter.notifyDataSetChanged();

                            // Firebase Database Method
                            // Allows extraction of nodes
                            // Asynchronous method
                            // Listener is always listening to database
                            myRef.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    // Fixture object is created by database return value
                                    // Setters automatically input data in to variables if names match up.
                                    Fixture fixture = dataSnapshot.getValue(Fixture.class);


                                    DateFormat format = new SimpleDateFormat("EEE dd MMM, yy", Locale.ENGLISH);
                                    try {


                                        Date date = format.parse(fixture.getDate().replaceAll("(?<=\\d)(st|nd|rd|th)", ""));
                                        Date currentDate = new Date();
                                        format.format(currentDate);


                                        Calendar c = Calendar.getInstance();
                                        c.setTime(currentDate);
                                        c.add(Calendar.WEEK_OF_MONTH, 2);
                                        Date dateInAWeek = c.getTime();



                                        theFixtureName = fixture.getHomeTeam() + " vs " + fixture.getAwayTeam();

                                        // Finding a fixture that contains the passed football club and is equal to the current date

                                        if (theFixtureName.contains(footballClub) && date.equals(currentDate)) {
                                            // If found, puts in to a hash map

                                            fixtureMap.put(dataSnapshot.getKey(), theFixtureName);

                                            // passed football club made bold for readability
                                            if (fixture.getHomeTeam().contains(footballClub)) {
                                                String homeTeamBold = "<b>" + fixture.getHomeTeam() + "</b>";
                                                homeTeam.add(homeTeamBold);
                                                awayTeam.add(fixture.getAwayTeam());
                                            } else if (fixture.getAwayTeam().contains(footballClub)) {
                                                String awayTeamBold = "<b>" + fixture.getAwayTeam() + "</b>";
                                                awayTeam.add(awayTeamBold);
                                                homeTeam.add(fixture.getHomeTeam());
                                            }


                                            fixtureTime.add(fixture.getTime());
                                            fixtureDate.add(fixture.getDate());
                                            fixtureNamesList.add(theFixtureName);

                                            customAdapter.notifyDataSetChanged();

                                        }


                                    } catch (ParseException e) {
                                        e.printStackTrace();
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


                            break;
                            // Only get fixtures that are two weeks from the current date
                        case "Two weeks":

                            // Clearing list view
                            homeTeam.clear();
                            awayTeam.clear();
                            fixtureTime.clear();
                            fixtureDate.clear();
                            customAdapter.notifyDataSetChanged();


                            // Firebase Database Method
                            // Allows extraction of nodes
                            // Asynchronous method
                            // Listener is always listening to database
                            myRef.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    Fixture fixture = dataSnapshot.getValue(Fixture.class);


                                    DateFormat format = new SimpleDateFormat("EEE dd MMM, yy", Locale.ENGLISH);
                                    try {

                                        Date date = format.parse(fixture.getDate().replaceAll("(?<=\\d)(st|nd|rd|th)", ""));
                                        Date currentDate = new Date();
                                        format.format(currentDate);


                                        Calendar c = Calendar.getInstance();
                                        c.setTime(currentDate);
                                        c.add(Calendar.WEEK_OF_MONTH, 2);
                                        Date dateInTwoWeek = c.getTime();


                                        theFixtureName = fixture.getHomeTeam() + " vs " + fixture.getAwayTeam();

                                        // Finding fixtures that are before the date in two weeks and after the current date
                                        // Only finds date in between the range
                                        // Also looks for fixtures containing the passed football club

                                        if (theFixtureName.contains(footballClub) && date.before(dateInTwoWeek) && date.after(currentDate)) {

                                            // Hash map used for passing fixture to selectVenueController
                                            fixtureMap.put(dataSnapshot.getKey(), theFixtureName);

                                            // Passed club made bold for readability
                                            if (fixture.getHomeTeam().contains(footballClub)) {
                                                String homeTeamBold = "<b>" + fixture.getHomeTeam() + "</b>";
                                                homeTeam.add(homeTeamBold);
                                                awayTeam.add(fixture.getAwayTeam());
                                            } else if (fixture.getAwayTeam().contains(footballClub)) {
                                                String awayTeamBold = "<b>" + fixture.getAwayTeam() + "</b>";
                                                awayTeam.add(awayTeamBold);
                                                homeTeam.add(fixture.getHomeTeam());
                                            }


                                            fixtureTime.add(fixture.getTime());
                                            fixtureDate.add(fixture.getDate());
                                            fixtureNamesList.add(theFixtureName);

                                            customAdapter.notifyDataSetChanged();


                                        }


                                    } catch (ParseException e) {
                                        e.printStackTrace();
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

                            break;

                        case "All":

                            // Clearing list view
                            homeTeam.clear();
                            awayTeam.clear();
                            fixtureTime.clear();
                            fixtureDate.clear();

                            customAdapter.notifyDataSetChanged();

                            // Firebase Database Method
                            // Allows extraction of nodes
                            // Asynchronous method
                            // Listener is always listening to database
                            myRef.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                    Fixture fixture = dataSnapshot.getValue(Fixture.class);

                                    DateFormat format = new SimpleDateFormat("EEE dd MMM, yy", Locale.ENGLISH);
                                    try {

                                        Date date = format.parse(fixture.getDate().replaceAll("(?<=\\d)(st|nd|rd|th)", ""));
                                        Date currentDate = new Date();
                                        format.format(currentDate);


                                        Calendar c = Calendar.getInstance();
                                        c.setTime(currentDate);
                                        c.add(Calendar.WEEK_OF_MONTH, 2);
                                        Date dateInAWeek = c.getTime();


                                        theFixtureName = fixture.getHomeTeam() + " vs " + fixture.getAwayTeam();

                                        // Finding all fixtures that contain the passed football club and are after the current date
                                        // Or, any fixtures that contain the passed football club and are equal to the current date
                                        // Finds all fixtures.
                                        if (theFixtureName.contains(footballClub) && date.after(currentDate) || theFixtureName.contains(footballClub) && date.equals(currentDate)) {

                                            // Hash map used for passing fixture to selectVenueController
                                            fixtureMap.put(dataSnapshot.getKey(), theFixtureName);

                                            // Passed club made bold for readability
                                            if (fixture.getHomeTeam().contains(footballClub)) {
                                                String homeTeamBold = "<b>" + fixture.getHomeTeam() + "</b>";
                                                homeTeam.add(homeTeamBold);
                                                awayTeam.add(fixture.getAwayTeam());
                                            } else if (fixture.getAwayTeam().contains(footballClub)) {
                                                String awayTeamBold = "<b>" + fixture.getAwayTeam() + "</b>";
                                                awayTeam.add(awayTeamBold);
                                                homeTeam.add(fixture.getHomeTeam());
                                            }


                                            fixtureTime.add(fixture.getTime());
                                            fixtureDate.add(fixture.getDate());
                                            fixtureNamesList.add(theFixtureName);


                                            customAdapter.notifyDataSetChanged();


                                        }


                                    } catch (ParseException e) {
                                        e.printStackTrace();
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

                            break;


                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }

            });

        } else {

            // User did not choose a football club
            // Gets premier league fixtures



            final DatabaseReference myRef = database.getReference("Premier League Fixtures");
            footballLeague = "Premier League";

            // Firebase Database Method
            // Allows extraction of nodes
            // Asynchronous method
            // Listener is always listening to database
            myRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Fixture fixture = dataSnapshot.getValue(Fixture.class);


                    DateFormat format = new SimpleDateFormat("EEE dd MMM, yy", Locale.ENGLISH);
                    try {

                        Date date = format.parse(fixture.getDate().replaceAll("(?<=\\d)(st|nd|rd|th)", ""));
                        Date currentDate = new Date();
                        format.format(currentDate);


                        Calendar c = Calendar.getInstance();
                        c.setTime(currentDate);
                        c.add(Calendar.WEEK_OF_MONTH, 2);
                        Date dateInTwoWeek = c.getTime();


                        theFixtureName = fixture.getHomeTeam() + " vs " + fixture.getAwayTeam();

                        // Finding fixtures that are before the date in two weeks and are after the current date
                        // Gets all fixtures from the premier league in that range
                        if (date.before(dateInTwoWeek) && date.after(currentDate)) {

                            // Hash map used for passing fixture to selectVenueController
                            fixtureMap.put(dataSnapshot.getKey(), theFixtureName);


                            // Adding to arraylists to be used by list view adapter

                            homeTeam.add(fixture.getHomeTeam());
                            awayTeam.add(fixture.getAwayTeam());
                            fixtureTime.add(fixture.getTime());
                            fixtureDate.add(fixture.getDate());
                            fixtureNamesList.add(theFixtureName);

                            listView.setAdapter(customAdapter);


                        }


                    } catch (ParseException e) {
                        e.printStackTrace();
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


            // List view if not club has been passed

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent(SelectFixtureController.this, SelectVenueController.class);

                    TextView homeTeam = view.findViewById(R.id.homeTeam);
                    TextView awayTeam = view.findViewById(R.id.awayTeam);

                    // Extracting the home team and away team from the selected view(row)


                    String listViewName = homeTeam.getText().toString() + " vs " + awayTeam.getText().toString();

                    // Looking if fixture name is in the hash map
                    // If it is, add the key to the intent
                    // Key is a key for the fixture
                    // Pass over the football league (premier league) and fixture name to SelectVenueController activity
                    for (Map.Entry<String, String> entry : fixtureMap.entrySet()) {
                        if (entry.getValue().equals(listViewName)) {

                            intent.putExtra("Passed Key", entry.getKey());

                        }
                    }


                    intent.putExtra("Passed League", "Premier League");
                    intent.putExtra("Passed Fixture", listViewName);
                    startActivity(intent);


                }
            });

                // Spinner for filter options
                // Switch statement depending on the text inside the spinner

            Spinner spinner = findViewById(R.id.fixtureFilter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String filterOption = (String) parent.getItemAtPosition(position);

                    // Gets all fixtures on the current date
                    switch (filterOption) {
                        case "Today":

                            homeTeam.clear();
                            awayTeam.clear();
                            fixtureTime.clear();
                            fixtureDate.clear();

                            customAdapter.notifyDataSetChanged();

                    // Clearing list view

                            // Firebase Database Method
                            // Allows extraction of nodes
                            // Asynchronous method
                            // Listener is always listening to database
                            myRef.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    Fixture fixture = dataSnapshot.getValue(Fixture.class);


                                    DateFormat format = new SimpleDateFormat("EEE dd MMM, yy", Locale.ENGLISH);
                                    try {

                                        Date date = format.parse(fixture.getDate().replaceAll("(?<=\\d)(st|nd|rd|th)", ""));
                                        Date currentDate = new Date();
                                        format.format(currentDate);


                                        Calendar c = Calendar.getInstance();
                                        c.setTime(currentDate);
                                        c.add(Calendar.WEEK_OF_MONTH, 2);
                                        Date dateInAWeek = c.getTime();


                                        theFixtureName = fixture.getHomeTeam() + " vs " + fixture.getAwayTeam();

                                        // Getting all fixtures on the current date
                                        if (date.equals(currentDate)) {

                                            // Hash map used for passing fixture to selectVenueController
                                            fixtureMap.put(dataSnapshot.getKey(), theFixtureName);


                                            // Adding fixture data to arraylists for list view adapter

                                            homeTeam.add(fixture.getHomeTeam());
                                            awayTeam.add(fixture.getAwayTeam());
                                            fixtureTime.add(fixture.getTime());
                                            fixtureDate.add(fixture.getDate());
                                            fixtureNamesList.add(theFixtureName);

                                            customAdapter.notifyDataSetChanged();


                                        }


                                    } catch (ParseException e) {
                                        e.printStackTrace();
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


                            break;
                            // Get all fixtures from the current date and the date two weeks ahead
                        case "Two weeks":
                            // Clear list view
                            homeTeam.clear();
                            awayTeam.clear();
                            fixtureTime.clear();
                            fixtureDate.clear();
                            customAdapter.notifyDataSetChanged();

                            myRef.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    Fixture fixture = dataSnapshot.getValue(Fixture.class);


                                    DateFormat format = new SimpleDateFormat("EEE dd MMM, yy", Locale.ENGLISH);
                                    try {

                                        Date date = format.parse(fixture.getDate().replaceAll("(?<=\\d)(st|nd|rd|th)", ""));
                                        Date currentDate = new Date();
                                        format.format(currentDate);


                                        Calendar c = Calendar.getInstance();
                                        c.setTime(currentDate);
                                        c.add(Calendar.WEEK_OF_MONTH, 2);
                                        Date dateInTwoWeek = c.getTime();


                                        theFixtureName = fixture.getHomeTeam() + " vs " + fixture.getAwayTeam();

                                        // Getting all premier league fixtures that are in the range of the current date and the date in two weeks
                                        if (date.before(dateInTwoWeek) && date.after(currentDate)) {

                                        // Hash map used for passing fixture to selectVenueController
                                            fixtureMap.put(dataSnapshot.getKey(), theFixtureName);

                                            // Adding fixture data to arraylists for list view adapter
                                            homeTeam.add(fixture.getHomeTeam());
                                            awayTeam.add(fixture.getAwayTeam());
                                            fixtureTime.add(fixture.getTime());
                                            fixtureDate.add(fixture.getDate());
                                            fixtureNamesList.add(theFixtureName);


                                            customAdapter.notifyDataSetChanged();


                                        }


                                    } catch (ParseException e) {
                                        e.printStackTrace();
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
                            break;

                        // Get all fixtures from premier league
                        case "All":

                            // Clear list view
                            homeTeam.clear();
                            awayTeam.clear();
                            fixtureTime.clear();
                            fixtureDate.clear();
                            customAdapter.notifyDataSetChanged();

                            myRef.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    Fixture fixture = dataSnapshot.getValue(Fixture.class);


                                    DateFormat format = new SimpleDateFormat("EEE dd MMM, yy", Locale.ENGLISH);
                                    try {

                                        Date date = format.parse(fixture.getDate().replaceAll("(?<=\\d)(st|nd|rd|th)", ""));
                                        Date currentDate = new Date();
                                        format.format(currentDate);


                                        Calendar c = Calendar.getInstance();
                                        c.setTime(currentDate);
                                        c.add(Calendar.WEEK_OF_MONTH, 2);
                                        Date dateInTwoWeek = c.getTime();

                                        // Get all premier league fixtures that are after the current date or equal the current date

                                        if (date.after(currentDate) || date.equals(currentDate)) {
                                            theFixtureName = fixture.getHomeTeam() + " vs " + fixture.getAwayTeam();

                                            // Hash map used for passing fixture to selectVenueController
                                            fixtureMap.put(dataSnapshot.getKey(), theFixtureName);

                                            // Adding fixture data to arraylists for list view adapter
                                            homeTeam.add(fixture.getHomeTeam());
                                            awayTeam.add(fixture.getAwayTeam());
                                            fixtureTime.add(fixture.getTime());
                                            fixtureDate.add(fixture.getDate());
                                            fixtureNamesList.add(theFixtureName);


                                            customAdapter.notifyDataSetChanged();


                                        }


                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }


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
                            break;


                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        }


    }

        // Adapter used for fixture list

    class CustomAdapter extends BaseAdapter {

        // Method to return how many fixtures there are
        // The size of the arraylist is how many rows are created
        @Override
        public int getCount() {
            return homeTeam.size();
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
        // Create a row
        public View getView(int position, View convertView, ViewGroup parent) {

            // Inflate the custom layout inside the row
            convertView = getLayoutInflater().inflate(R.layout.fixture_list_item, null);


            TextView homeTeamName = convertView.findViewById(R.id.homeTeam);
            TextView awayTeamName = convertView.findViewById(R.id.awayTeam);
            TextView matchTimeText = convertView.findViewById(R.id.matchTime);
            TextView matchDateText = convertView.findViewById(R.id.matchDate);
            TextView vsText = convertView.findViewById(R.id.vsID);
            TextView leagueText = convertView.findViewById(R.id.footballLeagueText);

            homeTeamName.setTextColor(Color.BLACK);
            awayTeamName.setTextColor(Color.BLACK);
            matchTimeText.setTextColor(Color.BLACK);
            matchDateText.setTextColor(Color.BLACK);
            vsText.setTextColor(Color.BLACK);
            leagueText.setTextColor(Color.BLACK);

            // Reading as HTML as '<b' tags are used to make club name bold.
            // Index position is row number

            homeTeamName.setText(Html.fromHtml(homeTeam.get(position)));
            awayTeamName.setText(Html.fromHtml(awayTeam.get(position)));
            matchTimeText.setText(fixtureTime.get(position));
            matchDateText.setText(fixtureDate.get(position));
            leagueText.setText(footballLeague);

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

                Intent intent = new Intent(SelectFixtureController.this, FavouritesController.class);

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


                adapter = new ArrayAdapter<>(SelectFixtureController.this, android.R.layout.simple_list_item_1, searchNames);

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
                Set<String> removeDuplicates = new HashSet<>();
                removeDuplicates.addAll(searchNames);
                searchNames.clear();
                searchNames.addAll(removeDuplicates);

                adapter = new ArrayAdapter<>(SelectFixtureController.this, android.R.layout.simple_list_item_1, searchNames);
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
                Intent searchIntent = new Intent(SelectFixtureController.this, searchResultsActivity.class);
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
