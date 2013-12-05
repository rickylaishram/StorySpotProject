package com.storyspot;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.storyspot.data.NearbyData;
import com.storyspot.data.SummaryData;

import java.util.Vector;

/**
 * Created by rickylaishram on 11/15/13.
 */
public class SummaryAdapter extends ArrayAdapter <SummaryData>{
    Context context;
    int layoutResourceId;
    Vector<SummaryData> data = new Vector<SummaryData>();

    public SummaryAdapter(Context context, int layoutResourceId, Vector<SummaryData> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder = null;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new Holder();
            holder.iv_image = (ImageView) row.findViewById(R.id.iv_image);
            holder.tv_dist = (TextView) row.findViewById(R.id.tv_distance);
            holder.tv_name = (TextView) row.findViewById(R.id.tv_name);

            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        Typeface font_light = Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");
        Typeface font_black = Typeface.createFromAsset(context.getAssets(), "Roboto-Black.ttf");
        Typeface font_blackitalics = Typeface.createFromAsset(context.getAssets(), "Roboto-BlackItalic.ttf");
        Typeface font_georgia = Typeface.createFromAsset(context.getAssets(), "Georgia-Italic.ttf");

        SummaryData item = data.elementAt(position);

        holder.tv_name.setText(item.name);
        holder.tv_dist.setText(item.distance + " meters");

        holder.tv_name.setTypeface(font_black);
        holder.tv_dist.setTypeface(font_georgia);

        Picasso.with(context).load(item.image).into(holder.iv_image);

        return row;
    }

    static class Holder {
        public ImageView iv_image;
        public TextView tv_dist;
        public TextView tv_name;
    }
}
