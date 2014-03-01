package com.storyspot;

import android.app.Activity;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class CoverActivity extends Activity {
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cover);

        bundle = getIntent().getExtras();

        setTitle(bundle.getString("title"));

        final View ll = findViewById(R.id.ll_cover);

        TextView title = (TextView) findViewById(R.id.tv_title);
        TextView creator = (TextView) findViewById(R.id.tv_creator);
        TextView date = (TextView) findViewById(R.id.tv_date);
        ImageView image = (ImageView) findViewById(R.id.iv_cover);
        Button start = (Button) findViewById(R.id.btn_start);
        Button summary = (Button) findViewById(R.id.btn_summary);

        Typeface font_light = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        Typeface font_black = Typeface.createFromAsset(getAssets(), "Roboto-Black.ttf");
        Typeface font_blackitalics = Typeface.createFromAsset(getAssets(), "Roboto-BlackItalic.ttf");
        Typeface font_georgia = Typeface.createFromAsset(getAssets(), "Georgia-Italic.ttf");

        Picasso.with(this).load(bundle.getString("image")).into(image);
        title.setText(bundle.getString("title"));
        date.setText(bundle.getString("date"));
        creator.setText(bundle.getString("creator"));

        final TextView description = new TextView(this);
        description.setText(bundle.getString("description"));
        description.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        description.setShadowLayer(10,0,0,getResources().getColor(android.R.color.black));
        description.setTextColor(getResources().getColor(android.R.color.white));
        description.setId(100);

        title.setTypeface(font_black);
        date.setTypeface(font_georgia);
        creator.setTypeface(font_georgia);

        Boolean status = bundle.getBoolean("completed");
        if(status) {
            start.setEnabled(false);
            summary.setEnabled(true);
        } else {
            summary.setEnabled(false);
            start.setEnabled(true);
        }

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ll.findViewById(100) == null) {
                    ((LinearLayout) ll).addView(description);
                } else {
                    ((LinearLayout) ll).removeView(description);
                }
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(CoverActivity.this, MapActivity.class);
                mIntent.putExtras(bundle);
                startActivity(mIntent);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });

        summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(CoverActivity.this, SummaryActivity.class);
                mIntent.putExtras(bundle);
                startActivity(mIntent);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });
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
        if (id == R.id.action_start) {
            Intent mIntent = new Intent(CoverActivity.this, MapActivity.class);
            mIntent.putExtras(bundle);
            startActivity(mIntent);
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
