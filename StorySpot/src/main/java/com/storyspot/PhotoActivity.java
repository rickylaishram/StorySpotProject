package com.storyspot;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static com.storyspot.utils.Constants.API_ENDPOINT;
import static com.storyspot.utils.Constants.API_GET_STORY_FRAGMENTS;
import static com.storyspot.utils.Constants.API_NEW_LOCATION;

public class PhotoActivity extends Activity {

    Context ctx;
    TextView tv_address;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        ctx = this;

        bundle = getIntent().getExtras();
        final int position = bundle.getInt("position");
        final String story_id = bundle.getString("story_id");
        int fragmentNUmber = bundle.getInt("nofragment");

        setTitle(bundle.getString("name"));

        TextView tv_name = (TextView) findViewById(R.id.tv_name);
        tv_address = (TextView) findViewById(R.id.tv_address);
        TextView tv_description = (TextView) findViewById(R.id.tv_description);
        ImageView iv_main = (ImageView) findViewById(R.id.iv_main);
        Button btn_ok = (Button) findViewById(R.id.btn_ok);

        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setTitle("");
        pDialog.setMessage("Fething data");
        pDialog.setCancelable(false);
        pDialog.setIndeterminate(true);

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

                        String jstoryid = element.getString("story_id");

                        if(story_id.equals(jstoryid)) {
                            if(jposition == position) {
                                String locationid = element.getInt("location_id")+"";
                            }
                            jposition ++;
                        }
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

        setTitle(bundle.getString("story_title"));
        tv_name.setText(bundle.getString("name"));
        tv_description.setText(bundle.getString("description"));
        tv_address.setText("");
        Picasso.with(this).load(bundle.getString("photo")).into(iv_main);

        Double[] location = new Double[]{bundle.getDouble("latitude"), bundle.getDouble("longitude") };

        (new GetAddress()).execute(location);

        if (position == (fragmentNUmber - 1)) {
            btn_ok.setEnabled(false);
        } else {
            btn_ok.setEnabled(true);
        }

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(PhotoActivity.this, MapActivity.class);
                bundle.putInt("position", bundle.getInt("position")+1);
                mIntent.putExtras(bundle);
                startActivity(mIntent);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class GetAddress extends AsyncTask<Double, Void, String> {

        @Override
        protected String doInBackground(Double... locations) {
            Geocoder geocoder = new Geocoder(ctx);

            List<Address> addresses = null;
            String addressText= "";

            try {
                addresses = geocoder.getFromLocation(locations[0], locations[1], 1);


                Address address = addresses.get(0);

                addressText = String.format("%s, %s, %s", address.getMaxAddressLineIndex() > 0 ?
                    address.getAddressLine(0) : "",address.getLocality(), address.getCountryName());
            } catch (Exception e) {

            }

            return addressText;
        }

        @Override
        protected void onPostExecute(String address) {
            tv_address.setText(address);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_left_out, R.anim.slide_left_in);
    }
}
