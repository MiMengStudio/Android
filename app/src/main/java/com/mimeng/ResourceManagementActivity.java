package com.mimeng;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.AnyThread;
import androidx.cardview.widget.CardView;

import com.google.gson.Gson;
import com.mimeng.BaseClass.BaseActivity;
import com.mimeng.resourcepack.ResourcePackInfo;

import com.mimeng.utils.IOUtils;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ResourceManagementActivity extends BaseActivity {

  private static final String TAG = "ResourceManagementActivity";
  private Handler mHandler;  
    
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    blackParentBar();
    setFullScreen(false);
    setContentView(R.layout.activity_resource_management);
    
    mHandler = new ManagerHandler(this);
    ImageButton backButton = findViewById(R.id.back);
    backButton.setOnClickListener(
        v -> {
          // 当按钮被点击时，关闭当前Activity
          finish();
        });

    CardView item_res = findViewById(R.id.item_res);
    item_res.setOnClickListener(
        v -> {
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
      new Thread(
              () -> {
                // 使用try-with-resources自动管理资源
                Message msg = Message.obtain();    
                try (InputStream inputStream = getContentResolver().openInputStream(uri);
                    ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
                  ZipEntry entry;
                  
                  msg.what = ManagerHandler.MSG_TOAST_SHORT;      
                  while ((entry = zipInputStream.getNextEntry()) != null) {
                    String fileName = entry.getName();
                    if (fileName
                        .equalsIgnoreCase("info.json")) {
                      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                      IOUtils.copy(zipInputStream, outputStream);
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
                          getAllResultFile(uri);
                        } else {
                          msg.obj = "该文件不是道具图片素材包，请重新选择";       
                        }
                        mHandler.sendMessage(msg);              
                      } else {
                        msg.obj = "info.json为空";
                        mHandler.sendMessage(msg);
                      }
                      break; // 找到info.json后退出循环
                    }
                    zipInputStream.closeEntry();
                  }
                    
                } catch (IOException e) {
                  msg.obj = "读取文件时发生错误";
                  mHandler.sendMessage(msg);      
                  Log.e(TAG, "Error when reading files", e);
                }
              },
              "ResourceReader")
          .start();
    }
  }

  private void getAllResultFile(Uri uri) {
    try (ZipInputStream zis = new ZipInputStream(getContentResolver().openInputStream(uri))) {
      ZipEntry zipEntry = zis.getNextEntry();

      File privateDir =
          new File(getApplicationContext().getExternalFilesDir("res/item").getAbsolutePath());
      if (!privateDir.exists()) {
        privateDir.mkdirs();
      }

      while (zipEntry != null) {
        String entryName = zipEntry.getName();
        if(!zipEntry.isDirectory()) {
            File targetFile = new File(privateDir, entryName);
            targetFile.getParentFile().mkdirs();        
            try (BufferedOutputStream bos =
                new BufferedOutputStream(new FileOutputStream(targetFile))) {
              IOUtils.copy(zis, bos);
            }
        }
        zipEntry = zis.getNextEntry();
      }
      zis.closeEntry();
    } catch (Exception e) {
      Log.e("getAllResultFile: ", "错误：", e);
    }
  }

  private static class ManagerHandler extends Handler {
    private WeakReference<ResourceManagementActivity> mResRef;
    static final int MSG_TOAST_LONG = 0;
    static final int MSG_TOAST_SHORT = 1;
    ManagerHandler(ResourceManagementActivity ctx) {
      this.mResRef = new WeakReference<>(ctx);
    }

    @Override
    public void handleMessage(Message msg) {
      Context ctx = mResRef.get();
      if (ctx == null) return;
      switch (msg.what) {
          case MSG_TOAST_SHORT:
                Toast.makeText(ctx, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                return;
          case MSG_TOAST_LONG:
                Toast.makeText(ctx, msg.obj.toString(), Toast.LENGTH_LONG).show();
                return;
                
      }
      super.handleMessage(msg);
    }
  }
}
