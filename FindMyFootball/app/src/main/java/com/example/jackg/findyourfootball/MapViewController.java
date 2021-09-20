package com.example.jackg.findyourfootball;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.example.jackg.findyourfootball.DirectionFinderListener;


public class MapViewController extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private static final String TAG = "";
    private ArrayList<Venue> venues = new ArrayList<>();
    private ArrayList<Venue> singleVenue = new ArrayList<>();
    private GoogleMap mMap;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    boolean getDirections;
    private FusedLocationProviderClient mFusedLocationClient;
    private DirectionFinderListener directionFinderListener;
    double currentLat, currentLng;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);


        mGeoDataClient = Places.getGeoDataClient(this, null);

        final PlaceDetectionClient mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        final DatabaseReference myRef = database.getReference("Venues");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        venues = (ArrayList<Venue>) getIntent().getSerializableExtra("Passed venues");
        singleVenue = (ArrayList<Venue>) getIntent().getSerializableExtra("Passed venue");
        getDirections = getIntent().getExtras().getBoolean("Get Directions");

        // Adding map to map fragment.
        // Asynchronous task as it is rendering map from API
        mapFragment.getMapAsync(this);

        // Auto complete textView for search bar
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


        // Filtering the auto complete search bar
        // 'setTypeFilter(9)' is filter for only showing bars for auto complete
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(9)
                .setCountry("UK")
                .build();

        autocompleteFragment.setFilter(typeFilter);

        // Using auto complete suggestions with Google Places API
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                // ADMIN CODE FOR ADDING VENUES TO DATABASE

                //  String venueName = (String) place.getName();
                //   String venueAddress = (String) place.getAddress();
                //    double venueLat = place.getLatLng().latitude;
                //    double venueLong = place.getLatLng().longitude;
                //   String phoneNumber = (String) place.getPhoneNumber();
                //     Float rating = place.getRating();
                //   String url = String.valueOf(place.getWebsiteUri());
                //   String venueID = place.getId();


                //   Venue venue = new Venue(venueName, venueAddress, phoneNumber, url, venueLat, venueLong, rating);

                //       DatabaseReference myRef2 = myRef.child(venueID);


                //   myRef2.setValue(venue);


                // ADDING MARKER AND MOVING CAMERA TO ENTERED LOCATION ON SEARCH BAR
                String searchedName = (String) place.getName();
                LatLng searchedLocation = place.getLatLng();
                mMap.addMarker(new MarkerOptions().position(searchedLocation).title(searchedName));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(searchedLocation));
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });


    }

    // Map has been rendered
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Set listener for on click of marker window
        mMap.setOnInfoWindowClickListener(this);

        try {
            // Styling map, with JSON file. No icons.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }


        // Location of University of Westminster, Cavendish campus
        LatLng westminster = new LatLng(51.520605, -0.141069);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Showing location on map if permissions are granted
            mMap.setMyLocationEnabled(true);

        }

        if (venues != null) {
            // If user has chosen to see multiple venues on map
            for (Venue venue : venues) {
                LatLng venueLocation = new LatLng(venue.getLat(), venue.getLng());
                mMap.addMarker(new MarkerOptions().position(venueLocation).title(venue.getName()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(westminster));
                mMap.addMarker(new MarkerOptions().position(westminster).title("University of Westminster"));
            }

            // Used to indicate user wants to get directions
            // Previous activity passes flag indicating true to get direction.
        } else if (singleVenue != null && getDirections) {
            getDirections(singleVenue.get(0));


        } else if (singleVenue != null) {

            // If user has chosen to see one venue on the map
            for (Venue venue : singleVenue) {
                LatLng venueLocation = new LatLng(venue.getLat(), venue.getLng());
                mMap.addMarker(new MarkerOptions().position(venueLocation).title(venue.getName()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(venueLocation));
            }
        } else {

            // If no venues, add marker at uni of westminster
            mMap.addMarker(new MarkerOptions().position(westminster).title("University of Westminster"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(westminster));

        }


    }

    // Used to get directions to venue
    private void getDirections(Venue venue) {
                // Checking permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;

        } else {

            // Using Google Play Services to get last known location
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {
                            // Last known location
                        currentLat = location.getLatitude();
                        currentLng = location.getLongitude();


                        LatLng currentLocation = new LatLng(currentLat, currentLng);
                        LatLng venueLocation = new LatLng(venue.getLat(), venue.getLng());

                        // initiating route finding class to get direction to venue
                        directionFinderListener = new DirectionFinderListener() {
                            @Override
                            public void onDirectionFinderStart() {
                                progressDialog = ProgressDialog.show(MapViewController.this, "Please wait.",
                                        "Finding direction", true);


                                // REMOVING ANY MARKERS ON MAP
                                if (originMarkers != null) {
                                    for (Marker marker : originMarkers) {
                                        marker.remove();
                                    }
                                }

                                if (destinationMarkers != null) {
                                    for (Marker marker : destinationMarkers) {
                                        marker.remove();
                                    }
                                }

                                //REMOVE ANY POLYLINES OFF MAP

                                if (polylinePaths != null) {
                                    for (Polyline polyline : polylinePaths) {
                                        polyline.remove();
                                    }
                                }

                            }


                            @Override
                            public void onDirectionFinderSuccess(List<Route> routes) {
                                progressDialog.dismiss();
                                polylinePaths = new ArrayList<>();
                                originMarkers = new ArrayList<>();
                                destinationMarkers = new ArrayList<>();



                                for (Route route : routes) {
                                    // Getting each points of polyline for Route object
                                    // Adding markers for current location and destination location

                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));

                                    originMarkers.add(mMap.addMarker(new MarkerOptions()
                                            .title(route.startAddress)
                                            .position(route.startLocation)));
                                    destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                                            .title(venue.getName() + ", " + route.endAddress)
                                            .position(route.endLocation)));

                                    // Styling polyline
                                    PolylineOptions polylineOptions = new PolylineOptions().
                                            geodesic(true).
                                            color(Color.BLUE).
                                            width(10);


                                    // Adding each point to the polyline
                                    for (int i = 0; i < route.points.size(); i++)
                                        polylineOptions.add(route.points.get(i));

                                    polylinePaths.add(mMap.addPolyline(polylineOptions));
                                }

                            }
                        };


                        try {
                            // Executing route-finder class
                            new DirectionFinder(directionFinderListener, currentLocation, venueLocation).execute();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }


                        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                        // Move camera to devices location

                    }

                }
            });
        }

    }



    @Override
    public void onInfoWindowClick(Marker marker) {

        // Called if user selects window pane of marker
        // Get venues ID number and opens the venue screen passing the ID number



        if (venues != null) {
                    // If multiple venues on map
                    // Find one in ArrayList that user has selected
            for (Venue venue : venues) {
                if (marker.getTitle().equals(venue.getName())) {
                    Intent intent = new Intent(MapViewController.this, VenueController.class);
                    intent.putExtra("Passed Key", venue.getPlaceID());
                    startActivity(intent);
                }

            }

        } else if (singleVenue != null) {

                // If single venue on map view, get the ID number of venue and pass it to venue screen
            for (Venue venue : singleVenue) {
                System.out.println(venue);
                String venueName = venue.getName();
                if (venueName.equals(marker.getTitle())) {
                    Intent intent = new Intent(MapViewController.this, VenueController.class);
                    intent.putExtra("Passed Key", venue.getPlaceID());
                    startActivity(intent);
                }
            }

        }


    }
}
