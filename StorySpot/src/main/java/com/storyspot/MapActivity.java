package com.storyspot;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.storyspot.data.NearbyData;
import com.storyspot.data.StoryProgressData;
import com.storyspot.data.UserData;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static com.storyspot.utils.Constants.API_ENDPOINT;
import static com.storyspot.utils.Constants.API_GET_STORY_FRAGMENTS;
import static com.storyspot.utils.Constants.API_GET_USERS;
import static com.storyspot.utils.Constants.API_NEW_LOCATION;
import static com.storyspot.utils.Constants.API_REGISTER;

public class MapActivity extends Activity implements LocationListener {

    private GoogleMap mMap;
    private double latitude_user, longitude_user, latitude_dest, longitude_dest;
    private String provider;
    private LocationManager locationManager;
    private Location location_user, location_user_map;
    private Polyline polylines;
    private String travel_mode = "walking";
    private Context ctx;
    private ProgressDialog pDialog;
    private Bundle bundle;
    private String photo, name, description;
    private int fragmentNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ctx = this;

        bundle = getIntent().getExtras();
        final int position = bundle.getInt("position");
        final String story_id = bundle.getString("story_id");

        pDialog = new ProgressDialog(ctx);
        pDialog.setTitle("");
        pDialog.setMessage("Fetching directions. Please wait.");
        pDialog.setCancelable(false);
        pDialog.setIndeterminate(true);

        Switch travel_switch = (Switch) findViewById(R.id.travel_mode);
        Button btn_update = (Button) findViewById(R.id.btn_update);
        Button btn_next = (Button) findViewById(R.id.btn_next);

        //Get user location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        location_user = locationManager.getLastKnownLocation(provider);

        try {
            latitude_user = location_user.getLatitude();
            longitude_user = location_user.getLongitude();
        } catch (Exception e) {
            Log.e("Location Error", e.toString());
        }

        ImageView image = (ImageView) findViewById(R.id.iv_cover);

        Typeface font = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");

        Picasso.with(this).load(bundle.getString("image")).into(image);


        // Get location
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(API_ENDPOINT + API_GET_STORY_FRAGMENTS, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                pDialog.hide();

                try {
                    JSONArray json = new JSONArray(new String(responseBody));
                    int jposition = 0;

                    for(int i = 0; i < json.length(); i++) {
                        JSONObject element = json.getJSONObject(i);

                        String jstoryid = element.getInt("story_id") + "";

                        if(story_id.equals(jstoryid)) {
                            if(jposition == position) {
                                String locationid = element.getString("location_id");
                                photo = element.getString("image_url");
                                name = element.getString("name");
                                description = element.getString("short_description");

                                AsyncHttpClient userclient = new AsyncHttpClient();
                                userclient.get(API_ENDPOINT + API_NEW_LOCATION+"/"+locationid+".json", new AsyncHttpResponseHandler() {

                                    @Override
                                    public void onStart() {
                                    }

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        //pDialog.hide();

                                        try {
                                            JSONObject locationjson = new JSONObject(new String(responseBody));

                                            latitude_dest = locationjson.getDouble("latitude");
                                            longitude_dest = locationjson.getDouble("longitude");

                                            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
                                            LatLng location = new LatLng(latitude_dest, longitude_dest);
                                            mMap.setMyLocationEnabled(true);
                                            location_user_map = mMap.getMyLocation();

                                            mMap.getUiSettings().setCompassEnabled(true);
                                            mMap.getUiSettings().setZoomControlsEnabled(false);
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));

                                            mMap.addMarker(new MarkerOptions()
                                                    .position(location)
                                                    .draggable(false));

                                            CircleOptions circleOptions = new CircleOptions()
                                                    .center(location)
                                                    .radius(50)
                                                    .strokeColor(Color.BLUE);
                                            mMap.addCircle(circleOptions);

                                            location_user_map = mMap.getMyLocation();

                                            if((location_user != null) && (location_user_map != null) && (location_user.getTime() < location_user_map.getTime())) {
                                                latitude_user = location_user.getLatitude();
                                                longitude_user = location_user.getLongitude();
                                            }

                                            getDirections(ctx, latitude_user, longitude_user, latitude_dest, longitude_dest, travel_mode);

                                        } catch (Exception e) {
                                            Log.e("NearbyStory", e.toString());
                                        }


                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    }

                                    @Override
                                    public void onRetry() {
                                        // Request was retried
                                    }

                                    @Override
                                    public void onProgress(int bytesWritten, int totalSize) {
                                        // Progress notification
                                    }

                                    @Override
                                    public void onFinish() {
                                        //pDialog.hide();
                                        int i2 = 0;
                                    }
                                });

                            }
                            jposition ++;
                        }

