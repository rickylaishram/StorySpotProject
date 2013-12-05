package com.storyspot.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import static com.storyspot.utils.Constants.SP_USER_AUTH;

import java.util.Map;
import java.util.Set;

/**
 * Created by rickylaishram on 11/22/13.
 */
public class UserData {
    public void saveUser(Context ctx, String username, String password, int userid) {
        SharedPreferences sp = ctx.getSharedPreferences(SP_USER_AUTH, Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("username", username);
        edit.putString("password", password);
        edit.putInt("userid", userid);
        edit.commit();
    }

    public String getUserName(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(SP_USER_AUTH, Activity.MODE_PRIVATE);
        return sp.getString("username", null);
    }

    public String getPassword(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(SP_USER_AUTH, Activity.MODE_PRIVATE);
        return sp.getString("password", null);
    }

    public int getUserId(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(SP_USER_AUTH, Activity.MODE_PRIVATE);
        return sp.getInt("userid", 0);
    }
}
