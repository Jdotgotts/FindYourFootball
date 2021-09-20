package com.example.jackg.findyourfootball;

import java.io.Serializable;

/**
 * Created by jackg on 03/04/2018.
 */

// MODEL CLASS FOR REVIEW DATA
// IMPLEMENTS SERIALIZABLE TO PASS VENUE DATA TO MAP VIEW
public class Review implements Serializable {
    private String review, displayName, dateOfReview;
    private float rating;


    public Review() {

    }

    public Review(String review, String displayName, String dateOfReview, float rating) {
        this.review = review;
        this.displayName = displayName;
        this.dateOfReview = dateOfReview;
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDateOfReview() {
        return dateOfReview;
    }

    public void setDateOfReview(String dateOfReview) {
        this.dateOfReview = dateOfReview;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Review{" +
                "review='" + review + '\'' +
                ", displayName='" + displayName + '\'' +
                ", dateOfReview='" + dateOfReview + '\'' +
                ", rating=" + rating +
                '}';
    }
}
