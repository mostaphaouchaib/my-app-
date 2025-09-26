package com.spacester.tweetsterupdate.live.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

@SuppressWarnings("deprecation")
public class WindowUtil {
    @SuppressLint("ObsoleteSdkInt")
    public static void hideWindowStatusBar(Window window) {

    }

    public static int getSystemStatusBarHeight(Context context) {
        int id = context.getResources().getIdentifier(
                "status_bar_height", "dimen", "android");
        return id > 0 ? context.getResources().getDimensionPixelSize(id) : id;
    }
}
