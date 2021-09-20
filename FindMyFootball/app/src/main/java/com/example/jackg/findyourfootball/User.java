package com.example.jackg.findyourfootball;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by jackg on 13/04/2018.
 */

// MODEL CLASS FOR USER DATA
public class User {

    private String displayName, email;
    private ArrayList<Venue> venueFavouritesList;
    private ArrayList<String> clubFavouritesList;
    private ArrayList<String> LeagueFavouritesList;

    public User(){

    }

    public User(String displayName, String email) {
        this.displayName = displayName;
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Venue> getVenueFavouritesList() {
        return venueFavouritesList;
    }

    public void setVenueFavouritesList(ArrayList<Venue> venueFavouritesList) {
        this.venueFavouritesList = venueFavouritesList;
    }

    public ArrayList<String> getClubFavouritesList() {
        return clubFavouritesList;
    }

    public void setClubFavouritesList(ArrayList<String> clubFavouritesList) {
        this.clubFavouritesList = clubFavouritesList;
    }

    public ArrayList<String> getLeagueFavouritesList() {
        return LeagueFavouritesList;
    }

    public void setLeagueFavouritesList(ArrayList<String> leagueFavouritesList) {
        LeagueFavouritesList = leagueFavouritesList;
    }

    @Override
    public String toString() {
        return "User{" +
                "displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", venueFavouritesList=" + venueFavouritesList +
                ", clubFavouritesList=" + clubFavouritesList +
                ", LeagueFavouritesList=" + LeagueFavouritesList +
                '}';
    }
}
