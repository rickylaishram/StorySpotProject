package com.storyspot.network;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.storyspot.utils.Constants;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rickylaishram on 11/24/13.
 */
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

        hpost.setHeader("Authorization", Constants.IMGUR_AUTH);

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
        } catch (IOException e) {
            Log.e("Upload", e.toString());
        }

        return null;
    }
}
