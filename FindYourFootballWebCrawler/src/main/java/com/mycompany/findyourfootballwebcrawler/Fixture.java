/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.findyourfootballwebcrawler;

/**
 *
 * @author jackg
 */
public class Fixture {

    private String homeTeam, awayTeam, time, date;

    public Fixture() {

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
