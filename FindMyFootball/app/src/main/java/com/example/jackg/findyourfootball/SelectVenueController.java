package com.example.jackg.findyourfootball;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.support.v7.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SelectVenueController extends AppCompatActivity {


    private ListView listView;
    private ArrayList<String> venueList = new ArrayList<>();
    private FusedLocationProviderClient mFusedLocationClient;
    private ArrayList<Venue> venues = new ArrayList<>();
    private ArrayList<Venue> mapVenues = new ArrayList<>();
    CustomAdapter customAdapter = new CustomAdapter();
    String footballLeague, fixtureKey, theFixtureName;
    private GeoDataClient mGeoDataClient;
    Bitmap bitmap;
    double currentLat, currentLng;
    private SearchView searchView;
    private SearchView.SearchAutoComplete searchAutoComplete;
    private ArrayList<String> searchNames = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_venue_view);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Venues");

        // Google Places API variable, used to get photos
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Google play services variable, used to get last known location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLocation();


        final TextView venuesTitle = findViewById(R.id.venuesTitle);
        listView = findViewById(R.id.venueListView);
        Button mapBtn = findViewById(R.id.viewMapbtn);

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass venues to map view
                Intent intent = new Intent(SelectVenueController.this, MapViewController.class);
                intent.putExtra("Passed venues", mapVenues);
                startActivity(intent);
            }
        });


        Bundle extras = getIntent().getExtras();

        // Checking if a fixture has been selected and passed
        if (extras != null) {
            footballLeague = extras.getString("Passed League");
            fixtureKey = extras.getString("Passed Key");
            theFixtureName = extras.getString("Passed Fixture");
            venuesTitle.setText("Venues showing" + " " + theFixtureName);
            getLocation();


            // Spinner for the filter options
            Spinner spinner = findViewById(R.id.venueFilter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String filterOption = (String) parent.getItemAtPosition(position);

                    // Switch statement for the filter option
                    switch (filterOption) {
                        // Filter for the most popular venues
                        case "Popular":

                            // Clearing list of venues
                            venues.clear();
                            customAdapter.notifyDataSetChanged();

                            // Firebase Database Method
                            // Allows extraction of nodes
                            // Asynchronous method
                            // Listener is always listening to database
                            myRef.addChildEventListener(new ChildEventListener() {
                                @Override

                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {



                                    // Looping through venue nodes
                                    // Node can be named after a fixture key
                                    // If found, that venue is showing that fixture
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {


                                        // Using passed fixture key from previous activity
                                        // See if a venue contains the node
                                        // If it does, extract that venue
                                        if (ds.getKey().equals(fixtureKey)) {

                                            Venue venue = dataSnapshot.getValue(Venue.class);

                                            String adultKey = dataSnapshot.getKey();
                                            venue.setPlaceID(adultKey);

                                            venue.setVenueImage(getPhotos(adultKey, venue));

                                            mapVenues.add(venue);


                                            // Using last known location of the device and the venues location
                                            // Calculate the distance between them
                                            Location currentLocation = new Location("currentLocation");

                                            currentLocation.setLongitude(currentLng);
                                            currentLocation.setLatitude(currentLat);

                                            Location location = new Location("venueLocation");
                                            location.setLatitude(venue.getLat());
                                            location.setLongitude(venue.getLng());

                                            // Convert the distance value in to the distance in miles
                                            double distanceToVenue2 = currentLocation.distanceTo(location);
                                            double convertToMiles = distanceToVenue2 * 0.000621;

                                            String distanceToVenue;

                                            distanceToVenue = String.format("%.2f", convertToMiles);

                                            // Add the distance to venue object

                                            venue.setDistanceToVenue(distanceToVenue);


                                            venues.add(venue);

                                            // Sort by rating as filter option os 'Popular'
                                            Collections.sort(venues,
                                                    (o1, o2) -> o2.getRating().compareTo(o1.getRating()));


                                        }
                                    }

                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                        // Extracts node if value is changed
                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {

                                    // Extracts node if value is removed

                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                    // Extracts node if value is moved
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                            break;

                            // Get venues that are closest to devices last known location
                        case "Closest":

                            // Clear list of venues
                            venues.clear();
                            customAdapter.notifyDataSetChanged();

                            // Firebase Database Method
                            // Allows extraction of nodes
                            // Asynchronous method
                            // Listener is always listening to database
                            myRef.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                    // Looping through venue nodes
                                    // Node can be named after a fixture key
                                    // If found, that venue is showing that fixture
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                        // Using passed fixture key from previous activity
                                        // See if a venue contains the node
                                        // If it does, extract that venue
                                        if (ds.getKey().equals(fixtureKey)) {

                                            Venue venue = dataSnapshot.getValue(Venue.class);

                                            String adultKey = dataSnapshot.getKey();
                                            venue.setPlaceID(adultKey);

                                            venue.setVenueImage(getPhotos(adultKey, venue));
                                            mapVenues.add(venue);


                                            // Using last known location of the device and the venues location
                                            // Calculate the distance between them
                                            Location currentLocation = new Location("currentLocation");

                                            currentLocation.setLongitude(currentLng);
                                            currentLocation.setLatitude(currentLat);

                                            Location location = new Location("venueLocation");
                                            location.setLatitude(venue.getLat());
                                            location.setLongitude(venue.getLng());

                                            double distanceToVenue2 = currentLocation.distanceTo(location);
                                            double convertToMiles = distanceToVenue2 * 0.000621;

                                            String distanceToVenue;

                                            distanceToVenue = String.format("%.2f", convertToMiles);

                                            // Add the distance to venue object
                                            venue.setDistanceToVenue(distanceToVenue);

                                            venues.add(venue);


                                            // Sort by distance as the filter option is 'Closest'

                                            Collections.sort(venues,
                                                    (o1, o2) -> o1.getDistanceToVenue().compareTo(o2.getDistanceToVenue()));


                                        }
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

                            // Sort list of venues by A-Z
                        case "A-Z":

                            // Clear list of venues
                            venues.clear();
                            customAdapter.notifyDataSetChanged();

                            // Firebase Database Method
                            // Allows extraction of nodes
                            // Asynchronous method
                            // Listener is always listening to database
                            myRef.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                                    // Looping through venue nodes
                                    // Node can be named after a fixture key
                                    // If found, that venue is showing that fixture
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                        // Using passed fixture key from previous activity
                                        // See if a venue contains the node
                                        // If it does, extract that venue
                                        if (ds.getKey().equals(fixtureKey)) {

                                            Venue venue = dataSnapshot.getValue(Venue.class);
                                            mapVenues.add(venue);
                                            String adultKey = dataSnapshot.getKey();
                                            venue.setPlaceID(adultKey);

                                            venue.setVenueImage(getPhotos(adultKey, venue));

                                            // Using last known location of the device and the venues location
                                            // Calculate the distance between them
                                            Location currentLocation = new Location("currentLocation");

                                            currentLocation.setLongitude(currentLng);
                                            currentLocation.setLatitude(currentLat);

                                            Location location = new Location("venueLocation");
                                            location.setLatitude(venue.getLat());
                                            location.setLongitude(venue.getLng());

                                            double distanceToVenue2 = currentLocation.distanceTo(location);
                                            double convertToMiles = distanceToVenue2 * 0.000621;

                                            String distanceToVenue;

                                            distanceToVenue = String.format("%.2f", convertToMiles);


                                            venue.setDistanceToVenue(distanceToVenue);


                                            venues.add(venue);

                                            // Sort by name as filter option is 'A-Z'
                                            Collections.sort(venues,
                                                    (o1, o2) -> o1.getName().compareTo(o2.getName()));


                                        }
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

            // Called if user selects a row in list view
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // Custom layout contains a hidden textview for venue key
                    // Venue key is obtained and passed to VenueController activity.

                    Intent intent = new Intent(SelectVenueController.this, VenueController.class);

                    TextView venueInvisKey = view.findViewById(R.id.venueKey);
                    String listViewName = venueInvisKey.getText().toString();

                    intent.putExtra("Passed Key", listViewName);
                    startActivity(intent);

                }


            });

        } else {

           // No fixture was selected. Extract all venues


            Spinner spinner = findViewById(R.id.venueFilter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String filterOption = (String) parent.getItemAtPosition(position);

                    switch (filterOption) {
                        // Sort list of venues by most popular
                        case "Popular":

                            // Clearing list of venues
                            venues.clear();
                            customAdapter.notifyDataSetChanged();

                            // Firebase Database Method
                            // Allows extraction of nodes
                            // Asynchronous method
                            // Listener is always listening to database
                            myRef.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                                    Venue venue = dataSnapshot.getValue(Venue.class);

                                    // Add venues to arraylist to pass to map view
                                    mapVenues.add(venue);
                                    String adultKey = dataSnapshot.getKey();
                                    venue.setPlaceID(adultKey);

                                    venue.setVenueImage(getPhotos(adultKey, venue));

                                    // Using last known location of the device and the venues location
                                    // Calculate the distance between them
                                    Location currentLocation = new Location("currentLocation");

                                    currentLocation.setLongitude(currentLng);
                                    currentLocation.setLatitude(currentLat);

                                    Location location = new Location("venueLocation");
                                    location.setLatitude(venue.getLat());
                                    location.setLongitude(venue.getLng());

                                    double distanceToVenue2 = currentLocation.distanceTo(location);
                                    double convertToMiles = distanceToVenue2 * 0.000621;

                                    String distanceToVenue;

                                    distanceToVenue = String.format("%.2f", convertToMiles);


                                    venue.setDistanceToVenue(distanceToVenue);


                                    venues.add(venue);

                                    // Sort by rating as filter option is 'Popular'

                                    Collections.sort(venues,
                                            (o1, o2) -> o2.getRating().compareTo(o1.getRating()));


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
                        // Sort list of venues by closest to devices last known location
                        case "Closest":

                            // Clearing list of venues
                            venues.clear();
                            customAdapter.notifyDataSetChanged();

                            // Firebase Database Method
                            // Allows extraction of nodes
                            // Asynchronous method
                            // Listener is always listening to database
                            myRef.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                                    Venue venue = dataSnapshot.getValue(Venue.class);

                                    String adultKey = dataSnapshot.getKey();
                                    venue.setPlaceID(adultKey);

                                    venue.setVenueImage(getPhotos(adultKey, venue));
                                    mapVenues.add(venue);

                                    // Using last known location of the device and the venues location
                                    // Calculate the distance between them
                                    Location currentLocation = new Location("currentLocation");

                                    currentLocation.setLongitude(currentLng);
                                    currentLocation.setLatitude(currentLat);

                                    Location location = new Location("venueLocation");
                                    location.setLatitude(venue.getLat());
                                    location.setLongitude(venue.getLng());

                                    double distanceToVenue2 = currentLocation.distanceTo(location);
                                    double convertToMiles = distanceToVenue2 * 0.000621;

                                    String distanceToVenue;

                                    distanceToVenue = String.format("%.2f", convertToMiles);


                                    venue.setDistanceToVenue(distanceToVenue);

                                    venues.add(venue);

                                    // Sort by distance as filter option is 'Closest'
                                    Collections.sort(venues,
                                            (o1, o2) -> o1.getDistanceToVenue().compareTo(o2.getDistanceToVenue()));


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
                        // Sort list of venues by A-Z
                        case "A-Z":
                        // Clearing list of venues
                            venues.clear();
                            customAdapter.notifyDataSetChanged();

                            // Firebase Database Method
                            // Allows extraction of nodes
                            // Asynchronous method
                            // Listener is always listening to database
                            myRef.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                                    Venue venue = dataSnapshot.getValue(Venue.class);

                                    String adultKey = dataSnapshot.getKey();
                                    venue.setPlaceID(adultKey);
                                    mapVenues.add(venue);
                                    venue.setVenueImage(getPhotos(adultKey, venue));

                                    // Using last known location of the device and the venues location
                                    // Calculate the distance between them
                                    Location currentLocation = new Location("currentLocation");

                                    currentLocation.setLongitude(currentLng);
                                    currentLocation.setLatitude(currentLat);

                                    Location location = new Location("venueLocation");
                                    location.setLatitude(venue.getLat());
                                    location.setLongitude(venue.getLng());

                                    double distanceToVenue2 = currentLocation.distanceTo(location);
                                    double convertToMiles = distanceToVenue2 * 0.000621;

                                    String distanceToVenue;

                                    distanceToVenue = String.format("%.2f", convertToMiles);


                                    venue.setDistanceToVenue(distanceToVenue);


                                    venues.add(venue);

                                    // Sort by name as filter option is 'A-Z'
                                    Collections.sort(venues,
                                            (o1, o2) -> o1.getName().compareTo(o2.getName()));


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

                // Called if user selects a row on list
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent(SelectVenueController.this, VenueController.class);

                    // Custom layout contains a hidden textview for venue key
                    // Venue key is obtained and passed to VenueController activity.
                    TextView venueInvisKey = view.findViewById(R.id.venueKey);
                    String listViewName = venueInvisKey.getText().toString();

                    intent.putExtra("Passed Key", listViewName);
                    startActivity(intent);

                }


            });


        }

    }

    private void getLocation() {
                // Method to get last know location of device
                // Checks permissions if user has granted them.
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {

            // Using Google Play Services, the API provides the function of accessing the devices last known location
            // Using one the devices providers (GPS, Network)

            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLat = location.getLatitude();
                        currentLng = location.getLongitude();
                    }

                }
            });
        }

    }


    // Method to get photos of the venues
    // Provided by Google Places API
    // Uses the Place ID of a venue to get the photos

    private Bitmap getPhotos(String adultKey, Venue venue) {
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(adultKey);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            // Method is called and photos are downloaded as metadata inside the object 'PlacePhotoMetaDataBuffer'
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();

                // Get the first photo in the list.
                PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);

                // Using the first photo in the list
                // Method to convert the photo to a bitmap
                Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlacePhotoResponse> task) {

                        // Photo is converted to a bit map and stored in the venues object.
                        PlacePhotoResponse photo = task.getResult();
                        bitmap = photo.getBitmap();

                        venue.setVenueImage(bitmap);

                        listView.setAdapter(customAdapter);


                    }
                });
            }
        });

        return bitmap;

    }


    class CustomAdapter extends BaseAdapter {
        // Method to return how many venues there are
        // The size of the arraylist is how many rows are created
        @Override
        public int getCount() {
            return venues.size();
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
            convertView = getLayoutInflater().inflate(R.layout.venue_list_item, null);


            ImageView imageview = convertView.findViewById(R.id.venueThumbnail);
            TextView venueTitle = convertView.findViewById(R.id.venueTitle);
            TextView distance = convertView.findViewById(R.id.distance);
            TextView venueKeysInvisible = convertView.findViewById(R.id.venueKey);
            RatingBar ratingBar = convertView.findViewById(R.id.ratingBar);

            venueTitle.setTextColor(Color.BLACK);
            distance.setTextColor(Color.BLACK);


            // Adding the venues data to the widgets
            // Custom layout uses an invisible textview to hide the venues place ID
            // This textview is accessed when passing the venue to the venueController activity.

            venueKeysInvisible.setText(venues.get(position).getPlaceID());
            venueTitle.setText(venues.get(position).getName());
            distance.setText(venues.get(position).getDistanceToVenue() + "m");
            imageview.setImageBitmap(venues.get(position).getVenueImage());
            ratingBar.setRating(venues.get(position).getRating());

            return convertView;
        }
    }

    // Method to create navigation bar
    // Used for search icon and favourites icon
    // Explained in MenuController

    @SuppressLint("RestrictedApi")
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.search);
        MenuItem favouriteIcon = menu.findItem(R.id.action_favorite);

        favouriteIcon.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent intent = new Intent(SelectVenueController.this, FavouritesController.class);

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


                adapter = new ArrayAdapter<>(SelectVenueController.this, android.R.layout.simple_list_item_1, searchNames);

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

                adapter = new ArrayAdapter<>(SelectVenueController.this, android.R.layout.simple_list_item_1, searchNames);
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
                Intent searchIntent = new Intent(SelectVenueController.this, searchResultsActivity.class);
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
