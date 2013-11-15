package com.storyspot;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.graphics.Typeface;
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

public class CoverActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover);

        //getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.blue));

        Bundle bundle = getIntent().getExtras();

        TextView title = (TextView) findViewById(R.id.tv_title);
        TextView description = (TextView) findViewById(R.id.tv_description);
        ImageView image = (ImageView) findViewById(R.id.iv_cover);

        //Typeface font = Typeface.createFromAsset(getAssets(), "PoiretOne-Regular.ttf");
        Typeface font = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");

        Picasso.with(this).load(bundle.getString("image")).into(image);
        title.setText(bundle.getString("title"));
        description.setText(bundle.getString("description"));

        title.setTypeface(font);
        description.setTypeface(font);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cover, menu);
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
