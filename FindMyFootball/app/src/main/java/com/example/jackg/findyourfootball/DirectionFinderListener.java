package com.example.jackg.findyourfootball;

import java.util.List;
import com.example.jackg.findyourfootball.Route;

/**
 * Created by jackg on 07/04/2018.
 */

public interface DirectionFinderListener {
            // Interface method for route-finding class. Used by menuViewController to inform user when looking for a route and to show route when sucessful.
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
