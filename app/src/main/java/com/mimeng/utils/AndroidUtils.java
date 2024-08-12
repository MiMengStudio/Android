package com.mimeng.utils;

import android.content.Context;
import android.content.Intent;

public class AndroidUtils {
    public static void shareText(Context context, String content) {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT, content);
        i.setType("text/plain");
        context.startActivity(Intent.createChooser(i, "分享到"));
    }
}
