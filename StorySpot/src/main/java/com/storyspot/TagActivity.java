package com.storyspot;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.storyspot.utils.Constants;
import com.storyspot.utils.PhotoUtils;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.storyspot.utils.Constants.API_ENDPOINT;
import static com.storyspot.utils.Constants.API_GET_LOCATIONS;
import static com.storyspot.utils.Constants.API_GET_STORIES;
import static com.storyspot.utils.Constants.API_NEW_FRAGMENT;
import static com.storyspot.utils.Constants.API_NEW_LOCATION;
import static com.storyspot.utils.Constants.API_NEW_STORY;

public class TagActivity extends Activity implements LocationListener {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;
    private File image;
    private static int PIC_REQUEST;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private File dir;
    private Context ctx;
    private double latitude_user, longitude_user;
    private String provider;
    private LocationManager locationManager;
    private Location location_user;
    private String story_id, story_title, image_url, locationid;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        bundle = getIntent().getExtras();
        story_id = bundle.getString("story_id");
        story_title = bundle.getString("story_title");

        setTitle(story_title);

        dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "StorySpot");

        ctx = this;

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

        final EditText et_name = (EditText) findViewById(R.id.et_name);
        final EditText et_description = (EditText) findViewById(R.id.et_description);
        ImageButton btn_camera = (ImageButton) findViewById(R.id.btn_camera);
        Button btn_save = (Button) findViewById(R.id.btn_save);

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PIC_REQUEST = (int) System.currentTimeMillis();
                // create Intent to take a picture and return control to the calling application
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if(!dir.exists()) {
                    dir.mkdirs();
                }

                image = new File(dir.getPath() + File.separator + PIC_REQUEST + ".jpg");
                fileUri = Uri.fromFile(image); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                // start the image capture Intent
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pDialog;
                pDialog = new ProgressDialog(ctx);

                pDialog.setTitle("");
                pDialog.setMessage("Creating Story. Please wait.");
                pDialog.setCancelable(false);
                pDialog.setIndeterminate(true);
                pDialog.show();

                final String title = et_name.getText().toString();
                final String description = et_description.getText().toString();

                RequestParams params = new RequestParams();
                params.put("story_fragment[name]",title);
                params.put("story_fragment[short_description]",description);
                params.put("story_fragment[story_id]",story_id);
                params.put("story_fragment[image_url]", image_url);
                params.put("story_fragment[location_id]", locationid);

                AsyncHttpClient client = new AsyncHttpClient();
                client.post(API_ENDPOINT + API_NEW_FRAGMENT, params, new AsyncHttpResponseHandler(){

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){

                        Intent mIntent = new Intent(TagActivity.this, TagActivity.class);
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);
                        overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
                        finish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                        //tv_status.setText("Something went wrong :(");
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
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tag, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tag, container, false);
            return rootView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                File reimage = new File(dir.getPath() + File.separator + PIC_REQUEST + ".jpg");

                Bitmap photo = PhotoUtils.decodeSampledBitmapFromFile(reimage.getAbsolutePath(), Constants.IMAGE_MAX_DIMENSION, Constants.IMAGE_MAX_DIMENSION);

                Matrix matrix = new Matrix();
                matrix.postRotate(PhotoUtils.getCameraPhotoOrientation(reimage.getAbsolutePath()));

                int height = photo.getHeight();
                int width = photo.getWidth();

                Bitmap scaledPhoto = Bitmap.createBitmap(photo, 0, 0, width, height, matrix, false);

                ImageView background = (ImageView) findViewById(R.id.iv_background);
                background.setImageBitmap(scaledPhoto);

                ImageUpload upload = new ImageUpload();
                upload.execute(scaledPhoto);

            }else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_left_out, R.anim.slide_left_in);
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
        latitude_user = location.getLatitude();
        longitude_user = location.getLongitude();
    }

    public class ImageUpload extends AsyncTask<Bitmap, Void, Void> {

        @Override
        public Void doInBackground(Bitmap... bmap) {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bmap[0].compress(Bitmap.CompressFormat.JPEG, 100, bos);
            String sPhoto = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);

            HttpPost hpost = new HttpPost(Constants.IMGUR_ENDPOINT+Constants.IMGUR_UPLOAD);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("image", sPhoto));
            nameValuePairs.add(new BasicNameValuePair("type", "base64"));

            try
            {
                hpost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            } catch (UnsupportedEncodingException e) {
                Log.e("Upload", e.toString());
            }

            hpost.setHeader("Authorization", getApplicationContext().getResources().getString(R.string.imgur_api_key));

            DefaultHttpClient client = new DefaultHttpClient();
            HttpResponse resp = null;
            try
            {
                resp = client.execute(hpost);
            } catch (ClientProtocolException e)
            {
                Log.e("Upload", e.toString());
            } catch (IOException e)
            {
                Log.e("Upload", e.toString());
            }

            String result = null;
            try {
                result = EntityUtils.toString(resp.getEntity());
                JSONObject json = new JSONObject(result);
                JSONObject data = json.getJSONObject("data");
                image_url = data.getString("link");
                int i = 0;
            } catch (Exception e) {
                Log.e("Upload", e.toString());
            }

            //Upload Location
            final String temp_lat = latitude_user+"";
            final String temp_lng = longitude_user+"";

            RequestParams params = new RequestParams();
            params.put("location[latitude]",temp_lat);
            params.put("location[longitude]",temp_lng);
            params.put("location[radius]",50);

            AsyncHttpClient locationclient = new AsyncHttpClient();
            locationclient.post(API_ENDPOINT + API_NEW_LOCATION, params, new AsyncHttpResponseHandler(){

                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    AsyncHttpClient client1 = new AsyncHttpClient();
                    client1.get(API_ENDPOINT + API_GET_LOCATIONS, new AsyncHttpResponseHandler() {

                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                            int id = 0;

                            try {
                                JSONArray json = new JSONArray(new String(responseBody));
                                for(int i = 0; i < json.length(); i++) {
                                    JSONObject element = json.getJSONObject(i);
                                    String lat = element.getDouble("latitude")+"";
                                    String lng = element.getDouble("longitude")+"";

                                    if(lat.equals(temp_lat) && lng.equals(temp_lng)) {

                                        locationid = element.getInt("id")+"";

                                        break;
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
                        }
                    });
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    //tv_status.setText("Something went wrong :(");
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
                }
            });

            return null;
        }

        @Override
        public void onPostExecute(Void a) {
            Button btn_save = (Button) findViewById(R.id.btn_save);
            btn_save.setEnabled(true);
        }
    }
}