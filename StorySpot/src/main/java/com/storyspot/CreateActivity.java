package com.storyspot;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.storyspot.data.UserData;
import com.storyspot.network.ImageUpload;
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

public class CreateActivity extends Activity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;
    private File image;
    private static int PIC_REQUEST;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private File dir;
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "StorySpot");

        ctx = this;

        EditText et_name = (EditText) findViewById(R.id.et_title);
        EditText et_description = (EditText) findViewById(R.id.et_description);
        TextView tv_date = (TextView) findViewById(R.id.tv_date);
        TextView tv_creator = (TextView) findViewById(R.id.tv_creator);
        Button btn_save = (Button) findViewById(R.id.btn_save);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(CreateActivity.this, TagActivity.class);
                startActivity(mIntent);
                overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
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
}