package com.mimeng;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.AnyThread;
import androidx.cardview.widget.CardView;
import cn.hutool.core.io.FileUtil;
import com.google.gson.Gson;
import com.mimeng.BaseClass.BaseActivity;
import com.mimeng.resourcepack.ResourcePackInfo;
import com.mimeng.utils.IOUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ResourceManagementActivity extends BaseActivity {

  private static final String TAG = "ResourceManagementActivity";
  private Handler mHandler;  
  private ProgressBar progressBar;    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blackParentBar();
        setFullScreen(false);
        setContentView(R.layout.activity_resource_management);
    
        mHandler = new ManagerHandler(this);
        ImageButton backButton = findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            // 当按钮被点击时，关闭当前Activity
            finish();
        });

        CardView item_res = findViewById(R.id.item_res);
        item_res.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/zip"); // 指定MIME类型为ZIP文件
            intent.addCategory(Intent.CATEGORY_OPENABLE); // 确保Intent可以处理打开文件的操作
            startActivityForResult(Intent.createChooser(intent, "请选择道具图片素材包"), 1);
        });
  }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Uri uri = Objects.requireNonNull(result.getData(), "No URI returned from file picker");
            Log.i(TAG, uri.toString() + "  " + uri.getPath());
            new Thread(() -> {
                // 使用try-with-resources自动管理资源
                Message msg = Message.obtain();    
                msg.what = ManagerHandler.MSG_TOAST_SHORT;      
                try (InputStream is = getContentResolver().openInputStream(uri);
                    ZipInputStream zis = new ZipInputStream(is)) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ZipEntry entry;
                    while ((entry = zis.getNextEntry()) != null) {
                        if (entry.getName().equalsIgnoreCase("info.json")) {
                            IOUtils.copy(zis, outputStream);
                            break;  
                        } 
                    }
                    zis.closeEntry();    
                    Log.i(TAG, "Size: " + size);
                    // 将字节流转换为字符串
                    String jsonContent =
                            new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
                    if (!jsonContent.isEmpty()) {
                        Gson gson = new Gson();
                        ResourcePackInfo resPackInfo =
                            gson.fromJson(jsonContent, ResourcePackInfo.class);
                        // 检查资源类型
                        if ("ItemResourcePack".equals(resPackInfo.getType())) {
                            // 处理找到的ResourcePackInfo
                            msg.obj = "导入成功";    
                            getAllResultFile(uri, is.available());
                        } else {
                            msg.obj = "该文件不是道具图片素材包，请重新选择";       
                        }
                        mHandler.sendMessage(msg);              
                    } else {
                        msg.obj = "info.json为空";
                        mHandler.sendMessage(msg);
                    }
                      
                } catch (Exception e) {
                    msg.obj = "读取文件时发生错误";
                    mHandler.sendMessage(msg);      
                    Log.e(TAG, "Error when reading files", e);
                }
              },
              "ResourceReader")
          .start();
        }
    }

    private long size = 0;  
    private void getAllResultFile(Uri uri, long totalSize) {
        Message msg = Message.obtain();
        msg.what = ManagerHandler.MSG_SHOW_PROGRESS;
        mHandler.sendMessage(msg);
        
        try (ZipInputStream zis = new ZipInputStream(getContentResolver().openInputStream(uri))) {
            ZipEntry zipEntry;
        
            File privateDir =
                new File(getApplicationContext().getExternalFilesDir("res/item").getAbsolutePath());
            if (!privateDir.exists()) {
                privateDir.mkdirs();
            }
            while ((zipEntry = zis.getNextEntry()) != null) {
                String entryName = zipEntry.getName();
                if(!zipEntry.isDirectory()) {
                    size += zipEntry.getSize();        
                    File targetFile = new File(privateDir, entryName);
                    targetFile.getParentFile().mkdirs();        
                    try (BufferedOutputStream bos =
                        new BufferedOutputStream(new FileOutputStream(targetFile))) {
                        IOUtils.copy(zis, bos);
                    }
                }
                mHandler.post(() -> progressBar.setProgress((int) ((size * 100) / totalSize)));
            }
            zis.closeEntry();          
            
        } catch (Exception e) {
            Log.e("getAllResultFile: ", "错误：", e);
        }
        Message dismiss = Message.obtain();
        dismiss.what = ManagerHandler.MSG_DISMISS;
        mHandler.sendMessage(dismiss);    
    }

    private static class ManagerHandler extends Handler {
        private WeakReference<ResourceManagementActivity> mResRef;
        private AlertDialog dialog;    
        static final int MSG_TOAST_SHORT = 1;
        static final int MSG_SHOW_PROGRESS = 2;
        static final int MSG_DISMISS = 3;    
         
        ManagerHandler(ResourceManagementActivity ctx) {
            this.mResRef = new WeakReference<>(ctx);
        }

        @Override
        public void handleMessage(Message msg) {
            ResourceManagementActivity ctx = mResRef.get();
            if (ctx == null) return;
            switch (msg.what) {
                case MSG_TOAST_SHORT:
                    Toast.makeText(ctx, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    return;
                case MSG_SHOW_PROGRESS:
                    View view = LayoutInflater.from(ctx).inflate(R.layout.alert_progress_bar, null);
                    ctx.progressBar = view.findViewById(R.id.alert_progress_bar);
                
                    dialog = new AlertDialog.Builder(ctx)
                        .setTitle("正在解压...")
                        .setView(view).create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    return;
                case MSG_DISMISS:
                    Log.i(TAG, "Dismiss dialog");
                    dialog.dismiss();
                    return;
            }
            super.handleMessage(msg);
        }
    }
}
