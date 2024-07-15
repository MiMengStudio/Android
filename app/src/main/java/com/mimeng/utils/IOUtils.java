package com.mimeng.utils;

import androidx.annotation.NonNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {
    public static void copy(@NonNull InputStream input, @NonNull OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = input.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
    	
    }
}
