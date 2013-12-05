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
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.storyspot.data.NearbyData;

import java.util.Vector;

/**
 * Created by rickylaishram on 11/15/13.
 */
public class NearbyAdapter extends ArrayAdapter <NearbyData>{
    Context context;
    int layoutResourceId;
    Vector<NearbyData> data = new Vector<NearbyData>();

    public NearbyAdapter(Context context, int layoutResourceId, Vector<NearbyData> data) {
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
            holder.iv_back = (ImageView) row.findViewById(R.id.iv_cover);
            holder.tv_distance = (TextView) row.findViewById(R.id.tv_distance);
            holder.tv_title = (TextView) row.findViewById(R.id.tv_title);
            holder.tv_creator = (TextView) row.findViewById(R.id.tv_creator);
            holder.tv_date = (TextView) row.findViewById(R.id.tv_date);

            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        Typeface font_light = Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");
        Typeface font_black = Typeface.createFromAsset(context.getAssets(), "Roboto-Black.ttf");
        Typeface font_blackitalics = Typeface.createFromAsset(context.getAssets(), "Roboto-BlackItalic.ttf");
        Typeface font_georgia = Typeface.createFromAsset(context.getAssets(), "Georgia-Italic.ttf");

        NearbyData item = data.elementAt(position);
        holder.tv_title.setText(item.title);
        holder.tv_distance.setText(item.distance);
        holder.tv_date.setText(item.date);
        holder.tv_creator.setText(item.creator);

        holder.tv_distance.setTypeface(font_georgia);
        holder.tv_title.setTypeface(font_black);
        holder.tv_creator.setTypeface(font_georgia);
        holder.tv_date.setTypeface(font_georgia);

        Picasso.with(context).load(item.image).into(holder.iv_back);

        return row;
    }

    static class Holder {
        public TextView tv_distance;
        public TextView tv_title;
        public TextView tv_creator;
        public TextView tv_date;
        public ImageView iv_back;
    }
}
