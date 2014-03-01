package com.storyspot;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.storyspot.data.UserData;
import com.storyspot.utils.PhotoUtils;
import com.storyspot.utils.Constants;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.storyspot.utils.Constants.API_ENDPOINT;
import static com.storyspot.utils.Constants.API_GET_STORIES;
import static com.storyspot.utils.Constants.API_GET_USERS;
import static com.storyspot.utils.Constants.API_NEW_STORY;
import static com.storyspot.utils.Constants.API_REGISTER;

public class CreateActivity extends Activity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;
    private File image;
    private static int PIC_REQUEST;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private File dir;
    private Context ctx;
    private static String image_url;
    private static int author_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "StorySpot");

        ctx = this;

        UserData userData = new UserData();
        author_id = userData.getUserId(ctx);

        String date = new SimpleDateFormat("MM-dd-yy").format(Calendar.getInstance().getTime());

        final EditText et_name = (EditText) findViewById(R.id.et_title);
        final EditText et_description = (EditText) findViewById(R.id.et_description);
        TextView tv_date = (TextView) findViewById(R.id.tv_date);
        TextView tv_creator = (TextView) findViewById(R.id.tv_creator);
        Button btn_save = (Button) findViewById(R.id.btn_save);

        tv_creator.setText(userData.getUserName(ctx));
        tv_date.setText(date);

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
                String description = et_description.getText().toString();

                RequestParams params = new RequestParams();
                params.put("story[story_title]",title);
                params.put("story[description]",description);
                params.put("story[image_url]",image_url);
                params.put("story[author_id]",author_id+"");

                AsyncHttpClient client = new AsyncHttpClient();
                client.post(API_ENDPOINT + API_NEW_STORY, params, new AsyncHttpResponseHandler(){

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){

                        AsyncHttpClient client1 = new AsyncHttpClient();
                        client1.get(API_ENDPOINT + API_GET_STORIES, new AsyncHttpResponseHandler() {

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
                                        String stitle = element.getString("story_title");

                                        if(stitle.equals(title)) {

                                            id = element.getInt(("id"));

                                            Bundle bundle = new Bundle();
                                            bundle.putString("story_id", id+"");
                                            bundle.putString("story_title", title);

                                            Intent mIntent = new Intent(CreateActivity.this, TagActivity.class);
                                            mIntent.putExtras(bundle);
                                            startActivity(mIntent);
                                            overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
                                            finish();

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
                                pDialog.hide();
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
                        pDialog.hide();
                    }
                });
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_camera) {
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
            return true;
        }
        return super.onOptionsItemSelected(item);
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

                ImageView background = (ImageView) findViewById(R.id.iv_cover);
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

            return null;
        }

        @Override
        public void onPostExecute(Void a) {
            Button btn_save = (Button) findViewById(R.id.btn_save);
            btn_save.setEnabled(true);
        }
    }
}