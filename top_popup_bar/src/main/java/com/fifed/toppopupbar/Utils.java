package com.fifed.toppopupbar;

import android.content.Context;

/**
 * Created by Fedir on 22.10.2017.
 */

class Utils {
    static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
