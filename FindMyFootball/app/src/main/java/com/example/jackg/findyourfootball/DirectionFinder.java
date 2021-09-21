package com.example.jackg.findyourfootball;

import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackg on 07/04/2018.
 */

// ClASS WAS CREATED BY FOLLOWING A GUIDE
public class DirectionFinder {

    private static final String DIRECTION_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String API_KEY = "";
    private DirectionFinderListener listener;
    private LatLng currentPlace, destination;


    public DirectionFinder(DirectionFinderListener listener, LatLng currentPlace, LatLng destination) {
        this.listener = listener;
        this.currentPlace = currentPlace;
        this.destination = destination;
    }



    // Start steps of getting directions
    // Call listener method, create encoded URL for API call
    // Download raw data from API return
    public void execute() throws UnsupportedEncodingException {
        listener.onDirectionFinderStart();
        new DownloadRawData().execute(createURL());


    }
    // Creating URL for API call, uses devices current latitude and longitude  and venues latitude and longitude
    // Encodes the URL and returns it
    private String createURL() throws UnsupportedEncodingException {
        String getCurrentPlace = String.valueOf(currentPlace.latitude + "," + currentPlace.longitude);
        String getVenuePlace = String.valueOf(destination.latitude + "," + destination.longitude);
        String url = URLEncoder.encode( getCurrentPlace, "utf-8");
       String urlDestination = URLEncoder.encode(getVenuePlace, "utf-8");
        System.out.println(DIRECTION_URL + "origin=" + url  + "&destination=" + urlDestination + "&key=" + API_KEY);
        return  DIRECTION_URL + "origin=" +  url  + "&destination=" +  urlDestination + "&key=" + API_KEY;
    }


    private class DownloadRawData extends AsyncTask<String,Void,String> {


        // Using passed URL, make API Call and download raw data

        @Override
        protected String doInBackground(String... strings) {
           String link = strings[0];
           try {
               URL url = new URL(link);
               InputStream is = url.openConnection().getInputStream();
               StringBuffer buffer = new StringBuffer();
               BufferedReader reader = new BufferedReader(new InputStreamReader(is));

               String line; while((line = reader.readLine()) != null){
                   buffer.append(line + "\n");
               }

               return buffer.toString();

           } catch (MalformedURLException e) {
               e.printStackTrace();
           } catch (IOException e) {
               e.printStackTrace();
           }
           return null;
        }

        // Once raw data is downloaded, it is parsed to a JSON object using parseJson method

        protected void onPostExecute(String res){
            try {
                parseJSon(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    // Converts raw data to JSON object
    // All nodes for 'routes' are selected, stored in a JSON array.
    // Each node is initialised as a JSON object
    // Using Route object, parts of nodes are extracted of there data.

    private void parseJSon(String data) throws JSONException {
        if (data == null)
            return;

        List<Route> routes = new ArrayList<Route>();
        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes = jsonData.getJSONArray("routes");
        for (int i = 0; i < jsonRoutes.length(); i++) {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            Route route = new Route();

            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");

            // Legs are container nodes for route
            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
            JSONObject jsonLeg = jsonLegs.getJSONObject(0);

            JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
            JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");

            route.endAddress = jsonLeg.getString("end_address");
            route.startAddress = jsonLeg.getString("start_address");
            route.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
            route.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));

            // Points are made up from current location to destination
            // Each point is a latitude and longitude value.
            // When each point is connected by a line, it shows the route.
            // Each point is encoded by the API, needs to be decoded.
            route.points = decodePolyLine(overview_polylineJson.getString("points"));

            // Once necessary route data is stored in object, it is added to an ArrayList
            // ArrayList is then passed back to map view controller for it to use.

            routes.add(route);
        }

        listener.onDirectionFinderSuccess(routes);
    }

    // Decoding polyline. Obtained from Guide.
    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }

}
