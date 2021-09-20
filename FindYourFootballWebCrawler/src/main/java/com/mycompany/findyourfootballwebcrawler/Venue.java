/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.findyourfootballwebcrawler;

import java.util.ArrayList;

/**
 *
 * @author jackg
 */
public class Venue {

    private String name, address, phoneNumber, url;
    private Double lat, lng;
    private Float rating;
    private ArrayList<String> ammenitites;
    private ArrayList<String> openingClosingTimes;

    public Venue() {

    }

    public Venue(String name, String address, String phoneNumber, String url, Double lat, Double lng, Float rating, ArrayList<String> ammenitites, ArrayList<String> openingClosingTimes) {
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


    public String getName() {
        return name;
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

  
}
