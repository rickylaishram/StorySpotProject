package com.storyspot.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import static com.storyspot.utils.Constants.SP_STORY_PROGRESS;
import static com.storyspot.utils.Constants.SP_USER_AUTH;

import java.util.Map;
import java.util.Set;

/**
 * Created by rickylaishram on 11/22/13.
 */
public class StoryProgressData {
    public void saveProgress(Context ctx, String story_id, int progress, boolean completed) {
        SharedPreferences sp = ctx.getSharedPreferences(SP_STORY_PROGRESS+"_"+story_id, Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("progress", progress);
        edit.putBoolean("completed", completed);
        edit.commit();
    }

    public int getProgress(Context ctx, String story_id) {
        SharedPreferences sp = ctx.getSharedPreferences(SP_STORY_PROGRESS+"_"+story_id, Activity.MODE_PRIVATE);
        return sp.getInt("progress", 0);
    }

    public Boolean getStatus(Context ctx, String story_id) {
        SharedPreferences sp = ctx.getSharedPreferences(SP_STORY_PROGRESS+"_"+story_id, Activity.MODE_PRIVATE);
        return sp.getBoolean("completed", false);
    }
}
