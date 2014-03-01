package com.storyspot;

import android.app.Fragment;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.storyspot.data.NearbyData;
import com.storyspot.data.StoryProgressData;
import com.storyspot.data.UserData;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import static com.storyspot.utils.Constants.API_ENDPOINT;
import static com.storyspot.utils.Constants.API_GET_STORIES;
import static com.storyspot.utils.Constants.API_GET_USERS;
import static com.storyspot.utils.Constants.API_REGISTER;

/**
 * Created by rickylaishram on 11/14/13.
 */
public class NearbyStoriesFragment extends ListFragment {

    Vector<NearbyData> data = new Vector<NearbyData>();
    Context ctx;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_nearby_stories, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ctx = this.getActivity();

        final NearbyAdapter adapter = new NearbyAdapter(getActivity(), R.layout.adapter_nearby, data);
        setListAdapter(adapter);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(API_ENDPOINT + API_GET_STORIES, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //pDialog.hide();

                try {
                    JSONArray json = new JSONArray(new String(responseBody));
                    for(int i = json.length()-1; i > 0; i--) {

                        JSONObject element = json.getJSONObject(i);

                        final String title = element.getString("story_title");
                        final String description = element.getString("description");
                        final String distance = "";
                        final String image = element.getString("image_url");
                        final String author_id = element.getInt("author_id")+"";
                        final String date_unformatted = element.getString("created_at");
                        final String story_id = element.getInt("id")+"";


                        AsyncHttpClient userclient = new AsyncHttpClient();
                        userclient.get(API_ENDPOINT + API_REGISTER+"/"+author_id+".json", new AsyncHttpResponseHandler() {

                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                //pDialog.hide();

                                try {
                                    JSONObject userjson = new JSONObject(new String(responseBody));

                                    String author_name = userjson.getString("user_name");

                                    String date = date_unformatted.substring(0,10);

                                    NearbyData nearby = new NearbyData();
                                    nearby.setData( title, description, distance, image, author_name, date, story_id);

                                    data.add(nearby);

                                    StoryProgressData progress = new StoryProgressData();
                                    progress.saveProgress(ctx, story_id, 0, false);

                                } catch (Exception e) {
                                    Log.e("NearbyStory", e.toString());
                                }

                                adapter.notifyDataSetChanged();
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
                                //pDialog.hide();
                                int i2 = 0;
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("NearbyStory", e.toString());
                }

                adapter.notifyDataSetChanged();
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
                //pDialog.hide();
            }
        });
    }

    @Override
    public void onListItemClick(ListView lv, View v, int pos, long id) {
        Bundle bundle = new Bundle();
        bundle.putString("title", data.get(pos).title);
        bundle.putString("description", data.get(pos).description);
        bundle.putString("distance", data.get(pos).distance);
        bundle.putString("image", data.get(pos).image);
        bundle.putString("creator", data.get(pos).creator);
        bundle.putString("date", data.get(pos).date);
        bundle.putString("story_id", data.get(pos).story_id);

        StoryProgressData progress = new StoryProgressData();
        int position = progress.getProgress(ctx, data.get(pos).story_id);
        Boolean status = progress.getStatus(ctx, data.get(pos).story_id);

        bundle.putInt("position", position);
        bundle.putBoolean("completed", status);

        Intent mIntent = new Intent(getActivity().getBaseContext(), CoverActivity.class);
        mIntent.putExtras(bundle);
        startActivity(mIntent);
        getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }
}