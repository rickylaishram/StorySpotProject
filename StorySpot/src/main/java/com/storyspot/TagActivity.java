package com.storyspot;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import com.storyspot.network.ImageUpload;
import com.storyspot.utils.Constants;
import com.storyspot.utils.PhotoUtils;

import java.io.File;

public class TagActivity extends Activity {

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
        setContentView(R.layout.activity_tag);

        dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "StorySpot");

        ctx = this;

        EditText et_name = (EditText) findViewById(R.id.et_name);
        EditText et_description = (EditText) findViewById(R.id.et_description);
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
}