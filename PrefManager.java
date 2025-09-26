package com.spacester.tweetsterupdate.live.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.spacester.tweetsterupdate.live.Constants;


public class PrefManager {
    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }
}
