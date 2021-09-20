package com.example.jackg.findyourfootball;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by jackg on 07/04/2018.
 */

    // MODEL CLASS FOR DIRECTION ROUTE DATA
public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;
}
