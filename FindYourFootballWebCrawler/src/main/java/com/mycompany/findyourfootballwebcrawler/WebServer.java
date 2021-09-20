/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.findyourfootballwebcrawler;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.jws.WebService;
import javax.jws.WebMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author jackg
 */
@WebService(serviceName = "WebCrawler")
public class WebServer {

    ArrayList<String> footballTeams = new ArrayList<String>();
    String venueKey;
    String homeTeam, awayTeam;

    /**
     * Web service operation
     *
     * @return
     * @throws java.io.FileNotFoundException
     */
    
    // Web service to get premier league fixtures 
    @WebMethod(operationName = "GetPremierFixtures")
    public String GetPremierFixtures() throws FileNotFoundException, IOException {
        
        // Set up account for Firebase Database
        // Service account contains options to connect to Project database
        // File downloaded from Firebase 
          FileInputStream serviceAccount = new FileInputStream("NetBeansProjects\\FindYourFootballWebCrawler//serviceAccountKey.json");
        ArrayList<Fixture> newFixtures = new ArrayList<Fixture>();
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://find-your-football.firebaseio.com/")
                .build();

        try {
            // Set up database instance or initalize the app.
            FirebaseApp.getInstance();
        } catch (IllegalStateException e) {
            //Firebase not initialized automatically, do it manually
            FirebaseApp.initializeApp(options);
        }

        // Get reference to premier league fixtures node 
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Premier League Fixtures");
        //DatabaseReference usersRef = ref.child("");

        // Connect to the sky sports website for premier league fixtures 
        Document document = Jsoup.connect("http://www.skysports.com/premier-league-fixtures").get();

        // Selecting the container that contains all of the fixtures 
        Element body = document.select("div.fixres__body").first();
        // Selecting all of the children of this container
        Elements body2 = body.children();

        String date = "";
        
      
           // Looping through each element in the container of fixtures 
           
        for (Element page : body2) {

            // Group of fixtures are organised by the date of the fixtures
            // h4 tag is the tag for the fixture date
            // By checking if this tag is first, and extracting the date 
            // It allows the application to group the extracted fixtures by date 
            if (page.tagName().equals("h4")) {
                date = page.ownText() + ", 2018";

                // The tag name for the container that contains a single fixture
            } else if (page.className().equals("fixres__item")) {

                // Select all of the football teams in the fixture container
                // Will only be two teams per container
                // Select all the times of the fixture
                // Will only contain one per container 
                Elements footballTeams = page.select("span.swap-text__target");
                Elements footballTimes = page.select("span.matches__date");

                ArrayList<String> fixture;
                ArrayList<String> fixtureTimes;

                // Element object for the time will contain all the data including the tag
                // The method 'eachText' extracts only the text from each of the Element objects. 
                // The extracted data can then be stored in to an arraylist 
                fixtureTimes = (ArrayList<String>) footballTimes.eachText();

                // Same again for the football teams
                
                fixture = (ArrayList<String>) footballTeams.eachText();
                //  System.out.println(fixture + " " + fixture.size() + " " + fixtureTimes);

                String homeTeam, awayTeam, time;

               // As each container will only contain two football teams
               // The first will always be the home team and the second will always be the away team
               // For the time, there will only be one per container
                homeTeam = fixture.get(0);
                awayTeam = fixture.get(1);
                time = fixtureTimes.get(0);
                
                

                Fixture newFixture = new Fixture(homeTeam, awayTeam, time, date);

                // Push the fixture to the database 
                ref.push().setValueAsync(newFixture);
            }

        }

        return "YES";
    }

