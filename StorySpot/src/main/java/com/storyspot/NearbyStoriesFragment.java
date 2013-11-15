package com.storyspot;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.storyspot.data.NearbyData;

import java.net.URI;
import java.net.URL;
import java.util.Vector;

/**
 * Created by rickylaishram on 11/14/13.
 */
public class NearbyStoriesFragment extends ListFragment {

    Vector<NearbyData> data = new Vector<NearbyData>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_nearby_stories, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        data = getdata();
        NearbyAdapter adapter = new NearbyAdapter(getActivity(), R.layout.adapter_nearby, data);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView lv, View v, int pos, long id) {
        Bundle bundle = new Bundle();
        bundle.putString("title", data.get(pos).title);
        bundle.putString("description", data.get(pos).description);
        bundle.putString("distance", data.get(pos).distance);
        bundle.putString("image", data.get(pos).image);

        Intent mIntent = new Intent(getActivity().getBaseContext(), CoverActivity.class);
        mIntent.putExtras(bundle);
        startActivity(mIntent);
        getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }

    //fixed data for now
    private Vector<NearbyData> getdata() {
        Vector<NearbyData> data = new Vector<NearbyData>();

        String[] titles = {"Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6", "Title 7", "Title 8", "Title 9"};
        String[] distances = {"500", "1000", "1000", "1500", "1500", "1500", "1500", "2000", "2000"};
        String[] images = {"http://i.imgur.com/YxaXM.jpg",
                            "http://i.imgur.com/SR2kW.jpg",
                            "http://i.imgur.com/gjCPt.jpg",
                            "http://i.imgur.com/p5ulQ.jpg",
                            "http://i.imgur.com/WgOWO.jpg",
                            "http://i.imgur.com/5qZmH.jpg",
                            "http://i.imgur.com/2Lvf5.jpg",
                            "http://i.imgur.com/xQf7e.jpg",
                            "http://i.imgur.com/pwsZB.jpg"
                        };
        String description = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";

        for (int i = 0; i < 9; i++) {
            NearbyData nearby = new NearbyData();
            nearby.setData( titles[i], description, distances[i], images[i] );
            data.add(nearby);
        }

        return data;
    }
}