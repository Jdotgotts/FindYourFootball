package com.example.jackg.findyourfootball;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jackg on 07/03/2018.
 */

public class Venue implements Serializable {

    private String name, address, phoneNumber, url, placeID, distanceToVenue;
    private Double lat, lng;
    private Float rating;
    private ArrayList<String> ammenitites;
    private ArrayList<String> openingClosingTimes;
    private ArrayList<Review> venuesReviews;
    private transient Bitmap venueImage;



    public Venue()  {

    }

    public Venue(String name, String address, String phoneNumber, String url, Double lat, Double lng, Float rating) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.url = url;
        this.lat = lat;
        this.lng = lng;
        this.rating = rating;
        this.ammenitites = ammenitites;
        this.openingClosingTimes = openingClosingTimes;


    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public Bitmap getVenueImage() {
        return venueImage;
    }

    public void setVenueImage(Bitmap venueImage) {
        this.venueImage = venueImage;
    }

    public ArrayList<String> getAmmenitites() {
        return ammenitites;
    }

    public void setAmmenitites(ArrayList<String> ammenitites) {
        this.ammenitites = ammenitites;
    }

    public ArrayList<String> getOpeningClosingTimes() {
        return openingClosingTimes;
    }

    public void setOpeningClosingTimes(ArrayList<String> openingClosingTimes) {
        this.openingClosingTimes = openingClosingTimes;
    }

    public ArrayList<Review> getVenuesReviews() {
        return venuesReviews;
    }

    public void setVenuesReviews(ArrayList<Review> venuesReviews) {
        this.venuesReviews = venuesReviews;
    }

    public String getDistanceToVenue() {
        return distanceToVenue;
    }

    public void setDistanceToVenue(String distanceToVenue) {
        this.distanceToVenue = distanceToVenue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Venue{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", url='" + url + '\'' +
                ", placeID='" + placeID + '\'' +
                ", distanceToVenue='" + distanceToVenue + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", rating=" + rating +
                ", ammenitites=" + ammenitites +
                ", openingClosingTimes=" + openingClosingTimes +
                ", venuesReviews=" + venuesReviews +
                ", venueImage=" + venueImage +
                '}';
    }
}
