package com.example.jackg.findyourfootball;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class VenueController extends AppCompatActivity {


    private ListView listView, reviewListView;
    private ScrollView adultScroll;
    private ArrayList<String> venueList = new ArrayList<>();
    private ArrayList<Bitmap> venueImages;
    private ArrayList<Venue> venues = new ArrayList<>();
    private ArrayList<Review> reviews;
    private ArrayList<String> homeTeam = new ArrayList<>();
    private ArrayList<String> awayTeam = new ArrayList<>();
    private ArrayList<String> fixtureTime = new ArrayList<>();
    private ArrayList<String> fixtureDate = new ArrayList<>();
    private ArrayList<String> fixtureLeague = new ArrayList<>();
    private ArrayList<String> searchNames = new ArrayList<>();
    private SearchView searchView;
    private SearchView.SearchAutoComplete searchAutoComplete;
    private ArrayAdapter<String> adapter;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    Boolean venueFound;

    private GeoDataClient mGeoDataClient;
    Bitmap bitmap;
    String venueKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_view);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        database = FirebaseDatabase.getInstance();
        venueFound = false;

        // Variable from Google Places API
        // Used to get photos from Google database for venue.
        mGeoDataClient = Places.getGeoDataClient(this, null);
        final DatabaseReference myRef = database.getReference("Venues");
        mAuth = FirebaseAuth.getInstance();

        venueImages = new ArrayList<>();

        // Array of fixture node names in database. Used to loop through each node, looking for fixtures.
        final String[] footballFixtures = {"Premier League Fixtures", "Champions League Fixtures", "La Liga Fixtures", "French Ligue 1 Fixtures", "FA Cup Fixtures",
                "EFL Championship Fixtures", "Bundesliga Fixture"};

        // Get current date, formatting it to specfied format.
        DateFormat format = new SimpleDateFormat("EEE dd MMM, yy", Locale.ENGLISH);
        Date currentDate = new Date();
        format.format(currentDate);


        listView = findViewById(R.id.venueFixtureListVIew);
        adultScroll = findViewById(R.id.parentScroll);
        reviewListView = findViewById(R.id.reviewsListview);
        ImageView favouriteIcon = findViewById(R.id.favouritesIcon);
        Button submitButton = findViewById(R.id.submitReviewBtn);
        Button getDirectionButton = findViewById(R.id.getDirectionsBtn);
        RatingBar reviewRatingBar = findViewById(R.id.reviewRating);
        TextView reviewTextField = findViewById(R.id.writeReviewTextfield);

        // Adapter for review list view
        reviewAdapter reviewAdapter = new reviewAdapter();
        reviews = new ArrayList<>();

        // Getting current logged in user
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            final DatabaseReference myRef2 = database.getReference("Users");
            final DatabaseReference myRef3 = myRef2.child(user.getUid());           // Getting reference to user node in database, using user ID number.

            myRef3.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User getUser = dataSnapshot.getValue(User.class);

                    //Checking if venue is already in users favourites
                    //Changing icon to confirmed icon
                    if (getUser.getVenueFavouritesList() != null) {
                        for (Venue venue : getUser.getVenueFavouritesList()) {
                            if (venue.getName().equals(venues.get(0).getName())) {
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


        // Checking if venue is already in users favourites
        // Changing icon colour to green if it is.

        favouriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();


                if (user != null) {
                    final DatabaseReference myRef = database.getReference("Users");
                    final DatabaseReference myRef2 = myRef.child(user.getUid());

                    myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User getUser = dataSnapshot.getValue(User.class);


                            String isFavourite = (String) favouriteIcon.getTag();


                            // Checking if the venue has been added to favourites by checking icon tag
                            // If user clicks icon with a 'notFavourite' tag, the venue will be added to the favourites
                            // The icon will change and the tag will be changed to 'isFavourite'
                            // Selecting the icon again, will remove the venue from the favourites and change the tag and icon.

                            if (getUser.getVenueFavouritesList() != null) {


                                if (isFavourite.equals("notFavourite")) {
                                    getUser.getVenueFavouritesList().addAll(venues);
                                    favouriteIcon.setImageResource(R.drawable.favourite_confirmation_icon);
                                    favouriteIcon.setTag("isFavourite");

                                } else if (isFavourite.equals("isFavourite")) {

                                    for (int i = 0; i < getUser.getVenueFavouritesList().size(); i++) {
                                        if (getUser.getVenueFavouritesList().get(i).getName().equals(venues.get(0).getName())) {
                                            getUser.getVenueFavouritesList().remove(i);

                                            favouriteIcon.setImageResource(R.drawable.favourites_icon);
                                            favouriteIcon.setTag("notFavourite");
                                        }
                                    }
                                }

                                //If venue favourites list is empty, add a new one.
                            } else {

                                getUser.setVenueFavouritesList(venues);
                                favouriteIcon.setImageResource(R.drawable.favourite_confirmation_icon);
                                favouriteIcon.setTag("isFavourite");

                            }

                            myRef2.setValue(getUser);
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {

                    // Checking if review text or rating bar is empty
                if (reviewRatingBar.getRating() == 0.0 || reviewTextField.getText().toString().isEmpty()) {
                    Toast toast = Toast.makeText(VenueController.this, "Please enter a review and select a rating.", Toast.LENGTH_LONG);
                    toast.show();
                } else {

                    // Getting current logged in user and obtaining the display name.

                    FirebaseUser user = mAuth.getCurrentUser();
                    String userName = user.getDisplayName();
                    Float rating = reviewRatingBar.getRating();
                    String writtenReview = reviewTextField.getText().toString();

                    // Instantiating a Review object with written review, the user who wrote it, the current date and the rating.
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = new Date();
                    String currentDate = formatter.format(date);
                    System.out.println(currentDate);
                    Review newReview = new Review(writtenReview, userName, currentDate, rating);

                    // Adding review to an ArrayList of Review object
                    // Set the ArrayList to the venue object.
                    reviews.add(newReview);
                    reviewListView.setAdapter(reviewAdapter);

                    venues.get(0).setVenuesReviews(reviews);

                    // Get reference to venue node in database
                    // Add a child called 'venuesReviews' adding the review.
                    // Updates the child if one already exists.
                    DatabaseReference myRef2 = myRef.child(venueKey);

                    myRef2.child("venuesReviews").setValue(venues.get(0).getVenuesReviews());


                }

            }
        });

        Bundle extras = getIntent().getExtras();

        if (extras != null)

        {
                // Getting passed venue key.
                // Used to get venue from database
            venueKey = extras.getString("Passed Key");

        }

        Button mapBtn = findViewById(R.id.viewMapbtn);

        mapBtn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                // Pass venue to map view
                Intent intent = new Intent(VenueController.this, MapViewController.class);
                intent.putExtra("Passed venue", venues);

                startActivity(intent);
            }
        });


        getDirectionButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                // Pass venue to map view with flag indicating user wants directions
                Intent intent = new Intent(VenueController.this, MapViewController.class);
                intent.putExtra("Passed venue", venues);
                intent.putExtra("Get Directions", true);
                startActivity(intent);
            }
        });



        // Disable touch on list views if scroll view is touched.
        adultScroll.setOnTouchListener(new View.OnTouchListener()

        {

            public boolean onTouch(View v, MotionEvent event) {

                findViewById(R.id.reviewsListview).getParent()
                        .requestDisallowInterceptTouchEvent(false);
                findViewById(R.id.venueFixtureListVIew).getParent()
                        .requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        // Disable touch on scroll view if review list view is touched.
        reviewListView.setOnTouchListener(new View.OnTouchListener()

        {

            public boolean onTouch(View v, MotionEvent event) {


                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        // Disable touch on scroll view if fixture list view is touched.
        listView.setOnTouchListener(new View.OnTouchListener()

        {

            public boolean onTouch(View v, MotionEvent event) {


                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });



        // Loop through each child node in database reference
        // Firebase Database Method
        // Allows extraction of nodes
        // Asynchronous method
        // Listener is always listening to database
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                // If the value of the node equals the venue key passed from the previous activity
                if (dataSnapshot.getKey().equals(venueKey)) {


                    ArrayList<String> venueAmmenitites;
                    ArrayList<String> venueTimes;

                    // Venue object is created by database return value
                    // Setters automatically input data in to variables if names match up.
                    Venue venue = dataSnapshot.getValue(Venue.class);

                    // Key of venue is adult node (getKey())
                    String adultKey = dataSnapshot.getKey();
                    venue.setPlaceID(adultKey);
                    venues.add(venue);

                    // Extracting the venues data
                    String venueName = venue.getName();
                    String venueAddress = venue.getAddress();
                    String venuePhoneNumber = venue.getPhoneNumber();
                    String venueUrl = venue.getUrl();
                    venueAmmenitites = venue.getAmmenitites();
                    venueTimes = venue.getOpeningClosingTimes();
                    String venueClosingTimes = "Opening/Closing Times ";
                    String ammenitites = "";

                    // Getting venue amenities
                    for (String amm : venueAmmenitites) {
                        ammenitites = ammenitites + "\n" + amm;
                    }

                    // Venue opening/closing times
                    for (String amm : venueTimes) {
                        venueClosingTimes = venueClosingTimes + "\n" + amm;
                    }

                    // Get venues reviews if there is any.
                    if (venue.getVenuesReviews() != null) {
                        reviews = venue.getVenuesReviews();
                        reviewListView.setAdapter(reviewAdapter);
                    }


                    // Setting view widgets

                    TextView venueNameTextField = findViewById(R.id.venueName);
                    TextView addressTextField = findViewById(R.id.addressTextView);
                    TextView phoneNumberTextField = findViewById(R.id.phoneNumberTextView);
                    TextView urlTextView = findViewById(R.id.urlTextView);
                    TextView ammenitiesTextview = findViewById(R.id.ammenititesTextView);
                    TextView openingClosingTextView = findViewById(R.id.openingClosingTextView);

                    venueNameTextField.setTextColor(Color.BLACK);
                    addressTextField.setTextColor(Color.BLACK);
                    phoneNumberTextField.setTextColor(Color.BLACK);
                    urlTextView.setTextColor(Color.BLACK);
                    ammenitiesTextview.setTextColor(Color.BLACK);
                    openingClosingTextView.setTextColor(Color.BLACK);

                    ammenitiesTextview.setText(ammenitites);
                    venueNameTextField.setText(venueName);
                    addressTextField.setText(venueAddress);
                    phoneNumberTextField.setText(venuePhoneNumber);
                    urlTextView.setText(venueUrl);
                    openingClosingTextView.setText(venueClosingTimes);

                    getPhotos(venueKey);

                    // Iterating over each node of the venue
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {


                        // Structure of database means only a Boolean value is a fixture key
                        // If value is a Boolean value(true), it must be a fixture key
                        // Extracting that fixture key a database reference is created
                        // At this reference, the value can return null.
                        if (ds.getValue().equals(true)) {

                            // Looping through node names of fixtures
                            for (int i = 0; i < footballFixtures.length; i++) {

                                    // Using node name from Array, creating database reference.
                                    // Using fixture key as child to find specific fixture.

                                DatabaseReference myRef2 = database.getReference(footballFixtures[i]).child(ds.getKey());

                                int finalI = i;
                                myRef2.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {


                                        Fixture fixture = dataSnapshot.getValue(Fixture.class);

                                        // Fixture value can still return null
                                        // If value is not null, the fixture has been found.
                                        if (fixture != null) {

                                            try {

                                                Date date = format.parse(fixture.getDate().replaceAll("(?<=\\d)(st|nd|rd|th)", ""));

                                                // Making sure fixture is after current date.
                                                // We don't want old fixtures.
                                                if (date.after(currentDate)) {
                                                    homeTeam.add(fixture.getHomeTeam());
                                                    awayTeam.add(fixture.getAwayTeam());
                                                    fixtureTime.add(fixture.getTime());
                                                    fixtureDate.add(fixture.getDate());
                                                    String footballLeague = footballFixtures[finalI].replace("Fixtures", "");
                                                    fixtureLeague.add(footballLeague);

                                                    // Adding fixtures to an ArrayList, adding to adapter for list view

                                                    CustomAdapter customAdapter = new CustomAdapter();
                                                    listView.setAdapter(customAdapter);
                                                }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }


                                        }


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                        }


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


    }
    // Provided by Google Places API
    // Used to get photos of a venue
    private void getPhotos(String adultKey) {

        // Using a venues Place ID, the API downloads the venue images from the Google database.
        // Images are stored in a list of metadata objects.
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(adultKey);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                PlacePhotoMetadataResponse photos = task.getResult();
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();

                // Once the photos are downloaded and stored in a list.
                // The first photo is accessed. This photo is then converted in to a bitmap image

                PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                        // Photo is first converted in to a 'PlacePhotoResponse' object.
                        // Object provides the method to convert the photo to a bitmap
                        // Once a bitmap type, set the photo to the venue object
                        // Adds the bitmap to an arraylist of venue images, used to portait in the list of venues
                        PlacePhotoResponse photo = task.getResult();
                        bitmap = photo.getBitmap();
                        ImageView venueImage = findViewById(R.id.venueImage);
                        venueImage.setImageBitmap(bitmap);
                        venueImages.add(bitmap);


                    }
                });
            }
        });

    }


    class CustomAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return homeTeam.size();
        }
        // Get count of ArrayList used for list view
        // For each item in the list, the adapters calls the 'getView' method.


        @Override
        public Object getItem(int position) {
            return null;
        }
        // Method to get an item in a view (row)

        @Override
        public long getItemId(int position) {
            return 0;
        }
        // Get a ID number of the item in the row

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Use a custom layout inside the view (row)
            convertView = getLayoutInflater().inflate(R.layout.fixture_list_item, null);


            // Using the view, insert data in to the widgets.
            TextView homeTeamName = convertView.findViewById(R.id.homeTeam);
            TextView awayTeamName = convertView.findViewById(R.id.awayTeam);
            TextView matchTimeText = convertView.findViewById(R.id.matchTime);
            TextView matchDateText = convertView.findViewById(R.id.matchDate);
            TextView vsText = convertView.findViewById(R.id.vsID);
            TextView footballleagueText = convertView.findViewById(R.id.footballLeagueText);

            homeTeamName.setTextColor(Color.BLACK);
            awayTeamName.setTextColor(Color.BLACK);
            matchTimeText.setTextColor(Color.BLACK);
            matchDateText.setTextColor(Color.BLACK);
            vsText.setTextColor(Color.BLACK);
            footballleagueText.setTextColor(Color.BLACK);


            // Using current position of 'getCount' method to access the ArrayLists
            // If 'getCount' returns size of 2. Index 2 is accessed in ArrayLists
            homeTeamName.setText(Html.fromHtml(homeTeam.get(position)));
            awayTeamName.setText(Html.fromHtml(awayTeam.get(position)));
            matchTimeText.setText(fixtureTime.get(position));
            matchDateText.setText(fixtureDate.get(position));
            footballleagueText.setText(fixtureLeague.get(position));


            // Adds the view
            return convertView;
        }
    }


    class reviewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return reviews.size();
        }
        // Get count of ArrayList used for list view
        // For each item in the list, the adapters calls the 'getView' method.

        @Override
        public Object getItem(int position) {
            return null;
        }
        // Method to get an item in a view (row)

        @Override
        public long getItemId(int position) {
            return 0;
        }
        // Get a ID number of the item in the row

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Use a custom layout inside the view (row)
            convertView = getLayoutInflater().inflate(R.layout.review_list_item, null);

            // Using the view, insert data in to the widgets.
            TextView reviewText = convertView.findViewById(R.id.reviewText);
            TextView userName = convertView.findViewById(R.id.userID);
            TextView dateOfReview = convertView.findViewById(R.id.currentDateTextView);
            RatingBar reviewRating = convertView.findViewById(R.id.reviewRatingIndicator);

            userName.setTextColor(Color.BLACK);
            reviewText.setTextColor(Color.BLACK);
            dateOfReview.setTextColor(Color.BLACK);


            // Using current position of 'getCount' method to access the ArrayLists
            // If 'getCount' returns size of 2. Index 2 is accessed in ArrayLists
            reviewText.setText(reviews.get(position).getReview());
            reviewRating.setRating(reviews.get(position).getRating());
            userName.setText(reviews.get(position).getDisplayName());
            dateOfReview.setText(reviews.get(position).getDateOfReview());

            // Adds the view
            return convertView;
        }

    }

    // Method to create top navigation bar
    // Explained in Menu Controller class.
    @SuppressLint("RestrictedApi")
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.search);
        MenuItem favouriteIcon = menu.findItem(R.id.action_favorite);

        favouriteIcon.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent intent = new Intent(VenueController.this, FavouritesController.class);

                startActivity(intent);

                return false;
            }
        });

        DatabaseReference myRef = database.getReference("Football Leagues");
        // Firebase Database Method
        // Allows extraction of nodes
        // Asynchronous method
        // Listener is always listening to database
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


                adapter = new ArrayAdapter<>(VenueController.this, android.R.layout.simple_list_item_1, searchNames);

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

                adapter = new ArrayAdapter<>(VenueController.this, android.R.layout.simple_list_item_1, searchNames);
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
                Intent searchIntent = new Intent(VenueController.this, searchResultsActivity.class);
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
