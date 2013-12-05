package com.storyspot;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoActivity extends Activity {

    Context ctx;
    TextView tv_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        ctx = this;

        Bundle bundle = getIntent().getExtras();

        setTitle(bundle.getString("name"));

        TextView tv_name = (TextView) findViewById(R.id.tv_name);
        tv_address = (TextView) findViewById(R.id.tv_address);
        TextView tv_description = (TextView) findViewById(R.id.tv_description);
        ImageView iv_main = (ImageView) findViewById(R.id.iv_main);

        tv_name.setText(bundle.getString("name"));
        tv_description.setText(bundle.getString("description"));
        tv_address.setText("");
        Picasso.with(this).load(bundle.getString("image")).into(iv_main);

        Double[] location = new Double[]{bundle.getDouble("latitude"), bundle.getDouble("longitude") };

        (new GetAddress()).execute(location);
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

            try {
                addresses = geocoder.getFromLocation(locations[0], locations[1], 1);
            } catch (Exception e) {

            }

            Address address = addresses.get(0);

            String addressText = String.format("%s, %s, %s", address.getMaxAddressLineIndex() > 0 ?
                    address.getAddressLine(0) : "",address.getLocality(), address.getCountryName());

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