    /**
     * Web service operation
     *
     * @return
     * @throws java.io.FileNotFoundException
     */
    @WebMethod(operationName = "GetChampionshipFixtures")
    public String GetChampionshipFixtures() throws FileNotFoundException, IOException {
        // Set up account for Firebase Database
        // Service account contains options to connect to Project database
        // File downloaded from Firebase 
          FileInputStream serviceAccount = new FileInputStream("NetBeansProjects\\FindYourFootballWebCrawler//serviceAccountKey.json");
        ArrayList<Fixture> newFixtures = new ArrayList<Fixture>();
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://find-your-football.firebaseio.com/")
                .build();

        try {
               // Set up database instance or initalize the app.
            FirebaseApp.getInstance();
        } catch (IllegalStateException e) {
            //Firebase not initialized automatically, do it manually
            FirebaseApp.initializeApp(options);
        }

        
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("EFL Championship Fixtures");
        //DatabaseReference usersRef = ref.child("");

        // Connect to the sky sports website for premier league fixtures 
        Document document = Jsoup.connect("http://www.skysports.com/championship-fixtures").get();
        
        // Selecting the container that contains all of the fixtures 
        Element body = document.select("div.fixres__body").first();
           // Selecting all of the children of this container
        Elements body2 = body.children();

        String date = "";

           // Looping through each element in the container of fixtures 
           
        for (Element page : body2) {
            
            // Group of fixtures are organised by the date of the fixtures
            // h4 tag is the tag for the fixture date
            // By checking if this tag is first, and extracting the date 
            // It allows the application to group the extracted fixtures by date 

            if (page.tagName().equals("h4")) {
                date = page.ownText() + ", 2018";

            } else if (page.className().equals("fixres__item")) {

                Elements footballTeams = page.select("span.swap-text__target");
                Elements footballTimes = page.select("span.matches__date");

                ArrayList<String> fixture;
                ArrayList<String> fixtureTimes;

                    // Element object for the time will contain all the data including the tag
                // The method 'eachText' extracts only the text from each of the Element objects. 
                // The extracted data can then be stored in to an arraylist 
                fixtureTimes = (ArrayList<String>) footballTimes.eachText();

                         // Same again for the football teams
                fixture = (ArrayList<String>) footballTeams.eachText();
                //  System.out.println(fixture + " " + fixture.size() + " " + fixtureTimes);

                String homeTeam, awayTeam, time;
                
                // As each container will only contain two football teams
               // The first will always be the home team and the second will always be the away team
               // For the time, there will only be one per container

                homeTeam = fixture.get(0);
                awayTeam = fixture.get(1);
                time = fixtureTimes.get(0);

                Fixture newFixture = new Fixture(homeTeam, awayTeam, time, date);
                
                // Push the fixture to the database 
                ref.push().setValueAsync(newFixture);

            }

        }

        return "YES";
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "GetLaLigaFixtures")
    public String GetLaLigaFixtures() throws IOException {
         // Set up account for Firebase Database
        // Service account contains options to connect to Project database
        // File downloaded from Firebase 
       FileInputStream serviceAccount = new FileInputStream("NetBeansProjects\\FindYourFootballWebCrawler//serviceAccountKey.json");
        ArrayList<Fixture> newFixtures = new ArrayList<Fixture>();
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://find-your-football.firebaseio.com/")
                .build();

        try {
            FirebaseApp.getInstance();
                   // Set up database instance or initalize the app.
        } catch (IllegalStateException e) {
            //Firebase not initialized automatically, do it manually
            FirebaseApp.initializeApp(options);
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("La Liga Fixtures");
        //DatabaseReference usersRef = ref.child("");
        
      // Connect to the sky sports website for premier league fixtures 
        Document document = Jsoup.connect("http://www.skysports.com/la-liga-fixtures").get();

                // Selecting the container that contains all of the fixtures 
        Element body = document.select("div.fixres__body").first();
           // Selecting all of the children of this container
        Elements body2 = body.children();

        String date = "";
   // Looping through each element in the container of fixtures 
        for (Element page : body2) {
            
                   // Group of fixtures are organised by the date of the fixtures
            // h4 tag is the tag for the fixture date
            // By checking if this tag is first, and extracting the date 
            // It allows the application to group the extracted fixtures by date 

            if (page.tagName().equals("h4")) {
                date = page.ownText() + ", 2018";

            } else if (page.className().equals("fixres__item")) {

                Elements footballTeams = page.select("span.swap-text__target");
                Elements footballTimes = page.select("span.matches__date");

                ArrayList<String> fixture;
                ArrayList<String> fixtureTimes;

                      // Element object for the time will contain all the data including the tag
                // The method 'eachText' extracts only the text from each of the Element objects. 
                // The extracted data can then be stored in to an arraylist 
                fixtureTimes = (ArrayList<String>) footballTimes.eachText();

                    // Same again for the football teams
                fixture = (ArrayList<String>) footballTeams.eachText();
                //  System.out.println(fixture + " " + fixture.size() + " " + fixtureTimes);

                
                String homeTeam, awayTeam, time;
                
                       // As each container will only contain two football teams
               // The first will always be the home team and the second will always be the away team
               // For the time, there will only be one per container

                homeTeam = fixture.get(0);
                awayTeam = fixture.get(1);
                time = fixtureTimes.get(0);

                Fixture newFixture = new Fixture(homeTeam, awayTeam, time, date);

                         // Push the fixture to the database 
                ref.push().setValueAsync(newFixture);

            }

        }

        return null;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "GetFrenchLigueOneFixtures")
    public String GetFrenchLigueOneFixtures() throws IOException {
        // Set up account for Firebase Database
        // Service account contains options to connect to Project database
        // File downloaded from Firebase 
          FileInputStream serviceAccount = new FileInputStream("NetBeansProjects\\FindYourFootballWebCrawler//serviceAccountKey.json");
        ArrayList<Fixture> newFixtures = new ArrayList<Fixture>();

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://find-your-football.firebaseio.com/")
                .build();

        try {
            FirebaseApp.getInstance();
                 // Set up database instance or initalize the app.
        } catch (IllegalStateException e) {
            //Firebase not initialized automatically, do it manually
            FirebaseApp.initializeApp(options);
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("French Ligue 1 Fixtures");
        //DatabaseReference usersRef = ref.child("");
        
   // Connect to the sky sports website for premier league fixtures 
        Document document = Jsoup.connect("http://www.skysports.com/ligue-1-fixtures").get();
        
    // Selecting the container that contains all of the fixtures 
        Element body = document.select("div.fixres__body").first();
                // Selecting all of the children of this container
        Elements body2 = body.children();

        String date = "";
        
   // Looping through each element in the container of fixtures 
        for (Element page : body2) {
            
                      // Group of fixtures are organised by the date of the fixtures
            // h4 tag is the tag for the fixture date
            // By checking if this tag is first, and extracting the date 
            // It allows the application to group the extracted fixtures by date 

            if (page.tagName().equals("h4")) {
                date = page.ownText() + ", 2018";

            } else if (page.className().equals("fixres__item")) {

                Elements footballTeams = page.select("span.swap-text__target");
                Elements footballTimes = page.select("span.matches__date");

                ArrayList<String> fixture;
                ArrayList<String> fixtureTimes;

                            // Element object for the time will contain all the data including the tag
                // The method 'eachText' extracts only the text from each of the Element objects. 
                // The extracted data can then be stored in to an arraylist 
                fixtureTimes = (ArrayList<String>) footballTimes.eachText();
                
                  // Same again for the football teams
                fixture = (ArrayList<String>) footballTeams.eachText();
           

                String homeTeam, awayTeam, time;

                
               // As each container will only contain two football teams
               // The first will always be the home team and the second will always be the away team
               // For the time, there will only be one per container
               
                homeTeam = fixture.get(0);
                awayTeam = fixture.get(1);
                time = fixtureTimes.get(0);

              
                
                Fixture newFixture = new Fixture(homeTeam, awayTeam, time, date);

           
                  // Push the fixture to the database 
                ref.push().setValueAsync(newFixture);

            }

        }

        return null;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "GetBundesligaFixtures")
    public String GetBundesligaFixtures() throws IOException {
              // Set up account for Firebase Database
        // Service account contains options to connect to Project database
        // File downloaded from Firebase 
           FileInputStream serviceAccount = new FileInputStream("NetBeansProjects\\FindYourFootballWebCrawler//serviceAccountKey.json");
        ArrayList<Fixture> newFixtures = new ArrayList<Fixture>();

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://find-your-football.firebaseio.com/")
                .build();

        try {
            FirebaseApp.getInstance();
                    // Set up database instance or initalize the app.
        } catch (IllegalStateException e) {
            //Firebase not initialized automatically, do it manually
            FirebaseApp.initializeApp(options);
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Bundesliga Fixtures");
        //DatabaseReference usersRef = ref.child("");
        
         // Connect to the sky sports website for premier league fixtures 
        Document document = Jsoup.connect("http://www.skysports.com/bundesliga-fixtures").get();
        
        // Selecting the container that contains all of the fixtures 
        Element body = document.select("div.fixres__body").first();
           // Selecting all of the children of this container
        Elements body2 = body.children();

        String date = "";

           // Looping through each element in the container of fixtures 
        for (Element page : body2) {
            
                            // Group of fixtures are organised by the date of the fixtures
            // h4 tag is the tag for the fixture date
            // By checking if this tag is first, and extracting the date 
            // It allows the application to group the extracted fixtures by date 

            if (page.tagName().equals("h4")) {
                date = page.ownText() + ", 2018";

            } else if (page.className().equals("fixres__item")) {

                Elements footballTeams = page.select("span.swap-text__target");
                Elements footballTimes = page.select("span.matches__date");

                ArrayList<String> fixture;
                ArrayList<String> fixtureTimes;

                             // Element object for the time will contain all the data including the tag
                // The method 'eachText' extracts only the text from each of the Element objects. 
                // The extracted data can then be stored in to an arraylist 
                fixtureTimes = (ArrayList<String>) footballTimes.eachText();

                     
                  // Same again for the football teams
                fixture = (ArrayList<String>) footballTeams.eachText();
         

                String homeTeam, awayTeam, time;

                       // As each container will only contain two football teams
               // The first will always be the home team and the second will always be the away team
               // For the time, there will only be one per container
                homeTeam = fixture.get(0);
                awayTeam = fixture.get(1);
                time = fixtureTimes.get(0);

                
                Fixture newFixture = new Fixture(homeTeam, awayTeam, time, date);
           // Push the fixture to the database 
                ref.push().setValueAsync(newFixture);
            }

        }

        return null;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "GetChampionsLeagueFixtures")
    public String GetChampionsLeagueFixtures() throws IOException {
        // Set up account for Firebase Database
        // Service account contains options to connect to Project database
        // File downloaded from Firebase 
           FileInputStream serviceAccount = new FileInputStream("NetBeansProjects\\FindYourFootballWebCrawler//serviceAccountKey.json");
        ArrayList<Fixture> newFixtures = new ArrayList<Fixture>();

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://find-your-football.firebaseio.com/")
                .build();

        try {
            FirebaseApp.getInstance();
                // Set up database instance or initalize the app.
        } catch (IllegalStateException e) {
            //Firebase not initialized automatically, do it manually
            FirebaseApp.initializeApp(options);
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Champions League Fixtures");
        //DatabaseReference usersRef = ref.child("");
        
        // Connect to the sky sports website for premier league fixtures 
        Document document = Jsoup.connect("http://www.skysports.com/champions-league-fixtures").get();
        
        // Selecting the container that contains all of the fixtures 
        Element body = document.select("div.fixres__body").first();
         // Selecting all of the children of this container
        Elements body2 = body.children();

        String date = "";

           // Looping through each element in the container of fixtures 
        for (Element page : body2) {
            // Group of fixtures are organised by the date of the fixtures
            // h4 tag is the tag for the fixture date
            // By checking if this tag is first, and extracting the date 
            // It allows the application to group the extracted fixtures by date 
            
            if (page.tagName().equals("h4")) {
                date = page.ownText() + ", 2018";

            } else if (page.className().equals("fixres__item")) {

                Elements footballTeams = page.select("span.swap-text__target");
                Elements footballTimes = page.select("span.matches__date");

                ArrayList<String> fixture;
                ArrayList<String> fixtureTimes;

                 // Element object for the time will contain all the data including the tag
                // The method 'eachText' extracts only the text from each of the Element objects. 
                // The extracted data can then be stored in to an arraylist 
                fixtureTimes = (ArrayList<String>) footballTimes.eachText();

                   // Same again for the football teams
                fixture = (ArrayList<String>) footballTeams.eachText();
        

                String homeTeam, awayTeam, time;
                
                
                // As each container will only contain two football teams
               // The first will always be the home team and the second will always be the away team
               // For the time, there will only be one per container

                homeTeam = fixture.get(0);
                awayTeam = fixture.get(1);
                time = fixtureTimes.get(0);

                Fixture newFixture = new Fixture(homeTeam, awayTeam, time, date);
                
                 // Push the fixture to the database 
                ref.push().setValueAsync(newFixture);

            }

        }

        return null;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "GetFACupFixtures")
    public String GetFACupFixtures() throws IOException {
        // Set up account for Firebase Database
        // Service account contains options to connect to Project database
        // File downloaded from Firebase 
          FileInputStream serviceAccount = new FileInputStream("NetBeansProjects\\FindYourFootballWebCrawler//serviceAccountKey.json");
        ArrayList<Fixture> newFixtures = new ArrayList<Fixture>();

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://find-your-football.firebaseio.com/")
                .build();

        try {
            FirebaseApp.getInstance();
                // Set up database instance or initalize the app.
        } catch (IllegalStateException e) {
            //Firebase not initialized automatically, do it manually
            FirebaseApp.initializeApp(options);
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("FA Cup Fixtures");
        //DatabaseReference usersRef = ref.child("");

           
        // Connect to the sky sports website for premier league fixtures 
        Document document = Jsoup.connect("http://www.skysports.com/fa-cup-fixtures").get();

             // Selecting the container that contains all of the fixtures 
        Element body = document.select("div.fixres__body").first();
             // Selecting all of the children of this container
        Elements body2 = body.children();

        String date = "";

           // Looping through each element in the container of fixtures
        for (Element page : body2) {
            
              // Group of fixtures are organised by the date of the fixtures
            // h4 tag is the tag for the fixture date
            // By checking if this tag is first, and extracting the date 
            // It allows the application to group the extracted fixtures by date 

            if (page.tagName().equals("h4")) {
                date = page.ownText() + ", 2018";

            } else if (page.className().equals("fixres__item")) {

                Elements footballTeams = page.select("span.swap-text__target");
                Elements footballTimes = page.select("span.matches__date");

                ArrayList<String> fixture;
                ArrayList<String> fixtureTimes;

                
                 // Element object for the time will contain all the data including the tag
                // The method 'eachText' extracts only the text from each of the Element objects. 
                // The extracted data can then be stored in to an arraylist 
                fixtureTimes = (ArrayList<String>) footballTimes.eachText();

                fixture = (ArrayList<String>) footballTeams.eachText();
                //  System.out.println(fixture + " " + fixture.size() + " " + fixtureTimes);

                String homeTeam, awayTeam, time;
                
               // As each container will only contain two football teams
               // The first will always be the home team and the second will always be the away team
               // For the time, there will only be one per container

                homeTeam = fixture.get(0);
                awayTeam = fixture.get(1);
                time = fixtureTimes.get(0);

                Fixture newFixture = new Fixture(homeTeam, awayTeam, time, date);
             // Push the fixture to the database 
                ref.push().setValueAsync(newFixture);

            }

        }

        return null;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "GetAmmenitites")
    public String GetAmmenitites() throws FileNotFoundException, IOException {

        // Array of websites that have the venue amenitites
        // Used to loop through each one 
        String[] websiteURls = {"https://www.matchpint.co.uk/view-court-14243", "https://www.matchpint.co.uk/view-green-man-14464",
            "https://www.matchpint.co.uk/view-fitzrovia-14589", "https://www.matchpint.co.uk/view-the-king-and-queen-5010",
            "https://www.matchpint.co.uk/view-naughty-burger-sports-4185", "https://www.matchpint.co.uk/view-one-tun-1402",
            "https://www.matchpint.co.uk/view-nordic-bar-2660", "https://www.matchpint.co.uk/view-the-ship-3578", "https://www.matchpint.co.uk/view-rising-sun-341"};
       
         // Set up account for Firebase Database
        // Service account contains options to connect to Project database
        // File downloaded from Firebase 
           FileInputStream serviceAccount = new FileInputStream("NetBeansProjects\\FindYourFootballWebCrawler//serviceAccountKey.json");
        ArrayList<Fixture> newFixtures = new ArrayList<Fixture>();

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://find-your-football.firebaseio.com/")
                .build();

        try {
            FirebaseApp.getInstance();
                  // Set up database instance or initalize the app.
        } catch (IllegalStateException e) {
            //Firebase not initialized automatically, do it manually
            FirebaseApp.initializeApp(options);
        }

        
        // Reference to venues in database 
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Venues");

        // Loop through each website url 
        // For loop, each venue in the database is iterated. 
        // The venue name is compared to the venue name on website
        // If they match, the amenities and opening/closing data from the website is added to the venue object
        
        for (String websites : websiteURls) {
            // Connect to the website, using the value in the array
            Document document = Jsoup.connect(websites).get();

            // Selecting the name of the venue on the page 'h1' tag.
            // Selecting all the venue amenities
            // Selecting all the opening/closing time
            final Element pageVenueName = document.select("h1").first();
            final Elements venueAmenities = document.select("div.facilities_item");
            final Elements venueOpeningClosingTimes = document.select("div.opening-hours_item");

            
            myRef.addChildEventListener(new ChildEventListener() {
                ArrayList<String> venueDetails = new ArrayList<>();
                ArrayList<String> venueOpeningClosing = new ArrayList<>();

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    // Looping through the venue nodes, extracting the venue data
                    
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        Venue venue = dataSnapshot.getValue(Venue.class);
                        String name = venue.getName();
                        String address = venue.getAddress();
                        Double lng = venue.getLng();
                        Double lat = venue.getLat();
                        String phoneNumber = venue.getPhoneNumber();
                        Float rating = venue.getRating();
                        String url = venue.getUrl();
                        String adultKey = dataSnapshot.getKey();

                        String newVenueName = pageVenueName.ownText();
                        
                        // Checking if the name of the venue on the web page, equals the venue object from the database 

                        if (newVenueName.equals(ds.getValue())) {

                            // Looping through the amenities from the web page 
                            for (Element amenities  : venueAmenities) {
                                // Due to strucute of web page, not all the selected amenities are valid
                                // The HTML parser also extracts the amenities that are not provided by the venue
                                // These amentities contain a class name called 'facilities_item facilities_item--muted'
                                // To extract the amenities that are provided by the venue, just the amenities with the class name 'facilities_item' are extracted.
     
                                if (amenities .className().equals("facilities_item")) {
                                    venueDetails.add(amenities.ownText());

                                }

                            }
                            // Adding the opening/closing times to an arraylist 
                            for (Element openingClosingTimes : venueOpeningClosingTimes) {
                                venueOpeningClosing.add(openingClosingTimes.ownText());
                            }

                          

                            // Removing duplicate amenities from arraylist 
                            Set<String> hs = new HashSet<>();
                            hs.addAll(venueDetails);
                            venueDetails.clear();
                            venueDetails.addAll(hs);

                            
                            Venue newVenue = new Venue(name, address, phoneNumber, url, lat, lng, rating, venueDetails, venueOpeningClosing);

                            DatabaseReference myRef2 = myRef.child(adultKey);

                            // Pushing venue object to database 
                            myRef2.setValueAsync(newVenue);

                        }

                    }

                }

                @Override
                public void onChildChanged(DataSnapshot ds, String string) {
                    // Is called when data in the database is changed. Value is changed node 
                }

                @Override
                public void onChildRemoved(DataSnapshot ds) {
                   // Called when a node is removed in the database. Value is removed node 
                }

                @Override
                public void onChildMoved(DataSnapshot ds, String string) {
                    // Called when a node in the database is moved. Value is the moved node. 
                }

                @Override
                public void onCancelled(DatabaseError de) {
                  // Called if database loses connection
                }

            });

        }
        return null;
    }

    /**
     * Web service operation
     *
     * @return
     * @throws java.io.FileNotFoundException
     */
    @WebMethod(operationName = "GetVenueFixtures")
    public String GetVenueFixtures() throws FileNotFoundException, IOException {

        // Array of website urls to find the fixtures on 
        // Each one is iterated. 
        String[] websiteURls = {"https://www.matchpint.co.uk/view-court-14243", "https://www.matchpint.co.uk/view-green-man-14464",
            "https://www.matchpint.co.uk/view-fitzrovia-14589", "https://www.matchpint.co.uk/view-the-king-and-queen-5010",
            "https://www.matchpint.co.uk/view-naughty-burger-sports-4185", "https://www.matchpint.co.uk/view-one-tun-1402",
            "https://www.matchpint.co.uk/view-nordic-bar-2660", "https://www.matchpint.co.uk/view-the-ship-3578", "https://www.matchpint.co.uk/view-rising-sun-341"};

        // Set up account for Firebase Database
        // Service account contains options to connect to Project database
        // File downloaded from Firebase 
        FileInputStream serviceAccount = new FileInputStream("NetBeansProjects\\FindYourFootballWebCrawler//serviceAccountKey.json");
        ArrayList<Fixture> newFixtures = new ArrayList<Fixture>();

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://find-your-football.firebaseio.com/")
                .build();

        try {
            FirebaseApp.getInstance();
                 // Set up database instance or initalize the app.
        } catch (IllegalStateException e) {
            //Firebase not initialized automatically, do it manually
            FirebaseApp.initializeApp(options);
        }

           // Reference to venues in database 
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Venues");

        // Array to hold names of nodes for the fixtures in the database
        // Each one is used to iterate over, looking for a fixture inside. 
        final String[] footballLeagues = {"Premier League Fixtures", "French Ligue 1 Fixtures", "FA Cup Fixtures", "EFL Championship Fixtures", "Champions League Fixtures", "Bundesligaa Fixtures", "La Liga Fixtures"};

        // Iterating over each website url
        for (String websites : websiteURls) {
            
            // Connecting to the website, using the array value
            final Document document = Jsoup.connect(websites).get();

            // Selecting name of the venue on the web page 
            Element pageName = document.select("h1").first();
           
             // Getting the name of the venue on the page 
            final String venueName = pageName.ownText();

            // Loops through each child node of venues 
            myRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String string) {
                    Venue venue = dataSnapshot.getValue(Venue.class);
                    
                    
                     // Comparing venue name on web page to venue name in database 
                
                    if (venue.getName().equals(venueName)) {

                        // Select all of the fixtures on the page 
                        Elements fixturesName = document.select("div.panel_content_team");
                        
                        // Get the parent node of the venue which is the venue key 
                        venueKey = dataSnapshot.getKey();

                     
                        
                        // Add all of the fixtures in to an arraylist 
                        for (Element st : fixturesName) {
                            footballTeams.add(st.ownText());

                        }

                        // Loop through the list of fixtures, getting the first index for the home team and second for the away team
                        // These are uses to concat together to make a fixture name
                        // With this name, it is compared to the fixutres in the database, to see if there is a match. 
                        // There is inconsistency with extracting the home team and away team as it will not always be an even number
                       
                        for (int i = 0; i < footballTeams.size(); i++) {

                            final String homeTeam = footballTeams.get(i);
                            final String awayTeam = footballTeams.get(i + 1);
                    
                   
                            // Loop through the name of fixture nodes 
                            for (String footballFixtures : footballLeagues) {
                                
                                // Creating a reference to a fixture node, using the array value. 
                                
                                DatabaseReference myRef2 = database.getReference(footballFixtures);
                                // Loop through the fixutres in the node 
                              
                                myRef2.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot ds, String string) {

                                        Fixture fixture = ds.getValue(Fixture.class);

                               
                                       // Compare the fixture in the database with the home team and away team gathered from the web page
                                       
                                        if (fixture.getHomeTeam().contains(homeTeam) && (fixture.getAwayTeam().contains(awayTeam))) {
                                            
                                            // If a match was found, create a reference to the specific venue, using the venue key
                                            // Then the fixture key is added with the value of 'true'. 
                                            // This is used by the Find Your Football application to compare venues and fixtures 
                                            DatabaseReference myRef2 = myRef.child(venueKey);
                                            myRef2.child(ds.getKey()).setValueAsync(true);

                                        }

                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot ds, String string) {
                                          // Is called when data in the database is changed. Value is changed node 
                                    }

                                    @Override
                                    public void onChildRemoved(DataSnapshot ds) {
                                         // Called when a node is removed in the database. Value is removed node 
                                    }

                                    @Override
                                    public void onChildMoved(DataSnapshot ds, String string) {
                                          // Called when a node in the database is moved. Value is the moved node. 
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError de) {
                                            // Called if database loses connection
                                    }

                                });
                            }

                        }

                    }

                }

                @Override
                public void onChildChanged(DataSnapshot ds, String string) {
                   // Is called when data in the database is changed. Value is changed node 
                }

                @Override
                public void onChildRemoved(DataSnapshot ds) {
                   // Called when a node is removed in the database. Value is removed node 
                }

                @Override
                public void onChildMoved(DataSnapshot ds, String string) {
                 // Called when a node in the database is moved. Value is the moved node. 
                }

                @Override
                public void onCancelled(DatabaseError de) {
                       // Called if database loses connection
                }

            });

            // Clear the football teams as a new page will be connected too.
            footballTeams.clear();
        }

        return null;
    }
}
