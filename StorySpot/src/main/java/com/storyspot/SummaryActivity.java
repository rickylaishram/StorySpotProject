package com.storyspot;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.internal.da;
import com.storyspot.data.NearbyData;
import com.storyspot.data.SummaryData;

import java.util.Vector;

public class SummaryActivity extends Activity {

    Vector<SummaryData> data = new Vector<SummaryData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Bundle bundle = getIntent().getExtras();

        setTitle(bundle.getString("title"));

        ListView timeline = (ListView) findViewById(R.id.ll_summary);

        data = getdata();
        SummaryAdapter adapter = new SummaryAdapter(this, R.layout.adapter_timeline, data);
        timeline.setAdapter(adapter);

        timeline.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("name", data.get(position).name);
                bundle.putString("description", data.get(position).description);
                bundle.putString("image", data.get(position).image);
                bundle.putDouble("latitude", data.get(position).latitude);
                bundle.putDouble("longitude", data.get(position).longitude);

                Intent mIntent = new Intent(SummaryActivity.this, PhotoActivity.class);
                mIntent.putExtras(bundle);
                startActivity(mIntent);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.summary, menu);
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

    //fixed data for now
    private Vector<SummaryData> getdata() {
        Vector<SummaryData> value = new Vector<SummaryData>();

        int distance = 100;
        String[] titles = {"Cat 1", "Cat 2", "Cat 3", "Cat 4", "Cat 5", "Cat 6", "Cat 7", "Cat 8", "Cat 9"};
        String[] images = {"http://i.imgur.com/xmt94zz.jpg",
                        "http://i.imgur.com/9GlYPAw.jpg",
                        "http://i.imgur.com/ED2GbYI.jpg",
                        "http://i.imgur.com/F4aSY2n.jpg",
                        "http://i.imgur.com/Hr1mrFa.jpg",
                        "http://i.imgur.com/dGq1YKB.jpg",
                        "http://i.imgur.com/AfF6QYp.jpg",
                        "http://i.imgur.com/cxURuwX.jpg",
                        "http://i.imgur.com/UHQxgK2.jpg",
                        "http://i.imgur.com/DGDcbIs.jpg"
                     };
        String description = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";
        double latitude = 43.0416609;
        double longitude = -76.1356004;

        for (int i = 0; i < 9; i++) {
            SummaryData summary = new SummaryData();
            summary.setData(titles[i], description, images[i], (distance+i*50), latitude, longitude);
            value.add(summary);
        }

        return value;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_left_out, R.anim.slide_left_in);
    }

}