                        fragmentNumber = jposition;
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }

            @Override
            public void onRetry() {
                // Request was retried
            }

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                // Progress notification
            }

            @Override
            public void onFinish() {
                pDialog.hide();
            }
        });

        travel_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    travel_mode = "driving";
                } else {
                    travel_mode = "walking";
                }

                location_user_map = mMap.getMyLocation();

                try {
                    if(location_user.getTime() < location_user_map.getTime()) {
                        latitude_user = location_user_map.getLatitude();
                        longitude_user = location_user_map.getLongitude();
                    }
                } catch (Exception e) {
                    Log.e("MapError", e.toString());
                    latitude_user = location_user_map.getLatitude();
                    longitude_user = location_user_map.getLongitude();
                }

                getDirections(ctx, latitude_user, longitude_user, latitude_dest, longitude_dest, travel_mode);
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location_user_map = mMap.getMyLocation();

                try {
                    if(location_user.getTime() < location_user_map.getTime()) {
                        latitude_user = location_user_map.getLatitude();
                        longitude_user = location_user_map.getLongitude();
                    }
                } catch (Exception e) {
                    Log.e("MapError", e.toString());
                    latitude_user = location_user_map.getLatitude();
                    longitude_user = location_user_map.getLongitude();
                }

                getDirections(ctx, latitude_user, longitude_user, latitude_dest, longitude_dest, travel_mode);
            }
        });

        btn_next.setEnabled(true);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("from", "map");
                bundle.putDouble("latitude", latitude_dest);
                bundle.putDouble("longitude", longitude_dest);
                bundle.putString("photo", photo);
                bundle.putString("name", name);
                bundle.putString("description", description);
                bundle.putInt("nofragment", fragmentNumber);

                Intent mIntent = new Intent(MapActivity.this, PhotoActivity.class);
                mIntent.putExtras(bundle);
                startActivity(mIntent);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 0, 0, this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            locationManager.removeUpdates(this);
        } catch (Exception e) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_left_out, R.anim.slide_left_in);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_navigate) {
            String navUrl = "http://maps.google.com/maps?saddr="+latitude_user+","+longitude_user+"&daddr="+latitude_dest+","+longitude_dest;
            Intent navIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(navUrl));
            startActivity(navIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_map, container, false);
            return rootView;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {
				/* If GPS is disable launch Locations Settings */
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    @Override
    public void onLocationChanged(Location location) {

        location_user_map = mMap.getMyLocation();

        if((location_user_map != null) && (location.getTime() < location_user_map.getTime())) {
            latitude_user = location_user.getLatitude();
            longitude_user = location_user.getLongitude();
        } else {
            latitude_user = location.getLatitude();
            longitude_user = location.getLongitude();
        }

        Float distace = distFrom((float)latitude_dest, (float)longitude_dest, (float)latitude_user, (float)longitude_user);

        if(distace < 50) {
            Button btn_next = (Button) findViewById(R.id.btn_next);
            btn_next.setEnabled(true);


        }
    }

    public void getDirections(Context ctx, double origin_latitude, double origin_longitude, double dest_latitude, double dest_longitude, String travel_mode) {
        String url = com.storyspot.utils.Location.getDirectionsUrl(origin_latitude, origin_longitude, dest_latitude, dest_longitude, travel_mode);

        pDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                TextView tv_status = (TextView) findViewById(R.id.tv_status);
                tv_status.setText("");
                pDialog.hide();

                String response = new String(responseBody);

                try {
                    polylines.remove();
                } catch (Exception e) {
                    Log.e("Polyline", "Polyline error");
                }

                List<LatLng> lines = com.storyspot.utils.Location.getSteps(response);
                polylines = mMap.addPolyline(new PolylineOptions().addAll(lines).color(Color.RED));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                pDialog.hide();
                TextView tv_status = (TextView) findViewById(R.id.tv_status);
                tv_status.setText("Something went wrong. Cannot get directions now.");
            }
        });
    }

    public static float distFrom(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        int meterConversion = 1609;

        return new Float(dist * meterConversion).floatValue();
    }

}
