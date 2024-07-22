package com.mimeng.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import androidx.annotation.NonNull;

public class ClipboardUtils {
    public static void copyToClipboard(@NonNull Context context, @NonNull CharSequence src) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText("", src));
    }
}
