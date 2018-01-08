package com.paiai.android.test;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

/**
 *
 */

public class SystemUtils {
    public static Display getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay();
    }
}
