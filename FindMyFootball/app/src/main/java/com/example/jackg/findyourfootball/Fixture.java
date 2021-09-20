package com.example.jackg.findyourfootball;

/**
 * Created by jackg on 07/03/2018.
 */

 // MODEL CLASS FOR FIXTURE DATA
public class Fixture {

    private String homeTeam, awayTeam, time , date;


    public Fixture(){

    }


    public Fixture(String homeTeam, String awayTeam, String time, String date) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.time = time;
        this.date = date;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Fixture{" + "homeTeam=" + homeTeam + ", awayTeam=" + awayTeam + ", time=" + time + ", date=" + date + '}';
    }
}
