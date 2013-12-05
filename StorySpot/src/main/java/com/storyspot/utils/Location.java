package com.storyspot.utils;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rickylaishram on 11/23/13.
 */
public class Location {

    private String response;

    public static String getDirectionsUrl(double origin_latitude, double origin_longitude, double dest_latitude, double dest_longitude, String travel_mode) {
        return "http://maps.googleapis.com/maps/api/directions/json?origin="
                + origin_latitude + "," + origin_longitude +"&destination="
                + dest_latitude + "," + dest_longitude + "&sensor=false&mode="+travel_mode;
    }

    public static List<LatLng> getSteps(String response) {
        List<LatLng> lines = new ArrayList<LatLng>();

        try {
            JSONObject result = new JSONObject(response);
            JSONArray routes = result.getJSONArray("routes");

            long distanceForSegment = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getInt("value");

            JSONArray steps = routes.getJSONObject(0).getJSONArray("legs")
                    .getJSONObject(0).getJSONArray("steps");



            for(int i=0; i < steps.length(); i++) {
                String polyline = steps.getJSONObject(i).getJSONObject("polyline").getString("points");

                for(LatLng p : decodePolyline(polyline)) {
                    lines.add(p);
                }
            }
        } catch (Exception e) {

        }

        return lines;
    }

    private static List<LatLng> decodePolyline(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();

        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(p);
        }

        return poly;
    }
}
